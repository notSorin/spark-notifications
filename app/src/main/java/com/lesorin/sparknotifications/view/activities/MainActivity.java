package com.lesorin.sparknotifications.view.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
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

    @Override
    public void showDialogForDisablingService()
    {
        showServiceDialog(R.string.ServiceDisableInfo);
    }

    @Override
    public void showDialogForEnablingService()
    {
        showServiceDialog(R.string.ServiceEnableInfo);
    }

    @Override
    public void servicePreferenceChanged(boolean isServiceEnabled)
    {
        _settingsFragment.servicePreferenceChanged(isServiceEnabled);
    }

    @Override
    public void deviceAdministratorPreferenceChanged(boolean deviceAdministratorEnabled, int screenTimeoutValue)
    {
        _settingsFragment.deviceAdministratorPreferenceChanged(deviceAdministratorEnabled, screenTimeoutValue);
    }

    public void notificationsServicePreferencePressed(boolean serviceEnabled)
    {
        _presenter.notificationsServicePreferencePressed(serviceEnabled);
    }

    private void showServiceDialog(int messageId)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        dialogBuilder.setTitle(R.string.Notice);
        dialogBuilder.setMessage(messageId);
        dialogBuilder.setPositiveButton(R.string.IUnderstand, (alertDialog, id) -> startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)));
        dialogBuilder.setCancelable(false);
        dialogBuilder.show();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        _presenter.appResumed();
    }
}