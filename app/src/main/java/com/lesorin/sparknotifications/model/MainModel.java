package com.lesorin.sparknotifications.model;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.lesorin.sparknotifications.presenter.Contract;
import com.lesorin.sparknotifications.model.receivers.DeviceAdministratorReceiver;
import com.lesorin.sparknotifications.model.services.NotificationListener;
import java.util.List;
import io.realm.Realm;

public class MainModel implements Contract.Model
{
    private final Context _context;
    private Contract.PresenterModel _presenter;
    private final SharedPreferences _preferences;
    private final DevicePolicyManager _devicePolicyManager;
    private final ComponentName _adminComponent;

    public MainModel(Context context)
    {
        _context = context;
        _preferences = PreferenceManager.getDefaultSharedPreferences(_context);
        _devicePolicyManager = (DevicePolicyManager)_context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        _adminComponent = new ComponentName(_context, DeviceAdministratorReceiver.class);

        Realm.init(_context);

    }

    public void setPresenter(Contract.PresenterModel presenter)
    {
        _presenter = presenter;
    }

    @Override
    public boolean isNotificationsServiceEnabled()
    {
        ActivityManager activityManager = (ActivityManager)_context.getSystemService(Activity.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = activityManager.getRunningServices(Integer.MAX_VALUE);

        //Check if any of the running services is the notifications listener service.
        for(ActivityManager.RunningServiceInfo serviceInfo : runningServices)
        {
            if(serviceInfo.service.getClassName().equals(NotificationListener.class.getName()))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isDeviceAdministratorEnabled()
    {
        return _devicePolicyManager.isAdminActive(_adminComponent);
    }

    @Override
    public int getScreenTimeoutValue(int defaultValue)
    {
        return _preferences.getInt(PreferencesKeys.SCREEN_TIMEOUT, defaultValue);
    }

    @Override
    public void disableDeviceAdministrator()
    {
        //removeActiveAdmin() is an asynchronous method, so isAdminActive() may still return true
        // if called immediately after removeActiveAdmin().
        _devicePolicyManager.removeActiveAdmin(_adminComponent);
    }

    @Override
    public ComponentName getAdminComponent()
    {
        return _adminComponent;
    }

    @Override
    public void setScreenTimeoutValue(int value)
    {
        _preferences.edit().putInt(PreferencesKeys.SCREEN_TIMEOUT, value).apply();
    }

    @Override
    public int getScreenDelayValue(int defaultValue)
    {
        return _preferences.getInt(PreferencesKeys.SCREEN_ON_DELAY, defaultValue);
    }

    @Override
    public void setScreenDelayValue(int value)
    {
        _preferences.edit().putInt(PreferencesKeys.SCREEN_ON_DELAY, value).apply();
    }

    @Override
    public void setFullBrightnessValue(boolean enabled)
    {
        _preferences.edit().putBoolean(PreferencesKeys.FULL_BRIGHTNESS_ENABLED, enabled).apply();
    }
}