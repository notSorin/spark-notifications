package com.lesorin.sparknotifications.view.fragments;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.NumberPicker;
import androidx.annotation.Nullable;
import com.lesorin.sparknotifications.BuildConfig;
import com.lesorin.sparknotifications.R;
import com.lesorin.sparknotifications.view.activities.AppsActivity;
import com.lesorin.sparknotifications.view.activities.MainActivity;
import com.lesorin.sparknotifications.view.activities.RecentAppsActivity;
import com.lesorin.sparknotifications.view.receivers.ScreenNotificationsDeviceAdminReceiver;
import com.lesorin.sparknotifications.view.services.NotificationListener;

//TODO make the preferences keys constants.
public class SettingsFragment extends PreferenceFragment
{
    private boolean _serviceActive;
    private SwitchPreference _servicePreference, _fullBrightnessPreference;
    private Preference _enabledAppsPreference, _recentActivityPreference, _screenTimeoutPreference,
            _screenDelayPreference;
    private DevicePolicyManager mDPM;
    private ComponentName mDeviceAdmin;
    private SwitchPreference mDeviceAdminPreference;
    private MainActivity _activity;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_layout);

        _activity = (MainActivity)getActivity();

        initializeAllPreferences();
    }

    private void initializeAllPreferences()
    {
        //Service.
        initializeService();
        initializeEnabledApps();
        initializeRecentActivity();

        //Options.
        initializeDeviceAdmin();
        initializeScreenTimeout();
        initializeScreenDelay();
        initializeFullBrightness();
        initializeNotificationsDrawer();
        initializeProximitySensor();
        initializeDetectPickUp();
        initializeQuietHours();
        initializeQuietHoursStart();
        initializeQuietHoursStop();

        //Other.
        initializeContactDeveloper();
        initializeDonations();
        initializeAppVersion();
    }

    private void initializeDonations()
    {
        //todo
    }

    private void initializeQuietHoursStop()
    {
        //todo
    }

    private void initializeQuietHoursStart()
    {
        //todo
    }

    private void initializeQuietHours()
    {
        //todo
    }

    private void initializeDetectPickUp()
    {
        //todo
    }

    private void initializeProximitySensor()
    {
        //todo
    }

    private void initializeNotificationsDrawer()
    {
        //todo
    }

    private void initializeFullBrightness()
    {
        _fullBrightnessPreference = (SwitchPreference)findPreference("FullBrightnessKey");

        _fullBrightnessPreference.setOnPreferenceClickListener(preference ->
        {
            //todo
            return true;
        });
    }

    private void initializeScreenDelay()
    {
        _screenDelayPreference = findPreference("ScreenOnDelayKey");

        _screenDelayPreference.setOnPreferenceClickListener(preference ->
        {
            LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View numberPickerView = inflater.inflate(R.layout.number_picker_dialog, null);
            final NumberPicker numberPicker = numberPickerView.findViewById(R.id.NumberPicker);

            numberPicker.setMinValue(0);
            numberPicker.setMaxValue(10);
            //numberPicker.setValue(mPrefs.getInt("ScreenOnDelayKey", 0));

            new AlertDialog.Builder(getActivity()).setTitle(R.string.ScreenOnDelayTitle).setView(numberPicker).
                    setPositiveButton(android.R.string.ok, (dialog, whichButton) ->
                    {
                        //mPrefs.edit().putInt("ScreenOnDelayKey", numberPicker.getValue()).apply();
                        //setScreenDelaySummary(); //TODO this will be called by the main ac
                    }).setNegativeButton(android.R.string.cancel, (dialog, whichButton) -> dialog.dismiss()).show();

            return true;
        });
    }

    private void initializeScreenTimeout()
    {
        _screenTimeoutPreference = findPreference("ScreenTimeoutKey");

        _screenTimeoutPreference.setOnPreferenceClickListener(preference ->
        {
            LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View numberPickerView = inflater.inflate(R.layout.number_picker_dialog, null);
            final NumberPicker numberPicker = numberPickerView.findViewById(R.id.NumberPicker);

            numberPicker.setMinValue(3);
            numberPicker.setMaxValue(30);
            //numberPicker.setValue(mPrefs.getInt("ScreenTimeoutKey", 10));

            new AlertDialog.Builder(getActivity()).setTitle(R.string.ScreenTimeoutKey).setView(numberPickerView).
                    setPositiveButton(android.R.string.ok, (dialog, whichButton) ->
                    {
                        //mPrefs.edit().putInt("ScreenTimeoutKey", numberPicker.getValue()).apply();
                        setScreenTimeoutSummary();
                        dialog.dismiss();
                    }).setNegativeButton(android.R.string.cancel, (dialog, whichButton) -> dialog.dismiss()).show();

            return true;
        });
    }

    private void initializeRecentActivity()
    {
        _recentActivityPreference = findPreference("RecentAppsKey");

        _recentActivityPreference.setOnPreferenceClickListener(preference ->
        {
            startActivity(new Intent(_activity, RecentAppsActivity.class));

            return true;
        });
    }

    private void initializeEnabledApps()
    {
        _enabledAppsPreference = findPreference("EnabledAppsKey");

        _enabledAppsPreference.setOnPreferenceClickListener(preference ->
        {
            startActivity(new Intent(_activity, AppsActivity.class));

            return true;
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        View rootView = getView();
        ListView list = rootView.findViewById(android.R.id.list);

        list.setDivider(null);
    }

    private void initializeAppVersion()
    {
        //TODO open an activity showing the update logs when the version is pressed.
        Preference versionPreference = findPreference("VersionKey");

        versionPreference.setSummary(BuildConfig.VERSION_NAME);
    }

    private void initializeContactDeveloper()
    {
        findPreference("ContactDeveloperKey").setOnPreferenceClickListener(preference ->
        {
            String[] emails = {"contact.lesorin@gmail.com"};
            Intent intent = new Intent(Intent.ACTION_SENDTO);

            intent.setData(Uri.parse("mailto:")); // only email apps should handle this
            intent.putExtra(Intent.EXTRA_EMAIL, emails);
            intent.putExtra(Intent.EXTRA_SUBJECT, "Spark Notifications");

            if(intent.resolveActivity(getActivity().getPackageManager()) != null)
            {
                getActivity().startActivity(intent);
            }

            return true;
        });
    }

    public void onResume()
    {
        super.onResume();
        checkForRunningService();
        checkForActiveDeviceAdmin();
    }

    private void initializeService()
    {
        _servicePreference = (SwitchPreference)findPreference("SparkNotificationsServiceKey");

        _servicePreference.setOnPreferenceClickListener(preference ->
        {
            if(_serviceActive)
            {
                showServiceDialog(R.string.ServiceDisableInfo);
            }
            else
            {
                showServiceDialog(R.string.ServiceEnableInfo);
            }

            //The state of the preference will update when checkForRunningService() is called later.

            return false;
        });
    }

    private void checkForRunningService()
    {
        _serviceActive = isServiceRunning(getActivity());

        _servicePreference.setChecked(_serviceActive);
        enableOptions(_serviceActive);
    }

    public boolean isServiceRunning(Context context)
    {
        ActivityManager manager = (ActivityManager)context.getSystemService(Activity.ACTIVITY_SERVICE);

        for(ActivityManager.RunningServiceInfo serviceInfo : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if(serviceInfo.service.getClassName().equals(NotificationListener.class.getName()))
            {
                return true;
            }
        }

        return false;
    }

    private void initializeDeviceAdmin()
    {
        mDPM = (DevicePolicyManager)getActivity().getSystemService(Context.DEVICE_POLICY_SERVICE);
        mDeviceAdmin = new ComponentName(getActivity(), ScreenNotificationsDeviceAdminReceiver.class);
        mDeviceAdminPreference = (SwitchPreference)findPreference("DeviceAdminKey");

        mDeviceAdminPreference.setOnPreferenceChangeListener((preference, newValue) ->
        {
            if((Boolean)newValue)
            {
                //Launch the activity to have the user enable our admin.
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);

                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdmin);
                startActivity(intent);

                //Don't update checkbox until we're really active.
                return false;
            }
            else
            {
                mDPM.removeActiveAdmin(mDeviceAdmin);
                enableScreenTimeout(false);

                return true;
            }
        });
    }

    private void checkForActiveDeviceAdmin()
    {
        boolean adminActive = mDPM.isAdminActive(mDeviceAdmin);

        mDeviceAdminPreference.setChecked(adminActive);
        enableScreenTimeout(adminActive);
    }

    private void enableScreenTimeout(boolean enabled)
    {
        _screenTimeoutPreference.setEnabled(enabled);
        setScreenTimeoutSummary();
    }

    private void setScreenTimeoutSummary()
    {
        if(_screenTimeoutPreference.isEnabled())
        {
            //wakeLength.setSummary(String.format(getString(R.string.ScreenTimeoutSummaryEnabled), mPrefs.getInt("ScreenTimeoutKey", 10)));
        }
        else
        {
            _screenTimeoutPreference.setSummary(R.string.ScreenTimeoutSummaryDisabled);
        }
    }

    private void setScreenDelaySummary()
    {
        //findPreference("ScreenOnDelayKey").setSummary(getString(R.string.ScreenOnDelaySummary, mPrefs.getInt("ScreenOnDelayKey", 0)));
    }

    private void initializeTime()
    {
        Preference.OnPreferenceChangeListener listener = (preference, newValue) ->
        {
            preference.setSummary(handleTime(newValue.toString()));

            return true;
        };

        Preference start = findPreference("QuietHoursStartKey");
        Preference stop = findPreference("QuietHoursStopKey");

        //start.setSummary(handleTime(mPrefs.getString("QuietHoursStartKey", "22:00")));
        //stop.setSummary(handleTime(mPrefs.getString("QuietHoursStopKey", "08:00")));
        start.setOnPreferenceChangeListener(listener);
        stop.setOnPreferenceChangeListener(listener);
    }

    private void enableOptions(boolean enable)
    {
        _enabledAppsPreference.setEnabled(enable);
        _screenTimeoutPreference.setEnabled(enable);
        _screenDelayPreference.setEnabled(enable);
        _fullBrightnessPreference.setEnabled(enable);
        findPreference("ProximitySensorKey").setEnabled(enable);
        findPreference("DetectPickUpKey").setEnabled(enable);
        findPreference("QuietHoursKey").setEnabled(enable);
        findPreference("QuietHoursStartKey").setEnabled(enable);
        findPreference("QuietHoursStopKey").setEnabled(enable);
        findPreference("NotificationsTrayKey").setEnabled(enable);
    }

    private String handleTime(String time)
    {
        String[] timeParts = time.split(":");
        int lastHour = Integer.parseInt(timeParts[0]);
        int lastMinute = Integer.parseInt(timeParts[1]);
        boolean is24HourFormat = DateFormat.is24HourFormat(getActivity());

        if(is24HourFormat)
        {
            return ((lastHour < 10) ? "0" : "") + lastHour + ":" + ((lastMinute < 10) ? "0" : "") + lastMinute;
        }
        else
        {
            int myHour = lastHour % 12;

            return ((myHour == 0) ? "12" : ((myHour < 10) ? "0" : "") + myHour) + ":" + ((lastMinute < 10) ? "0" : "")
                    + lastMinute + ((lastHour >= 12) ? " PM" : " AM");
        }
    }

    private void showServiceDialog(int messageId)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

        dialogBuilder.setTitle(R.string.Notice);
        dialogBuilder.setMessage(messageId);
        dialogBuilder.setPositiveButton(R.string.IUnderstand, (alertDialog, id) ->
                startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)));
        dialogBuilder.setCancelable(false);

        dialogBuilder.show();
    }
}