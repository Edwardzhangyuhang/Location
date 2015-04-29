package com.foxconn.cnsbg.escort.subsys.communication;

import java.util.Date;

public final class CmdRespMsg {
    public String device_id;
    public Date time;
    public String cmd;
    public int cmd_id;
    public String result;
    public String reason;
}
