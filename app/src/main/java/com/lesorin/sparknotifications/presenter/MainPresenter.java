package com.lesorin.sparknotifications.presenter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainPresenter implements Contract.PresenterView, Contract.PresenterModel
{
    private final int MIN_SCREEN_TIMEOUT = 3, MAX_SCREEN_TIMEOUT = 30;
    private final int MIN_SCREEN_DELAY = 0, MAX_SCREEN_DELAY = 10;
    private final String DEFAULT_QUIET_HOURS_START_24H = "23:00", DEFAULT_QUIET_HOURS_STOP_24H = "07:00";
    private final String DEFAULT_QUIET_HOURS_START_12H = "11:00 PM", DEFAULT_QUIET_HOURS_STOP_12H = "07:00 AM";
    private final boolean DEFAULT_QUIET_HOURS_ENABLED = false;

    private Contract.View _view;
    private Contract.Model _model;

    public MainPresenter()
    {
        _view = null;
        _model = null;
    }

    public void setView(Contract.View view)
    {
        _view = view;
    }

    @Override
    public void notificationsServicePreferencePressed(boolean serviceEnabled)
    {
        if(_model.isNotificationsServiceEnabled())
        {
            _view.showDialogForDisablingService();
        }
        else
        {
            _view.showDialogForEnablingService();
        }
    }

    @Override
    public void appResumed(boolean hourFormat24)
    {
        //Set service-related preferences.
        boolean serviceEnabled = _model.isNotificationsServiceEnabled();

        _view.servicePreferenceChanged(serviceEnabled);
        _view.enabledAppsPreferenceChanged(serviceEnabled);

        //Set option-related preferences.
        _view.screenDelayPreferenceChanged(serviceEnabled, _model.getScreenDelayValue(MIN_SCREEN_DELAY));
        _view.proximitySensorPreferenceChanged(serviceEnabled, _model.isProximitySensorEnabled(true));
        _view.detectPickUpPreferenceChanged(serviceEnabled, _model.isDetectPickUpEnabled(false));

        if(_model.isDarkThemeEnabled(false))
        {
            _view.setDarkTheme();
        }
        else
        {
            _view.setLightTheme();
        }

        boolean quietHoursEnabled = _model.isQuietHoursEnabled(DEFAULT_QUIET_HOURS_ENABLED);

        _view.quietHoursPreferenceChanged(serviceEnabled, quietHoursEnabled);
        _view.quietHoursStartPreferenceChanged(serviceEnabled, quietHoursEnabled,
                _model.getQuietHoursStart(hourFormat24 ? DEFAULT_QUIET_HOURS_START_24H : DEFAULT_QUIET_HOURS_START_12H));
        _view.quietHoursStopPreferenceChanged(serviceEnabled, quietHoursEnabled,
                _model.getQuietHoursStop(hourFormat24 ? DEFAULT_QUIET_HOURS_STOP_24H : DEFAULT_QUIET_HOURS_STOP_12H));

        //Set admin-related preferences.
        boolean deviceAdminEnabled = _model.isDeviceAdministratorEnabled();

        _view.deviceAdministratorPreferenceChanged(deviceAdminEnabled);
        _view.screenTimeoutPreferenceChanged(serviceEnabled, deviceAdminEnabled, _model.getScreenTimeoutValue(MIN_SCREEN_TIMEOUT));
    }

    @Override
    public void deviceAdminPreferencePressed(boolean deviceAdminEnabled)
    {
        if(_model.isDeviceAdministratorEnabled())
        {
            _model.disableDeviceAdministrator();
            _view.deviceAdministratorPreferenceChanged(false);
            _view.screenTimeoutPreferenceChanged(_model.isNotificationsServiceEnabled(), false, _model.getScreenTimeoutValue(MIN_SCREEN_TIMEOUT));
        }
        else
        {
            _view.startDeviceAdministratorActivity(_model.getAdminComponent());
        }
    }

    @Override
    public void screenTimeoutPreferencePressed()
    {
        _view.openScreenTimeoutNumberPicker(_model.getScreenTimeoutValue(MIN_SCREEN_TIMEOUT), MIN_SCREEN_TIMEOUT, MAX_SCREEN_TIMEOUT);
    }

    @Override
    public void screenTimeoutChanged(int value)
    {
        if(value >= MIN_SCREEN_TIMEOUT && value <= MAX_SCREEN_TIMEOUT)
        {
            _model.setScreenTimeoutValue(value);
            _view.screenTimeoutPreferenceChanged(_model.isNotificationsServiceEnabled(), _model.isDeviceAdministratorEnabled(), value);
        }
    }

    @Override
    public void screenDelayPreferencePressed()
    {
        _view.openScreenDelayNumberPicker(_model.getScreenDelayValue(MIN_SCREEN_DELAY), MIN_SCREEN_DELAY, MAX_SCREEN_DELAY);
    }

    @Override
    public void screenDelayChanged(int value)
    {
        if(value >= MIN_SCREEN_DELAY && value <= MAX_SCREEN_DELAY)
        {
            _model.setScreenDelayValue(value);
            _view.screenDelayPreferenceChanged(_model.isNotificationsServiceEnabled(), value);
        }
    }

    @Override
    public void proximitySensorPreferenceChanged(boolean enabled)
    {
        _model.setProximitySensorValue(enabled);
    }

    @Override
    public void detectPickUpPreferenceChanged(boolean enabled)
    {
        _model.setDetectPickUpValue(enabled);
    }

    @Override
    public void quietHoursPreferenceChanged(boolean enabled, boolean hourFormat24)
    {
        _model.setQuietHoursValue(enabled);

        boolean serviceEnabled = _model.isNotificationsServiceEnabled();

        _view.quietHoursStartPreferenceChanged(serviceEnabled, enabled,
                _model.getQuietHoursStart(hourFormat24 ? DEFAULT_QUIET_HOURS_START_24H : DEFAULT_QUIET_HOURS_START_12H));
        _view.quietHoursStopPreferenceChanged(serviceEnabled, enabled,
                _model.getQuietHoursStop(hourFormat24 ? DEFAULT_QUIET_HOURS_STOP_24H : DEFAULT_QUIET_HOURS_STOP_12H));
    }

    @Override
    public void recentActivityPreferencePressed()
    {
        _view.displayLoadingRecentActivityDialog();
        _model.requestRecentlyActiveApps();
    }

    @Override
    public void allAppsPreferencePressed()
    {
        _view.displayLoadingAppsDialog();
        _model.requestAllApps();
    }

    @Override
    public void appStateChanged(App app, boolean enabled)
    {
        _model.appStateChanged(app, enabled);
    }

    @Override
    public void darkThemePreferencePressed(boolean enabled)
    {
        _model.setDarkThemeEnabled(enabled);

        if(enabled)
        {
            _view.setDarkTheme();
        }
        else
        {
            _view.setLightTheme();
        }
    }

    @Override
    public void quietHoursStartPreferencePressed(String startTime, boolean hourFormat24)
    {
        _model.setQuietHoursStart(startTime);
        _model.setQuietHoursStart24H(hourFormat24 ? startTime : convertTo24HourFormat(startTime));
        _view.quietHoursStartPreferenceChanged(_model.isNotificationsServiceEnabled(),
                _model.isQuietHoursEnabled(DEFAULT_QUIET_HOURS_ENABLED), startTime);
    }

    @Override
    public void quietHoursStopPreferencePressed(String stopTime, boolean hourFormat24)
    {
        _model.setQuietHoursStop(stopTime);
        _model.setQuietHoursStop24H(hourFormat24 ? stopTime : convertTo24HourFormat(stopTime));
        _view.quietHoursStopPreferenceChanged(_model.isNotificationsServiceEnabled(),
                _model.isQuietHoursEnabled(DEFAULT_QUIET_HOURS_ENABLED), stopTime);
    }

    private String convertTo24HourFormat(String time)
    {
        String newTime = null;

        try
        {
            SimpleDateFormat format24 = new SimpleDateFormat("HH:mm", Locale.getDefault());
            SimpleDateFormat format12 = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            Date date12 = format12.parse(time);

            newTime = format24.format(date12);
        }
        catch (Exception ignored)
        {
        }

        return newTime;
    }

    public void setModel(Contract.Model model)
    {
        _model = model;
    }

    @Override
    public void responseRecentlyActiveApps(List<? extends RecentApp> appsList)
    {
        _view.displayRecentlyActiveApps(appsList);
    }

    @Override
    public void responseAllApps(List<? extends App> appsList)
    {
        _view.displayAllApps(appsList);
    }
}