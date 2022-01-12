package com.lesorin.sparknotifications.model.services;

import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.os.PowerManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import com.lesorin.sparknotifications.BuildConfig;
import com.lesorin.sparknotifications.model.AppHelper;
import com.lesorin.sparknotifications.model.receivers.ScreenNotificationsDeviceAdminReceiver;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

//TODO figure out where the methods from this class go.
class ScreenController
{
    private static AtomicLong sLastNotificationTime = new AtomicLong();
    private final Context _context;
    private final SharedPreferences mPrefs;
    private final PowerManager mPowerManager;

    public ScreenController(Context context)
    {
        _context = context;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        mPowerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
    }

    public void handleNotification(String packageName, boolean isProximitySensorEnabled, boolean isObjectCoveringDevice)
    {
        sLastNotificationTime.set(System.currentTimeMillis());

        if(shouldTurnOnScreen(isProximitySensorEnabled, isObjectCoveringDevice))
        {
            AppHelper.recordScreenWakeFromApp(packageName);
            Executors.newSingleThreadExecutor().submit(this::turnOnScreen);
        }
    }

    public void handlePickup()
    {
        if(!isInCall() && !mPowerManager.isScreenOn())
        {
            AppHelper.recordScreenWakeFromApp(BuildConfig.APPLICATION_ID);
            //mLogger.debug("Turning on screen for pickup");

            int flag;

            if(mPrefs.getBoolean("FullBrightnessKey", false))
            {
                flag = PowerManager.SCREEN_BRIGHT_WAKE_LOCK;
            }
            else
            {
                flag = PowerManager.SCREEN_DIM_WAKE_LOCK;
            }

            PowerManager.WakeLock wakeLock = mPowerManager.newWakeLock(flag | PowerManager.ACQUIRE_CAUSES_WAKEUP, "Spark Notifications:");

            wakeLock.acquire();
            wakeLock.release();
        }
    }

    private void turnOnScreen()
    {
       // mLogger.debug("Turning on screen");

        int delay = mPrefs.getInt("delay", 0);

        if(delay > 0)
        {
            //mLogger.debug("Sleeping for " + delay + " seconds before turning on screen");
            SystemClock.sleep(delay * 1000L);
        }

        int flag;

        if(mPrefs.getBoolean("FullBrightnessKey", false))
        {
            flag = PowerManager.SCREEN_BRIGHT_WAKE_LOCK;
        }
        else
        {
            flag = PowerManager.SCREEN_DIM_WAKE_LOCK;
        }

        PowerManager.WakeLock wakeLock = mPowerManager.newWakeLock(flag | PowerManager.ACQUIRE_CAUSES_WAKEUP, "Spark Notifications:");

        wakeLock.acquire();

        if(mPrefs.getBoolean("NotificationsTrayKey", false))
        {
            expandStatusBar();
        }

        DevicePolicyManager dpm = (DevicePolicyManager)_context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName deviceAdmin = new ComponentName(_context, ScreenNotificationsDeviceAdminReceiver.class);

        long desiredWakeLength = mPrefs.getInt("wake_length", 10) * 1000L;
        long actualWakeLength = desiredWakeLength;

        //TODO use while instead.
        do
        {
            //mLogger.debug("Sleeping for " + actualWakeLength);
            SystemClock.sleep(actualWakeLength);
            actualWakeLength = sLastNotificationTime.get() + desiredWakeLength - System.currentTimeMillis();
        }
        while(actualWakeLength > 1000);

        wakeLock.release();

        if(dpm.isAdminActive(deviceAdmin) && isDeviceLocked())
        {
            //mLogger.debug("Device is an active admin and device is still on lock screen, locking");
            dpm.lockNow();
        }
    }

    @SuppressWarnings("ResourceType")
    private void expandStatusBar()
    {
        try
        {
            Object statusBarService = _context.getSystemService("statusbar");
            Class<?> statusBarManager = Class.forName("android.app.StatusBarManager");

            Method showStatusBar;

            if(Build.VERSION.SDK_INT >= 17)
            {
                showStatusBar = statusBarManager.getMethod("expandNotificationsPanel");
            }
            else
            {
                showStatusBar = statusBarManager.getMethod("expand");
            }

            showStatusBar.invoke(statusBarService);

            //mLogger.debug("Expanding status bar");
        }
        catch(Exception e)
        {
            //mLogger.debug("Failed to expand the status bar: " + e.getMessage());
        }
    }

    private boolean isDeviceLocked()
    {
        return ((KeyguardManager)_context.getSystemService(Context.KEYGUARD_SERVICE)).inKeyguardRestrictedInputMode();
    }

    private boolean shouldTurnOnScreen(boolean isProximitySensorEnabled, boolean isObjectCoveringDevice)
    {
        boolean turnOnScreen = !isInQuietTime() && !isInCall() && !mPowerManager.isScreenOn();

        //If the proximity sensor is enabled, only turn on the screen if an object is not close to
        //the device's screen.
        if(turnOnScreen && isProximitySensorEnabled)
        {
            turnOnScreen = !isObjectCoveringDevice;
        }

        return turnOnScreen;
    }

    private boolean isInQuietTime()
    {
        boolean quietTime = false;

        if(mPrefs.getBoolean("QuietHoursKey", false))
        {
            String startTime = mPrefs.getString("QuietHoursStartKey", "22:00");
            String stopTime = mPrefs.getString("QuietHoursStopKey", "08:00");
            SimpleDateFormat sdfDate = new SimpleDateFormat("H:mm");
            String currentTimeStamp = sdfDate.format(new Date());
            int currentHour = Integer.parseInt(currentTimeStamp.split("[:]+")[0]);
            int currentMinute = Integer.parseInt(currentTimeStamp.split("[:]+")[1]);
            int startHour = Integer.parseInt(startTime.split("[:]+")[0]);
            int startMinute = Integer.parseInt(startTime.split("[:]+")[1]);
            int stopHour = Integer.parseInt(stopTime.split("[:]+")[0]);
            int stopMinute = Integer.parseInt(stopTime.split("[:]+")[1]);

            if(startHour < stopHour && currentHour > startHour && currentHour < stopHour)
            {
                quietTime = true;
            }
            else if(startHour > stopHour && (currentHour > startHour || currentHour < stopHour))
            {
                quietTime = true;
            }
            else if(currentHour == startHour && currentMinute >= startMinute)
            {
                quietTime = true;
            }
            else if(currentHour == stopHour && currentMinute < stopMinute)
            {
                quietTime = true;
            }
        }

        //mLogger.debug("Device is in quiet time: " + quietTime);
        return quietTime;
    }

    private boolean isInCall()
    {
        AudioManager manager = (AudioManager)_context.getSystemService(Context.AUDIO_SERVICE);
        boolean inCall = (manager.getMode() == AudioManager.MODE_IN_CALL || manager.getMode() == AudioManager.MODE_IN_COMMUNICATION);

        //mLogger.debug("Device is in a call: " + inCall);
        return inCall;
    }
}