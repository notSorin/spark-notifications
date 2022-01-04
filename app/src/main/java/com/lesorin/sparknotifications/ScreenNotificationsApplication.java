package com.lesorin.sparknotifications;

import android.app.Application;
import com.lesorin.sparknotifications.helpers.DatabaseMigrations;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class ScreenNotificationsApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        Realm.init(this);

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().schemaVersion(1)
                .migration(new DatabaseMigrations()).build();

        Realm.setDefaultConfiguration(realmConfiguration);
    }
}