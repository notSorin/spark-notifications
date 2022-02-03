package com.lesorin.sparknotifications.model;

import android.graphics.drawable.Drawable;
import com.lesorin.sparknotifications.presenter.RecentApp;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.Required;

public class RealmRecentApp extends RealmObject implements RecentApp
{
    @Required
    private String packageName;
    private long timestamp;
    @Ignore
    private boolean installed;
    @Ignore
    private String name;
    @Ignore
    private Drawable icon;

    @Override
    public String getPackageName()
    {
        return packageName;
    }

    @Override
    public void setPackageName(String packageName)
    {
        this.packageName = packageName;
    }

    @Override
    public long getTimestamp()
    {
        return timestamp;
    }

    @Override
    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

    @Override
    public boolean isInstalled()
    {
        return installed;
    }

    @Override
    public void setInstalled(boolean installed)
    {
        this.installed = installed;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public Drawable getIcon()
    {
        return icon;
    }

    @Override
    public void setIcon(Drawable icon)
    {
        this.icon = icon;
    }
}