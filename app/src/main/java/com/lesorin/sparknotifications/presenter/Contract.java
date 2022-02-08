package com.lesorin.sparknotifications.presenter;

import android.content.ComponentName;
import java.util.List;

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
        void proximitySensorPreferenceChanged(boolean enabled);
        void detectPickUpPreferenceChanged(boolean enabled);
        void quietHoursPreferenceChanged(boolean enabled);
        void recentActivityPreferencePressed();
        void allAppsPreferencePressed();
        void appStateChanged(App app, boolean enabled);
        void darkThemePreferencePressed(boolean enabled);
        void quietHoursStartPreferencePressed(String startTime);
    }

    interface PresenterModel
    {
        void setModel(Model model);
        void responseRecentlyActiveApps(List<? extends RecentApp> appsList);
        void responseAllApps(List<? extends App> appsList);
    }

    interface View
    {
        //Service preferences changes.
        void servicePreferenceChanged(boolean serviceEnabled);
        void enabledAppsPreferenceChanged(boolean enabledAppsEnabled);

        //Options preferences changes.
        void screenDelayPreferenceChanged(boolean serviceEnabled, int screenDelayValue);
        void proximitySensorPreferenceChanged(boolean serviceEnabled, boolean proximitySensorEnabled);
        void detectPickUpPreferenceChanged(boolean serviceEnabled, boolean detectPickUpEnabled);
        void quietHoursPreferenceChanged(boolean serviceEnabled, boolean quietHoursEnabled);
        void quietHoursStartPreferenceChanged(boolean serviceEnabled, boolean quietHoursEnabled, String quietHoursStart);
        void quietHoursStopPreferenceChanged(boolean serviceEnabled, boolean quietHoursEnabled, String quietHoursStop);

        //Device administrator preferences changes.
        void deviceAdministratorPreferenceChanged(boolean deviceAdministratorEnabled);
        void screenTimeoutPreferenceChanged(boolean serviceEnabled, boolean deviceAdministratorEnabled, int value);

        //Other methods.
        void setPresenter(PresenterView presenter);
        void showDialogForDisablingService();
        void showDialogForEnablingService();
        void startDeviceAdministratorActivity(ComponentName adminComponent);
        void openScreenTimeoutNumberPicker(int screenTimeoutValue, int minValue, int maxValue);
        void openScreenDelayNumberPicker(int screenDelayValue, int minValue, int maxValue);
        void displayRecentlyActiveApps(List<? extends RecentApp> appsList);
        void displayAllApps(List<? extends App> appsList);
        void displayLoadingAppsDialog();
        void displayLoadingRecentActivityDialog();
        void setLightTheme();
        void setDarkTheme();
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
        void setProximitySensorValue(boolean enabled);
        boolean isProximitySensorEnabled(boolean defaultValue);
        void setDetectPickUpValue(boolean enabled);
        boolean isDetectPickUpEnabled(boolean defaultValue);
        void setQuietHoursValue(boolean enabled);
        boolean isQuietHoursEnabled(boolean defaultValue);
        String getQuietHoursStart(String defaultValue);
        String getQuietHoursStop(String defaultValue);
        void requestRecentlyActiveApps();
        void requestAllApps();
        void appStateChanged(App app, boolean enabled);
        boolean isDarkThemeEnabled(boolean defaultValue);
        void setDarkThemeEnabled(boolean enabled);
        void setQuietHoursStart(String startTime);
    }
}