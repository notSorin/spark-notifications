package com.lesorin.sparknotifications.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.lesorin.sparknotifications.presenter.Contract;
import io.realm.Realm;

public class MainModel implements Contract.Model
{
    private Context _context;
    private Contract.PresenterModel _presenter;
    private SharedPreferences _preferences;

    public MainModel(Context context)
    {
        _context = context;
        _preferences = PreferenceManager.getDefaultSharedPreferences(_context);

        Realm.init(_context);

    }

    public void setPresenter(Contract.PresenterModel presenter)
    {
        _presenter = presenter;
    }
}