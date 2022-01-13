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
    private LayoutInflater _layoutInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((MainApplication)getApplication()).activityChanged(this);

        _layoutInflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
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
    public void deviceAdministratorPreferenceChanged(boolean deviceAdministratorEnabled)
    {
        _settingsFragment.deviceAdministratorPreferenceChanged(deviceAdministratorEnabled);
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
        View numberPickerView = _layoutInflater.inflate(R.layout.number_picker_dialog, null);
        final NumberPicker numberPicker = numberPickerView.findViewById(R.id.NumberPicker);

        numberPicker.setMinValue(minValue);
        numberPicker.setMaxValue(maxValue);
        numberPicker.setValue(screenTimeoutValue);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        dialogBuilder.setTitle(R.string.ScreenTimeoutKey);
        dialogBuilder.setView(numberPickerView);
        dialogBuilder.setPositiveButton(R.string.Set, (dialog, whichButton) -> _presenter.screenTimeoutChanged(numberPicker.getValue()));
        dialogBuilder.show();
    }

    @Override
    public void screenTimeoutPreferenceChanged(boolean deviceAdministratorEnabled, int value)
    {
        _settingsFragment.updateScreenTimeout(deviceAdministratorEnabled, value);
    }

    @Override
    public void screenDelayPreferenceChanged(int screenDelayValue)
    {
        _settingsFragment.updateScreenDelaySummary(screenDelayValue);
    }

    @Override
    public void openScreenDelayNumberPicker(int screenDelayValue, int minValue, int maxValue)
    {
        View numberPickerView = _layoutInflater.inflate(R.layout.number_picker_dialog, null);
        final NumberPicker numberPicker = numberPickerView.findViewById(R.id.NumberPicker);

        numberPicker.setMinValue(minValue);
        numberPicker.setMaxValue(maxValue);
        numberPicker.setValue(screenDelayValue);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        dialogBuilder.setTitle(R.string.ScreenOnDelayTitle);
        dialogBuilder.setView(numberPicker);
        dialogBuilder.setPositiveButton(R.string.Set, (dialog, whichButton) -> _presenter.screenDelayChanged(numberPicker.getValue()));
        dialogBuilder.show();
    }

    @Override
    public void fullBrightnessPreferenceChanged(boolean enabled)
    {
        _settingsFragment.updateFullBrightness(enabled);
    }

    @Override
    public void notificationsDrawerPreferenceChanged(boolean enabled)
    {
        _settingsFragment.updateNotificationsDrawer(enabled);
    }

    @Override
    public void proximitySensorPreferenceChanged(boolean enabled)
    {
        _settingsFragment.updateProximitySensor(enabled);
    }

    @Override
    public void detectPickUpPreferenceChanged(boolean enabled)
    {
        _settingsFragment.updateDetectPickUp(enabled);
    }

    @Override
    public void quietHoursPreferenceChanged(boolean quietHoursEnabled)
    {
        _settingsFragment.updateQuietHours(quietHoursEnabled);
    }

    @Override
    public void quietHoursStartPreferenceChanged(boolean quietHoursEnabled, String quietHoursStart)
    {
        _settingsFragment.updateQuietHoursStart(quietHoursEnabled, quietHoursStart);
    }

    @Override
    public void quietHoursStopPreferenceChanged(boolean quietHoursEnabled, String quietHoursStop)
    {
        _settingsFragment.updateQuietHoursStop(quietHoursEnabled, quietHoursStop);
    }

    public void screenTimeoutPreferencePressed()
    {
        _presenter.screenTimeoutPreferencePressed();
    }

    public void screenDelayPreferencePressed()
    {
        _presenter.screenDelayPreferencePressed();
    }

    public void fullBrightnessPreferencePressed(boolean enabled)
    {
        _presenter.fullBrightnessChanged(enabled);
    }

    public void notificationsDrawerPreferencePressed(boolean enabled)
    {
        _presenter.notificationsDrawerChanged(enabled);
    }

    public void proximitySensorPreferencePressed(boolean enabled)
    {
        _presenter.proximitySensorPreferenceChanged(enabled);
    }

    public void detectPickUpPreferencePressed(boolean enabled)
    {
        _presenter.detectPickUpPreferenceChanged(enabled);
    }

    public void quietHoursPreferencePressed(boolean enabled)
    {
        _presenter.quietHoursPreferenceChanged(enabled);
    }
}