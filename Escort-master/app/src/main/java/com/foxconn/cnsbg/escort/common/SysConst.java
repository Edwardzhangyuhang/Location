package com.foxconn.cnsbg.escort.common;

public interface SysConst {
    ////////////////////////////////////////////////////////////////////////////////////////////////
    //HTTP Server
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static final String HTTP_SERVER_HOST = "61.129.93.20";
    //public static final String HTTP_SERVER_INTERNAL_HOST = "10.116.57.136";
    public static final int HTTP_SERVER_PORT = 80;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //MQ Server
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static final String MQ_SERVER_HOST = "61.129.93.20";
    public static final int MQ_SERVER_PORT = 1883;
    public static final short MQ_KEEP_ALIVE = 20; //seconds
    public static final long MQ_CONNECT_ATTEMPTS = -1L;
    public static final long MQ_RECONNECT_ATTEMPTS = -1L;
    public static final long MQ_RECONNECT_DELAY = 5 * 1000L; //milliseconds
    public static final long MQ_RECONNECT_MAX_DELAY = 15 * 1000L; //milliseconds
    public static final long MQ_SEND_MAX_TIMEOUT = 10 * 1000L; //milliseconds
    public static final long MQ_RECV_MAX_TIMEOUT = 10 * 1000L; //milliseconds

    public static final String MQ_TOPIC_GPS_DATA = "data/dev/";
    public static final String MQ_TOPIC_BLE_DATA = "data/dev/";
    public static final String MQ_TOPIC_COMMAND = "control/dev/";
    public static final String MQ_TOPIC_RESPONSE = "response/dev/";
    public static final String MQ_TOPIC_ALERT = "alerts/dev/";

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Filename
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static final String APP_DB_NAME = "escort-db";
    public static final String APP_PREF_NAME = "escort-perf";
    public static final String APP_CRASH_LOG_FILE = "escort_crash_log.txt";

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Location Task Parameter
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static final int LOC_TASK_RUN_INTERVAL = 1000;
    //change these to fine tune the power consumption
    //minimal accuracy
    public static final float LOC_MIN_ACCURACY = 51.0F;
    //minimal update distance
    public static final float LOC_UPDATE_MIN_DISTANCE = 5.0F;
    //minimal update interval
    public static final long LOC_UPDATE_MIN_TIME = 5 * 1000L;
    //interval of try to switch provider
    public static final long LOC_PROVIDER_CHECK_TIME = 90 * 1000L;
    //stop location tracking if no motion is detected
    public static final long LOC_UPDATE_PAUSE_IDLE_TIME = 300 * 1000L;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //BLE Task Parameter
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static final int BLE_TASK_RUN_INTERVAL = 1000;
    public static final String BLE_DEVICE_NAME_FILTER = "InFocus";
    public static final int BLE_RSSI_THRESHOLD = -80;
    public static final long BLE_UPDATE_MIN_TIME = 5 * 1000L;
}
