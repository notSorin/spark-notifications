package com.lesorin.sparknotifications.model;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import com.lesorin.sparknotifications.presenter.Contract;
import com.lesorin.sparknotifications.model.receivers.DeviceAdministratorReceiver;
import com.lesorin.sparknotifications.model.services.NotificationListener;
import com.lesorin.sparknotifications.presenter.RecentApp;
import java.util.List;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class MainModel implements Contract.Model
{
    private final Context _context;
    private Contract.PresenterModel _presenter;
    private final SharedPreferences _preferences;
    private final DevicePolicyManager _devicePolicyManager;
    private final ComponentName _adminComponent;
    private volatile boolean _deviceScanned;

    public MainModel(Context context)
    {
        _context = context;
        _preferences = PreferenceManager.getDefaultSharedPreferences(_context);
        _devicePolicyManager = (DevicePolicyManager)_context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        _adminComponent = new ComponentName(_context, DeviceAdministratorReceiver.class);
        _deviceScanned = false;

        Realm.init(_context);
        launchAppScanner();
    }

    /**
     * Launches an @AppsScanner on a separate thread.
     */
    private void launchAppScanner()
    {
        Executors.newSingleThreadExecutor().submit(() ->
        {
            AppsScanner as = new AppsScanner(_context.getPackageManager());

            as.scanAppsOnDevice();
            _deviceScanned = true;
        });
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

    @Override
    public boolean isFullBrightnessEnabled(boolean defaultValue)
    {
        return _preferences.getBoolean(PreferencesKeys.FULL_BRIGHTNESS_ENABLED, defaultValue);
    }

    @Override
    public void setNotificationsDrawerValue(boolean enabled)
    {
        _preferences.edit().putBoolean(PreferencesKeys.NOTIFICATIONS_DRAWER_ENABLED, enabled).apply();
    }

    @Override
    public boolean isNotificationsDrawerEnabled(boolean defaultValue)
    {
        return _preferences.getBoolean(PreferencesKeys.NOTIFICATIONS_DRAWER_ENABLED, defaultValue);
    }

    @Override
    public void setProximitySensorValue(boolean enabled)
    {
        _preferences.edit().putBoolean(PreferencesKeys.PROXIMITY_SENSOR_ENABLED, enabled).apply();
    }

    @Override
    public boolean isProximitySensorEnabled(boolean defaultValue)
    {
        return _preferences.getBoolean(PreferencesKeys.PROXIMITY_SENSOR_ENABLED, defaultValue);
    }

    @Override
    public void setDetectPickUpValue(boolean enabled)
    {
        _preferences.edit().putBoolean(PreferencesKeys.DETECT_PICK_UP, enabled).apply();
    }

    @Override
    public boolean isDetectPickUpEnabled(boolean defaultValue)
    {
        return _preferences.getBoolean(PreferencesKeys.DETECT_PICK_UP, defaultValue);
    }

    @Override
    public void setQuietHoursValue(boolean enabled)
    {
        _preferences.edit().putBoolean(PreferencesKeys.QUIET_HOURS_ENABLED, enabled).apply();
    }

    @Override
    public boolean isQuietHoursEnabled(boolean defaultValue)
    {
        return _preferences.getBoolean(PreferencesKeys.QUIET_HOURS_ENABLED, defaultValue);
    }

    @Override
    public String getQuietHoursStart(String defaultValue)
    {
        return _preferences.getString(PreferencesKeys.QUIET_HOURS_START, defaultValue);
    }

    @Override
    public String getQuietHoursStop(String defaultValue)
    {
        return _preferences.getString(PreferencesKeys.QUIET_HOURS_STOP, defaultValue);
    }

    @Override
    public List<? extends RecentApp> getRecentlyActiveApps()
    {
        RealmResults<RealmRecentApp> recentApps = Realm.getDefaultInstance().where(RealmRecentApp.class).findAll().sort("timestamp", Sort.DESCENDING);

        for(RealmRecentApp app: recentApps)
        {
            fetchAppInformation(app);
        }

        return recentApps;
    }

    private void fetchAppInformation(RecentApp app)
    {
        PackageManager packageManager = _context.getPackageManager();

        try
        {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(app.getPackageName(), 0);

            app.setInstalled(true);
            app.setName((String)applicationInfo.loadLabel(packageManager));
            app.setIcon(applicationInfo.loadIcon(packageManager));
        }
        catch(PackageManager.NameNotFoundException e)
        {
            app.setInstalled(false);
        }
    }
}