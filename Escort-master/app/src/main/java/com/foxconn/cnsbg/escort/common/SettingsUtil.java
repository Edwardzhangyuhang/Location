package com.foxconn.cnsbg.escort.common;

import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;

import java.util.HashMap;

/**
 * Created by Edward on 2015/4/28.
 */
public class SettingsUtil {

    public static HashMap<String,Integer> Edit_key = new HashMap<String,Integer>();

    public static void setDefaultvalue(PreferenceActivity mContext){

        EditTextPreference mEditText = (EditTextPreference) mContext.findPreference("http_server_host");
        if(mEditText.getText() == "" || mEditText.getText() == null)
            mEditText.setText(SysConst.HTTP_SERVER_HOST);


        mEditText = (EditTextPreference) mContext.findPreference("http_server_port");
        if(mEditText.getText() == "" || mEditText.getText() == null) {
            mEditText.setText("" + SysConst.HTTP_SERVER_PORT);
        }

        mEditText = (EditTextPreference) mContext.findPreference("mq_server_host");
        if(mEditText.getText() == "" || mEditText.getText() == null) {
            mEditText.setText(SysConst.MQ_SERVER_HOST);
        }

        mEditText = (EditTextPreference) mContext.findPreference("mq_server_port");
        if(mEditText.getText() == "" || mEditText.getText() == null) {
            mEditText.setText("" + SysConst.MQ_SERVER_PORT);
        }

        mEditText = (EditTextPreference) mContext.findPreference("mq_keep_alive");
        if(mEditText.getText() == "" || mEditText.getText() == null) {
            mEditText.setText("" + SysConst.MQ_KEEP_ALIVE);
        }

        mEditText = (EditTextPreference) mContext.findPreference("mq_connect_attempts");
        if(mEditText.getText() == "" || mEditText.getText() == null) {
            mEditText.setText("" + SysConst.MQ_CONNECT_ATTEMPTS);
        }

        mEditText = (EditTextPreference) mContext.findPreference("mq_reconnect_attempts");
        if(mEditText.getText() == "" || mEditText.getText() == null) {
            mEditText.setText("" + SysConst.MQ_RECONNECT_ATTEMPTS);
        }

        mEditText = (EditTextPreference) mContext.findPreference("mq_reconnect_delay");
        if(mEditText.getText() == "" || mEditText.getText() == null) {
            mEditText.setText("" + SysConst.MQ_RECONNECT_DELAY);
        }

        mEditText = (EditTextPreference) mContext.findPreference("mq_reconnect_max_delay");
        if(mEditText.getText() == "" || mEditText.getText() == null) {
            mEditText.setText("" + SysConst.MQ_RECONNECT_MAX_DELAY);
        }

        mEditText = (EditTextPreference) mContext.findPreference("mq_send_max_timeout");
        if(mEditText.getText() == "" || mEditText.getText() == null) {
            mEditText.setText("" + SysConst.MQ_SEND_MAX_TIMEOUT);
        }

        mEditText = (EditTextPreference) mContext.findPreference("mq_recv_max_timeout");
        if(mEditText.getText() == "" || mEditText.getText() == null) {
            mEditText.setText("" + SysConst.MQ_RECV_MAX_TIMEOUT);
        }

        mEditText = (EditTextPreference) mContext.findPreference("mq_topic_gps_data");
        if(mEditText.getText() == "" || mEditText.getText() == null) {
            mEditText.setText(SysConst.MQ_TOPIC_GPS_DATA);
        }

        mEditText = (EditTextPreference) mContext.findPreference("mq_topic_ble_data");
        if(mEditText.getText() == "" || mEditText.getText() == null) {
            mEditText.setText(SysConst.MQ_TOPIC_BLE_DATA);
        }

        mEditText = (EditTextPreference) mContext.findPreference("mq_topic_cmd");
        if(mEditText.getText() == "" || mEditText.getText() == null) {
            mEditText.setText(SysConst.MQ_TOPIC_COMMAND);
        }

        mEditText = (EditTextPreference) mContext.findPreference("mq_topic_response");
        if(mEditText.getText() == "" || mEditText.getText() == null) {
            mEditText.setText(SysConst.MQ_TOPIC_RESPONSE);
        }

        mEditText = (EditTextPreference) mContext.findPreference("mq_topic_alert");
        if(mEditText.getText() == "" || mEditText.getText() == null) {
            mEditText.setText(SysConst.MQ_TOPIC_ALERT);
        }

        mEditText = (EditTextPreference) mContext.findPreference("app_db_name");
        if(mEditText.getText() == "" || mEditText.getText() == null) {
            mEditText.setText(SysConst.APP_DB_NAME);
        }

        mEditText = (EditTextPreference) mContext.findPreference("app_pref_name");
        if(mEditText.getText() == "" || mEditText.getText() == null) {
            mEditText.setText(SysConst.APP_PREF_NAME);
        }

        mEditText = (EditTextPreference) mContext.findPreference("app_crash_log");
        if(mEditText.getText() == "" || mEditText.getText() == null) {
            mEditText.setText(SysConst.APP_CRASH_LOG_FILE);
        }

        mEditText = (EditTextPreference) mContext.findPreference("loc_task_run_interval");
        if(mEditText.getText() == "" || mEditText.getText() == null) {
            mEditText.setText("" + SysConst.LOC_TASK_RUN_INTERVAL);
        }

        mEditText = (EditTextPreference) mContext.findPreference("loc_min_accuracy");
        if(mEditText.getText() == "" || mEditText.getText() == null) {
            mEditText.setText("" + SysConst.LOC_MIN_ACCURACY);
        }

        mEditText = (EditTextPreference) mContext.findPreference("loc_update_min_distance");
        if(mEditText.getText() == "" || mEditText.getText() == null) {
            mEditText.setText("" + SysConst.LOC_UPDATE_MIN_DISTANCE);
        }

        mEditText = (EditTextPreference) mContext.findPreference("loc_update_min_time");
        if(mEditText.getText() == "" || mEditText.getText() == null) {
            mEditText.setText("" + SysConst.LOC_UPDATE_MIN_TIME);
        }

        mEditText = (EditTextPreference) mContext.findPreference("loc_provide_check_time");
        if(mEditText.getText() == "" || mEditText.getText() == null) {
            mEditText.setText("" + SysConst.LOC_PROVIDER_CHECK_TIME);
        }

        mEditText = (EditTextPreference) mContext.findPreference("loc_update_pause_idle_time");
        if(mEditText.getText() == "" || mEditText.getText() == null) {
            mEditText.setText("" + SysConst.LOC_UPDATE_PAUSE_IDLE_TIME);
        }

        mEditText = (EditTextPreference) mContext.findPreference("ble_task_run_interval");
        if(mEditText.getText() == "" || mEditText.getText() == null) {
            mEditText.setText("" + SysConst.BLE_TASK_RUN_INTERVAL);
        }

        mEditText = (EditTextPreference) mContext.findPreference("ble_device_name_filter");
        if(mEditText.getText() == "" || mEditText.getText() == null) {
            mEditText.setText(SysConst.BLE_DEVICE_NAME_FILTER);
        }

        mEditText = (EditTextPreference) mContext.findPreference("ble_rssi_threshold");
        if(mEditText.getText() == "" || mEditText.getText() == null) {
            mEditText.setText("" + SysConst.BLE_RSSI_THRESHOLD);
        }

        mEditText = (EditTextPreference) mContext.findPreference("ble_update_min_time");
        if(mEditText.getText() == "" || mEditText.getText() == null) {
            mEditText.setText("" + SysConst.BLE_UPDATE_MIN_TIME);
        }
    }

    public static int key_map(HashMap<String,Integer> map,String key){
        int map_key = -1;

        map_key = map.get(key);

        return map_key;
    }

    public static void setValue(String key, String value){

        int key_id = -1;
        key_id = key_map(Edit_key,key);
        switch (key_id){
            case 1:
                SysConst.HTTP_SERVER_HOST = value;
                break;
            case 2:
                SysConst.HTTP_SERVER_PORT = Integer.parseInt(value);
                break;
            case 3:
                SysConst.MQ_SERVER_HOST = value;
                break;
            case 4:
                SysConst.MQ_SERVER_PORT = Integer.parseInt(value);
                break;
            case 5:
                SysConst.MQ_KEEP_ALIVE = Short.parseShort(value);
                break;
            case 6:
                SysConst.MQ_CONNECT_ATTEMPTS = Long.parseLong(value);
                break;
            case 7:
                SysConst.MQ_RECONNECT_ATTEMPTS = Long.parseLong(value);
                break;
            case 8:
                SysConst.MQ_RECONNECT_DELAY = Long.parseLong(value);
                break;
            case 9:
                SysConst.MQ_RECONNECT_MAX_DELAY = Long.parseLong(value);
                break;
            case 10:
                SysConst.MQ_SEND_MAX_TIMEOUT = Long.parseLong(value);
                break;
            case 11:
                SysConst.MQ_RECV_MAX_TIMEOUT = Long.parseLong(value);
                break;
            case 12:
                SysConst.MQ_TOPIC_GPS_DATA = value;
                break;
            case 13:
                SysConst.MQ_TOPIC_BLE_DATA = value;
                break;
            case 14:
                SysConst.MQ_TOPIC_COMMAND = value;
                break;
            case 15:
                SysConst.MQ_TOPIC_RESPONSE = value;
                break;
            case 16:
                SysConst.MQ_TOPIC_ALERT = value;
                break;
            case 17:
                SysConst.APP_DB_NAME = value;
                break;
            case 18:
                SysConst.APP_PREF_NAME = value;
                break;
            case 19:
                SysConst.APP_CRASH_LOG_FILE = value;
                break;
            case 20:
                SysConst.LOC_TASK_RUN_INTERVAL = Integer.parseInt(value);
                break;
            case 21:
                SysConst.LOC_MIN_ACCURACY = Float.parseFloat(value);
                break;
            case 22:
                SysConst.LOC_UPDATE_MIN_DISTANCE = Float.parseFloat(value);
                break;
            case 23:
                SysConst.LOC_UPDATE_MIN_TIME = Long.parseLong(value);
                break;
            case 24:
                SysConst.LOC_PROVIDER_CHECK_TIME = Long.parseLong(value);
                break;
            case 25:
                SysConst.LOC_UPDATE_PAUSE_IDLE_TIME = Long.parseLong(value);
                break;
            case 26:
                SysConst.BLE_TASK_RUN_INTERVAL = Integer.parseInt(value);
                break;
            case 27:
                SysConst.BLE_DEVICE_NAME_FILTER = value;
                break;
            case 284:
                SysConst.BLE_RSSI_THRESHOLD = Integer.parseInt(value);
                break;
            case 29:
                SysConst.BLE_UPDATE_MIN_TIME = Long.parseLong(value);
                break;
            default:
                System.out.println("Do not have this case");
                break;
        }

    }
}
