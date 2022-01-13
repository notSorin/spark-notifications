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
        void screenDelayPreferencePressed();
        void screenDelayChanged(int value);
        void fullBrightnessChanged(boolean enabled);
        void notificationsDrawerChanged(boolean enabled);
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
        void deviceAdministratorPreferenceChanged(boolean deviceAdministratorEnabled);
        void startDeviceAdministratorActivity(ComponentName adminComponent);
        void openScreenTimeoutNumberPicker(int screenTimeoutValue, int minValue, int maxValue);
        void screenTimeoutPreferenceChanged(boolean deviceAdministratorEnabled, int value);
        void screenDelayPreferenceChanged(int screenDelayValue);
        void openScreenDelayNumberPicker(int screenDelayValue, int minValue, int maxValue);
        void fullBrightnessPreferenceChanged(boolean enabled);
        void notificationsDrawerPreferenceChanged(boolean enabled);
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
        int getScreenDelayValue(int defaultValue);
        void setScreenDelayValue(int value);
        void setFullBrightnessValue(boolean enabled);
        boolean isFullBrightnessEnabled(boolean defaultValue);
        void setNotificationsDrawerValue(boolean enabled);
        boolean isNotificationsDrawerEnabled(boolean defaultValue);
    }
}