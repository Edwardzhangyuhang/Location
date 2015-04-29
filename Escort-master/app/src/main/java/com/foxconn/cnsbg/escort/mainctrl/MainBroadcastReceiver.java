package com.foxconn.cnsbg.escort.mainctrl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MainBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent mainService = new Intent(context, MainService.class);
        context.startService(mainService);
    }
}