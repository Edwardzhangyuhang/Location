package com.foxconn.cnsbg.escort.subsys.location;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import com.foxconn.cnsbg.escort.common.SysConst;
import com.foxconn.cnsbg.escort.common.SysUtil;
import com.foxconn.cnsbg.escort.mainctrl.CtrlCenter;
import com.foxconn.cnsbg.escort.subsys.communication.ComDataTxTask;
import com.foxconn.cnsbg.escort.subsys.communication.ComMQ;
import com.foxconn.cnsbg.escort.subsys.usbserial.SerialStatus;
import com.google.gson.JsonParseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class BLETask extends ComDataTxTask implements BluetoothAdapter.LeScanCallback {
    private static final String TAG = BLETask.class.getSimpleName();

    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning = false;

    private List<BLEData.DeviceData> dataList = new ArrayList<BLEData.DeviceData>();
    private long lastUpdateTime = 0L;

    private boolean bleDataUpdated = false;
    private BLEData bleData = new BLEData();

    private static final String bleTopic = SysConst.MQ_TOPIC_BLE_DATA + CtrlCenter.getUDID();

    public BLETask(Context context, ComMQ mq) {
        mContext = context;
        mComMQ = mq;

        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            SysUtil.showToast(context, "FEATURE_BLUETOOTH_LE is not supported!", Toast.LENGTH_SHORT);
            requestShutdown = true;
            return;
        }

        mBluetoothAdapter = ((BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        if (mBluetoothAdapter == null) {
            SysUtil.showToast(context, "BLUETOOTH_SERVICE is not supported!", Toast.LENGTH_SHORT);
            requestShutdown = true;
            return;
        }

        if (!mBluetoothAdapter.isEnabled())
            mBluetoothAdapter.enable();

        runInterval = SysConst.BLE_TASK_RUN_INTERVAL;
    }

    private void setBleScanning(boolean enable) {
        if (mScanning == enable)
            return;

        mScanning = enable;
        if (enable)
            mBluetoothAdapter.startLeScan(this);
        else
            mBluetoothAdapter.stopLeScan(this);
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        long time = System.currentTimeMillis();
        if (lastUpdateTime == 0)
            lastUpdateTime = time;

        if (!device.getName().contains(SysConst.BLE_DEVICE_NAME_FILTER))
            return;

        if (rssi < SysConst.BLE_RSSI_THRESHOLD)
            return;

        BLEData.DeviceData data = new BLEData.DeviceData();
        data.mac = device.getAddress();
        data.rssi = rssi;
        dataList.add(data);

        if (time - lastUpdateTime > SysConst.BLE_UPDATE_MIN_TIME) {
            lastUpdateTime = time;

            BLEData.DeviceData result = analyse(dataList);
            dataList.clear();

            bleData.device_id = CtrlCenter.getUDID();
            bleData.time = new Date();

            bleData.battery_level = SysUtil.getBatteryLevel(mContext);
            bleData.signal_strength = SysUtil.getSignalStrength(mContext);
            bleData.lock_status = SerialStatus.getLockStatus();
            bleData.door_status = SerialStatus.getDoorStatus();

            bleData.location = new BLEData.BLELoc();
            bleData.location.data = new BLEData.DeviceData();
            bleData.location.data.mac = result.mac;
            bleData.location.data.rssi = result.rssi;

            bleDataUpdated = true;
        }
    }

    public BLEData.DeviceData analyse(List<BLEData.DeviceData> list) {
        Collections.sort(list, new Comparator<BLEData.DeviceData>() {
            @Override
            public int compare(BLEData.DeviceData data1, BLEData.DeviceData data2) {
                return data2.rssi - data1.rssi;
            }
        });

        return list.get(0);
    }

    @Override
    protected String collectData() {
        if (bleDataUpdated) {
            bleDataUpdated = false;
            return gson.toJson(bleData, BLEData.class);
        }

        return null;
    }

    @Override
    protected boolean sendData(String dataStr) {
        if (dataStr == null)
            return false;

        if (!mComMQ.publish(bleTopic, dataStr, SysConst.MQ_SEND_MAX_TIMEOUT))
            return false;

        return true;
    }

    @Override
    protected boolean sendCachedData() {
        List<BLEData> dataList = CtrlCenter.getDao().queryCachedBleData();
        if (dataList == null || dataList.isEmpty())
            return true;

        List<BLEData> sentList = new ArrayList<BLEData>();
        for (BLEData data : dataList) {
            String dataString = gson.toJson(data, BLEData.class);
            if (sendData(dataString))
                sentList.add(data);
            else
                break;
        }

        //could delete one by one, bulk deletion is just for convenience
        CtrlCenter.getDao().deleteCachedBleData(sentList);

        if (sentList.size() == dataList.size())
            return true;

        return false;
    }

    @Override
    protected void saveCachedData(String dataStr) {
        if (dataStr == null || dataStr.length() == 0)
            return;

        try {
            BLEData data = gson.fromJson(dataStr, BLEData.class);
            CtrlCenter.getDao().saveCachedBleData(data);
        } catch (JsonParseException e) {
            Log.w(TAG + ":saveCachedData", "JsonParseException");
        } catch (NullPointerException e) {
            Log.w(TAG + ":saveCachedData", "NullPointerException");
        }
    }

    @Override
    protected void checkTask() {
        setBleScanning(false);

        if (CtrlCenter.isTrackingLocation()) {
            long currentTime = new Date().getTime();
            long motionDetectTime = CtrlCenter.getMotionDetectionTime();

            if (currentTime - motionDetectTime < SysConst.LOC_UPDATE_PAUSE_IDLE_TIME)
                setBleScanning(true);
        }
    }
}
