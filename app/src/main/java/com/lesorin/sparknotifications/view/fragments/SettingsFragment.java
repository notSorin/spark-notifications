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
import com.lesorin.sparknotifications.view.activities.MainActivity;

//TODO make the preferences keys constants.
public class SettingsFragment extends PreferenceFragment
{
    private SwitchPreference _deviceAdminPreference, _servicePreference, _proximitySensorPreference,
            _detectPickUpPreference, _quietHoursPreference, _darkThemePreference;
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
        initializeProximitySensor();
        initializeDetectPickUp();
        initializeDarkTheme();
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

    private void initializeDarkTheme()
    {
        _darkThemePreference = (SwitchPreference)findPreference("DarkThemeKey");

        _darkThemePreference.setOnPreferenceClickListener(preference ->
        {
            _activity.darkThemePreferencePressed(_darkThemePreference.isChecked());

            return true;
        });
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
            //todo

            return true;
        });
    }

    private void initializeQuietHoursStart()
    {
        _quietHoursStartPreference = (TimePreference)findPreference("QuietHoursStartKey");

        _quietHoursStartPreference.setOnPreferenceChangeListener((preference, newValue) ->
        {
            _activity.quietHoursStartPreferencePressed(_quietHoursStartPreference.toString());

            return true;
        });
    }

    private void initializeQuietHours()
    {
        _quietHoursPreference = (SwitchPreference)findPreference("QuietHoursKey");

        _quietHoursPreference.setOnPreferenceClickListener(preference ->
        {
           _activity.quietHoursPreferencePressed(_quietHoursPreference.isChecked());

            return true;
        });
    }

    private void initializeDetectPickUp()
    {
        _detectPickUpPreference = (SwitchPreference)findPreference("DetectPickUpKey");

        _detectPickUpPreference.setOnPreferenceClickListener(preference ->
        {
            _activity.detectPickUpPreferencePressed(_detectPickUpPreference.isChecked());

            return true;
        });
    }

    private void initializeProximitySensor()
    {
        _proximitySensorPreference = (SwitchPreference)findPreference("ProximitySensorKey");

        _proximitySensorPreference.setOnPreferenceClickListener(preference ->
        {
           _activity.proximitySensorPreferencePressed(_proximitySensorPreference.isChecked());

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
            _activity.recentActivityPreferencePressed();

            return true;
        });
    }

    private void initializeEnabledApps()
    {
        _enabledAppsPreference = findPreference("EnabledAppsKey");

        _enabledAppsPreference.setOnPreferenceClickListener(preference ->
        {
            _activity.enabledAppsPreferencePressed();

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

    public void updateScreenDelaySummary(boolean screenDelayEnabled, int screenDelayValue)
    {
        _screenDelayPreference.setEnabled(screenDelayEnabled);
        _screenDelayPreference.setSummary(getString(screenDelayValue == 1 ? R.string.ScreenOnDelaySummarySingle : R.string.ScreenOnDelaySummary, screenDelayValue));
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

    public void updateServicePreference(boolean serviceEnabled)
    {
        _servicePreference.setChecked(serviceEnabled);
    }

    public void updateDeviceAdministrator(boolean deviceAdministratorEnabled)
    {
        _deviceAdminPreference.setChecked(deviceAdministratorEnabled);
    }

    public void updateScreenTimeout(boolean serviceEnabled, boolean deviceAdministratorEnabled, int screenTimeoutValue)
    {
        _screenTimeoutPreference.setEnabled(serviceEnabled && deviceAdministratorEnabled);

        if(serviceEnabled && deviceAdministratorEnabled)
        {
            _screenTimeoutPreference.setSummary(String.format(getString(R.string.ScreenTimeoutSummaryEnabled), screenTimeoutValue));
        }
        else if(serviceEnabled && !deviceAdministratorEnabled)
        {
            _screenTimeoutPreference.setSummary(R.string.ScreenTimeoutSummaryEnableAdmin);
        }
        else if(!serviceEnabled && deviceAdministratorEnabled)
        {
            _screenTimeoutPreference.setSummary(R.string.ScreenTimeoutSummaryEnableService);
        }
        else
        {
            _screenTimeoutPreference.setSummary(R.string.ScreenTimeoutSummaryEnableServiceAndAdmin);
        }
    }

    public void updateProximitySensor(boolean serviceEnabled, boolean proximitySensorEnabled)
    {
        _proximitySensorPreference.setEnabled(serviceEnabled);
        _proximitySensorPreference.setChecked(proximitySensorEnabled);
    }

    public void updateDetectPickUp(boolean serviceEnabled, boolean detectPickUpEnabled)
    {
        _detectPickUpPreference.setEnabled(serviceEnabled);
        _detectPickUpPreference.setChecked(detectPickUpEnabled);
    }

    public void updateQuietHours(boolean serviceEnabled, boolean quietHoursEnabled)
    {
        _quietHoursPreference.setEnabled(serviceEnabled);
        _quietHoursPreference.setChecked(quietHoursEnabled);
    }

    public void updateQuietHoursStart(boolean serviceEnabled, boolean quietHoursEnabled, String quietHoursStart)
    {
        _quietHoursStartPreference.setEnabled(serviceEnabled && quietHoursEnabled);
        _quietHoursStartPreference.setSummary(quietHoursStart);
    }

    public void updateQuietHoursStop(boolean serviceEnabled, boolean quietHoursEnabled, String quietHoursStop)
    {
        _quietHoursStopPreference.setEnabled(serviceEnabled && quietHoursEnabled);
        _quietHoursStopPreference.setSummary(quietHoursStop);
    }

    public void updateEnabledApps(boolean enabledAppsEnabled)
    {
        _enabledAppsPreference.setEnabled(enabledAppsEnabled);
    }
}