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

public class NotificationListener extends NotificationListenerService implements SensorEventListener,
        SharedPreferences.OnSharedPreferenceChangeListener
{
    private String mLastNotifyingPackage;
    private TriggerEventListener _pickUpListener;
    private ScreenController _screenController;
    private SensorManager _sensorManager;
    private Sensor _pickupSensor;

    @Override
    public void onCreate()
    {
        super.onCreate();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

        _sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        _pickupSensor = _sensorManager.getDefaultSensor(25);
        _screenController = new ScreenController(this, false);

        initializePickUpListener();

        if(isDetectDevicePickUpEnabled())
        {
            registerPickupListener();
        }
    }

    private void initializePickUpListener()
    {
        _pickUpListener = new TriggerEventListener()
        {
            @Override
            public void onTrigger(TriggerEvent triggerEvent)
            {
                _screenController.handlePickup();

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
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
        unregisterPickupListener();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn)
    {
        if(sbn.isOngoing())
        {
            return;
        }

        AppHelper.recordNotificationFromApp(sbn.getPackageName());

        if(!AppHelper.isAppEnabled(sbn.getPackageName()))
        {
            return;
        }

        //LoggerFactory.getLogger("NotificationListener").debug("Got a non-ongoing notification for an enabled app. " + sbn.getPackageName());

        mLastNotifyingPackage = sbn.getPackageName();

        //TODO make sure this logic is correct because the logic of the sensor changed.
        if(isProximitySensorEnabled())
        {
            if(!registerProximitySensorListener())
            {
                new ScreenController(this, false).handleNotification(mLastNotifyingPackage);
            }
        }
        else
        {
            new ScreenController(this, false).handleNotification(mLastNotifyingPackage);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if(event.sensor.getType() == Sensor.TYPE_PROXIMITY)
        {
            unregisterProximitySensorListener();

            boolean close = event.values[0] < event.sensor.getMaximumRange();

            new ScreenController(this, close).handleNotification(mLastNotifyingPackage);
        }
    }

    private boolean isProximitySensorEnabled()
    {
        //TODO make sure this logic is correct because the logic of the sensor changed.
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean("ProximitySensorKey", true);
    }

    private boolean registerProximitySensorListener()
    {
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        if(proximitySensor == null)
        {
            return false;
        }
        else
        {
            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);

            return true;
        }
    }

    private void unregisterProximitySensorListener()
    {
        ((SensorManager)getSystemService(Context.SENSOR_SERVICE)).unregisterListener(this);
    }

    private boolean isDetectDevicePickUpEnabled()
    {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(PreferencesKeys.DETECT_PICK_UP, false);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key)
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
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
    }
}