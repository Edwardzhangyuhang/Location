package com.foxconn.cnsbg.escort.subsys.location;

import java.util.Date;

public class Accelerometer {
    public float accelX_avg = 0;
    public float accelY_avg = 0;
    public float accelZ_avg = 0;
    public double accelX_stddev = 0;
    public double accelY_stddev = 0;
    public double accelZ_stddev = 0;
    public Date datetimestamp = null;
    public String UDID = null;

    @Override
    public boolean equals(Object other) {
        Accelerometer acc = Accelerometer.class.cast(other);
        if ((acc.UDID == UDID || acc.UDID.equals(UDID))
                && acc.datetimestamp.getTime() == datetimestamp.getTime()
                && acc.accelX_avg == accelX_avg
                && acc.accelY_avg == accelY_avg
                && acc.accelZ_avg == accelZ_avg
                && acc.accelX_stddev == accelX_stddev
                && acc.accelY_stddev == accelY_stddev
                && acc.accelZ_stddev == accelZ_stddev)
            return true;
        else
            return false;
    }

    @Override
    public int hashCode() {
        return (UDID.hashCode() + datetimestamp.hashCode());
    }
}
