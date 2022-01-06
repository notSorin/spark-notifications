package com.lesorin.sparknotifications.model;

import com.lesorin.sparknotifications.presenter.Contract;

public class RealmModel implements Contract.Model
{
    private Contract.PresenterModel _presenter;

    public void setPresenter(Contract.PresenterModel presenter)
    {
        _presenter = presenter;
    }
}