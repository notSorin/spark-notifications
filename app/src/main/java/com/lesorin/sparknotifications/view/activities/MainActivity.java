package com.lesorin.sparknotifications.view.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.lesorin.sparknotifications.MainApplication;
import com.lesorin.sparknotifications.R;
import com.lesorin.sparknotifications.presenter.Contract;
import com.lesorin.sparknotifications.view.fragments.SettingsFragment;

public class MainActivity extends AppCompatActivity implements Contract.View
{
    private Contract.PresenterView _presenter;
    private SettingsFragment _settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((MainApplication)getApplication()).activityChanged(this);

        _settingsFragment = (SettingsFragment)getFragmentManager().findFragmentById(R.id.SettingsFragment);
    }

    @Override
    public void setPresenter(Contract.PresenterView presenter)
    {
        _presenter = presenter;
    }
}