package com.lesorin.sparknotifications.presenter;

public interface Contract
{
    interface PresenterView
    {
        void setView(View view);
    }

    interface PresenterModel
    {
        void setModel(Model model);
    }

    interface View
    {
        void setPresenter(PresenterView presenter);
    }

    interface Model
    {
        void setPresenter(PresenterModel presenter);
    }
}