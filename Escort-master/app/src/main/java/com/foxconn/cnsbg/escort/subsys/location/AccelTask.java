package com.foxconn.cnsbg.escort.subsys.location;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

import com.foxconn.cnsbg.escort.common.SysUtil;
import com.foxconn.cnsbg.escort.mainctrl.CtrlCenter;
import com.foxconn.cnsbg.escort.subsys.communication.ComDataTxTask;

import java.util.Date;

public class AccelTask extends ComDataTxTask implements SensorEventListener {
    private static final String TAG = AccelTask.class.getSimpleName();

    private boolean mListening = false;
    private boolean accelerometerSupport = true;
    private SensorManager mSensorMgr;
    private Sensor mAccelerometer;
    private int mRate;
    private AccelSampling accelSampling = new AccelSampling();
    //private AccelData accelData = new AccelData();

    public AccelTask(Context context) {
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER)) {
            SysUtil.showToast(context, "FEATURE_SENSOR_ACCELEROMETER is not supported!", Toast.LENGTH_SHORT);
            accelerometerSupport = false;
            return;
        }

        runInterval = 1000;

        //Initial sampling instance
        accelSampling.startSampling();

        mSensorMgr = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mRate = SensorManager.SENSOR_DELAY_NORMAL;
        //movement pattern identification requires getting sensor data as fast as possible
        //if (accelSampling.IDENTIFY_MOVEMENT_PATTERN)
        //    rate = SensorManager.SENSOR_DELAY_FASTEST;

        //accelData.UDID = CtrlCenter.getUDID();
        //mAccelData_list = new CopyOnWriteArrayList<AccelSensorData>();
    }

    private void setAccelListening(boolean enable) {
        if (mListening == enable)
            return;

        mListening = enable;
        if (enable)
            mSensorMgr.registerListener(this, mAccelerometer, mRate);
        else
            mSensorMgr.unregisterListener(this);
    }

    @Override
    protected String collectData() {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
            return;

        //process motion detection with raw acceleration data
        accelSampling.processSample(event.values);

        //stop collecting data if no motion is detected
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.i(TAG, "onAccuracyChanged: accuracy = " + accuracy);
    }

    @Override
    protected void checkTask() {
        //if accelerometer is not supported, always update motion detection time
        if (!accelerometerSupport) {
            CtrlCenter.setMotionDetectionTime((new Date()).getTime());
            return;
        }

        if (CtrlCenter.isTrackingLocation())
            setAccelListening(true);
        else
            setAccelListening(false);
    }

    @Override
    protected boolean sendData(String dataStr) {
        return true;
    }

    @Override
    protected boolean sendCachedData() {
        return true;
    }

    @Override
    protected void saveCachedData(String dataStr) {
    }

}
