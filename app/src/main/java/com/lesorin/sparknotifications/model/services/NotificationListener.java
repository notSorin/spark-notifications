package com.lesorin.sparknotifications.model.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import com.lesorin.sparknotifications.model.AppHelper;
import com.lesorin.sparknotifications.model.PreferencesKeys;

public class NotificationListener extends NotificationListenerService
{
    private String _lastNotifyingPackage;
    private TriggerEventListener _pickUpListener;
    private ScreenController _screenController;
    private SensorManager _sensorManager;
    private Sensor _pickupSensor, _proximitySensor;
    private SharedPreferences.OnSharedPreferenceChangeListener _preferencesListener;
    private SensorEventListener _proximitySensorListener;
    private SharedPreferences _preferences;

    @Override
    public void onCreate()
    {
        super.onCreate();

        _preferences = PreferenceManager.getDefaultSharedPreferences(this);

        initializePreferencesListener();
        initializePickUpListener();
        initializeProximitySensorListener();
        _preferences.registerOnSharedPreferenceChangeListener(_preferencesListener);

        _sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        _pickupSensor = _sensorManager.getDefaultSensor(25);
        _proximitySensor = _sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        _screenController = new ScreenController(this);

        if(isDetectDevicePickUpEnabled())
        {
            registerPickupListener();
        }
    }

    private void initializeProximitySensorListener()
    {
        _proximitySensorListener = new SensorEventListener()
        {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent)
            {
                //Must read first from the sensor before the listener is unregistered.
                //TODO this is not working properly. it works while debugging but not when running...
                boolean isObjectCoveringDevice = sensorEvent.values[0] < sensorEvent.sensor.getMaximumRange();

                unregisterProximitySensorListener(); //Remove the listener so it doesn't keep triggering.

                _screenController.handleNotification(_lastNotifyingPackage, true, isObjectCoveringDevice,
                        getScreenDelay(), isFullBrightnessEnabled(), isNotificationsDrawerEnabled(),
                        getScreenTimeoutMs());
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i)
            {
            }
        };
    }

    private int getScreenDelay()
    {
        return _preferences.getInt(PreferencesKeys.SCREEN_ON_DELAY, 0);
    }

    private boolean isFullBrightnessEnabled()
    {
        return _preferences.getBoolean(PreferencesKeys.FULL_BRIGHTNESS_ENABLED, false);
    }

    private boolean isNotificationsDrawerEnabled()
    {
        return _preferences.getBoolean(PreferencesKeys.NOTIFICATIONS_DRAWER_ENABLED, false);
    }

    private int getScreenTimeoutMs()
    {
        return _preferences.getInt(PreferencesKeys.SCREEN_TIMEOUT, 3) * 1000;
    }

    private void initializePreferencesListener()
    {
        _preferencesListener = (sharedPreferences, key) ->
        {
            if(key.equals(PreferencesKeys.DETECT_PICK_UP))
            {
                if(isDetectDevicePickUpEnabled())
                {
                    registerPickupListener();
                }
                else
                {
                    unregisterPickupListener();
                }
            }
        };
    }

    private void initializePickUpListener()
    {
        _pickUpListener = new TriggerEventListener()
        {
            @Override
            public void onTrigger(TriggerEvent triggerEvent)
            {
                _screenController.handlePickup(isFullBrightnessEnabled());

                //Need to register the listener again if the option is enabled.
                if(isDetectDevicePickUpEnabled())
                {
                    registerPickupListener();
                }
            }
        };
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        _preferences.unregisterOnSharedPreferenceChangeListener(_preferencesListener);
        unregisterPickupListener();
        unregisterProximitySensorListener();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn)
    {
        if(!sbn.isOngoing() && AppHelper.isAppEnabled(sbn.getPackageName()))
        {
            _lastNotifyingPackage = sbn.getPackageName();

            //If the proximity sensor is disabled, handle the notification directly.
            if(!isProximitySensorEnabled())
            {
                _screenController.handleNotification(_lastNotifyingPackage, false, false,
                        getScreenDelay(), isFullBrightnessEnabled(), isNotificationsDrawerEnabled(), getScreenTimeoutMs());
            }
            else if(!registerProximitySensorListener())
            {
                //If the proximity sensor is registered, it will take care of handling the
                //notification, otherwise handle the notification here as if the proximity
                //sensor was disabled.
                _screenController.handleNotification(_lastNotifyingPackage, false, false,
                        getScreenDelay(), isFullBrightnessEnabled(), isNotificationsDrawerEnabled(), getScreenTimeoutMs());
            }
        }
    }

    private boolean isProximitySensorEnabled()
    {
        return _preferences.getBoolean(PreferencesKeys.PROXIMITY_SENSOR_ENABLED, true);
    }

    private boolean registerProximitySensorListener()
    {
        return _sensorManager.registerListener(_proximitySensorListener, _proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void unregisterProximitySensorListener()
    {
        _sensorManager.unregisterListener(_proximitySensorListener);
    }

    private boolean isDetectDevicePickUpEnabled()
    {
        return _preferences.getBoolean(PreferencesKeys.DETECT_PICK_UP, false);
    }

    private void registerPickupListener()
    {
        if(_pickupSensor != null)
        {
            _sensorManager.requestTriggerSensor(_pickUpListener, _pickupSensor);
        }
    }

    private void unregisterPickupListener()
    {
        if(_pickupSensor != null)
        {
            _sensorManager.cancelTriggerSensor(_pickUpListener, _pickupSensor);
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn)
    {
        //Nothing to do here.
    }
}