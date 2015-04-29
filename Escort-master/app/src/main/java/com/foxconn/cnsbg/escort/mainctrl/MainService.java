package com.foxconn.cnsbg.escort.mainctrl;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;

import com.foxconn.cnsbg.escort.common.SysUtil;

public class MainService extends Service {
    public class MainServiceBinder extends Binder {
        MainService getService() {
            return MainService.this;
        }
    }

    private final IBinder mBinder = new MainServiceBinder();

    private CtrlCenter mCtrlCenter;
    private BroadcastReceiver mConnReceiver;

    private void checkServerReachability(Context context, Intent intent) {
        final Context taskContext = context;
        final Intent taskIntent = intent;
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return SysUtil.isServerReachable(taskContext, taskIntent);
            }
        }.execute();
    }

    @Override
    public void onCreate() {
        mCtrlCenter = new CtrlCenter(this);
        mConnReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                checkServerReachability(context, intent);
            }
        };
        registerReceiver(mConnReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        startForeground(0, null);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCtrlCenter.cleanup();

        if (mConnReceiver != null)
            unregisterReceiver(mConnReceiver);

        stopForeground(true);

        sendBroadcast(new Intent("com.foxconn.cnsbg.escort.main.destroy"));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
