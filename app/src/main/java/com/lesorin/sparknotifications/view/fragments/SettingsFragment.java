package com.lesorin.sparknotifications.view.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ListView;
import androidx.annotation.Nullable;
import com.lesorin.sparknotifications.BuildConfig;
import com.lesorin.sparknotifications.R;
import com.lesorin.sparknotifications.view.activities.AppsActivity;
import com.lesorin.sparknotifications.view.activities.MainActivity;
import com.lesorin.sparknotifications.view.activities.RecentAppsActivity;

//TODO make the preferences keys constants.
public class SettingsFragment extends PreferenceFragment
{
    private SwitchPreference _deviceAdminPreference, _servicePreference, _fullBrightnessPreference, _notificationsDrawerPreference,
        _proximitySensorPreference, _detectPickUpPreference, _quietHoursPreference;
    private Preference _enabledAppsPreference, _recentActivityPreference, _screenTimeoutPreference,
            _screenDelayPreference;
    private TimePreference _quietHoursStartPreference, _quietHoursStopPreference;

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
        initializeScreenDelay();
        initializeFullBrightness();
        initializeNotificationsDrawer();
        initializeProximitySensor();
        initializeDetectPickUp();
        initializeQuietHours();
        initializeQuietHoursStart();
        initializeQuietHoursStop();

        //Admin options.
        initializeDeviceAdmin();
        initializeScreenTimeout();

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
        _quietHoursStopPreference = (TimePreference)findPreference("QuietHoursStopKey");

        _quietHoursStopPreference.setOnPreferenceChangeListener((preference, newValue) ->
        {
            preference.setSummary(handleTime(newValue.toString()));

            return true;
        });
    }

    private void initializeQuietHoursStart()
    {
        _quietHoursStartPreference = (TimePreference)findPreference("QuietHoursStartKey");

        _quietHoursStartPreference.setOnPreferenceChangeListener((preference, newValue) ->
        {
            preference.setSummary(handleTime(newValue.toString()));

            return true;
        });
    }

    private void initializeQuietHours()
    {
        _quietHoursPreference = (SwitchPreference)findPreference("QuietHoursKey");

        _quietHoursPreference.setOnPreferenceClickListener(preference ->
        {
           //todo
            return true;
        });
    }

    private void initializeDetectPickUp()
    {
        _detectPickUpPreference = (SwitchPreference)findPreference("DetectPickUpKey");

        _detectPickUpPreference.setOnPreferenceClickListener(preference ->
        {
            //todo
            return true;
        });
    }

    private void initializeProximitySensor()
    {
        _proximitySensorPreference = (SwitchPreference)findPreference("ProximitySensorKey");

        _proximitySensorPreference.setOnPreferenceClickListener(preference ->
        {
           //todo
            return true;
        });
    }

    private void initializeNotificationsDrawer()
    {
        _notificationsDrawerPreference = (SwitchPreference)findPreference("NotificationsTrayKey");

        _notificationsDrawerPreference.setOnPreferenceClickListener(preference ->
        {
           //todo
            return true;
        });
    }

    private void initializeFullBrightness()
    {
        _fullBrightnessPreference = (SwitchPreference)findPreference("FullBrightnessKey");

        _fullBrightnessPreference.setOnPreferenceClickListener(preference ->
        {
            _activity.fullBrightnessPreferencePressed(_fullBrightnessPreference.isChecked());

            return true;
        });
    }

    private void initializeScreenDelay()
    {
        _screenDelayPreference = findPreference("ScreenOnDelayKey");

        _screenDelayPreference.setOnPreferenceClickListener(preference ->
        {
            _activity.screenDelayPreferencePressed();

            return true;
        });
    }

    private void initializeScreenTimeout()
    {
        _screenTimeoutPreference = findPreference("ScreenTimeoutKey");

        _screenTimeoutPreference.setOnPreferenceClickListener(preference ->
        {
            _activity.screenTimeoutPreferencePressed();

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

    private void initializeService()
    {
        _servicePreference = (SwitchPreference)findPreference("SparkNotificationsServiceKey");

        _servicePreference.setOnPreferenceChangeListener((preference, newValue) ->
        {
            _activity.notificationsServicePreferencePressed((boolean)newValue);

            //The state of the preference will update when checkForRunningService() is called later.

            return true;
        });
    }

    private void initializeDeviceAdmin()
    {
        _deviceAdminPreference = (SwitchPreference)findPreference("DeviceAdminKey");

        _deviceAdminPreference.setOnPreferenceChangeListener((preference, newValue) ->
        {
            _activity.deviceAdminPreferencePressed((boolean)newValue);

            return true;
        });
    }

    public void updateScreenDelaySummary(int screenDelayValue)
    {
        _screenDelayPreference.setSummary(getString(screenDelayValue == 1 ? R.string.ScreenOnDelaySummarySingle : R.string.ScreenOnDelaySummary, screenDelayValue));
    }

    private void setOptionsState(boolean enabled)
    {
        _enabledAppsPreference.setEnabled(enabled);
        _screenDelayPreference.setEnabled(enabled);
        _fullBrightnessPreference.setEnabled(enabled);
        _notificationsDrawerPreference.setEnabled(enabled);
        _proximitySensorPreference.setEnabled(enabled);
        _detectPickUpPreference.setEnabled(enabled);
        _quietHoursPreference.setEnabled(enabled);
        _quietHoursStartPreference.setEnabled(enabled);
        _quietHoursStopPreference.setEnabled(enabled);
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

    public void servicePreferenceChanged(boolean isServiceEnabled)
    {
        _servicePreference.setChecked(isServiceEnabled);
        setOptionsState(isServiceEnabled);
    }

    public void deviceAdministratorPreferenceChanged(boolean deviceAdministratorEnabled)
    {
        _deviceAdminPreference.setChecked(deviceAdministratorEnabled);
    }

    public void updateScreenTimeout(boolean deviceAdministratorEnabled, int screenTimeoutValue)
    {
        _screenTimeoutPreference.setEnabled(deviceAdministratorEnabled);

        if(deviceAdministratorEnabled)
        {
            _screenTimeoutPreference.setSummary(String.format(getString(R.string.ScreenTimeoutSummaryEnabled), screenTimeoutValue));
        }
        else
        {
            _screenTimeoutPreference.setSummary(R.string.ScreenTimeoutSummaryDisabled);
        }
    }

    public void updateFullBrightness(boolean enabled)
    {
        _fullBrightnessPreference.setChecked(enabled);
    }
}