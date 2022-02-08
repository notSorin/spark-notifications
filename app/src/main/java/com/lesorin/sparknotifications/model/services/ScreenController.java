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
import java.util.concurrent.Executors;
import io.realm.Realm;

class ScreenController
{
    private final String TAG = "Spark Notifications:";
    private final Context _context;
    private final PowerManager _powerManager;
    private final DevicePolicyManager _devicePolicyManager;
    private final ComponentName _adminComponent;
    private final KeyguardManager _keyguardManager;
    private final AudioManager _audioManager;

    @SuppressLint("WrongConstant")
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
                                   int screenDelayMs, int screenTimeoutMs, boolean isInQuietHours)
    {
        if(shouldTurnOnScreen(isProximitySensorEnabled, isObjectCoveringDevice, isInQuietHours))
        {
            recordScreenWakeFromApp(packageName);
            Executors.newSingleThreadExecutor().submit(() -> turnOnScreen(screenDelayMs, screenTimeoutMs));
        }
    }

    public void handlePickup()
    {
        if(!isInCall() && !_powerManager.isScreenOn())
        {
            recordScreenWakeFromApp(BuildConfig.APPLICATION_ID);

            PowerManager.WakeLock wakeLock = _powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, TAG);

            wakeLock.acquire();
            wakeLock.release();
        }
    }

    private void recordScreenWakeFromApp(String packageName)
    {
        Realm realm = Realm.getDefaultInstance();

        realm.beginTransaction();

        RealmRecentApp recentApp = realm.createObject(RealmRecentApp.class);

        recentApp.setPackageName(packageName);
        recentApp.setTimestamp(System.currentTimeMillis());
        realm.commitTransaction();
        realm.close();
    }

    private void turnOnScreen(int screenDelayMs, int screenTimeoutMs)
    {
        if(screenDelayMs > 0)
        {
            SystemClock.sleep(screenDelayMs);
        }

        PowerManager.WakeLock wakeLock = _powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, TAG);

        wakeLock.acquire(60000L);

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

    private boolean isDeviceLocked()
    {
        return _keyguardManager.inKeyguardRestrictedInputMode();
    }

    private boolean shouldTurnOnScreen(boolean isProximitySensorEnabled, boolean isObjectCoveringDevice,
                                       boolean isInQuietHours)
    {
        boolean turnOnScreen = !isInQuietHours && !isInCall() && !isScreenOn();

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

    private boolean isInCall()
    {
        return (_audioManager.getMode() == AudioManager.MODE_IN_CALL ||
                _audioManager.getMode() == AudioManager.MODE_IN_COMMUNICATION);
    }
}