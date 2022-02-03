package com.lesorin.sparknotifications.model.services;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.media.AudioManager;
import android.os.PowerManager;
import android.os.SystemClock;
import com.lesorin.sparknotifications.BuildConfig;
import com.lesorin.sparknotifications.model.RealmRecentApp;
import com.lesorin.sparknotifications.model.receivers.DeviceAdministratorReceiver;
import java.lang.reflect.Method;
import java.util.concurrent.Executors;
import io.realm.Realm;

//TODO figure out where the methods from this class go.
class ScreenController
{
    private final String TAG = "Spark Notifications:";
    private final Context _context;
    private final PowerManager _powerManager;
    private final DevicePolicyManager _devicePolicyManager;
    private final ComponentName _adminComponent;
    private final KeyguardManager _keyguardManager;
    private final AudioManager _audioManager;

    public ScreenController(Context context)
    {
        _context = context;
        _powerManager = (PowerManager)_context.getSystemService(Context.POWER_SERVICE);
        _devicePolicyManager = (DevicePolicyManager)_context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        _adminComponent = new ComponentName(_context, DeviceAdministratorReceiver.class);
        _keyguardManager = ((KeyguardManager)_context.getSystemService(Context.KEYGUARD_SERVICE));
        _audioManager = (AudioManager)_context.getSystemService(Context.AUDIO_SERVICE);
    }

    public void handleNotification(String packageName, boolean isProximitySensorEnabled, boolean isObjectCoveringDevice,
                                   int screenDelay, boolean fullBrightnessEnabled, boolean notificationsDrawerEnabled,
                                   int screenTimeoutMs)
    {
        if(shouldTurnOnScreen(isProximitySensorEnabled, isObjectCoveringDevice))
        {
            recordScreenWakeFromApp(packageName);
            Executors.newSingleThreadExecutor().submit(() -> turnOnScreen(screenDelay,
                    fullBrightnessEnabled, notificationsDrawerEnabled, screenTimeoutMs));
        }
    }

    public void handlePickup(boolean fullBrightnessEnabled)
    {
        if(!isInCall() && !_powerManager.isScreenOn())
        {
            recordScreenWakeFromApp(BuildConfig.APPLICATION_ID);

            int flag = fullBrightnessEnabled ? PowerManager.SCREEN_BRIGHT_WAKE_LOCK : PowerManager.SCREEN_DIM_WAKE_LOCK;

            PowerManager.WakeLock wakeLock = _powerManager.newWakeLock(flag | PowerManager.ACQUIRE_CAUSES_WAKEUP, TAG);

            wakeLock.acquire();
            wakeLock.release();
        }
    }

    private void recordScreenWakeFromApp(String packageName)
    {
        Realm realm = Realm.getDefaultInstance();
        RealmRecentApp recentApp = realm.createObject(RealmRecentApp.class);

        realm.beginTransaction();
        recentApp.setPackageName(packageName);
        recentApp.setTimestamp(System.currentTimeMillis());
        realm.commitTransaction();
        realm.close();
    }

    private void turnOnScreen(int screenDelay, boolean fullBrightnessEnabled,
                              boolean notificationsDrawerEnabled, int screenTimeoutMs)
    {
        if(screenDelay > 0)
        {
            SystemClock.sleep(screenDelay * 1000L);
        }

        int flag = fullBrightnessEnabled ? PowerManager.SCREEN_BRIGHT_WAKE_LOCK : PowerManager.SCREEN_DIM_WAKE_LOCK;
        PowerManager.WakeLock wakeLock = _powerManager.newWakeLock(flag | PowerManager.ACQUIRE_CAUSES_WAKEUP, TAG);

        wakeLock.acquire();

        if(notificationsDrawerEnabled)
        {
            expandStatusBar();
        }

        if(_devicePolicyManager.isAdminActive(_adminComponent) && isDeviceLocked())
        {
            SystemClock.sleep(screenTimeoutMs);
            wakeLock.release();
            _devicePolicyManager.lockNow();
        }
        else
        {
            wakeLock.release();
        }
    }

    private void expandStatusBar()
    {
        try
        {
            @SuppressLint("WrongConstant") Object statusBarService = _context.getSystemService("statusbar");
            Class<?> statusBarManager = Class.forName("android.app.StatusBarManager");
            Method showStatusBar = statusBarManager.getMethod("expandNotificationsPanel");

            showStatusBar.invoke(statusBarService);
        }
        catch(Exception ignored)
        {
        }
    }

    private boolean isDeviceLocked()
    {
        return _keyguardManager.inKeyguardRestrictedInputMode();
    }

    private boolean shouldTurnOnScreen(boolean isProximitySensorEnabled, boolean isObjectCoveringDevice)
    {
        boolean turnOnScreen = !isInQuietTime() && !isInCall() && !isScreenOn();

        //If the proximity sensor is enabled, only turn on the screen if an object is not close to
        //the device's screen.
        if(turnOnScreen && isProximitySensorEnabled)
        {
            turnOnScreen = !isObjectCoveringDevice;
        }

        return turnOnScreen;
    }

    private boolean isScreenOn()
    {
        return _powerManager.isInteractive();
    }

    private boolean isInQuietTime()
    {
        boolean quietTime = false;

        //todo
        /*if(mPrefs.getBoolean("QuietHoursKey", false))
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
        }*/

        //mLogger.debug("Device is in quiet time: " + quietTime);
        return quietTime;
    }

    private boolean isInCall()
    {
        return (_audioManager.getMode() == AudioManager.MODE_IN_CALL ||
                _audioManager.getMode() == AudioManager.MODE_IN_COMMUNICATION);
    }
}