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
import com.lesorin.sparknotifications.model.PreferencesKeys;
import com.lesorin.sparknotifications.model.RealmApp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import io.realm.Realm;

public class NotificationListener extends NotificationListenerService
{
    private final String DEFAULT_QUIET_HOURS_START_24H = "23:00", DEFAULT_QUIET_HOURS_STOP_24H = "07:00";
    private final int START_DAY_POINT = 0, END_DAY_POINT = 24 * 60;

    private String _lastNotifyingPackage;
    private TriggerEventListener _pickUpListener;
    private ScreenController _screenController;
    private SensorManager _sensorManager;
    private Sensor _pickupSensor, _proximitySensor;
    private SharedPreferences.OnSharedPreferenceChangeListener _preferencesListener;
    private SensorEventListener _proximitySensorListener;
    private SharedPreferences _preferences;
    private SimpleDateFormat _dateFormatter;

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
        _dateFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());

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
                        getScreenDelay(), getScreenTimeoutMs(), isInQuietHours());
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
        _preferences.unregisterOnSharedPreferenceChangeListener(_preferencesListener);
        unregisterPickupListener();
        unregisterProximitySensorListener();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn)
    {
        if(!sbn.isOngoing() && isAppEnabled(sbn.getPackageName()))
        {
            _lastNotifyingPackage = sbn.getPackageName();

            //If the proximity sensor is disabled, handle the notification directly.
            if(!isProximitySensorEnabled())
            {
                _screenController.handleNotification(_lastNotifyingPackage, false, false,
                        getScreenDelay(), getScreenTimeoutMs(), isInQuietHours());
            }
            else if(!registerProximitySensorListener())
            {
                //If the proximity sensor is registered, it will take care of handling the
                //notification, otherwise handle the notification here as if the proximity
                //sensor was disabled.
                _screenController.handleNotification(_lastNotifyingPackage, false, false,
                        getScreenDelay(), getScreenTimeoutMs(), isInQuietHours());
            }
        }
    }

    private boolean isAppEnabled(String packageName)
    {
        Realm realm = Realm.getDefaultInstance();
        RealmApp existingApp = realm.where(RealmApp.class).equalTo("packageName", packageName).findFirst();
        boolean enabled = false;

        if(existingApp != null)
        {
            enabled = existingApp.getEnabled();
        }

        realm.close();

        return enabled;
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

    private boolean isInQuietHours()
    {
        boolean inQuietHours = false;

        if(isQuietHoursEnabled())
        {
            try
            {
                //Grab the current time, along with the start and end of the quiet hours.
                String currentTime = _dateFormatter.format(new Date());
                String quietHoursStart = getQuietHoursStart24H(DEFAULT_QUIET_HOURS_START_24H);
                String quietHoursEnd = getQuietHoursStop24H(DEFAULT_QUIET_HOURS_STOP_24H);

                //Grab the hours and minutes from all three times.
                int currentHour = Integer.parseInt(currentTime.split("[:]+")[0]);
                int currentMinute = Integer.parseInt(currentTime.split("[:]+")[1]);
                int quietHoursStartHour = Integer.parseInt(quietHoursStart.split("[:]+")[0]);
                int quietHoursStartMinute = Integer.parseInt(quietHoursStart.split("[:]+")[1]);
                int quietHoursStopHour = Integer.parseInt(quietHoursEnd.split("[:]+")[0]);
                int quietHoursStopMinute = Integer.parseInt(quietHoursEnd.split("[:]+")[1]);

                //Convert all three times to minutes into the day.
                int currentPoint = currentHour * 60 + currentMinute;
                int quietHoursStartPoint = quietHoursStartHour * 60 + quietHoursStartMinute;
                int quietHoursEndPoint = quietHoursStopHour * 60 + quietHoursStopMinute;

                //Check if the current time is inside the range of the quiet hours.
                if(quietHoursStartPoint < quietHoursEndPoint)
                {
                    inQuietHours = (currentPoint >= quietHoursStartPoint) && (currentPoint < quietHoursEndPoint);
                }
                else if(quietHoursStartPoint > quietHoursEndPoint)
                {
                    inQuietHours = (currentPoint >= quietHoursStartPoint && currentPoint < END_DAY_POINT) ||
                            (currentPoint >= START_DAY_POINT && currentPoint < quietHoursEndPoint);
                }
                else
                {
                    inQuietHours = (currentPoint == quietHoursStartPoint);
                }
            }
            catch (Exception ignored)
            {
            }
        }

        return inQuietHours;
    }

    private String getQuietHoursStart24H(String defaultValue)
    {
        return _preferences.getString(PreferencesKeys.QUIET_HOURS_START_24H, defaultValue);
    }

    private String getQuietHoursStop24H(String defaultValue)
    {
        return _preferences.getString(PreferencesKeys.QUIET_HOURS_STOP_24H, defaultValue);
    }

    private boolean isQuietHoursEnabled()
    {
        return _preferences.getBoolean(PreferencesKeys.QUIET_HOURS_ENABLED, false);
    }
}