package com.foxconn.cnsbg.escort.mainctrl;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;

import com.foxconn.cnsbg.escort.common.SettingsUtil;
import com.foxconn.cnsbg.escort.common.SysUtil;

import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity {
    /**
     * Determines whether to always show the simplified settings UI, where
     * settings are presented in a single list. When false, settings are shown
     * as a master/detail two-pane view on tablets. When true, a single pane is
     * shown on tablets.
     */
    private static final boolean ALWAYS_SIMPLE_PREFS = false;

    private static boolean First_bootup = true;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        init();
        setupSimplePreferencesScreen();
    }

    /**
     * Shows the simplified settings UI if the device configuration if the
     * device configuration dictates that a simplified, single-pane UI should be
     * shown.
     */
    private void setupSimplePreferencesScreen() {
        if (!isSimplePreferences(this)) {
            return;
        }

        // In the simplified UI, fragments are not used at all and we instead
        // use the older PreferenceActivity APIs.

        // Add 'general' preferences.
        addPreferencesFromResource(com.foxconn.cnsbg.escort.R.xml.setting_http);
			
        // Add 'notifications' preferences, and a corresponding header.
        PreferenceCategory fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(com.foxconn.cnsbg.escort.R.string.mq_server_title);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(com.foxconn.cnsbg.escort.R.xml.setting_mq);

        // Add 'data and sync' preferences, and a corresponding header.
        fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(com.foxconn.cnsbg.escort.R.string.filename_title);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(com.foxconn.cnsbg.escort.R.xml.setting_file);

        fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(com.foxconn.cnsbg.escort.R.string.loc_title);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(com.foxconn.cnsbg.escort.R.xml.setting_location_task);

        fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(com.foxconn.cnsbg.escort.R.string.ble_title);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(com.foxconn.cnsbg.escort.R.xml.setting_ble_task);
        // Bind the summaries of EditText/List/Dialog/Ringtone preferences to
        // their values. When their values change, their summaries are updated
        // to reflect the new value, per the Android Design guidelines.
        SettingsUtil.setDefaultvalue(this);
        bindSummary();
        //bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
        //bindPreferenceSummaryToValue(findPreference("sync_frequency"));
    }

    public void bindSummary(){

        bindPreferenceSummaryToValue(findPreference("http_server_host"));
        bindPreferenceSummaryToValue(findPreference("http_server_port"));

        bindPreferenceSummaryToValue(findPreference("mq_server_host"));
        bindPreferenceSummaryToValue(findPreference("mq_server_port"));
        bindPreferenceSummaryToValue(findPreference("mq_keep_alive"));
        bindPreferenceSummaryToValue(findPreference("mq_connect_attempts"));
        bindPreferenceSummaryToValue(findPreference("mq_reconnect_attempts"));
        bindPreferenceSummaryToValue(findPreference("mq_reconnect_delay"));
        bindPreferenceSummaryToValue(findPreference("mq_reconnect_max_delay"));
        bindPreferenceSummaryToValue(findPreference("mq_send_max_timeout"));
        bindPreferenceSummaryToValue(findPreference("mq_recv_max_timeout"));
        bindPreferenceSummaryToValue(findPreference("mq_topic_gps_data"));
        bindPreferenceSummaryToValue(findPreference("mq_topic_ble_data"));
        bindPreferenceSummaryToValue(findPreference("mq_topic_cmd"));
        bindPreferenceSummaryToValue(findPreference("mq_topic_response"));
        bindPreferenceSummaryToValue(findPreference("mq_topic_alert"));

        bindPreferenceSummaryToValue(findPreference("app_db_name"));
        bindPreferenceSummaryToValue(findPreference("app_pref_name"));
        bindPreferenceSummaryToValue(findPreference("app_crash_log"));

        bindPreferenceSummaryToValue(findPreference("loc_task_run_interval"));
        bindPreferenceSummaryToValue(findPreference("loc_min_accuracy"));
        bindPreferenceSummaryToValue(findPreference("loc_update_min_distance"));
        bindPreferenceSummaryToValue(findPreference("loc_update_min_time"));
        bindPreferenceSummaryToValue(findPreference("loc_provide_check_time"));
        bindPreferenceSummaryToValue(findPreference("loc_update_pause_idle_time"));

        bindPreferenceSummaryToValue(findPreference("ble_task_run_interval"));
        bindPreferenceSummaryToValue(findPreference("ble_device_name_filter"));
        bindPreferenceSummaryToValue(findPreference("ble_rssi_threshold"));
        /* Set the flag to false before the last summary change */
        setConfig();
        bindPreferenceSummaryToValue(findPreference("ble_update_min_time"));
    }
    public void init(){

        SettingsUtil.Edit_key.put("http_server_host", 1);
        SettingsUtil.Edit_key.put("http_server_port", 2);

        SettingsUtil.Edit_key.put("mq_server_host", 3);
        SettingsUtil.Edit_key.put("mq_server_port", 4);
        SettingsUtil.Edit_key.put("mq_keep_alive", 5);
        SettingsUtil.Edit_key.put("mq_connect_attempts", 6);
        SettingsUtil.Edit_key.put("mq_reconnect_attempts", 7);
        SettingsUtil.Edit_key.put("mq_reconnect_delay", 8);
        SettingsUtil.Edit_key.put("mq_reconnect_max_delay", 9);
        SettingsUtil.Edit_key.put("mq_send_max_timeout", 10);
        SettingsUtil.Edit_key.put("mq_recv_max_timeout", 11);
        SettingsUtil.Edit_key.put("mq_topic_gps_data", 12);
        SettingsUtil.Edit_key.put("mq_topic_ble_data", 13);
        SettingsUtil.Edit_key.put("mq_topic_cmd", 14);
        SettingsUtil.Edit_key.put("mq_topic_response", 15);
        SettingsUtil.Edit_key.put("mq_topic_alert", 16);

        SettingsUtil.Edit_key.put("app_db_name", 17);
        SettingsUtil.Edit_key.put("app_pref_name", 18);
        SettingsUtil.Edit_key.put("app_crash_log", 19);

        SettingsUtil.Edit_key.put("loc_task_run_interval", 20);
        SettingsUtil.Edit_key.put("loc_min_accuracy", 21);
        SettingsUtil.Edit_key.put("loc_update_min_distance", 22);
        SettingsUtil.Edit_key.put("loc_update_min_time", 23);
        SettingsUtil.Edit_key.put("loc_provide_check_time", 24);
        SettingsUtil.Edit_key.put("loc_update_pause_idle_time", 25);

        SettingsUtil.Edit_key.put("ble_task_run_interval", 26);
        SettingsUtil.Edit_key.put("ble_device_name_filter", 27);
        SettingsUtil.Edit_key.put("ble_rssi_threshold", 28);
        SettingsUtil.Edit_key.put("ble_update_min_time", 29);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this) && !isSimplePreferences(this);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Determines whether the simplified settings UI should be shown. This is
     * true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device
     * doesn't have newer APIs like {@link PreferenceFragment}, or the device
     * doesn't have an extra-large screen. In these cases, a single-pane
     * "simplified" settings UI should be shown.
     */
    private static boolean isSimplePreferences(Context context) {
        return ALWAYS_SIMPLE_PREFS
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
                || !isXLargeTablet(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        if (!isSimplePreferences(this)) {
            loadHeadersFromResource(com.foxconn.cnsbg.escort.R.xml.pref_headers, target);
        }
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

             {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
                //preference.
                 if(stringValue != null)
                    SettingsUtil.setValue(preference.getKey(),stringValue);

                 if (First_bootup == false) {
                     restart_service();
                 }
               }
            return true;
        }
    };

    private void setConfig() {
        First_bootup = false;
    }
    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    public void restart_service(){

        Intent serviceIntent;
        serviceIntent = new Intent(this, MainService.class);

        if(SysUtil.isServiceRunning(this,MainService.class) == true)
            stopService(serviceIntent);

        startService(serviceIntent);

    }
}
