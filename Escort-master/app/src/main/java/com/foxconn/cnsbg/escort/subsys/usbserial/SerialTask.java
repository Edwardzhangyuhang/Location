package com.foxconn.cnsbg.escort.subsys.usbserial;

import android.content.Context;
import android.widget.Toast;

import com.foxconn.cnsbg.escort.mainctrl.CtrlCenter;
import com.foxconn.cnsbg.escort.common.SysConst;
import com.foxconn.cnsbg.escort.common.SysUtil;
import com.foxconn.cnsbg.escort.subsys.communication.ComMQ;

public class SerialTask extends Thread {
    private final String TAG = SerialTask.class.getSimpleName();

    protected int runInterval = 1000;
    protected boolean requestShutdown = false;

    private Context mContext;
    private SerialCtrl mSerialCtrl;
    private ComMQ mComMQ;
    private boolean mMCUConfigured = false;
    private int mStatus = 2;

    private static final String taskTopic = SysConst.MQ_TOPIC_ALERT + CtrlCenter.getUDID();

    public SerialTask(Context context, SerialCtrl sc, ComMQ mq) {
        mContext = context;
        mSerialCtrl = sc;
        mComMQ = mq;
    }

    @Override
    public void run() {
        while (!requestShutdown) {
            int status = mSerialCtrl.open();

            if (status != mStatus) {
                mStatus = status;
                SysUtil.showToast(mContext, "MCU status:" + status, Toast.LENGTH_LONG);
            }

            if (status == 2) {
                mComMQ.publish(taskTopic, "MCU detached!", runInterval);
                mMCUConfigured = false;
            } else if (status == 1) {
                if (!mMCUConfigured) {
                    mMCUConfigured = true;
                    mSerialCtrl.config(
                            SerialCtrl.BAUD_RATE_9600,
                            SerialCtrl.DATA_BITS_8,
                            SerialCtrl.STOP_BITS_1,
                            SerialCtrl.PARITY_NONE,
                            SerialCtrl.FLOW_CONTROL_NONE);

                    SysUtil.showToast(mContext, "MCU configured!", Toast.LENGTH_LONG);
                }
            }

            try {
                Thread.sleep(runInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void requestShutdown() {
        requestShutdown = true;
    }
}
