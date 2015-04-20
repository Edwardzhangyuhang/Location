package com.foxconn.cnsbg.escort.mainctrl;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;

import com.foxconn.cnsbg.escort.subsys.cache.CacheDao;
import com.foxconn.cnsbg.escort.common.CrashHandler;
import com.foxconn.cnsbg.escort.common.SysConst;
import com.foxconn.cnsbg.escort.subsys.usbserial.UARTLoopbackActivity;

public class MainActivity extends Activity {
    private final String TAG = MainActivity.class.getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static Context appContext;
    private static SharedPreferences mPrefs;
    private static CacheDao mDao;

    protected static MainService mBoundService = null;
    private static boolean isTrackingLocation = false;
    private static boolean isSyncingServer = false;
    private static boolean isAppVisible = false;

    private static ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            MainService mBoundService = ((MainService.MainServiceBinder)service).getService();
            setBoundService(mBoundService);
            if (mBoundService != null)
                syncWithService(mBoundService);
        }

        public void onServiceDisconnected(ComponentName className) {
            setBoundService(null);
        }
    };

    public static MainService getBoundService() {
        return mBoundService;
    }

    public static void setBoundService(MainService boundService) {
        mBoundService = boundService;
    }

    protected static void syncWithService(MainService service) {
        if (service == null)
            return;

        if (service.isTrackingLocation() != isTrackingLocation)
            service.setLocationTracking(isTrackingLocation);

        if (service.isAppVisible() != isAppVisible)
            service.setAppVisible(isAppVisible);
    }

    protected static Context getContext() {
        if (mBoundService == null)
            return appContext;
        else
            return mBoundService.getContext();
    }

    protected static SharedPreferences getPrefs() {
        if (mBoundService == null)
            return mPrefs;
        else
            return mBoundService.getPrefs();
    }

    protected static CacheDao getDao() {
        if (mBoundService == null)
            return mDao;
        else
            return mBoundService.getDao();
    }

    protected static void setLocationTracking(boolean track) {
        isTrackingLocation = track;

        if (mBoundService != null)
            mBoundService.setLocationTracking(track);
    }

    protected static boolean applyAccuracyLevel(int percentage) {
        if (mBoundService == null)
            return false;
        else {
            mBoundService.applyAccuracyLevel(percentage);
            return true;
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CrashHandler.getInstance().init(SysConst.APP_CRASH_LOG_PATH);
        startBindServiceThread();
    }

    private void startBindServiceThread() {
        Thread bindServiceThread = new Thread() {
            @Override
            public void run() {
                try {
                    Intent serviceIntent = new Intent(MainActivity.this, MainService.class);
                    startService(serviceIntent);
                    if (bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE)) {
                        //need to wait some seconds to get the instance
                        while (mBoundService == null)
                            Thread.sleep(1000);
                    } else {
                        appContext = getApplicationContext();
                        mPrefs = getSharedPreferences(SysConst.APP_PREF_NAME, Context.MODE_PRIVATE);
                        mDao = new CacheDao(appContext, SysConst.APP_DB_NAME);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    startActivity(new Intent(MainActivity.this, UARTLoopbackActivity.class));
                    finish();
                }
            }
        };
        bindServiceThread.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }
}
