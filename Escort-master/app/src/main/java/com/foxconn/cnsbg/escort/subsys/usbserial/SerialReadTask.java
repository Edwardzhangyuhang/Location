package com.foxconn.cnsbg.escort.subsys.usbserial;

import android.content.Context;

import com.foxconn.cnsbg.escort.common.SysConst;
import com.foxconn.cnsbg.escort.mainctrl.CtrlCenter;
import com.foxconn.cnsbg.escort.subsys.communication.AlertMsg;
import com.foxconn.cnsbg.escort.subsys.communication.CmdRespMsg;
import com.foxconn.cnsbg.escort.subsys.communication.ComMQ;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

public final class SerialReadTask extends Thread {
    private static final String TAG = SerialReadTask.class.getSimpleName();
    private static final int SERIAL_READ_BUF_SIZE = 1024;

    protected int runInterval = 500;
    protected boolean requestShutdown = false;

    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    private Context mContext;
    private SerialCtrl mSerialCtrl;
    private ComMQ mComMQ;
    private byte[] mAckBuffer;

    private static final String alertTopic = SysConst.MQ_TOPIC_ALERT + CtrlCenter.getUDID();
    private static final String respTopic = SysConst.MQ_TOPIC_RESPONSE + CtrlCenter.getUDID();

    public SerialReadTask(Context context, SerialCtrl sc, ComMQ mq) {
        mContext = context;
        mSerialCtrl = sc;
        mComMQ = mq;
        mAckBuffer = new byte[SERIAL_READ_BUF_SIZE];
    }

    @Override
    public void run() {
        while (!requestShutdown) {
            int num = mSerialCtrl.read(mAckBuffer, mAckBuffer.length);
            if (num > 0)
                parseAck(mAckBuffer, num);

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

    private void parseAck(byte[] ackBytes, int num) {
        String ackStr = new String(ackBytes, 0, num);
        String acks[] = ackStr.split("\r\n");

        for (String ack : acks) {
            if (ack.length() == 0)
                continue;

            //SysUtil.showToast(mContext, ack, Toast.LENGTH_SHORT);

            String ackCode = ack.substring(0, 1);
            SerialCode.AckResp resp = SerialCode.getAckResp(ackCode);

            if (resp == null)
                continue;

            switch (resp.getAckSource()) {
                case ALERT:
                    handleAlert(resp);
                    break;
                case SET:
                    handleCmdResp(resp);
                    break;
                case GET:
                    handleStatusResp(resp);
                    break;
                case HEARTBEAT:
                    mSerialCtrl.write(SerialCode.MCU_HEARTBEAT_ACK + "\r\n");
                    break;
                default:
                    break;
            }
        }
    }

    private boolean handleAlert(SerialCode.AckResp resp) {
        AlertMsg msg = new AlertMsg();

        msg.device_id = CtrlCenter.getUDID();
        msg.time = new Date();

        msg.alert = new AlertMsg.AlertData();
        msg.alert.type = resp.getAckType().ordinal();
        msg.alert.level = "urgent";
        msg.alert.info = resp.getInfo();

        String json = gson.toJson(msg, AlertMsg.class);
        return mComMQ.publish(alertTopic, json, runInterval);
    }

    private boolean handleCmdResp(SerialCode.AckResp resp) {
        CmdRespMsg msg = new CmdRespMsg();

        msg.device_id = CtrlCenter.getUDID();
        msg.time = new Date();
        msg.cmd = resp.getCmd();
        msg.cmd_id = SerialCode.getCmdId();
        msg.result = resp.getResult();
        msg.reason = resp.getInfo();

        String json = gson.toJson(msg, CmdRespMsg.class);
        return mComMQ.publish(respTopic, json, runInterval);
    }

    private void handleStatusResp(SerialCode.AckResp resp) {
        switch (resp.getAckType()) {
            case LOCK:
                SerialStatus.setLockStatus(resp.getInfo());
                break;
            case DOOR:
                SerialStatus.setDoorStatus(resp.getInfo());
                break;
            case MAGNET:
                SerialStatus.setMagnetStatus(resp.getInfo());
                break;
            default:
                break;
        }
    }
}
