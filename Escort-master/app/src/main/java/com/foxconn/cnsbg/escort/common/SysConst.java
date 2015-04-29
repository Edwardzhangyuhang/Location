package com.foxconn.cnsbg.escort.common;

public class SysConst {
    ////////////////////////////////////////////////////////////////////////////////////////////////
    //HTTP Server
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static String HTTP_SERVER_HOST = "61.129.93.20";
    //public static final String HTTP_SERVER_INTERNAL_HOST = "10.116.57.136";
    public static int HTTP_SERVER_PORT = 80;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //MQ Server
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static String MQ_SERVER_HOST = "61.129.93.20";
    public static int MQ_SERVER_PORT = 1883;
    public static short MQ_KEEP_ALIVE = 20; //seconds
    public static long MQ_CONNECT_ATTEMPTS = -1L;
    public static long MQ_RECONNECT_ATTEMPTS = -1L;
    public static long MQ_RECONNECT_DELAY = 5 * 1000L; //milliseconds
    public static long MQ_RECONNECT_MAX_DELAY = 15 * 1000L; //milliseconds
    public static long MQ_SEND_MAX_TIMEOUT = 10 * 1000L; //milliseconds
    public static long MQ_RECV_MAX_TIMEOUT = 10 * 1000L; //milliseconds

    public static String MQ_TOPIC_GPS_DATA = "data/dev/";
    public static String MQ_TOPIC_BLE_DATA = "data/dev/";
    public static String MQ_TOPIC_COMMAND = "control/dev/";
    public static String MQ_TOPIC_RESPONSE = "response/dev/";
    public static String MQ_TOPIC_ALERT = "alerts/dev/";

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Filename
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static String APP_DB_NAME = "escort-db";
    public static String APP_PREF_NAME = "escort-perf";
    public static String APP_CRASH_LOG_FILE = "escort_crash_log.txt";

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Location Task Parameter
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static int LOC_TASK_RUN_INTERVAL = 1000;
    //change these to fine tune the power consumption
    //minimal accuracy
    public static float LOC_MIN_ACCURACY = 51.0F;
    //minimal update distance
    public static float LOC_UPDATE_MIN_DISTANCE = 5.0F;
    //minimal update interval
    public static long LOC_UPDATE_MIN_TIME = 5 * 1000L;
    //interval of try to switch provider
    public static long LOC_PROVIDER_CHECK_TIME = 90 * 1000L;
    //stop location tracking if no motion is detected
    public static long LOC_UPDATE_PAUSE_IDLE_TIME = 300 * 1000L;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //BLE Task Parameter
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static int BLE_TASK_RUN_INTERVAL = 1000;
    public static String BLE_DEVICE_NAME_FILTER = "InFocus";
    public static int BLE_RSSI_THRESHOLD = -80;
    public static long BLE_UPDATE_MIN_TIME = 5 * 1000L;
}
