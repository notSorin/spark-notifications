package com.lesorin.sparknotifications.model;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import com.lesorin.sparknotifications.presenter.App;
import com.lesorin.sparknotifications.presenter.Contract;
import com.lesorin.sparknotifications.model.receivers.DeviceAdministratorReceiver;
import com.lesorin.sparknotifications.model.services.NotificationListener;
import com.lesorin.sparknotifications.presenter.RecentApp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
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
    private final PackageManager _packageManager;

    public MainModel(Context context)
    {
        _context = context;
        _preferences = PreferenceManager.getDefaultSharedPreferences(_context);
        _devicePolicyManager = (DevicePolicyManager)_context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        _adminComponent = new ComponentName(_context, DeviceAdministratorReceiver.class);
        _deviceScanned = false;
        _packageManager = _context.getPackageManager();

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
    public void setQuietHoursStart(String startTime)
    {
        _preferences.edit().putString(PreferencesKeys.QUIET_HOURS_START, startTime).apply();
    }

    @Override
    public String getQuietHoursStart(String defaultValue)
    {
        return _preferences.getString(PreferencesKeys.QUIET_HOURS_START, defaultValue);
    }

    @Override
    public void setQuietHoursStop(String stopTime)
    {
        _preferences.edit().putString(PreferencesKeys.QUIET_HOURS_STOP, stopTime).apply();
    }

    @Override
    public String getQuietHoursStop(String defaultValue)
    {
        return _preferences.getString(PreferencesKeys.QUIET_HOURS_STOP, defaultValue);
    }

    @Override
    public void setQuietHoursStart24H(String startTime)
    {
        _preferences.edit().putString(PreferencesKeys.QUIET_HOURS_START_24H, startTime).apply();
    }

    @Override
    public String getQuietHoursStart24H(String defaultValue)
    {
        return _preferences.getString(PreferencesKeys.QUIET_HOURS_START_24H, defaultValue);
    }

    @Override
    public void setQuietHoursStop24H(String stopTime)
    {
        _preferences.edit().putString(PreferencesKeys.QUIET_HOURS_STOP_24H, stopTime).apply();
    }

    @Override
    public String getQuietHoursStop24H(String defaultValue)
    {
        return _preferences.getString(PreferencesKeys.QUIET_HOURS_STOP_24H, defaultValue);
    }

    @Override
    public void requestRecentlyActiveApps()
    {
        //Do it on a separate thread as it might take a while...
        Executors.newSingleThreadExecutor().submit(() -> _presenter.responseRecentlyActiveApps(queryRecentlyActiveApps()));
    }

    private ArrayList<RealmRecentApp> queryRecentlyActiveApps()
    {
        ArrayList<RealmRecentApp> apps = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmRecentApp> recentApps = realm.where(RealmRecentApp.class).findAll().sort("timestamp", Sort.DESCENDING);

        for(RealmRecentApp app: recentApps)
        {
            RealmRecentApp appCopy = realm.copyFromRealm(app);

            fetchRecentAppInformation(appCopy);
            apps.add(appCopy);
        }

        realm.close();

        return apps;
    }

    private void fetchRecentAppInformation(RecentApp app)
    {
        try
        {
            PackageManager packageManager = _context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(app.getPackageName(), 0);

            app.setInstalled(true);
            app.setName((String)applicationInfo.loadLabel(packageManager));
            app.setIcon(applicationInfo.loadIcon(packageManager));
        }
        catch(Exception ignored)
        {
            app.setInstalled(false);
        }
    }

    @Override
    public void requestAllApps()
    {
        //Do it on a separate thread as it might take a while...
        Executors.newSingleThreadExecutor().submit(() ->
        {
            //Wait until the apps on the device have been scanned.
            while(!_deviceScanned)
            {
                try
                {
                    Thread.sleep(1000);
                }
                catch(Exception ignored)
                {
                }
            }

            _presenter.responseAllApps(queryAllApps());
        });
    }

    @Override
    public void appStateChanged(App app, boolean enabled)
    {
        Realm realm = Realm.getDefaultInstance();
        RealmApp realmApp = realm.where(RealmApp.class).equalTo("packageName", app.getPackageName()).findFirst();

        if(realmApp != null)
        {
            realm.beginTransaction();
            realmApp.setEnabled(enabled);
            realm.commitTransaction();
            app.setEnabled(enabled);
        }

        realm.close();
    }

    @Override
    public boolean isDarkThemeEnabled(boolean defaultValue)
    {
        return _preferences.getBoolean(PreferencesKeys.DARK_THEME_ENABLED, defaultValue);
    }

    @Override
    public void setDarkThemeEnabled(boolean enabled)
    {
        _preferences.edit().putBoolean(PreferencesKeys.DARK_THEME_ENABLED, enabled).apply();
    }

    @Override
    public void clearOldActivity(int maxRecentActivity)
    {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmRecentApp> recentApps = realm.where(RealmRecentApp.class).findAll().sort("timestamp", Sort.DESCENDING);

        if(recentApps.size() > maxRecentActivity)
        {
            realm.beginTransaction();

            while(recentApps.size() > maxRecentActivity)
            {
                recentApps.get(maxRecentActivity).deleteFromRealm();
            }

            realm.commitTransaction();
        }

        realm.close();
    }

    @Override
    public boolean isAllAppsEnabled(boolean defaultValue)
    {
        return _preferences.getBoolean(PreferencesKeys.ALL_APPS_ENABLED, defaultValue);
    }

    @Override
    public void setAllAppsEnabled(boolean enabled)
    {
        _preferences.edit().putBoolean(PreferencesKeys.ALL_APPS_ENABLED, enabled).apply();
    }

    @Override
    public int getEnabledAppsAmount()
    {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmApp> realmApps = realm.where(RealmApp.class).equalTo("enabled", true).findAll();
        int enabledApps = realmApps.size();

        realm.close();

        return enabledApps;
    }

    private ArrayList<RealmApp> queryAllApps()
    {
        ArrayList<RealmApp> apps = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmApp> realmApps = realm.where(RealmApp.class).findAll().sort("name");

        for(RealmApp realmApp: realmApps)
        {
            RealmApp appCopy = realm.copyFromRealm(realmApp);

            //Comment the next line to significantly speed up the query time, at the expense of not
            //loading the app's icon.
            fetchAppIcon(appCopy);
            apps.add(appCopy);
        }

        realm.close();

        return apps;
    }

    private void fetchAppIcon(RealmApp app)
    {
        app.setIcon(null);

        try
        {
            Drawable appIcon = _packageManager.getApplicationInfo(app.getPackageName(), 0).loadIcon(_packageManager);

            app.setIcon(appIcon);
        }
        catch(Exception ignored)
        {
        }
    }
}