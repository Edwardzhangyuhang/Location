package com.foxconn.cnsbg.escort.subsys.location;

import android.content.Context;

import com.foxconn.cnsbg.escort.subsys.communication.ComMQ;
import com.foxconn.cnsbg.escort.subsys.communication.ComPublishTask;

public class BLETask extends ComPublishTask {

    public BLETask(Context context, ComMQ mq) {
        mContext = context;
        mComMQ = mq;
    }

    @Override
    protected String collectData() {
        return null;
    }

    @Override
    protected boolean sendData(String data) {
        return false;
    }

    @Override
    protected boolean sendCachedData() {
        return true;
    }

    @Override
    protected void saveCachedData(String date) {

    }

    @Override
    protected void checkTask() {

    }
}
