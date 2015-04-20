package com.foxconn.cnsbg.escort.subsys.communication;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.foxconn.cnsbg.escort.BuildConfig;
import com.foxconn.cnsbg.escort.common.SysUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class ComPublishTask extends Thread {
    private final String TAG = ComPublishTask.class.getSimpleName();

    protected volatile int runInterval = 1000; //Default wait time of 1 sec
    protected volatile boolean requestShutdown = false;

    protected Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    protected Context mContext;
    protected ComMQ mComMQ;

    @Override
    public void run() {
        //stop this loop by calling the .stop() method is not recommended
        while (!requestShutdown) {
            //collect data
            String data = collectData();
            //Log.d("TAG", "CollectedData:" + data);

            if (BuildConfig.DEBUG && data != null)
                SysUtil.showToast(mContext, "MQ Tx:" + data, Toast.LENGTH_SHORT);

            //then send it to the server
            if (sendData(data)) {
                //send OK, then send cached data as well
                //sent data will be cleared as well
                //sendCachedData();
            } else {
                //send fail, then save collected data to cache
                //saveCachedData(data);
            }

            //check for location provider and sensor change periodically
            checkTask();

            //then relax
            try {
                Thread.sleep(runInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.w(TAG, "Note that this may not be a bad thing. It may have been stopped on purpose.");
            }
        }
    }

    public void requestShutdown() {
        requestShutdown = true;
    }

    protected abstract String collectData();
    protected abstract boolean sendData(String data);
    protected abstract boolean sendCachedData();
    protected abstract void saveCachedData(String date);
    protected abstract void checkTask();
}
