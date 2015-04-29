package com.foxconn.cnsbg.escort.mainctrl;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.foxconn.cnsbg.escort.common.SysConst;
import com.foxconn.cnsbg.escort.subsys.cache.CacheDao;
import com.foxconn.cnsbg.escort.subsys.communication.ComCmdRxTask;
import com.foxconn.cnsbg.escort.subsys.communication.ComDataTxTask;
import com.foxconn.cnsbg.escort.subsys.communication.ComMQ;
import com.foxconn.cnsbg.escort.subsys.location.AccelTask;
import com.foxconn.cnsbg.escort.subsys.location.BLETask;
import com.foxconn.cnsbg.escort.subsys.location.LocTask;
import com.foxconn.cnsbg.escort.subsys.usbserial.SerialCtrl;
import com.foxconn.cnsbg.escort.subsys.usbserial.SerialMonitorTask;
import com.foxconn.cnsbg.escort.subsys.usbserial.SerialReadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CtrlCenter {
    private static final String TAG = CtrlCenter.class.getSimpleName();

    private static String UDID;

    private static CacheDao mDao;

    private static SerialMonitorTask mSerialMonitorTask;
    private static SerialReadTask mSerialReadTask;
    private static ComCmdRxTask mCmdTask;
    private static ComDataTxTask mAccelTask;
    private static ComDataTxTask mGPSTask;
    private static ComDataTxTask mBLETask;

    private static boolean isTrackingLocation = false;
    private static long motionDetectionTime = new Date().getTime();

    public static String getUDID() {
        return UDID;
    }

    public static CacheDao getDao() {
        return mDao;
    }

    public static boolean isTrackingLocation() {
        return isTrackingLocation;
    }

    public static void setTrackingLocation(boolean track) {
        isTrackingLocation = track;
    }

    public static long getMotionDetectionTime() {
        return motionDetectionTime;
    }

    public static void setMotionDetectionTime(long time) {
        motionDetectionTime = time;
    }

    public CtrlCenter(Context context) {
        UDID = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        if (UDID == null) {
            Log.e(TAG, "Can't get UDID, exiting...");
            return;
        }

        UDID = "0"; //debug

        //setup database for cache
        mDao = new CacheDao(context, SysConst.APP_DB_NAME);

        SerialCtrl sc = new SerialCtrl(context);
        ComMQ mq = new ComMQ(context);

        mSerialMonitorTask = new SerialMonitorTask(context, sc, mq);
        mSerialReadTask = new SerialReadTask(context, sc, mq);
        mCmdTask = new ComCmdRxTask(context, sc, mq);
        mAccelTask = new AccelTask(context);
        mGPSTask = new LocTask(context, mq);
        mBLETask = new BLETask(context, mq);

        List<String> subscribes = new ArrayList<String>();
        subscribes.add(SysConst.MQ_TOPIC_COMMAND + UDID);
        if (mq.init(subscribes)) {
            setTrackingLocation(true);
            startTask();
        }
    }

    public void cleanup() {
        stopTask();
        mDao.closeDao();
    }

    private void startTask() {
        mSerialMonitorTask.start();
        mSerialReadTask.start();
        mCmdTask.start();
        mAccelTask.start();
        mGPSTask.start();
        mBLETask.start();
    }

    private void stopTask() {
        mSerialMonitorTask.requestShutdown();
        mSerialReadTask.requestShutdown();
        mCmdTask.requestShutdown();
        mAccelTask.requestShutdown();
        mGPSTask.requestShutdown();
        mBLETask.requestShutdown();

        boolean monitorTaskAlive = mSerialMonitorTask.isAlive();
        boolean readTaskAlive = mSerialReadTask.isAlive();
        boolean cmdTaskAlive = mCmdTask.isAlive();
        boolean accelTaskAlive = mAccelTask.isAlive();
        boolean gpsTaskAlive = mGPSTask.isAlive();
        boolean bleTaskAlive = mBLETask.isAlive();

        while (monitorTaskAlive || readTaskAlive
                || cmdTaskAlive || accelTaskAlive
                || gpsTaskAlive || bleTaskAlive) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            monitorTaskAlive = mSerialMonitorTask.isAlive();
            readTaskAlive = mSerialReadTask.isAlive();
            cmdTaskAlive = mCmdTask.isAlive();
            accelTaskAlive = mAccelTask.isAlive();
            gpsTaskAlive = mGPSTask.isAlive();
            bleTaskAlive = mBLETask.isAlive();
        }
    }
}
