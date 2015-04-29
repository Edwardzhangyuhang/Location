package com.foxconn.cnsbg.escort.subsys.location;

import java.util.Date;

public final class LocData {
    public String device_id;
    public Date time;
    public int battery_level;
    public int signal_strength;
    public String lock_status;
    public String door_status;
    public GPSLoc location;

    public static class GPSLoc {
        public final String type = "gps";
        public GPSData data;
    }

    public static class GPSData {
        public double latitude;
        public double longitude;

        public String provider;
        public float accuracy;
        public double altitude;
        public float bearing;
        public float speed;
        public boolean mock;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        LocData data = LocData.class.cast(obj);
        if (!data.device_id.equals(device_id))
            return false;

        if (data.time.getTime() != time.getTime())
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (device_id.hashCode() + time.hashCode());
    }
}
