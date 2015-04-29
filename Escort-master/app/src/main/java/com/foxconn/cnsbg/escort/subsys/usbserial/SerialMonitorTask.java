package com.foxconn.cnsbg.escort.subsys.usbserial;

import android.content.Context;
import android.widget.Toast;

import com.foxconn.cnsbg.escort.common.SysUtil;
import com.foxconn.cnsbg.escort.subsys.communication.ComMQ;

public class SerialMonitorTask extends Thread {
    private static final String TAG = SerialMonitorTask.class.getSimpleName();

    private int runInterval = 1000;
    protected boolean requestShutdown = false;

    private Context mContext;
    private SerialCtrl mSerialCtrl;
    private ComMQ mComMQ;
    private boolean mMCUConfigured = false;
    private int mStatus = 2;

    public SerialMonitorTask(Context context, SerialCtrl sc, ComMQ mq) {
        mContext = context;
        mSerialCtrl = sc;
        mComMQ = mq;
    }

    @Override
    public void run() {
        while (!requestShutdown) {
            int status = mSerialCtrl.open();

            if (mStatus != status) {
                mStatus = status;
                SysUtil.showToast(mContext, "MCU status:" + status, Toast.LENGTH_SHORT);

                if (status == 2) {
                    mMCUConfigured = false;

                    SerialStatus.setLockStatus("");
                    SerialStatus.setDoorStatus("");
                    SerialStatus.setMagnetStatus("");
                } else if (status == 1) {
                    if (!mMCUConfigured) {
                        mSerialCtrl.config(
                                SerialCtrl.BAUD_RATE_9600,
                                SerialCtrl.DATA_BITS_8,
                                SerialCtrl.STOP_BITS_1,
                                SerialCtrl.PARITY_NONE,
                                SerialCtrl.FLOW_CONTROL_NONE);

                        SysUtil.showToast(mContext, "MCU configured!", Toast.LENGTH_SHORT);
                        mMCUConfigured = true;
                    }
                }
            }

            if (status == 1) {
                //trigger serial read task to set status
                mSerialCtrl.write(SerialCode.CMD_GET_LOCK + "\r\n");
                mSerialCtrl.write(SerialCode.CMD_GET_DOOR + "\r\n");
                mSerialCtrl.write(SerialCode.CMD_GET_MAGNET + "\r\n");
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
