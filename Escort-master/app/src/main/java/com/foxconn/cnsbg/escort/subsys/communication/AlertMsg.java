package com.foxconn.cnsbg.escort.subsys.communication;

import java.util.Date;

public final class AlertMsg {
    public String device_id;
    public Date time;
    public AlertData alert;

    public static class AlertData {
        public int type;
        public String level;
        public String info;
    }
}
