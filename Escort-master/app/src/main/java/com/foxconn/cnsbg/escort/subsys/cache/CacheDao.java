package com.foxconn.cnsbg.escort.subsys.cache;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;

import com.foxconn.cnsbg.escort.subsys.dao.CachedAccelerationEntity;
import com.foxconn.cnsbg.escort.subsys.dao.CachedAccelerationEntityDao;
import com.foxconn.cnsbg.escort.subsys.dao.CachedLocationEntity;
import com.foxconn.cnsbg.escort.subsys.dao.CachedLocationEntityDao;
import com.foxconn.cnsbg.escort.subsys.dao.DaoMaster;
import com.foxconn.cnsbg.escort.subsys.dao.DaoMaster.DevOpenHelper;
import com.foxconn.cnsbg.escort.subsys.dao.DaoSession;
import com.foxconn.cnsbg.escort.subsys.dao.LocationEntity;
import com.foxconn.cnsbg.escort.subsys.dao.LocationEntityDao;
import com.foxconn.cnsbg.escort.subsys.dao.MainUserEntityDao;
import com.foxconn.cnsbg.escort.subsys.dao.UserEntity;
import com.foxconn.cnsbg.escort.subsys.dao.UserEntityDao;
import com.foxconn.cnsbg.escort.subsys.location.Accelerometer;
import com.foxconn.cnsbg.escort.subsys.location.GPSData;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.greenrobot.dao.query.Query;

public class CacheDao {
    public static final int QUERY_LIMIT = 5000;

    public static class MVDaoLocation{
        public Double latitude = null;
        public Double longitude = null;
        public Long datetimestamp = null;
        public Boolean firstPoint = null;
        public Float distance = null;

        @Override
        public boolean equals(Object other) {
            MVDaoLocation loc = MVDaoLocation.class.cast(other);
            if (loc.datetimestamp.longValue() == datetimestamp.longValue()
                    && loc.latitude.doubleValue() == latitude.doubleValue()
                    && loc.longitude.doubleValue() == longitude.doubleValue()
                    && loc.firstPoint.booleanValue() == firstPoint.booleanValue()
                    && loc.distance.floatValue() == distance.floatValue())
                return true;
            else
                return false;
        }

        @Override
        public int hashCode() {
            return (latitude.hashCode() + longitude.hashCode() + datetimestamp.hashCode()
                    + firstPoint.hashCode() + distance.hashCode());
        }
    }

    public static class MVDaoFriend{
        public String username = null;
        public Float hourDistance = null;
        public Float totalDistance = null;
        public Long lastUpdate = null;

        @Override
        public boolean equals(Object other) {
            MVDaoFriend daoFriend = MVDaoFriend.class.cast(other);
            if (daoFriend.username.equals(username)
                    && daoFriend.hourDistance.floatValue() == hourDistance.floatValue()
                    && daoFriend.totalDistance.floatValue() == totalDistance.floatValue()
                    && daoFriend.lastUpdate.longValue() == lastUpdate.longValue())
                return true;
            else
                return false;
        }

        @Override
        public int hashCode() {
            return (username.hashCode() + hourDistance.hashCode() + totalDistance.hashCode());
        }
    }

    private static SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private LocationEntityDao locationDao;
    private UserEntityDao userDao;
    private MainUserEntityDao mainuserDao;
    private CachedLocationEntityDao cachedLocationDao;
    private CachedAccelerationEntityDao cachedAccelerationDao;

    private Query<LocationEntity> locationQb;
    private Query<UserEntity> userQb;

    private Query<CachedLocationEntity> cachedLocationQb;
    private Query<CachedAccelerationEntity> cachedAccelerationQb;

    private Map<String, Long> friendToLastUpdate;
    private boolean hasUsername;
    private List<String> updatedUserList;

    public CacheDao(Context appContext, String dbName) {
        closeDao();

        DevOpenHelper helper = new DaoMaster.DevOpenHelper(appContext, dbName, null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        locationDao = daoSession.getLocationEntityDao();
        userDao = daoSession.getUserEntityDao();
        mainuserDao = daoSession.getMainUserEntityDao();
        cachedLocationDao = daoSession.getCachedLocationEntityDao();
        cachedAccelerationDao = daoSession.getCachedAccelerationEntityDao();

        locationQb = locationDao.queryBuilder().where(LocationEntityDao.Properties.UserId.eq(1),
                LocationEntityDao.Properties.Datetimestamp.gt(getInitDate().getTime()))
                .orderAsc(LocationEntityDao.Properties.Datetimestamp)
                .limit(QUERY_LIMIT).offset(0).build();
        userQb = userDao.queryBuilder().where(UserEntityDao.Properties.Username.eq("@self"))
                .limit(QUERY_LIMIT).offset(0).build();

        cachedLocationQb = cachedLocationDao.queryBuilder().limit(QUERY_LIMIT).offset(0).build();
        cachedAccelerationQb = cachedAccelerationDao.queryBuilder().limit(QUERY_LIMIT).offset(0).build();

        friendToLastUpdate = new HashMap<String, Long>();
        hasUsername = false;
        updatedUserList = new ArrayList<String>();
    }

    public void closeDao() {
        if (db != null && db.isOpen())
            db.close();
    }

    private static Date getInitDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        return sdf.parse("1970-01-01 00:00:00", new ParsePosition(0));
    }

    private static List<MVDaoLocation> convertEntityDaoLocation(List<LocationEntity> locations) {
        if (locations == null || locations.isEmpty())
            return null;

        List<MVDaoLocation> returnList = new ArrayList<MVDaoLocation>();
        for (LocationEntity loc : locations) {
            MVDaoLocation daoLoc = new MVDaoLocation();
            daoLoc.latitude = loc.getLatitude();
            daoLoc.longitude = loc.getLongitude();
            daoLoc.datetimestamp = loc.getDatetimestamp();
            daoLoc.firstPoint = loc.getFirstPoint();
            daoLoc.distance = loc.getDistance();
            returnList.add(daoLoc);
        }

        return returnList;
    }

    private void updateLocations(List<MVDaoLocation> locations, String username, Float totalDist) {
        if (username == null || username.length() == 0)
            return;

        UserEntity user = getUser(username, true);
        if (user == null)
            return;

        LocationEntity lastLocationEntity = user.getLastLocation();
        Float lastTotalDistance = 0.0f;
        Location lastLocation = null;
        Location thisLocation = new Location("thisLocation");

        if (lastLocationEntity != null) {
            Float valueDistance = lastLocationEntity.getDistance();
            if (valueDistance != null)
                lastTotalDistance = valueDistance;

            Double valueLat = lastLocationEntity.getLatitude();
            Double valueLong = lastLocationEntity.getLongitude();
            if (valueLat != null && valueLong != null) {
                lastLocation = new Location("lastLocation");
                lastLocation.setLatitude(valueLat.doubleValue());
                lastLocation.setLongitude(valueLong.doubleValue());
            }
        }

        if (locations == null)
            locations = new ArrayList<MVDaoLocation>();

        for (MVDaoLocation loc : locations) {
            if (loc == null || loc.latitude == null || loc.longitude == null || loc.datetimestamp == null)
                continue;

            thisLocation.setLatitude(loc.latitude);
            thisLocation.setLongitude(loc.longitude);

            if (lastLocation != null)
                lastTotalDistance += thisLocation.distanceTo(lastLocation);

            if (loc.distance != null)
                lastTotalDistance = loc.distance;

            LocationEntity newLocation = new LocationEntity();
            newLocation.setLatitude(loc.latitude);
            newLocation.setLongitude(loc.longitude);
            newLocation.setDatetimestamp(loc.datetimestamp);
            newLocation.setUser(user);
            newLocation.setFirstPoint(loc.firstPoint);
            newLocation.setDistance(lastTotalDistance);
            locationDao.insert(newLocation);

            user.setLastLocation(newLocation);

            lastLocation = thisLocation;
        }

        if (totalDist == null)
            user.setTotalDistance(lastTotalDistance);
        else
            user.setTotalDistance(totalDist);

        updateHourDistance(user);
        userDao.update(user);

        updatedUserList.add(username);
    }

    private void updateHourDistance(UserEntity user) {
        if (user == null)
            return;

        Float hourDistance = 0.0f;
        //change past hour distance to last hour distance
		/*
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, -1);

		List<MVDaoLocation> hourLocations = getLocations(user, calendar.getTime());
		*/
        Date startDate = new Date();
        long lastUpdateTime = getLastUpdateTime(user.getUsername());
        startDate.setTime(lastUpdateTime - 3600*1000L);
        List<MVDaoLocation> hourLocations = getLocations(user, startDate);
        if (hourLocations != null && hourLocations.size() >= 2) {
            Float firstDistance = hourLocations.get(0).distance;
            Float lastDistance = hourLocations.get(hourLocations.size() - 1).distance;
            if (firstDistance != null && lastDistance != null)
                hourDistance = Math.abs(lastDistance - firstDistance);
        }

        user.setHourDistance(hourDistance);
        //userDao.update(user);
        return;
    }

    public synchronized long getLastUpdateTime(String username) {
        Long lastUpdate = getInitDate().getTime();

        if (username == null || username.length() == 0)
            return lastUpdate;

        lastUpdate = friendToLastUpdate.get(username);
        if (lastUpdate == null)
            lastUpdate = getInitDate().getTime();

        return lastUpdate;
    }

    public synchronized UserEntity getUser(String username, boolean needCreate) {
        if (username == null || username.length() == 0)
            return null;

        userQb.setParameter(0, username);
        UserEntity user = userQb.unique();
        if (user == null) {
            if (!needCreate)
                return null;

            UserEntity newUser = new UserEntity();
            newUser.setUsername(username);
            userDao.insert(newUser);

            return newUser;
        }

        return user;
    }

    public synchronized void removeFriend(String username) {
        if (username == null || username.length() == 0)
            return;

        UserEntity friend = getUser(username, false);
        if (friend == null)
            return;

        friend.setFriend(null);
        userDao.update(friend);
        return;
    }

    public synchronized List<MVDaoLocation> getLocations(UserEntity user, Date startDate) {
        if (user == null || startDate == null)
            return null;

        String username = user.getUsername();
        if (username == null || username.length() == 0)
            return null;

        List<LocationEntity> locationList = new ArrayList<LocationEntity>();
        int offset = 0;
        locationQb.setOffset(offset);
        locationQb.setParameter(0, user.getId());
        locationQb.setParameter(1, startDate.getTime());
        List<LocationEntity> queryList = locationQb.list();
        while (queryList != null && !queryList.isEmpty()) {
            locationList.addAll(queryList);

            offset += QUERY_LIMIT;
            locationQb.setOffset(offset);
            queryList = locationQb.list();
        }

        return convertEntityDaoLocation(locationList);
    }

    public synchronized List<GPSData> queryCachedLocationData() {
        List<GPSData> cachedLocationList = new ArrayList<GPSData>();
        int offset = 0;
        cachedLocationQb.setOffset(offset);
        List<CachedLocationEntity> queryList = cachedLocationQb.list();
        while (queryList != null && !queryList.isEmpty()) {
            for (CachedLocationEntity cachedLocation : queryList) {
                GPSData newLoc = new GPSData();
                newLoc.UDID = cachedLocation.getUDID();
                newLoc.datetimestamp = new Date(cachedLocation.getDatetimestamp());
                newLoc.uLatitude = cachedLocation.getLatitudeE6();
                newLoc.uLongitude = cachedLocation.getLongitudeE6();
                cachedLocationList.add(newLoc);
            }

            offset += QUERY_LIMIT;
            cachedLocationQb.setOffset(offset);
            queryList = cachedLocationQb.list();
        }

        return cachedLocationList;
    }

    public synchronized void saveCachedLocationData(GPSData data) {
        if (data == null)
            return;

        CachedLocationEntity newLoc = new CachedLocationEntity();
        newLoc.setUDID(data.UDID);
        newLoc.setDatetimestamp(data.datetimestamp.getTime());
        newLoc.setLatitudeE6(data.uLatitude);
        newLoc.setLongitudeE6(data.uLongitude);

        cachedLocationDao.insert(newLoc);

        return;
    }

    public synchronized void deleteCachedLocationData(List<GPSData> data) {
        if (data == null || data.isEmpty())
            return;

        List<CachedLocationEntity> cachedLocationList = new ArrayList<CachedLocationEntity>();
        int offset = 0;
        cachedLocationQb.setOffset(offset);
        List<CachedLocationEntity> queryList = cachedLocationQb.list();
        while (queryList != null && !queryList.isEmpty()) {
            cachedLocationList.addAll(queryList);

            offset += QUERY_LIMIT;
            cachedLocationQb.setOffset(offset);
            queryList = cachedLocationQb.list();
        }

        if (cachedLocationList == null || cachedLocationList.isEmpty())
            return;

        List<CachedLocationEntity> deleteList = new ArrayList<CachedLocationEntity>();
        for (CachedLocationEntity cachedLocation : cachedLocationList) {
            GPSData deleteLoc = new GPSData();
            deleteLoc.UDID = cachedLocation.getUDID();
            deleteLoc.datetimestamp = new Date(cachedLocation.getDatetimestamp());
            deleteLoc.uLatitude = cachedLocation.getLatitudeE6();
            deleteLoc.uLongitude = cachedLocation.getLongitudeE6();

            //to avoid ConcurrentModificationException
            if (data.contains(deleteLoc))
                deleteList.add(cachedLocation);
        }

        for (CachedLocationEntity cachedLocation : deleteList)
            cachedLocationDao.delete(cachedLocation);

        return;
    }

    public synchronized List<Accelerometer> queryCachedAccelerationData() {
        List<Accelerometer> cachedAccelerationList = new ArrayList<Accelerometer>();
        int offset = 0;
        cachedAccelerationQb.setOffset(offset);
        List<CachedAccelerationEntity> queryList = cachedAccelerationQb.list();
        while (queryList != null && !queryList.isEmpty()) {
            for (CachedAccelerationEntity cachedAcceleration : queryList) {
                Accelerometer newAcc = new Accelerometer();
                newAcc.UDID = cachedAcceleration.getUDID();
                newAcc.datetimestamp = new Date(cachedAcceleration.getDatetimestamp());
                newAcc.accelX_avg = cachedAcceleration.getAccelX_avg();
                newAcc.accelY_avg = cachedAcceleration.getAccelY_avg();
                newAcc.accelZ_avg = cachedAcceleration.getAccelZ_avg();
                newAcc.accelX_stddev = cachedAcceleration.getAccelX_stddev();
                newAcc.accelY_stddev = cachedAcceleration.getAccelY_stddev();
                newAcc.accelZ_stddev = cachedAcceleration.getAccelZ_stddev();
                cachedAccelerationList.add(newAcc);
            }

            offset += QUERY_LIMIT;
            cachedAccelerationQb.setOffset(offset);
            queryList = cachedAccelerationQb.list();
        }

        return cachedAccelerationList;
    }

    public synchronized void saveCachedAccelerationData(Accelerometer data) {
        if (data == null)
            return;

        CachedAccelerationEntity newAcc = new CachedAccelerationEntity();
        newAcc.setUDID(data.UDID);
        newAcc.setDatetimestamp(data.datetimestamp.getTime());
        newAcc.setAccelX_avg(data.accelX_avg);
        newAcc.setAccelY_avg(data.accelY_avg);
        newAcc.setAccelZ_avg(data.accelZ_avg);
        newAcc.setAccelX_stddev(data.accelX_stddev);
        newAcc.setAccelY_stddev(data.accelY_stddev);
        newAcc.setAccelZ_stddev(data.accelZ_stddev);

        cachedAccelerationDao.insert(newAcc);

        return;
    }

    public synchronized void deleteCachedAccelerationData(List<Accelerometer> data) {
        if (data == null || data.isEmpty())
            return;

        List<CachedAccelerationEntity> cachedAccelerationList = new ArrayList<CachedAccelerationEntity>();
        int offset = 0;
        cachedAccelerationQb.setOffset(offset);
        List<CachedAccelerationEntity> queryList = cachedAccelerationQb.list();
        while (queryList != null && !queryList.isEmpty()) {
            cachedAccelerationList.addAll(queryList);

            offset += QUERY_LIMIT;
            cachedAccelerationQb.setOffset(offset);
            queryList = cachedAccelerationQb.list();
        }

        if (cachedAccelerationList == null || cachedAccelerationList.isEmpty())
            return;

        List<CachedAccelerationEntity> deleteList = new ArrayList<CachedAccelerationEntity>();
        for (CachedAccelerationEntity cachedAcceleration : cachedAccelerationList) {
            Accelerometer deleteAcc = new Accelerometer();
            deleteAcc.UDID = cachedAcceleration.getUDID();
            deleteAcc.datetimestamp = new Date(cachedAcceleration.getDatetimestamp());
            deleteAcc.accelX_avg = cachedAcceleration.getAccelX_avg();
            deleteAcc.accelY_avg = cachedAcceleration.getAccelY_avg();
            deleteAcc.accelZ_avg = cachedAcceleration.getAccelZ_avg();
            deleteAcc.accelX_stddev = cachedAcceleration.getAccelX_stddev();
            deleteAcc.accelY_stddev = cachedAcceleration.getAccelY_stddev();
            deleteAcc.accelZ_stddev = cachedAcceleration.getAccelZ_stddev();

            //to avoid ConcurrentModificationException
            if (data.contains(deleteAcc))
                deleteList.add(cachedAcceleration);
        }

        for (CachedAccelerationEntity cachedAcceleration : deleteList)
            cachedAccelerationDao.delete(cachedAcceleration);

        return;
    }
}
