package com.foxconn.cnsbg.escort.subsys.location;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.foxconn.cnsbg.escort.BuildConfig;
import com.foxconn.cnsbg.escort.mainctrl.CtrlCenter;
import com.foxconn.cnsbg.escort.common.SysConst;
import com.foxconn.cnsbg.escort.common.SysUtil;
import com.foxconn.cnsbg.escort.subsys.communication.ComDataTxTask;
import com.foxconn.cnsbg.escort.subsys.communication.ComMQ;
import com.foxconn.cnsbg.escort.subsys.usbserial.SerialStatus;
import com.google.gson.JsonParseException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LocTask extends ComDataTxTask {
    private static final String TAG = LocTask.class.getSimpleName();

    private boolean locUpdating = false;
    private LocationManager locManager;
    private LocationUpdateHandler locHandler;
    private long curMinTime;
    private float curMinDistance;
    private float curAccuracy;
    private String curProvider;
    private long lastProviderCheckTime;
    private boolean isProviderChanged = false;

    private long lastLocTime = 0;

    private boolean locDataUpdated = false;
    private LocData locData = new LocData();

    private static final String gpsTopic = SysConst.MQ_TOPIC_GPS_DATA + CtrlCenter.getUDID();

    public LocTask(Context context, ComMQ mq) {
        mContext = context;
        mComMQ = mq;

        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_LOCATION)) {
            SysUtil.showToast(context, "FEATURE_LOCATION is not supported!", Toast.LENGTH_SHORT);
            requestShutdown = true;
            return;
        }

        runInterval = SysConst.LOC_TASK_RUN_INTERVAL;

        //context.getSharedPreferences(SysConst.APP_PREF_NAME, Context.MODE_PRIVATE);
        setAccuracyLevel(SysConst.LOC_MIN_ACCURACY, SysConst.LOC_UPDATE_MIN_TIME, SysConst.LOC_UPDATE_MIN_DISTANCE);

        //get a handle on the location manager
        locHandler = new LocationUpdateHandler();
        locManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        curProvider = LocationManager.NETWORK_PROVIDER;//used to trigger the notification
        curProvider = findAvailableProvider(Criteria.ACCURACY_COARSE);
        lastProviderCheckTime = new Date().getTime();

        //don't setup LocationUpdates at this point
        //locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, curMinTime, curMinDistance, locHandler);
        //locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, curMinTime, curMinDistance, locHandler);

        //Send our last known location to the database
        //List<String> matchingProviders = locManager.getAllProviders();
        //for (String provider : matchingProviders) {
        //    Location location = locManager.getLastKnownLocation(provider);
        //    if (location != null)
        //        handleLastKnownLocation(location);
        //}
    }

    private void handleLastKnownLocation(Location loc) {
        if (loc == null)
            return;

        if (!loc.hasAccuracy())
            return;

        if(loc.getAccuracy() > curAccuracy)
            return;

        lastLocTime = loc.getTime();
        handleLocation(loc);
    }

    private void setLocUpdating(boolean enable) {
        if (locUpdating == enable)
            return;

        locUpdating = enable;
        if (enable) {
            if (curProvider == null) {
                locManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, locHandler);
                locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locHandler);
                locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locHandler);
            } else {
                locManager.requestLocationUpdates(curProvider, curMinTime, curMinDistance, locHandler);
            }
        } else {
            locManager.removeUpdates(locHandler);
        }
    }

    private void setAccuracyLevel(float accuracy, long time, float distance) {
        curAccuracy = accuracy;
        curMinTime = time;
        curMinDistance = distance;
    }

    @Override
    protected String collectData() {
        //new loc have been constructed and be ready to send
        if (locDataUpdated) {
            locDataUpdated = false;
            return gson.toJson(locData, LocData.class);
        }
        return null;
    }

    public class LocationUpdateHandler implements LocationListener {
        public void onLocationChanged(Location loc) {
            if (!loc.hasAccuracy()) {
                curProvider = findAvailableProvider(Criteria.ACCURACY_COARSE);
                return;
            }

            //find a provide with better accuracy
            if(loc.getAccuracy() > curAccuracy) {
                curProvider = findAvailableProvider(Criteria.ACCURACY_FINE);
                return;
            }

            handleLocation(loc);

            if (BuildConfig.DEBUG) {
                String text = "Provider:" + loc.getProvider() + ", Accuracy:" + loc.getAccuracy()
                        + ", Interval:" + (loc.getTime() - lastLocTime)/1000;
                SysUtil.showToast(mContext, "Updated Location:" + text, Toast.LENGTH_LONG);
            }

            lastLocTime = loc.getTime();
        }

        public void onProviderDisabled(String provider) {
            Log.i(TAG + ":onProviderDisabled", "Status changed. Provider: " + provider + " Status: Disabled");
            curProvider = findAvailableProvider(Criteria.ACCURACY_COARSE);
        }

        public void onProviderEnabled(String provider) {
            Log.i(TAG + ":onProviderEnabled", "Status changed. Provider: " + provider + " Status: Enabled");
            curProvider = findAvailableProvider(Criteria.ACCURACY_COARSE);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i(TAG + ":onStatusChanged", "Status changed. Provider: " + provider + " Status: " + String.valueOf(status));
            curProvider = findAvailableProvider(Criteria.ACCURACY_COARSE);
        }
    }

    private String findAvailableProvider(int accuracy) {
        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
        if (accuracy == Criteria.ACCURACY_FINE)
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
        else
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);

        String previousProvider = curProvider;
        String provider = locManager.getBestProvider(criteria, true);

        if (previousProvider != null && provider != null) {
            if (!provider.equals(previousProvider))
                isProviderChanged = true;
        } else if (previousProvider != null || provider != null) {
            isProviderChanged = true;
        }

        return provider;
    }

    private void handleLocation(Location loc) {
        if (loc == null)
            return;

        if (loc.getTime() <= 0)
            return;

        locData.device_id = CtrlCenter.getUDID();
        locData.time = new Date(loc.getTime());

        locData.battery_level = SysUtil.getBatteryLevel(mContext);
        locData.signal_strength = SysUtil.getSignalStrength(mContext);
        locData.lock_status = SerialStatus.getLockStatus();
        locData.door_status = SerialStatus.getDoorStatus();

        locData.location = new LocData.GPSLoc();
        locData.location.data = new LocData.GPSData();
        locData.location.data.latitude = loc.getLatitude();
        locData.location.data.longitude = loc.getLongitude();

        locData.location.data.provider = loc.getProvider();
        locData.location.data.accuracy = loc.getAccuracy();
        locData.location.data.altitude = loc.getAltitude();
        locData.location.data.bearing = loc.getBearing();
        locData.location.data.speed = loc.getSpeed();
        locData.location.data.mock = loc.isFromMockProvider();

        locDataUpdated = true;
    }

    @Override
    protected void checkTask() {
        if (CtrlCenter.isTrackingLocation())
            checkProvider();
        else
            setLocUpdating(false);
    }

    private void checkProvider() {
        //not use GPS as far as possible
        long currentTime = new Date().getTime();
        long motionDetectTime = CtrlCenter.getMotionDetectionTime();

        if (currentTime - motionDetectTime > SysConst.LOC_UPDATE_PAUSE_IDLE_TIME
                && !curProvider.equals(LocationManager.PASSIVE_PROVIDER)) {
            System.out.println("Pause location tracking...");
            curProvider = LocationManager.PASSIVE_PROVIDER;
            setLocUpdating(false);
        } else if (motionDetectTime > lastProviderCheckTime
                && currentTime - lastProviderCheckTime > SysConst.LOC_PROVIDER_CHECK_TIME) {
            if (curProvider != null
                    && curProvider.equals(LocationManager.PASSIVE_PROVIDER))
                System.out.println("Resume location tracking...");
            else
                System.out.println("Checking provider...");
            lastProviderCheckTime = currentTime;
            curProvider = findAvailableProvider(Criteria.ACCURACY_COARSE);
        }

        if (isProviderChanged) {
            isProviderChanged = false;
            //Update location provider with less power consumption if possible
            Handler handler = new Handler(mContext.getMainLooper());
            final Runnable providerUpdateThread = new Runnable() {
                public void run() {
                    setLocUpdating(false);
                    setLocUpdating(true);
                }
            };
            handler.post(providerUpdateThread);

            SysUtil.showToast(mContext, "Updated Provider:" + curProvider, Toast.LENGTH_LONG);
        }
    }

    @Override
    protected boolean sendData(String dataStr) {
        if (dataStr == null)
            return false;

        if (!mComMQ.publish(gpsTopic, dataStr, SysConst.MQ_SEND_MAX_TIMEOUT))
            return false;

        return true;
    }

    @Override
    protected boolean sendCachedData() {
        List<LocData> dataList = CtrlCenter.getDao().queryCachedLocData();
        if (dataList == null || dataList.isEmpty())
            return true;

        List<LocData> sentList = new ArrayList<LocData>();
        for (LocData data : dataList) {
            String dataString = gson.toJson(data, LocData.class);
            if (sendData(dataString))
                sentList.add(data);
            else
                break;
        }

        //could delete one by one, bulk deletion is just for convenience
        CtrlCenter.getDao().deleteCachedLocData(sentList);

        if (sentList.size() == dataList.size())
            return true;

        return false;
    }

    @Override
    protected void saveCachedData(String dataStr) {
        if (dataStr == null || dataStr.length() == 0)
            return;

        try {
            LocData data = gson.fromJson(dataStr, LocData.class);
            CtrlCenter.getDao().saveCachedLocData(data);
        } catch (JsonParseException e) {
            Log.w(TAG + ":saveCachedData", "JsonParseException");
        } catch (NullPointerException e) {
            Log.w(TAG + ":saveCachedData", "NullPointerException");
        }
    }

}
