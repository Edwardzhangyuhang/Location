package com.foxconn.cnsbg.escort.subsys.communication;

import android.content.Context;

import com.foxconn.cnsbg.escort.common.SysConst;

import org.fusesource.mqtt.client.Future;
import org.fusesource.mqtt.client.FutureConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ComMQ {
    private final String TAG = ComMQ.class.getSimpleName();

    private Context mContext;
    private FutureConnection mConn;
    Future<Message> mReceive = null;

    public ComMQ(Context context) {
        mContext = context;
    }

    public boolean init(List<String> subscribes) {
        MQTT mqtt = new MQTT();

        try {
            mqtt.setHost(SysConst.MQ_SERVER_HOST, SysConst.MQ_SERVER_PORT);
        } catch (Exception e) {
            return false;
        }

        //setClientId(CtrlCenter.getUDID());
        //setCleanSession(false);
        mqtt.setKeepAlive(SysConst.MQ_KEEP_ALIVE);
        mqtt.setConnectAttemptsMax(SysConst.MQ_CONNECT_ATTEMPTS);
        mqtt.setReconnectAttemptsMax(SysConst.MQ_RECONNECT_ATTEMPTS);
        mqtt.setReconnectDelay(SysConst.MQ_RECONNECT_DELAY);
        mqtt.setReconnectDelayMax(SysConst.MQ_RECONNECT_MAX_DELAY);

        mConn = mqtt.futureConnection();
        mConn.connect();

        if (subscribes != null) {
            for (String topic : subscribes)
                subscribe(topic, QoS.EXACTLY_ONCE);
        }

        mReceive = mConn.receive();

        return true;
    }

    public boolean isConnected() {
        return mConn.isConnected();
    }

    public boolean publish(String topic, String payload, long milliseconds) {
        try {
            mConn.publish(topic, payload.getBytes(), QoS.AT_MOST_ONCE, false)
                    .await(milliseconds, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public boolean subscribe(String topic, QoS qos) {
        Topic[] topics = {new Topic(topic, qos)};
        mConn.subscribe(topics);

        return true;
    }

    public String receive(long milliseconds) {
        String result;

        try {
            Message msg = mReceive.await(milliseconds, TimeUnit.MILLISECONDS);
            result = new String(msg.getPayload());
            msg.ack();
            mReceive = mConn.receive();
        } catch (Exception e) {
            return null;
        }

        return result;
    }

    public boolean disconnect() {
        mConn.disconnect();

        return true;
    }
}
