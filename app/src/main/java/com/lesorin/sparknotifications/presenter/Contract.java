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
    }

    interface Model
    {
        void setPresenter(PresenterModel presenter);
        boolean isNotificationsServiceEnabled();
        boolean isDeviceAdministratorEnabled();
        int getScreenTimeoutValue();
        void disableDeviceAdministrator();
        ComponentName getAdminComponent();
    }
}