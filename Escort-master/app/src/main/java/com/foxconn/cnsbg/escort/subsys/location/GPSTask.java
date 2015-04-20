package com.foxconn.cnsbg.escort.subsys.location;

import android.content.Context;
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
import com.foxconn.cnsbg.escort.subsys.communication.ComMQ;
import com.foxconn.cnsbg.escort.subsys.communication.ComPublishTask;
import com.google.gson.JsonParseException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GPSTask extends ComPublishTask {
    private final String TAG = GPSTask.class.getSimpleName();

    //change these to fine tune the power consumption
    private final static float MIN_ACCURACY = 101.0f;
    private final static float MIN_DISTANCE_IN_METER = 10.0f;//Only care about updates greater than 10 meters apart
    private final static long MIN_TIME_IN_MS = 60000L;//Only update every 1min or so.
    private final static long MAX_PROVIDER_CHECK_TIME_IN_MS = 300000L;//try to switch network every 5min

    private final static long MAX_IDLE_TIME_IN_MS = 600000L;//stop location tracking if no motion is detected in 10min

    private static LocationManager locManager;
    private static LocationUpdateHandler locHandler;
    private static long curMinTime;
    private static float curMinDistance;
    private static float curAccuracy;
    private static String curProvider;
    private static long lastProviderCheckTime;
    private static boolean isProviderChanged;

    private static long lastLocTime = 0;

    private static boolean updated = false;
    private static GPSData curLoc = new GPSData();

    public GPSTask(Context context, ComMQ mq) {
        mContext = context;
        mComMQ = mq;
        runInterval = 10000;

        setAccuracyLevel(CtrlCenter.getPrefs().getInt(SysConst.MV_SETTING_GPS_ACCURACY_LEVEL, 50));

        curLoc.UDID = CtrlCenter.getUDID();

        //get a handle on the location manager
        locHandler = new LocationUpdateHandler();
        locManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        curProvider = LocationManager.NETWORK_PROVIDER;//used to trigger the notification
        curProvider = findAvailableProvider(Criteria.ACCURACY_COARSE);
        lastProviderCheckTime = new Date().getTime();
        isProviderChanged = false;

        //don't setup LocationUpdates at this point
        //locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, curMinTime, curMinDistance, locHandler);
        //locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, curMinTime, curMinDistance, locHandler);

        Log.i(TAG, "Starting up location collection...");
        //Send our last known location to the database
        List<String> matchingProviders = locManager.getAllProviders();
        for (String provider : matchingProviders) {
            Location location = locManager.getLastKnownLocation(provider);
            if (location != null)
                handleLastKnownLocation(location);
        }
    }

    public void restartLocationUpdates() {
        if (CtrlCenter.isTrackingLocation()) {
            stopLocationUpdates();
            startLocationUpdates();
        }
    }

    public void stopLocationUpdates() {
        if (CtrlCenter.isTrackingLocation()) {
            locManager.removeUpdates(locHandler);
            CtrlCenter.setTrackingLocation(false);
        }
    }

    public void startLocationUpdates() {
        if (!CtrlCenter.isTrackingLocation()) {
            if (curProvider == null) {
                locManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, locHandler);
                locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locHandler);
                locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locHandler);
            } else {
                locManager.requestLocationUpdates(curProvider, curMinTime, curMinDistance, locHandler);
            }
            CtrlCenter.setTrackingLocation(true);
        }
    }

    private void setAccuracyLevel(int percentage) {
        float accuracyLevel = (5.0f - (percentage/20.0f));

        curMinTime = (long)(MIN_TIME_IN_MS * accuracyLevel);
        curMinDistance = (MIN_DISTANCE_IN_METER * accuracyLevel);
        if (accuracyLevel < 1.0f)
            curAccuracy = (MIN_ACCURACY);
        else
            curAccuracy = (MIN_ACCURACY * accuracyLevel);
    }

    public void applyAccuracyLevel(int percentage) {
        setAccuracyLevel(percentage);

        if (CtrlCenter.isTrackingLocation()) {
            stopLocationUpdates();
            startLocationUpdates();
        }
    }

    @Override
    protected String collectData() {
        //updated means new loc have been constructed and be ready to send
        if (updated) {
            updated = false;
            return gson.toJson(curLoc, GPSData.class);
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
            Log.i("onProviderDisabled", "Status changed. Provider: " + provider + " Status: Disabled");
            curProvider = findAvailableProvider(Criteria.ACCURACY_COARSE);
        }

        public void onProviderEnabled(String provider) {
            Log.i("onProviderEnabled", "Status changed. Provider: " + provider + " Status: Enabled");
            curProvider = findAvailableProvider(Criteria.ACCURACY_COARSE);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i("onStatusChanged", "Status changed. Provider: " + provider + " Status: " + String.valueOf(status));
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

    private void handleLocation(Location loc) {
        if (loc == null)
            return;

        if (loc.getTime() <= 0)
            return;

        int newLat = (int) (loc.getLatitude()*1E6);
        int newLng = (int) (loc.getLongitude()*1E6);

        curLoc.uLatitude = newLat;
        curLoc.uLongitude = newLng;
        curLoc.datetimestamp = new Date(loc.getTime());
        curLoc.UDID = CtrlCenter.getUDID();
        updated = true;
    }

    @Override
    protected void checkTask() {
        if (CtrlCenter.isTrackingLocation())
            checkProvider();
    }

    private void checkProvider() {
        //not use GPS as far as possible
        long currentTime = new Date().getTime();
        long motionDetectTime = CtrlCenter.getMotionDetectionTime();

        if (currentTime - motionDetectTime > MAX_IDLE_TIME_IN_MS
                && curProvider != LocationManager.PASSIVE_PROVIDER) {
            System.out.println("Pause location tracking...");
            curProvider = LocationManager.PASSIVE_PROVIDER;
            locManager.removeUpdates(locHandler);
        } else if (motionDetectTime > lastProviderCheckTime
                && currentTime - lastProviderCheckTime > MAX_PROVIDER_CHECK_TIME_IN_MS) {
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
                    restartLocationUpdates();
                }
            };
            handler.post(providerUpdateThread);

            SysUtil.showToast(mContext, "Updated Provider:" + curProvider, Toast.LENGTH_LONG);
        }
    }

    @Override
    protected boolean sendData(String data) {
        if (data == null)
            return false;

        if (!mComMQ.publish(SysConst.MQ_TOPIC_GPS_DATA, data, SysConst.MQ_SEND_MAX_TIMEOUT))
            return false;

        return true;
    }

    @Override
    protected boolean sendCachedData() {
        List<GPSData> dataList = CtrlCenter.getDao().queryCachedLocationData();
        if (dataList == null || dataList.isEmpty())
            return true;

        List<GPSData> sentList = new ArrayList<GPSData>();
        for (GPSData data : dataList) {
            String dataString = gson.toJson(data, GPSData.class);
            if (sendData(dataString))
                sentList.add(data);
            else
                break;
        }

        //could delete one by one, bulk deletion is just for convenience
        CtrlCenter.getDao().deleteCachedLocationData(sentList);

        if (sentList.size() == dataList.size())
            return true;

        return false;
    }

    @Override
    protected void saveCachedData(String dataString) {
        if (dataString == null || dataString.length() == 0)
            return;

        try {
            GPSData data = gson.fromJson(dataString, GPSData.class);
            CtrlCenter.getDao().saveCachedLocationData(data);
        } catch (JsonParseException e) {
            Log.w("saveCachedData", "JsonParseException");
        } catch (NullPointerException e) {
            Log.w("saveCachedData", "NullPointerException");
        }

        return;
    }
}
