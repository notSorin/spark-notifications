package com.lesorin.sparknotifications.presenter;

import android.content.ComponentName;
import java.util.List;

public interface Contract
{
    interface PresenterView
    {
        void setView(View view);
        void notificationsServicePreferencePressed(boolean serviceEnabled);
        void appResumed(boolean hourFormat24);
        void deviceAdminPreferencePressed(boolean deviceAdminEnabled);
        void screenTimeoutPreferencePressed();
        void screenTimeoutChanged(int valueSeconds);
        void screenDelayPreferencePressed();
        void screenDelayChanged(int valueSeconds);
        void proximitySensorPreferenceChanged(boolean enabled);
        void detectPickUpPreferenceChanged(boolean enabled);
        void quietHoursPreferenceChanged(boolean enabled, boolean hourFormat24);
        void recentActivityPreferencePressed();
        void allAppsPreferencePressed();
        void appStateChanged(App app, boolean enabled);
        void darkThemePreferencePressed(boolean enabled);
        void quietHoursStartPreferencePressed(String startTime, boolean hourFormat24);
        void quietHoursStopPreferencePressed(String stopTime, boolean hourFormat24);
        void allAppsEnabledPreferencePressed(boolean enabled);
        void allAppsViewClosed();
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
        void allAppsEnabledPreferenceChanged(boolean serviceEnabled, boolean allAppsEnabled);
        void enabledAppsPreferenceChanged(boolean serviceEnabled, boolean allAppsEnabled, int appsEnabledAmount);

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
        void setQuietHoursStart(String startTime);
        String getQuietHoursStart(String defaultValue);
        void setQuietHoursStop(String stopTime);
        String getQuietHoursStop(String defaultValue);
        void setQuietHoursStart24H(String startTime);
        String getQuietHoursStart24H(String defaultValue);
        void setQuietHoursStop24H(String stopTime);
        String getQuietHoursStop24H(String defaultValue);
        void requestRecentlyActiveApps();
        void requestAllApps();
        void appStateChanged(App app, boolean enabled);
        boolean isDarkThemeEnabled(boolean defaultValue);
        void setDarkThemeEnabled(boolean enabled);
        void clearOldActivity(int maxRecentActivity);
        boolean isAllAppsEnabled(boolean default_all_apps_enabled);
        void setAllAppsEnabled(boolean enabled);
        int getEnabledAppsAmount();
    }
}