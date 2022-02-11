package com.lesorin.sparknotifications.view.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.lesorin.sparknotifications.MainApplication;
import com.lesorin.sparknotifications.R;
import com.lesorin.sparknotifications.presenter.App;
import com.lesorin.sparknotifications.presenter.Contract;
import com.lesorin.sparknotifications.presenter.RecentApp;
import com.lesorin.sparknotifications.view.adapters.AppAdapter;
import com.lesorin.sparknotifications.view.adapters.RecentAppsAdapter;
import com.lesorin.sparknotifications.view.fragments.SettingsFragment;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Contract.View
{
    private Contract.PresenterView _presenter;
    private SettingsFragment _settingsFragment;
    private LayoutInflater _layoutInflater;
    private ProgressDialog _progressDialog;
    private BillingManager _billingManager;
    private String[] _purchaseItems;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((MainApplication)getApplication()).activityChanged(this);

        _layoutInflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        _settingsFragment = (SettingsFragment)getFragmentManager().findFragmentById(R.id.SettingsFragment);
        _progressDialog = new ProgressDialog(this);
        _billingManager = new BillingManager(this);
        _purchaseItems = null;
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
    protected void onDestroy()
    {
        super.onDestroy();

        //Without calling _billingManager.endConnection(), onPurchasesUpdated() in the billing manager is
        //called multiples times.
        _billingManager.endConnection();
    }

    @Override
    public void servicePreferenceChanged(boolean serviceEnabled)
    {
        _settingsFragment.updateServicePreference(serviceEnabled);
    }

    @Override
    public void allAppsEnabledPreferenceChanged(boolean serviceEnabled, boolean allAppsEnabled)
    {
        _settingsFragment.updateAllAppsPreference(serviceEnabled, allAppsEnabled);
    }

    @Override
    public void deviceAdministratorPreferenceChanged(boolean deviceAdministratorEnabled)
    {
        _settingsFragment.updateDeviceAdministrator(deviceAdministratorEnabled);
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
        _presenter.appResumed(DateFormat.is24HourFormat(this));
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
    public void screenTimeoutPreferenceChanged(boolean serviceEnabled, boolean deviceAdministratorEnabled, int value)
    {
        _settingsFragment.updateScreenTimeout(serviceEnabled, deviceAdministratorEnabled, value);
    }

    @Override
    public void screenDelayPreferenceChanged(boolean serviceEnabled, int screenDelayValue)
    {
        _settingsFragment.updateScreenDelaySummary(serviceEnabled, screenDelayValue);
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
    public void proximitySensorPreferenceChanged(boolean serviceEnabled, boolean proximitySensorEnabled)
    {
        _settingsFragment.updateProximitySensor(serviceEnabled, proximitySensorEnabled);
    }

    @Override
    public void detectPickUpPreferenceChanged(boolean serviceEnabled, boolean detectPickUpEnabled)
    {
        _settingsFragment.updateDetectPickUp(serviceEnabled, detectPickUpEnabled);
    }

    @Override
    public void quietHoursPreferenceChanged(boolean serviceEnabled, boolean quietHoursEnabled)
    {
        _settingsFragment.updateQuietHours(serviceEnabled, quietHoursEnabled);
    }

    @Override
    public void quietHoursStartPreferenceChanged(boolean serviceEnabled, boolean quietHoursEnabled, String quietHoursStart)
    {
        _settingsFragment.updateQuietHoursStart(serviceEnabled, quietHoursEnabled, quietHoursStart);
    }

    @Override
    public void quietHoursStopPreferenceChanged(boolean serviceEnabled, boolean quietHoursEnabled, String quietHoursStop)
    {
        _settingsFragment.updateQuietHoursStop(serviceEnabled, quietHoursEnabled, quietHoursStop);
    }

    @Override
    public void enabledAppsPreferenceChanged(boolean serviceEnabled, boolean allAppsEnabled)
    {
        _settingsFragment.updateEnabledApps(serviceEnabled, allAppsEnabled);
    }

    public void screenTimeoutPreferencePressed()
    {
        _presenter.screenTimeoutPreferencePressed();
    }

    public void screenDelayPreferencePressed()
    {
        _presenter.screenDelayPreferencePressed();
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
        _presenter.quietHoursPreferenceChanged(enabled, DateFormat.is24HourFormat(this));
    }

    public void recentActivityPreferencePressed()
    {
        _presenter.recentActivityPreferencePressed();
    }

    @Override
    public void displayRecentlyActiveApps(List<? extends RecentApp> appsList)
    {
        runOnUiThread(() ->
        {
            View recentAppsView = _layoutInflater.inflate(R.layout.recent_apps_layout, null);
            ListView listView = recentAppsView.findViewById(R.id.RecentAppsList);
            RecentAppsAdapter raa = new RecentAppsAdapter(this);

            raa.setApps(appsList);
            listView.setAdapter(raa);
            listView.setEmptyView(recentAppsView.findViewById(R.id.NoRecentAppsText));

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

            dialogBuilder.setTitle(R.string.RecentAppsTitle);
            dialogBuilder.setView(recentAppsView);
            dialogBuilder.setPositiveButton(R.string.Close, (dialog, whichButton) -> {});
            dialogBuilder.show();
            _progressDialog.dismiss();
        });
    }

    public void enabledAppsPreferencePressed()
    {
        _presenter.allAppsPreferencePressed();
    }

    @Override
    public void displayAllApps(List<? extends App> appsList)
    {
        runOnUiThread(() ->
        {
            View appsView = _layoutInflater.inflate(R.layout.apps_layout, null);
            ListView listView = appsView.findViewById(R.id.AppsList);
            EditText searchFilter = appsView.findViewById(R.id.SearchFilter);
            AppAdapter appAdapter = new AppAdapter(this);

            appAdapter.setApps(appsList);
            listView.setAdapter(appAdapter);

            searchFilter.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after)
                {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count)
                {
                    appAdapter.getFilter().filter(s);
                }

                @Override
                public void afterTextChanged(Editable s)
                {
                }
            });

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

            dialogBuilder.setTitle(R.string.EnabledAppsTitle);
            dialogBuilder.setView(appsView);
            dialogBuilder.setPositiveButton(R.string.Close, (dialog, whichButton) -> {});
            dialogBuilder.setCancelable(false);
            dialogBuilder.show();
            _progressDialog.dismiss();
        });
    }

    @Override
    public void displayLoadingAppsDialog()
    {
        _progressDialog.setTitle("Loading Apps");
        _progressDialog.setMessage("Please wait...");
        _progressDialog.setCancelable(false);
        _progressDialog.show();
    }

    @Override
    public void displayLoadingRecentActivityDialog()
    {
        _progressDialog.setTitle("Loading Recent Activity");
        _progressDialog.setMessage("Please wait...");
        _progressDialog.setCancelable(false);
        _progressDialog.show();
    }

    @Override
    public void setLightTheme()
    {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        _settingsFragment.lightThemeEnabled();
    }

    @Override
    public void setDarkTheme()
    {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        _settingsFragment.darkThemeEnabled();
    }

    public void appStateChanged(App app, boolean enabled)
    {
        _presenter.appStateChanged(app, enabled);
    }

    public void darkThemePreferencePressed(boolean enabled)
    {
        _presenter.darkThemePreferencePressed(enabled);
    }

    public void quietHoursStartPreferencePressed(String startTime)
    {
        _presenter.quietHoursStartPreferencePressed(startTime, DateFormat.is24HourFormat(this));
    }

    public void quietHoursStopPreferencePressed(String stopTime)
    {
        _presenter.quietHoursStopPreferencePressed(stopTime, DateFormat.is24HourFormat(this));
    }

    public void rateAppPreferencePressed()
    {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.StoreLink))));
    }

    public void contactAppDeveloperPreferencePressed()
    {
        String[] emails = {"contact.lesorin@gmail.com"};
        Intent intent = new Intent(Intent.ACTION_SENDTO);

        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, emails);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Spark Notifications");

        if(intent.resolveActivity(getPackageManager()) != null)
        {
            startActivity(intent);
        }
    }

    public void aboutAppPreferencePressed()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        dialogBuilder.setTitle(R.string.AboutAppTitle);
        dialogBuilder.setMessage(R.string.AboutSparkNotifications);
        dialogBuilder.setPositiveButton(R.string.Close, (alertDialog, id) -> {});
        dialogBuilder.show();
    }

    public void donatePreferencePressed()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        dialogBuilder.setTitle(R.string.DonateTitle);

        if(_purchaseItems != null && _purchaseItems.length > 0)
        {
            final NumberPicker numberPicker = new NumberPicker(this);

            numberPicker.setMinValue(1);
            numberPicker.setMaxValue(_purchaseItems.length);
            numberPicker.setDisplayedValues(_purchaseItems);
            dialogBuilder.setView(numberPicker);
            dialogBuilder.setPositiveButton(android.R.string.ok, (dialog, whichButton) ->
            {
                _billingManager.startBillingFlow(numberPicker.getValue());
            });
        }
        else
        {
            dialogBuilder.setMessage(R.string.DonationErrorSummary);
            dialogBuilder.setPositiveButton(R.string.Close, (dialog, whichButton) -> {});
        }

        dialogBuilder.show();
    }

    public void purchaseItemsAcquired(ArrayList<String> itemsList)
    {
        _purchaseItems = new String[itemsList.size()];

        for(int i = 0; i < itemsList.size(); i++)
        {
            _purchaseItems[i] = itemsList.get(i);
        }
    }

    public void purchaseCancelled()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        dialogBuilder.setTitle(R.string.Notice);
        dialogBuilder.setMessage(R.string.PurchaseCancelled);
        dialogBuilder.setPositiveButton(R.string.Close, (alertDialog, id) -> {});
        dialogBuilder.setCancelable(false);
        dialogBuilder.show();
    }

    public void purchaseCompleted()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        dialogBuilder.setTitle(R.string.Notice);
        dialogBuilder.setMessage(R.string.DonationCompleted);
        dialogBuilder.setPositiveButton(R.string.Close, (alertDialog, id) -> {});
        dialogBuilder.setCancelable(false);
        dialogBuilder.show();
    }

    public void cannotStartBillingFlow()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        dialogBuilder.setTitle(R.string.Notice);
        dialogBuilder.setMessage(R.string.DonationErrorSummary);
        dialogBuilder.setPositiveButton(R.string.Close, (alertDialog, id) -> {});
        dialogBuilder.setCancelable(false);
        dialogBuilder.show();
    }

    public void allAppsEnabledPreferencePressed(boolean enabled)
    {
        _presenter.allAppsEnabledPreferencePressed(enabled);
    }
}