package com.foxconn.cnsbg.escort.subsys.usbserial;

public class SerialStatus {
    private static String mLockStatus = "";
    private static String mDoorStatus = "";
    private static String mMagnetStatus = "";

    public synchronized static void setLockStatus(String status) {
        mLockStatus = status;
    }

    public synchronized static String getLockStatus() {
        return mLockStatus;
    }

    public synchronized static String getDoorStatus() {
        return mDoorStatus;
    }

    public synchronized static void setDoorStatus(String status) {
        mDoorStatus = status;
    }

    public synchronized static String getMagnetStatus() {
        return mMagnetStatus;
    }

    public synchronized static void setMagnetStatus(String status) {
        mMagnetStatus = status;
    }
}
