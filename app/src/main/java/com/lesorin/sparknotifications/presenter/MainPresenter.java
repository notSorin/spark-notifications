package com.lesorin.sparknotifications.presenter;

public class MainPresenter implements Contract.PresenterView, Contract.PresenterModel
{
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
        _view.deviceAdministratorPreferenceChanged(_model.isDeviceAdministratorEnabled(), _model.getScreenTimeoutValue());
    }

    public void setModel(Contract.Model model)
    {
        _model = model;
    }
}