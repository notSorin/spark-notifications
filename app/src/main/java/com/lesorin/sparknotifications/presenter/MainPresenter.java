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

    public void setModel(Contract.Model model)
    {
        _model = model;
    }
}