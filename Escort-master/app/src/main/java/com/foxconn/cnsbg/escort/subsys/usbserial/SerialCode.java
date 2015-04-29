package com.foxconn.cnsbg.escort.subsys.usbserial;

public class SerialCode {
    public static final String CMD_SET_UNLOCK = "kf";
    public static final String ACK_SET_UNLOCK_OK = "A";
    public static final String ACK_SET_UNLOCK_ERROR = "C";

    public static final String CMD_SET_UNLOCK_ONCE = "of";
    public static final String ACK_SET_UNLOCK_ONCE_OK = "B";
    public static final String ACK_SET_UNLOCK_ONCE_ERROR = "D";

    public static final String CMD_SET_LOCK = "on";
    public static final String ACK_SET_LOCK_OK = "E";
    public static final String ACK_SET_LOCK_ERROR = "F";
    public static final String ACK_SET_LOCK_ERROR_DOOR_OPEN = "G";
    public static final String ACK_SET_LOCK_ERROR_MAGNET_LEAVE = "L";

    public static final String CMD_GET_LOCK = "gl";
    public static final String ACK_LOCK_UNLOCKED = "I";
    public static final String ACK_LOCK_LOCKED = "J";
    public static final String ACK_LOCK_NG = "N";

    public static final String CMD_GET_DOOR = "gd";
    public static final String ACK_DOOR_OPEN = "Q";
    public static final String ACK_DOOR_CLOSED = "K";

    public static final String CMD_GET_MAGNET = "gm";
    public static final String ACK_MAGNET_EXIST = "P";
    public static final String ACK_MAGNET_LEAVE = "O";

    public static final String CMD_CLEAR_ALARM = "ca";
    public static final String ACK_CLEAR_ALARM_OK = "M";

    public static final String ACK_DOOR_ALARM = "H";

    public static final String MCU_HEARTBEAT_REQ = "R";
    public static final String MCU_HEARTBEAT_ACK = "ha";

    public enum CmdTarget {
        SELF,
        SERIAL
    }

    public enum AckSource {
        SET,
        GET,
        ALERT,
        HEARTBEAT
    }

    public enum AckType {
        LOCK,
        DOOR,
        MAGNET,
        OTHERS
    }

    public static class CmdCode {
        private CmdTarget mTarget;
        private String mCode;

        public CmdCode(CmdTarget target, String code) {
            mTarget = target;
            mCode = code;
        }

        public CmdTarget getTarget() {
            return mTarget;
        }

        public String getCode() {
            return mCode;
        }
    }

    public static CmdCode getCmdCode(String cmdStr) {
        CmdCode cmdCode;

        if (cmdStr.equals("kf")) {
            cmdCode = new CmdCode(CmdTarget.SERIAL, CMD_SET_UNLOCK);
        } else if (cmdStr.equals("of")) {
            cmdCode = new CmdCode(CmdTarget.SERIAL, CMD_SET_UNLOCK_ONCE);
        } else if (cmdStr.equals("on")) {
            cmdCode = new CmdCode(CmdTarget.SERIAL, CMD_SET_LOCK);
        } else if (cmdStr.equals("gl")) {
            cmdCode = new CmdCode(CmdTarget.SERIAL, CMD_GET_LOCK);
        } else if (cmdStr.equals("gd")) {
            cmdCode = new CmdCode(CmdTarget.SERIAL, CMD_GET_DOOR);
        } else if (cmdStr.equals("gm")) {
            cmdCode = new CmdCode(CmdTarget.SERIAL, CMD_GET_MAGNET);
        } else if (cmdStr.equals("ca")) {
            cmdCode = new CmdCode(CmdTarget.SERIAL, CMD_CLEAR_ALARM);
        } else if (cmdStr.startsWith("#")) { //backdoor for debug
            cmdCode = new CmdCode(CmdTarget.SERIAL, cmdStr.substring(1));
        } else {
            cmdCode = null;
        }

        return cmdCode;
    }

    public static class AckResp {
        private AckSource mAckSource;
        private AckType mAckType;
        private String mCmd;
        private String mResult;
        private String mInfo;

        public AckResp(AckSource source, AckType type, String cmd, String result, String info) {
            mAckSource = source;
            mAckType = type;
            mCmd = cmd;
            mResult = result;
            mInfo = info;
        }

        public AckSource getAckSource() {
            return mAckSource;
        }

        public AckType getAckType() {
            return mAckType;
        }

        public String getCmd() {
            return mCmd;
        }

        public String getResult() {
            return mResult;
        }

        public String getInfo() {
            return mInfo;
        }
    }

    public static AckResp getAckResp(String ackCode) {
        AckResp resp;

        if (ackCode.equals(ACK_SET_UNLOCK_OK)) {
            resp = new AckResp(AckSource.SET, AckType.LOCK, CMD_SET_UNLOCK, "success", "unlock ok");
        } else if (ackCode.equals(ACK_SET_UNLOCK_ERROR)) {
            resp = new AckResp(AckSource.SET, AckType.LOCK, CMD_SET_UNLOCK, "fail", "unlock error");
        } else if (ackCode.equals(ACK_SET_UNLOCK_ONCE_OK)) {
            resp = new AckResp(AckSource.SET, AckType.LOCK, CMD_SET_UNLOCK_ONCE, "success", "unlock once ok");
        } else if (ackCode.equals(ACK_SET_UNLOCK_ONCE_ERROR)) {
            resp = new AckResp(AckSource.SET, AckType.LOCK, CMD_SET_UNLOCK_ONCE, "fail", "unlock once error");
        } else if (ackCode.equals(ACK_SET_LOCK_OK)) {
            resp = new AckResp(AckSource.SET, AckType.LOCK, CMD_SET_LOCK, "success", "lock ok");
        } else if (ackCode.equals(ACK_SET_LOCK_ERROR)) {
            resp = new AckResp(AckSource.SET, AckType.LOCK, CMD_SET_LOCK, "fail", "lock error");
        } else if (ackCode.equals(ACK_SET_LOCK_ERROR_DOOR_OPEN)) {
            resp = new AckResp(AckSource.SET, AckType.DOOR, CMD_SET_LOCK, "fail", "door is open");
        } else if (ackCode.equals(ACK_SET_LOCK_ERROR_MAGNET_LEAVE)) {
            resp = new AckResp(AckSource.SET, AckType.MAGNET, CMD_SET_LOCK, "fail", "magnet is not detected");
        } else if (ackCode.equals(ACK_LOCK_LOCKED)) {
            resp = new AckResp(AckSource.GET, AckType.LOCK, CMD_GET_LOCK, "success", "locked");
        } else if (ackCode.equals(ACK_LOCK_UNLOCKED)) {
            resp = new AckResp(AckSource.GET, AckType.LOCK, CMD_GET_LOCK, "success", "unlocked");
        } else if (ackCode.equals(ACK_LOCK_NG)) {
            resp = new AckResp(AckSource.ALERT, AckType.LOCK, CMD_GET_LOCK, "success", "status abnormal");
        } else if (ackCode.equals(ACK_DOOR_OPEN)) {
            resp = new AckResp(AckSource.GET, AckType.DOOR, CMD_GET_DOOR, "success", "open");
        } else if (ackCode.equals(ACK_DOOR_CLOSED)) {
            resp = new AckResp(AckSource.GET, AckType.DOOR, CMD_GET_DOOR, "success", "closed");
        } else if (ackCode.equals(ACK_MAGNET_EXIST)) {
            resp = new AckResp(AckSource.GET, AckType.MAGNET, CMD_GET_MAGNET, "success", "magnet is detected");
        } else if (ackCode.equals(ACK_MAGNET_LEAVE)) {
            resp = new AckResp(AckSource.GET, AckType.MAGNET, CMD_GET_MAGNET, "success", "magnet is not detected");
        } else if (ackCode.equals(ACK_CLEAR_ALARM_OK)) {
            resp = new AckResp(AckSource.SET, AckType.OTHERS, CMD_CLEAR_ALARM, "success", "clear alarm ok");
        } else if (ackCode.equals(ACK_DOOR_ALARM)) {
            resp = new AckResp(AckSource.ALERT, AckType.DOOR, "", "success", "door is hacked");
        } else if (ackCode.equals(MCU_HEARTBEAT_REQ)) {
            resp = new AckResp(AckSource.HEARTBEAT, AckType.OTHERS, "", "success", "heartbeat");
        } else {
            resp = null;
        }

        return resp;
    }

    private static int mCmdId = 0;

    public static void setCmdId(int id) {
        mCmdId = id;
    }

    public static int getCmdId() {
        return mCmdId;
    }
}
