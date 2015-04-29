package com.foxconn.cnsbg.escort.subsys.location;

import java.util.Date;

public class AccelData {
    public String device_id;
    public Date time;

    public float accelX_avg;
    public float accelY_avg;
    public float accelZ_avg;
    public double accelX_stddev;
    public double accelY_stddev;
    public double accelZ_stddev;

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        AccelData data = AccelData.class.cast(obj);
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
