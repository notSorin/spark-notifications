package com.lesorin.sparknotifications.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.lesorin.sparknotifications.presenter.Contract;
import io.realm.Realm;

public class MainModel implements Contract.Model
{
    private Context _context;
    private Contract.PresenterModel _presenter;
    private SharedPreferences _preferences;

    public MainModel(Context context)
    {
        _context = context;
        _preferences = PreferenceManager.getDefaultSharedPreferences(_context);

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
        return false;
    }

    @Override
    public int getScreenTimeoutValue()
    {
        //todo read value from the preferences
        return 0;
    }

    /*private void checkForRunningService()
    {
        _serviceActive = isServiceRunning(getActivity());

        _servicePreference.setChecked(_serviceActive);
        enableOptions(_serviceActive);
    }

    private void checkForActiveDeviceAdmin()
    {
        boolean adminActive = _devicePolicyManager.isAdminActive(_adminComponent);

        _deviceAdminPreference.setChecked(adminActive);
        enableScreenTimeout(adminActive);
    }*/
}