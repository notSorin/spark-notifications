package com.lesorin.sparknotifications.presenter;

import android.graphics.drawable.Drawable;

public interface RecentApp
{
    String getPackageName();
    void setPackageName(String packageName);
    long getTimestamp();
    void setTimestamp(long timestamp);
    boolean isInstalled();
    void setInstalled(boolean installed);
    String getName();
    void setName(String name);
    Drawable getIcon();
    void setIcon(Drawable icon);
}