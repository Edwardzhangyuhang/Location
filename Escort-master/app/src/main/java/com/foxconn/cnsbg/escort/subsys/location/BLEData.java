package com.foxconn.cnsbg.escort.subsys.location;

import java.util.Date;

public final class BLEData {
    public String device_id;
    public Date time;
    public int battery_level;
    public int signal_strength;
    public String lock_status;
    public String door_status;
    public BLELoc location;

    public static class BLELoc {
        public final String type = "ibeacon";
        public DeviceData data;
    }

    public static class DeviceData {
        public String mac;
        public int rssi;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        BLEData data = BLEData.class.cast(obj);
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
