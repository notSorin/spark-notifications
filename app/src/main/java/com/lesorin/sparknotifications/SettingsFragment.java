package com.lesorin.sparknotifications;

import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import com.lesorin.sparknotifications.helpers.LogReporting;
import com.lesorin.sparknotifications.helpers.NotificationServiceHelper;
import com.lesorin.sparknotifications.receivers.ScreenNotificationsDeviceAdminReceiver;
import fr.nicolaspomepuy.discreetapprate.AppRate;
import fr.nicolaspomepuy.discreetapprate.RetryPolicy;

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener
{
    private static final int REQUEST_CODE_ENABLE_ADMIN = 1;
    private SharedPreferences mPrefs;
    private boolean mServiceActive;
    private CheckBoxPreference mServicePreference;
    private DevicePolicyManager mDPM;
    private ComponentName mDeviceAdmin;
    private CheckBoxPreference mDeviceAdminPreference;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        findPreference("recent_apps").setOnPreferenceClickListener(this);
        findPreference("contact").setOnPreferenceClickListener(this);
        findPreference("version").setSummary(BuildConfig.VERSION_NAME);
        initializeService();
        initializeDeviceAdmin();
        initializeTime();
        setDelaySummary();
        //initializeDonations();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        AppRate.with(getActivity()).text(R.string.rate).initialLaunchCount(9).
                retryPolicy(RetryPolicy.EXPONENTIAL).checkAndShow();
    }

    public void onResume()
    {
        super.onResume();
        checkForRunningService();
        checkForActiveDeviceAdmin();
    }

    private void initializeService()
    {
        mServicePreference = (CheckBoxPreference)findPreference("service");

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
        mDeviceAdminPreference = (CheckBoxPreference)findPreference("device_admin");

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
        Preference wakeLength = findPreference("wake_length");

        wakeLength.setEnabled(enable);

        if(enable)
        {
            setWakeLengthSummary();
        }
        else
        {
            wakeLength.setSummary(R.string.disabled_wake_length);
        }
    }

    private void setWakeLengthSummary()
    {
        findPreference("wake_length").setSummary(getString(R.string.wake_length_summary) + " " +
                mPrefs.getInt("wake_length", 10) + " " + getString(R.string.wake_length_summary_2));
    }

    private void setDelaySummary()
    {
        findPreference("delay").setSummary(getString(R.string.delay_summary, mPrefs.getInt("delay", 0)));
    }

    private void initializeTime()
    {
        Preference.OnPreferenceChangeListener listener = (preference, newValue) ->
        {
            preference.setSummary(handleTime(newValue.toString()));

            return true;
        };

        Preference start = findPreference("startTime");
        Preference stop = findPreference("stopTime");

        start.setSummary(handleTime(mPrefs.getString("startTime", "22:00")));
        stop.setSummary(handleTime(mPrefs.getString("stopTime", "08:00")));
        start.setOnPreferenceChangeListener(listener);
        stop.setOnPreferenceChangeListener(listener);

        findPreference("wake_length").setOnPreferenceClickListener(preference ->
        {
            LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View numberPickerView = inflater.inflate(R.layout.number_picker_dialog, null);
            final NumberPicker numberPicker = (NumberPicker)numberPickerView.findViewById(R.id.number_picker);

            numberPicker.setMinValue(1);
            numberPicker.setMaxValue(900);
            numberPicker.setValue(mPrefs.getInt("wake_length", 10));

            new AlertDialog.Builder(getActivity()).setTitle(R.string.wake_length).setView(numberPickerView).
                    setPositiveButton(android.R.string.ok, (dialog, whichButton) ->
                    {
                        mPrefs.edit().putInt("wake_length", numberPicker.getValue()).apply();
                        setWakeLengthSummary();
                        dialog.dismiss();
                    }).setNegativeButton(android.R.string.cancel, (dialog, whichButton) -> dialog.dismiss()).show();

            return true;
        });

        findPreference("delay").setOnPreferenceClickListener(preference ->
        {
            LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View numberPickerView = inflater.inflate(R.layout.number_picker_dialog, null);
            final NumberPicker numberPicker = (NumberPicker) numberPickerView.findViewById(R.id.number_picker);

            numberPicker.setMinValue(0);
            numberPicker.setMaxValue(900);
            numberPicker.setValue(mPrefs.getInt("delay", 0));

            new AlertDialog.Builder(getActivity()).setTitle(R.string.delay_title).setView(numberPickerView).
                    setPositiveButton(android.R.string.ok, (dialog, whichButton) ->
                    {
                        mPrefs.edit().putInt("delay", numberPicker.getValue()).apply();
                        setDelaySummary();
                        dialog.dismiss();
                    }).setNegativeButton(android.R.string.cancel, (dialog, whichButton) -> dialog.dismiss()).show();

            return true;
        });
    }

    private void enableOptions(boolean enable)
    {
        findPreference("app").setEnabled(enable);
        findPreference("wake_length").setEnabled(enable);
        findPreference("delay").setEnabled(enable);
        findPreference("bright").setEnabled(enable);
        findPreference("proxSensor").setEnabled(enable);
        findPreference("wake_on_pickup").setEnabled(enable);
        findPreference("quiet").setEnabled(enable);
        findPreference("startTime").setEnabled(enable);
        findPreference("stopTime").setEnabled(enable);
        findPreference("status-bar").setEnabled(enable);
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

    @Override
    public boolean onPreferenceClick(Preference preference)
    {
        if(preference.getKey().equals("contact"))
        {
            new LogReporting(getActivity()).collectAndSendLogs();

            return true;
        }
        else if(preference.getKey().equals("recent_apps"))
        {
            startActivity(new Intent(getActivity(), RecentAppsActivity.class));

            return true;
        }

        return false;
    }
}