package com.lesorin.sparknotifications.presenter;

public class PresenterFactory
{
    public static Contract.PresenterView getPresenter()
    {
        return new MainPresenter();
    }
}