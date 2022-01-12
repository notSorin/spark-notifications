package com.lesorin.sparknotifications.model;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.lesorin.sparknotifications.presenter.Contract;
import com.lesorin.sparknotifications.view.receivers.ScreenNotificationsDeviceAdminReceiver;
import com.lesorin.sparknotifications.view.services.NotificationListener;
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
        _adminComponent = new ComponentName(_context, ScreenNotificationsDeviceAdminReceiver.class);

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
            //TODO may need to pass the name of the service name instead of accessing NotificationListener from here,
            // or maybe move NotificationListener to the model layer.
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
    public int getScreenTimeoutValue()
    {
        //todo read value from the preferences
        return 0;
    }

    @Override
    public void disableDeviceAdministrator()
    {
        _devicePolicyManager.removeActiveAdmin(_adminComponent);
    }

    @Override
    public ComponentName getAdminComponent()
    {
        return _adminComponent;
    }
}