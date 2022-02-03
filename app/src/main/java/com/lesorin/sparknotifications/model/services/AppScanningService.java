package com.lesorin.sparknotifications.model.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import com.lesorin.sparknotifications.model.RealmApp;
import java.util.ArrayList;
import java.util.List;
import io.realm.Realm;
import io.realm.RealmResults;

//TODO try to remove this as service and call the functions when needed.
public class AppScanningService extends IntentService
{
    public AppScanningService()
    {
        super("AppScanningService");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        ArrayList<String> previousAppPackages = getRealmAppPackages();
        ArrayList<String> installedAppPackages = getInstalledAppPackages();

        updateRealmApps(installedAppPackages);
        previousAppPackages.removeAll(installedAppPackages);
        removeAppsFromRealm(previousAppPackages);
    }

    /**
     * Updates Realm with new apps from a list of app packages.
     *
     * @param appPackages A list of app packages to add to Realm.
     */
    private void updateRealmApps(ArrayList<String> appPackages)
    {
        PackageManager packageManager = getPackageManager();
        Realm realm = Realm.getDefaultInstance();

        realm.beginTransaction();

        for(String packageName : appPackages)
        {
            RealmApp app = realm.where(RealmApp.class).equalTo("packageName", packageName).findFirst();

            if(app == null) //If the app isn't in Realm, create it.
            {
                try
                {
                    ApplicationInfo appInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
                    app = realm.createObject(RealmApp.class);

                    app.setPackageName(packageName);
                    app.setName(appInfo.loadLabel(packageManager).toString());
                }
                catch(PackageManager.NameNotFoundException ignored)
                {
                }
            }
        }

        realm.commitTransaction();
        realm.close();
    }

    /**
     * @return A list with all the packages of the apps currently installed on the device.
     */
    private ArrayList<String> getInstalledAppPackages()
    {
        ArrayList<String> installedAppPackages = new ArrayList<>();
        List<ApplicationInfo> installedApps = getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);

        for(ApplicationInfo appInfo : installedApps)
        {
            if(appInfo.enabled) //Disabled apps are considered uninstalled.
            {
                installedAppPackages.add(appInfo.packageName);
            }
        }

        return installedAppPackages;
    }

    /**
     * Removes apps from Realm that match the packages in previousAppPackages.
     *
     * @param appPackages A list with the app packages to remove from Realm.
     */
    private void removeAppsFromRealm(ArrayList<String> appPackages)
    {
        Realm realm = Realm.getDefaultInstance();

        realm.beginTransaction();

        //Loop each app package and remove it from Realm.
        for(String appPackage : appPackages)
        {
            RealmApp app = realm.where(RealmApp.class).equalTo("packageName", appPackage).findFirst();

            if(app != null)
            {
                app.deleteFromRealm();
            }
        }

        realm.commitTransaction();
    }

    /**
     * @return A list with all the app packages currently saved in Realm.
     */
    private ArrayList<String> getRealmAppPackages()
    {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmApp> savedApps = realm.where(RealmApp.class).findAll();
        ArrayList<String> appPackages = new ArrayList<>();

        for(RealmApp app : savedApps)
        {
            appPackages.add(app.getPackageName());
        }

        realm.close();

        return appPackages;
    }
}