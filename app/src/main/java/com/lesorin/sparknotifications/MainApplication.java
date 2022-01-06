package com.lesorin.sparknotifications;

import android.app.Application;
import com.lesorin.sparknotifications.model.ModelFactory;
import com.lesorin.sparknotifications.presenter.Contract;
import com.lesorin.sparknotifications.presenter.PresenterFactory;

public class MainApplication extends Application
{
    private Contract.View _view;
    private Contract.PresenterView _presenterView;
    private Contract.PresenterModel _presenterModel;
    private Contract.Model _model;

    @Override
    public void onCreate()
    {
        super.onCreate();

        _view = null;
        _presenterView = PresenterFactory.getPresenter();
        _presenterModel = (Contract.PresenterModel) _presenterView;
        _model = ModelFactory.getRealmModel(getApplicationContext());

        _presenterModel.setModel(_model);
        _model.setPresenter(_presenterModel);
    }

    /**
     * Sets the current application's view.
     * @param view The current app's view.
     */
    public void setView(Contract.View view)
    {
        _view = view;
    }

    /**
     * This method must be called when the current running activity changes.
     * @param view The activity that acts as the current view.
     */
    public void activityChanged(Contract.View view)
    {
        setView(view);
        _view.setPresenter(_presenterView);
        _presenterView.setView(_view);
    }
}