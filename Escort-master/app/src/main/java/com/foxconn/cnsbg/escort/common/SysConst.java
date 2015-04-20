package com.foxconn.cnsbg.escort.common;

public interface SysConst {
    ////////////////////////////////////////////////////////////////////////////////////////////////
    //HTTP Server
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static final String HTTP_SERVER_HOST = "61.129.93.20";
    public static final String HTTP_SERVER_INTERNAL_HOST = "10.116.57.136";
    public static final int HTTP_SERVER_PORT = 80;
    public static final String HTTP_SESSION_KEY = "PHPSESSID";

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //iBeacon Service
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static final String IBEACON_SERVICE_URL = "http://61.129.93.20/xtalball/Beacon/Services/";

    //APIs
    public static final String API_REGISTER = IBEACON_SERVICE_URL + "register";
    public static final String API_REGISTER_BY_PHONE = IBEACON_SERVICE_URL + "registerByPhone";
    public static final String API_LOGIN = IBEACON_SERVICE_URL + "login";
    public static final String API_CHANGE_PASSWORD = IBEACON_SERVICE_URL + "changepassword";
    public static final String API_CHANGE_INFO = IBEACON_SERVICE_URL + "changeuserinfo";
    public static final String API_QUERY_INFO = IBEACON_SERVICE_URL + "queryuserinfo";
    public static final String API_QUERY_MAP = IBEACON_SERVICE_URL + "querymap";
    public static final String API_QUERY_MAP_BY_MAPID = IBEACON_SERVICE_URL + "querymapbymapid";
    public static final String API_QUERY_POSITION = IBEACON_SERVICE_URL + "queryposition";
    public static final String API_QUERY_POSITION_BY_MAC = IBEACON_SERVICE_URL + "querypositionbymac";
    public static final String API_QUERY_POSITION_BY_UID = IBEACON_SERVICE_URL + "querypositionbyuid";
    public static final String API_QUERY_POSITION_BY_NAME = IBEACON_SERVICE_URL + "querypositionbyname";
    public static final String API_QUERY_DEVICES_INFO = IBEACON_SERVICE_URL + "querydevicesbymapid";
    public static final String API_SEND_VERTIFICATION_CODE = IBEACON_SERVICE_URL + "getCaptcha";
    public static final String API_POST_BATCH_TRACK_DATA = IBEACON_SERVICE_URL + "posttrack";
    public static final String API_POST_SINGLE_TRACK_DATA = IBEACON_SERVICE_URL + "postdata";

    // arguments
    public static final String ARG_USER_INFO = "userinfo";
    public static final String ARG_USER_ID = "uid";
    public static final String ARG_USER_PWD = "password";
    public static final String ARG_USER_NEW_PWD = "newpassword";
    public static final String ARG_USER_NAME = "name";
    public static final String ARG_USER_GENDER = "gender";
    public static final String ARG_USER_PHONE = "phone";
    public static final String ARG_USER_EMAIL = "email";
    public static final String ARG_POSITION = "position";
    public static final String ARG_POSITION_X = "x";
    public static final String ARG_POSITION_Y = "y";
    public static final String ARG_POSITION_MSG = "message";
    public static final String ARG_MAP_ID = "mapid";
    public static final String ARG_MAP_MSG = "map_msg";
    public static final String ARG_MAP_SITE_ID = "siteid";
    public static final String ARG_MAP_VERSION = "version";
    public static final String ARG_MAP_DESCRIPTION = "description";
    public static final String ARG_MAP_URL = "mapurl";
    public static final String ARG_LOCAL_TIME = "localtime";
    public static final String ARG_DEV_MAC = "devicemac";
    
    public static final String ARG_REQ_RESULT = "result";
    public static final String ARG_ERROR_CODE = "error_code";

    // messages
    public static final String MSG_PASS = "pass";
    public static final String MSG_FAIL_DESC = " fail, ";
    public static final String MSG_REG_OK = "Register success,please use the account to login!";
    public static final String MSG_CONN_ERROR = "Connection Error, please check your Internet connection";

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //MQ Server
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static final String MQ_SERVER_HOST = "61.129.93.20";
    public static final String MQ_SERVER_INTERNAL_HOST = "10.116.57.136";
    public static final int MQ_SERVER_PORT = 1883;
    public static final short MQ_KEEP_ALIVE = 10; /* seconds */
    public static final long MQ_CONNECT_ATTEMPTS = -1;
    public static final long MQ_RECONNECT_ATTEMPTS = -1;
    public static final long MQ_RECONNECT_DELAY = 5000; /* milliseconds */
    public static final long MQ_RECONNECT_MAX_DELAY = 15000; /* milliseconds */
    public static final long MQ_SEND_MAX_TIMEOUT = 10000; /* milliseconds */
    public static final long MQ_RECV_MAX_TIMEOUT = 10000; /* milliseconds */

    public static final String MQ_TOPIC_GPS_DATA = "data/dev/";
    public static final String MQ_TOPIC_BLE_DATA = "data/dev/";
    public static final String MQ_TOPIC_COMMAND = "control/dev/";
    public static final String MQ_TOPIC_RESPONSE = "response/dev/";
    public static final String MQ_TOPIC_ALERT = "alerts/dev/";

    //Filenames
    public static final String APP_DB_NAME = "escort-db";
    public static final String APP_PREF_NAME = "escort-perf";
    public static final String APP_CRASH_LOG_PATH = "/mnt/sdcard/escort_crash_log.txt";

    //Error Strings
    public static final String MV_ERROR_STRING_SERVER_ERROR = "Server returns error...";
    public static final String MV_ERROR_STRING_FAIL_CONNECT = "Fail to connect to Server...";

    //Settings
    public static final String MV_SETTING_ACCEPTED_CONSENT = "MV_SETTING_ACCEPTED_CONSENT";
    public static final String MV_SETTING_GPS_ACCURACY_LEVEL = "MV_SETTING_GPS_ACCURACY_LEVEL";
    public static final String MV_SETTING_HIDDEN_MODE = "MV_SETTING_HIDDEN_MODE";

    //Keys to pass arguments
    public static final String MV_ARG_USERNAME = "MV_ARG_USERNAME";
    public static final String MV_ARG_ERROR_STRING = "MV_ARG_ERROR_STRING";
    public static final String MV_ARG_FRIEND_NAME = "MV_ARG_FRIEND_NAME";
    public static final String MV_ARG_HIDDEN = "MV_ARG_HIDDEN";
}
