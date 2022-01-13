package com.lesorin.sparknotifications.view.activities;

import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
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

    public void deviceAdminPreferencePressed(boolean deviceAdminEnabled)
    {
        _presenter.deviceAdminPreferencePressed(deviceAdminEnabled);
    }

    @Override
    public void startDeviceAdministratorActivity(ComponentName adminComponent)
    {
        //Launch the activity to have the user enable the admin option.
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);

        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent);
        startActivity(intent);
    }

    @Override
    public void openScreenTimeoutNumberPicker(int screenTimeoutValue, int minValue, int maxValue)
    {
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View numberPickerView = inflater.inflate(R.layout.number_picker_dialog, null);
        final NumberPicker numberPicker = numberPickerView.findViewById(R.id.NumberPicker);

        numberPicker.setMinValue(minValue);
        numberPicker.setMaxValue(maxValue);
        numberPicker.setValue(screenTimeoutValue);

        new AlertDialog.Builder(this).setTitle(R.string.ScreenTimeoutKey).setView(numberPickerView).
                setPositiveButton(android.R.string.ok, (dialog, whichButton) ->
                {
                    _presenter.screenTimeoutChanged(numberPicker.getValue());
                    dialog.dismiss();
                }).setNegativeButton(android.R.string.cancel, (dialog, whichButton) -> dialog.dismiss()).show();
    }

    @Override
    public void screenTimeoutPreferenceChanged(int value)
    {
        _settingsFragment.updateScreenTimeoutSummary(true, value);
    }

    @Override
    public void screenDelayPreferenceChanged(int screenDelayValue)
    {
        _settingsFragment.updateScreenDelaySummary(screenDelayValue);
    }

    public void screenTimeoutPreferencePressed()
    {
        _presenter.screenTimeoutPreferencePressed();
    }
}