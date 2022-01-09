package com.lesorin.sparknotifications.model;

import android.content.Context;
import com.lesorin.sparknotifications.presenter.Contract;

public class ModelFactory
{
    public static Contract.Model getRealmModel(Context context)
    {
        return new MainModel(context);
    }
}