package com.foxconn.cnsbg.escort.subsys.location;

import java.util.Date;

public final class GPSData {
    public int uLatitude = 0, uLongitude = 0;//The latitude and longitude
    public Date datetimestamp = null;
    public String UDID = null;

    @Override
    public boolean equals(Object other) {
        GPSData loc = GPSData.class.cast(other);
        if ((loc.UDID == UDID || loc.UDID.equals(UDID))
                && loc.datetimestamp.getTime() == datetimestamp.getTime()
                && loc.uLatitude == uLatitude
                && loc.uLongitude == uLongitude)
            return true;
        else
            return false;
    }

    @Override
    public int hashCode() {
        return (UDID.hashCode() + datetimestamp.hashCode());
    }
}
