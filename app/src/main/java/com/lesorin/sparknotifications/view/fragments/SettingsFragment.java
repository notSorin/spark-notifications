package com.lesorin.sparknotifications.view.fragments;

import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.NumberPicker;
import androidx.annotation.Nullable;
import com.lesorin.sparknotifications.BuildConfig;
import com.lesorin.sparknotifications.R;
import com.lesorin.sparknotifications.helpers.NotificationServiceHelper;
import com.lesorin.sparknotifications.receivers.ScreenNotificationsDeviceAdminReceiver;

//TODO make the preferences keys constants.
public class SettingsFragment extends PreferenceFragment
{
    private static final int REQUEST_CODE_ENABLE_ADMIN = 1;
    private SharedPreferences mPrefs;
    private boolean mServiceActive;
    private SwitchPreference mServicePreference;
    private DevicePolicyManager mDPM;
    private ComponentName mDeviceAdmin;
    private SwitchPreference mDeviceAdminPreference;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_layout);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        initializeContactDeveloper();
        initializeAppVersion();
        initializeService();
        initializeDeviceAdmin();
        initializeTime();
        setDelaySummary();
        //initializeDonations();
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
        mServicePreference = (SwitchPreference)findPreference("SparkNotificationsServiceKey");

        mServicePreference.setOnPreferenceClickListener(preference ->
        {
            if(mServiceActive)
            {
                showServiceDialog(R.string.notification_listener_launch);
            }
            else
            {
                showServiceDialog(R.string.notification_listener_warning);
            }

            //Don't update checkbox until we're really active.
            return false;
        });
    }

    private void checkForRunningService()
    {
        mServiceActive = NotificationServiceHelper.isServiceRunning(getActivity());

        mServicePreference.setChecked(mServiceActive);
        enableOptions(mServiceActive);
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
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, R.string.device_admin_explanation);
                startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);

                //Don't update checkbox until we're really active.
                return false;
            }
            else
            {
                mDPM.removeActiveAdmin(mDeviceAdmin);
                enableScreenTimeoutOption(false);

                return true;
            }
        });
    }

    private void checkForActiveDeviceAdmin()
    {
        boolean adminActive = mDPM.isAdminActive(mDeviceAdmin);

        mDeviceAdminPreference.setChecked(adminActive);
        enableScreenTimeoutOption(adminActive);
    }

    private void enableScreenTimeoutOption(boolean enable)
    {
        Preference wakeLength = findPreference("ScreenTimeoutKey");

        wakeLength.setEnabled(enable);
        setScreenTimeoutSummary();
    }

    private void setScreenTimeoutSummary()
    {
        Preference wakeLength = findPreference("ScreenTimeoutKey");

        if(wakeLength.isEnabled())
        {
            wakeLength.setSummary(String.format(getString(R.string.ScreenTimeoutSummaryEnabled), mPrefs.getInt("ScreenTimeoutKey", 10)));
        }
        else
        {
            wakeLength.setSummary(R.string.ScreenTimeoutSummaryDisabled);
        }
    }

    private void setDelaySummary()
    {
        findPreference("ScreenOnDelayKey").setSummary(getString(R.string.ScreenOnDelaySummary, mPrefs.getInt("ScreenOnDelayKey", 0)));
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

        start.setSummary(handleTime(mPrefs.getString("QuietHoursStartKey", "22:00")));
        stop.setSummary(handleTime(mPrefs.getString("QuietHoursStopKey", "08:00")));
        start.setOnPreferenceChangeListener(listener);
        stop.setOnPreferenceChangeListener(listener);

        findPreference("ScreenTimeoutKey").setOnPreferenceClickListener(preference ->
        {
            LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View numberPickerView = inflater.inflate(R.layout.number_picker_dialog, null);
            final NumberPicker numberPicker = numberPickerView.findViewById(R.id.NumberPicker);

            numberPicker.setMinValue(1);
            numberPicker.setMaxValue(900);
            numberPicker.setValue(mPrefs.getInt("ScreenTimeoutKey", 10));

            new AlertDialog.Builder(getActivity()).setTitle(R.string.ScreenTimeoutKey).setView(numberPickerView).
                    setPositiveButton(android.R.string.ok, (dialog, whichButton) ->
                    {
                        mPrefs.edit().putInt("ScreenTimeoutKey", numberPicker.getValue()).apply();
                        setScreenTimeoutSummary();
                        dialog.dismiss();
                    }).setNegativeButton(android.R.string.cancel, (dialog, whichButton) -> dialog.dismiss()).show();

            return true;
        });

        findPreference("ScreenOnDelayKey").setOnPreferenceClickListener(preference ->
        {
            LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View numberPickerView = inflater.inflate(R.layout.number_picker_dialog, null);
            final NumberPicker numberPicker = numberPickerView.findViewById(R.id.NumberPicker);

            numberPicker.setMinValue(0);
            numberPicker.setMaxValue(900);
            numberPicker.setValue(mPrefs.getInt("ScreenOnDelayKey", 0));

            new AlertDialog.Builder(getActivity()).setTitle(R.string.ScreenOnDelayTitle).setView(numberPicker).
                    setPositiveButton(android.R.string.ok, (dialog, whichButton) ->
                    {
                        mPrefs.edit().putInt("ScreenOnDelayKey", numberPicker.getValue()).apply();
                        setDelaySummary();
                        dialog.dismiss();
                    }).setNegativeButton(android.R.string.cancel, (dialog, whichButton) -> dialog.dismiss()).show();

            return true;
        });
    }

    private void enableOptions(boolean enable)
    {
        findPreference("EnabledAppsKey").setEnabled(enable);
        findPreference("ScreenTimeoutKey").setEnabled(enable);
        findPreference("ScreenOnDelayKey").setEnabled(enable);
        findPreference("FullBrightnessKey").setEnabled(enable);
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

    private void showServiceDialog(int message)
    {
        new AlertDialog.Builder(getActivity()).setMessage(message).setCancelable(false).
                setPositiveButton(android.R.string.ok, (alertDialog, id) ->
                {
                    alertDialog.cancel();
                    startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                }).show();
    }
}