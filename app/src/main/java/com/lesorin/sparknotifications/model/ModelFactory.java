package com.lesorin.sparknotifications.model;

import com.lesorin.sparknotifications.presenter.Contract;

public class ModelFactory
{
    public static Contract.Model getRealmModel()
    {
        return new RealmModel();
    }
}