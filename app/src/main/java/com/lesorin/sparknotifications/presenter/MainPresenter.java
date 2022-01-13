package com.lesorin.sparknotifications.presenter;

public class MainPresenter implements Contract.PresenterView, Contract.PresenterModel
{
    private final int MIN_SCREEN_TIMEOUT = 3, MAX_SCREEN_TIMEOUT = 30;
    private final int MIN_SCREEN_DELAY = 0, MAX_SCREEN_DELAY = 10;
    private final String DEFAULT_QUIET_HOURS_START = "23:00", DEFAULT_QUIET_HOURS_STOP = "07:00";

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
    public void appResumed()
    {
        //Set service-related preferences.
        _view.servicePreferenceChanged(_model.isNotificationsServiceEnabled());

        //Set option-related preferences.
        _view.screenDelayPreferenceChanged(_model.getScreenDelayValue(MIN_SCREEN_DELAY));
        _view.fullBrightnessPreferenceChanged(_model.isFullBrightnessEnabled(false));
        _view.notificationsDrawerPreferenceChanged(_model.isNotificationsDrawerEnabled(false));
        _view.proximitySensorPreferenceChanged(_model.isProximitySensorEnabled(true));
        _view.detectPickUpPreferenceChanged(_model.isDetectPickUpEnabled(false));

        boolean quietHoursEnabled = _model.isQuietHoursEnabled(false);

        _view.quietHoursPreferenceChanged(quietHoursEnabled);
        _view.quietHoursStartPreferenceChanged(quietHoursEnabled, _model.getQuietHoursStart(DEFAULT_QUIET_HOURS_START));
        _view.quietHoursStopPreferenceChanged(quietHoursEnabled, _model.getQuietHoursStop(DEFAULT_QUIET_HOURS_STOP));

        //Set admin-related preferences.
        boolean deviceAdminEnabled = _model.isDeviceAdministratorEnabled();

        _view.deviceAdministratorPreferenceChanged(deviceAdminEnabled);
        _view.screenTimeoutPreferenceChanged(deviceAdminEnabled, _model.getScreenTimeoutValue(MIN_SCREEN_TIMEOUT));
    }

    @Override
    public void deviceAdminPreferencePressed(boolean deviceAdminEnabled)
    {
        if(_model.isDeviceAdministratorEnabled())
        {
            _model.disableDeviceAdministrator();
            _view.deviceAdministratorPreferenceChanged(false);
            _view.screenTimeoutPreferenceChanged(false, _model.getScreenTimeoutValue(MIN_SCREEN_TIMEOUT));
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
            _view.screenTimeoutPreferenceChanged(_model.isDeviceAdministratorEnabled(), value);
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
            _view.screenDelayPreferenceChanged(value);
        }
    }

    @Override
    public void fullBrightnessChanged(boolean enabled)
    {
        _model.setFullBrightnessValue(enabled);
    }

    @Override
    public void notificationsDrawerChanged(boolean enabled)
    {
        _model.setNotificationsDrawerValue(enabled);
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
    public void quietHoursPreferenceChanged(boolean enabled)
    {
        _model.setQuietHoursValue(enabled);
        _view.quietHoursStartPreferenceChanged(enabled, _model.getQuietHoursStart(DEFAULT_QUIET_HOURS_START));
        _view.quietHoursStopPreferenceChanged(enabled, _model.getQuietHoursStop(DEFAULT_QUIET_HOURS_STOP));
    }

    public void setModel(Contract.Model model)
    {
        _model = model;
    }
}