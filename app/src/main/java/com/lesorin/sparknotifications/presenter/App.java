package com.lesorin.sparknotifications.presenter;

import android.graphics.drawable.Drawable;

public interface App
{
    String getPackageName();
    void setPackageName(String packageName);
    boolean getEnabled();
    void setEnabled(boolean enabled);
    String getName();
    void setName(String name);
    void setIcon(Drawable icon);
    Drawable getIcon();
}