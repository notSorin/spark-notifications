package com.lesorin.sparknotifications.presenter;

public class MainPresenter implements Contract.PresenterView, Contract.PresenterModel
{
    private final int MIN_SCREEN_TIMEOUT = 3, MAX_SCREEN_TIMEOUT = 30;
    private final int MIN_SCREEN_DELAY = 0, MAX_SCREEN_DELAY = 10;

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
        _view.servicePreferenceChanged(_model.isNotificationsServiceEnabled());
        _view.deviceAdministratorPreferenceChanged(_model.isDeviceAdministratorEnabled(), _model.getScreenTimeoutValue(MIN_SCREEN_TIMEOUT));
        _view.screenDelayPreferenceChanged(_model.getScreenDelayValue(MIN_SCREEN_DELAY));
    }

    @Override
    public void deviceAdminPreferencePressed(boolean deviceAdminEnabled)
    {
        if(_model.isDeviceAdministratorEnabled())
        {
            _model.disableDeviceAdministrator();
            _view.deviceAdministratorPreferenceChanged(false, _model.getScreenTimeoutValue(MIN_SCREEN_TIMEOUT));
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
            _view.screenTimeoutPreferenceChanged(value);
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

    public void setModel(Contract.Model model)
    {
        _model = model;
    }
}