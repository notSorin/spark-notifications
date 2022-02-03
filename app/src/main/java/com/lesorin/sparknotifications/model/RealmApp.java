package com.lesorin.sparknotifications.model;

import android.graphics.drawable.Drawable;
import com.lesorin.sparknotifications.presenter.App;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.Required;

public class RealmApp extends RealmObject implements App
{
    @Required
    private String packageName;
    private String name;
    private boolean enabled;
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
    public boolean getEnabled()
    {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
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
    public void setIcon(Drawable icon)
    {
        this.icon = icon;
    }

    @Override
    public Drawable getIcon()
    {
        return icon;
    }
}