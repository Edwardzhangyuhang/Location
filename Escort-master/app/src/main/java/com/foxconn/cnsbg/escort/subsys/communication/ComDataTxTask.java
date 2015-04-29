package com.foxconn.cnsbg.escort.subsys.communication;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class ComDataTxTask extends Thread {
    private static final String TAG = ComDataTxTask.class.getSimpleName();

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

            //then send it to the server
            if (sendData(data)) {
                //send OK, then send cached data as well
                //sent data will be cleared as well
                sendCachedData();
            } else {
                //send fail, then save collected data to cache
                saveCachedData(data);
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
    protected abstract boolean sendData(String dataStr);
    protected abstract boolean sendCachedData();
    protected abstract void saveCachedData(String dataStr);
    protected abstract void checkTask();
}
