package com.foxconn.cnsbg.escort.mainctrl;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.foxconn.cnsbg.escort.subsys.cache.CacheDao;
import com.foxconn.cnsbg.escort.common.SysConst;
import com.foxconn.cnsbg.escort.subsys.communication.ComMQ;
import com.foxconn.cnsbg.escort.subsys.communication.ComPublishTask;
import com.foxconn.cnsbg.escort.subsys.communication.ComSubscribeTask;
import com.foxconn.cnsbg.escort.subsys.location.BLETask;
import com.foxconn.cnsbg.escort.subsys.location.GPSTask;
import com.foxconn.cnsbg.escort.subsys.usbserial.SerialCtrl;
import com.foxconn.cnsbg.escort.subsys.usbserial.SerialTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CtrlCenter {
    private final String TAG = CtrlCenter.class.getSimpleName();

    private static Context mContext;
    private static SharedPreferences mPrefs;
    private static CacheDao mDao;
    private static String UDID;

    private static SerialTask mSerialTask;
    private static ComSubscribeTask mCmdTask;
    private static ComPublishTask mGPSTask;
    private static ComPublishTask mBLETask;

    private static boolean isTrackingLocation = false;
    private static boolean isAppVisible = false;
    private static long motionDetectionTime = new Date().getTime();

    public static Context getContext() {
        return mContext;
    }

    public static SharedPreferences getPrefs() {
        return mPrefs;
    }

    public static CacheDao getDao() {
        return mDao;
    }

    public static String getUDID() {
        return UDID;
    }

    public static boolean isTrackingLocation() {
        return isTrackingLocation;
    }

    public static void setTrackingLocation(boolean track) {
        isTrackingLocation = track;
        return;
    }

    public static boolean isAppVisible() {
        return isAppVisible;
    }

    public static void setAppVisible(boolean visible) {
        isAppVisible = visible;
    }

    public static long getMotionDetectionTime() {
        return motionDetectionTime;
    }

    public static void setMotionDetectionTime(long time) {
        motionDetectionTime = time;
    }

    public CtrlCenter(Context context) {
        try {
            PackageInfo pinfo  = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            System.out.println("Version:" + pinfo.versionName + "-r" + pinfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, "Can't get version!");
        }

        mContext = context;
        //mPrefs is used for saving settings
        mPrefs =  context.getSharedPreferences(SysConst.APP_PREF_NAME, Context.MODE_PRIVATE);
        //setup database for cache
        mDao = new CacheDao(context, SysConst.APP_DB_NAME);
        //set UDID
        UDID = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        if (UDID == null) {
            Log.w(TAG, "Can't get UDID!");
        }

        isTrackingLocation = true;

        SerialCtrl sc = new SerialCtrl(context);
        ComMQ mq = new ComMQ(context);

        mSerialTask = new SerialTask(context, sc, mq);
        mCmdTask = new ComSubscribeTask(context, sc, mq);
        mGPSTask = new GPSTask(context, mq);
        mBLETask = new BLETask(context, mq);

        List<String> subscribes = new ArrayList<String>();
        subscribes.add(SysConst.MQ_TOPIC_COMMAND + UDID);
        subscribes.add("yg1"); //debug
        if (mq.init(subscribes))
            startTask();
    }

    public void cleanup() {
        stopTask();
        mDao.closeDao();
    }

    private void startTask() {
        mSerialTask.start();
        mCmdTask.start();
        mGPSTask.start();
        mBLETask.start();
    }

    private void stopTask() {
        boolean serialTaskAlive = mSerialTask.isAlive();
        boolean cmdTaskAlive = mCmdTask.isAlive();
        boolean gpsTaskAlive = mGPSTask.isAlive();
        boolean bleTaskAlive = mBLETask.isAlive();

        mSerialTask.requestShutdown();
        mCmdTask.requestShutdown();
        mGPSTask.requestShutdown();
        mBLETask.requestShutdown();

        while (serialTaskAlive || cmdTaskAlive || gpsTaskAlive || bleTaskAlive) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            serialTaskAlive = mSerialTask.isAlive();
            cmdTaskAlive = mCmdTask.isAlive();
            gpsTaskAlive = mGPSTask.isAlive();
            bleTaskAlive = mBLETask.isAlive();
        }
    }
}
