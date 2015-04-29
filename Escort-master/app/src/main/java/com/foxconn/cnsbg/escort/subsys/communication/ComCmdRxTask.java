package com.foxconn.cnsbg.escort.subsys.communication;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.foxconn.cnsbg.escort.common.SysConst;
import com.foxconn.cnsbg.escort.common.SysUtil;
import com.foxconn.cnsbg.escort.mainctrl.CtrlCenter;
import com.foxconn.cnsbg.escort.subsys.usbserial.SerialCode;
import com.foxconn.cnsbg.escort.subsys.usbserial.SerialCtrl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

public final class ComCmdRxTask extends Thread {
    private static final String TAG = ComCmdRxTask.class.getSimpleName();

    private boolean requestShutdown = false;

    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    private Context mContext;
    private SerialCtrl mSerialCtrl;
    private ComMQ mComMQ;
    private boolean mMQReady = false;

    public ComCmdRxTask(Context context, SerialCtrl sc, ComMQ mq) {
        mContext = context;
        mSerialCtrl = sc;
        mComMQ = mq;
    }

    @Override
    public void run() {
        while (!requestShutdown) {
            boolean ready = mComMQ.isConnected();
            if (mMQReady != ready) {
                mMQReady = ready;
                SysUtil.showToast(mContext, "MQ Ready:" + ready, Toast.LENGTH_SHORT);
            }
            String msg = mComMQ.receive(SysConst.MQ_RECV_MAX_TIMEOUT);
            String cmd = handleMessage(msg);
            handleCmd(cmd);
        }
    }

    public void requestShutdown() {
        requestShutdown = true;
    }

    private String handleMessage(String msg) {
        if (msg == null)
            return null;

        try {
            CmdCtrlMsg ctrlMsg = gson.fromJson(msg, CmdCtrlMsg.class);
            if (!ctrlMsg.device_id.equals(CtrlCenter.getUDID()))
                return null;

            SerialCode.setCmdId(ctrlMsg.cmd_id);
            return ctrlMsg.cmd;
        } catch (JsonParseException e) {
            Log.w(TAG + ":handleMessage", "JsonParseException");
        } catch (NullPointerException e) {
            Log.w(TAG + ":handleMessage", "NullPointerException");
        }

        SerialCode.setCmdId(0);
        return msg;
    }

    private boolean handleCmd(String cmd) {
        if (cmd == null)
            return false;

        SerialCode.CmdCode cmdCode = SerialCode.getCmdCode(cmd);
        if (cmdCode == null)
            return false;

        if (cmdCode.getTarget() == SerialCode.CmdTarget.SERIAL)
            mSerialCtrl.write(cmdCode.getCode() + "\r\n");

        return true;
    }
}
