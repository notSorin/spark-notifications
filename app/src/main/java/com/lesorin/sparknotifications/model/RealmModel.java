package com.lesorin.sparknotifications.model;

import android.content.Context;
import com.lesorin.sparknotifications.presenter.Contract;
import io.realm.Realm;

public class RealmModel implements Contract.Model
{
    private Contract.PresenterModel _presenter;

    public RealmModel(Context context)
    {
        Realm.init(context);
    }

    public void setPresenter(Contract.PresenterModel presenter)
    {
        _presenter = presenter;
    }
}