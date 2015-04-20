package com.foxconn.cnsbg.escort.subsys.usbserial;

public interface SerialCode {
    public static final String CMD_NOOP = "";

    public static final String CMD_SET_KEEP_UNLOCK = "kf\r\n";
    public static final String ACK_SET_KEEP_UNLOCK_OK = "A\r\n";
    public static final String ACK_SET_KEEP_UNLOCK_ERROR = "C\r\n";

    public static final String CMD_SET_UNLOCK = "of\r\n";
    public static final String ACK_SET_UNLOCK_OK = "B\r\n";
    public static final String ACK_SET_UNLOCK_ERROR = "D\r\n";

    public static final String CMD_SET_LOCK = "on\r\n";
    public static final String ACK_SET_LOCK_OK = "E\r\n";
    public static final String ACK_SET_LOCK_ERROR = "F\r\n";

    public static final String CMD_GET_LOCK_STATUS = "gl\r\n";
    public static final String ACK_LOCK_LOCKED = "\r\n";
    public static final String ACK_LOCK_UNLOCKED = "\r\n";

    public static final String CMD_GET_DOOR_STATUS = "gd\r\n";
    public static final String ACK_DOOR_OPEN = "G\r\n";
    public static final String ACK_DOOR_CLOSED = "\r\n";
    public static final String ACK_DOOR_HACK = "H\r\n";
}
