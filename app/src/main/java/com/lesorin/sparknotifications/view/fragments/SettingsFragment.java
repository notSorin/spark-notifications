package com.lesorin.sparknotifications.view.fragments;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.view.View;
import android.widget.ListView;
import androidx.annotation.Nullable;
import com.lesorin.sparknotifications.BuildConfig;
import com.lesorin.sparknotifications.R;
import com.lesorin.sparknotifications.view.activities.MainActivity;

public class SettingsFragment extends PreferenceFragment
{
    private SwitchPreference _deviceAdminPreference, _servicePreference, _proximitySensorPreference,
            _detectPickUpPreference, _quietHoursPreference, _darkThemePreference, _allAppsPreference;
    private Preference _enabledAppsPreference, _recentActivityPreference, _screenTimeoutPreference,
            _screenDelayPreference, _rateAppPreference, _donatePreference;
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
        initializeAllAppsEnabled();
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
        initializeRateApp();
        initializeAppVersion();
    }

    private void initializeAllAppsEnabled()
    {
        _allAppsPreference = (SwitchPreference)findPreference("AllAppsEnabledKey");

        _allAppsPreference.setOnPreferenceClickListener(preference ->
        {
            _activity.allAppsEnabledPreferencePressed(_allAppsPreference.isChecked());

            return true;
        });
    }

    private void initializeRateApp()
    {
        _rateAppPreference = findPreference("RateAppKey");

        _rateAppPreference.setOnPreferenceClickListener(preference ->
        {
            _activity.rateAppPreferencePressed();

            return true;
        });
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
        _donatePreference = findPreference("DonateKey");

        _donatePreference.setOnPreferenceClickListener(preference ->
        {
            _activity.donatePreferencePressed();

            return true;
        });
    }

    private void initializeQuietHoursStop()
    {
        _quietHoursStopPreference = (TimePreference)findPreference("QuietHoursStopKey");

        _quietHoursStopPreference.setOnPreferenceChangeListener((preference, newValue) ->
        {
            _activity.quietHoursStopPreferencePressed(_quietHoursStopPreference.toString());

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
        Preference versionPreference = findPreference("AboutAppKey");

        versionPreference.setSummary(BuildConfig.VERSION_NAME);
        versionPreference.setOnPreferenceClickListener(preference ->
        {
            _activity.aboutAppPreferencePressed();

            return true;
        });
    }

    private void initializeContactDeveloper()
    {
        findPreference("ContactDeveloperKey").setOnPreferenceClickListener(preference ->
        {
            _activity.contactAppDeveloperPreferencePressed();

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

        if(screenDelayValue == 0)
        {
            _screenDelayPreference.setSummary(getString(R.string.ScreenOnDelaySummaryZero));
        }
        else if(screenDelayValue == 1)
        {
            _screenDelayPreference.setSummary(getString(R.string.ScreenOnDelaySummarySingle, screenDelayValue));
        }
        else
        {
            _screenDelayPreference.setSummary(getString(R.string.ScreenOnDelaySummary, screenDelayValue));
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

    public void updateEnabledApps(boolean serviceEnabled, boolean allAppsEnabled, int appsEnabledAmount)
    {
        _enabledAppsPreference.setEnabled(serviceEnabled && !allAppsEnabled);

        if(serviceEnabled && !allAppsEnabled)
        {
            _enabledAppsPreference.setSummary(getString(R.string.EnabledAppsSummary, appsEnabledAmount));
        }
        else
        {
            _enabledAppsPreference.setSummary(R.string.EnabledAppsOffSummary);
        }
    }

    public void updateAllAppsPreference(boolean serviceEnabled, boolean allAppsEnabled)
    {
        _allAppsPreference.setEnabled(serviceEnabled);
        _allAppsPreference.setChecked(allAppsEnabled);
    }

    public void lightThemeEnabled()
    {
        _darkThemePreference.setTitle(R.string.DarkThemeTitle);
        _darkThemePreference.setSummary(R.string.DarkThemeSummary);
        _darkThemePreference.setIcon(R.drawable.theme);
    }

    public void darkThemeEnabled()
    {
        _darkThemePreference.setTitle(R.string.DarkThemeTitleEasterEgg);
        _darkThemePreference.setSummary(R.string.DarkThemeSummaryEasterEgg);
        _darkThemePreference.setIcon(R.drawable.evil);
    }
}