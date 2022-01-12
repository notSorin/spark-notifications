package com.lesorin.sparknotifications.presenter;

import android.content.ComponentName;

public interface Contract
{
    interface PresenterView
    {
        void setView(View view);
        void notificationsServicePreferencePressed(boolean serviceEnabled);
        void appResumed();
        void deviceAdminPreferencePressed(boolean deviceAdminEnabled);
        void screenTimeoutPreferencePressed();
        void screenTimeoutChanged(int value);
    }

    interface PresenterModel
    {
        void setModel(Model model);
    }

    interface View
    {
        void setPresenter(PresenterView presenter);
        void showDialogForDisablingService();
        void showDialogForEnablingService();
        void servicePreferenceChanged(boolean isServiceEnabled);
        void deviceAdministratorPreferenceChanged(boolean deviceAdministratorEnabled, int screenTimeoutValue);
        void startDeviceAdministratorActivity(ComponentName adminComponent);
        void openScreenTimeoutNumberPicker(int screenTimeoutValue, int minValue, int maxValue);
        void screenTimeoutPreferenceChanged(int value);
    }

    interface Model
    {
        void setPresenter(PresenterModel presenter);
        boolean isNotificationsServiceEnabled();
        boolean isDeviceAdministratorEnabled();
        int getScreenTimeoutValue(int defaultValue);
        void disableDeviceAdministrator();
        ComponentName getAdminComponent();
        void setScreenTimeoutValue(int value);
    }
}