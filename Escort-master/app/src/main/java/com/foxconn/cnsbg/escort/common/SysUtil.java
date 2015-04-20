package com.foxconn.cnsbg.escort.common;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class SysUtil {
    public static boolean isServerReachable(Context context, Intent intent) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo;
        //netInfo could be null if network is switching, so wait up to 5 seconds
        for (int i = 0; i < 5; i++) {
            netInfo = connMgr.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                break;
            }

            try {
                Thread.sleep(1000);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < 5; i++) {
            if (checkHttpConnection(10 * 1000))
                return true;

            try {
                Thread.sleep(1000);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public static boolean checkHttpConnection(int timeout) {
        try {
            URI uri = new URI("http", null, SysConst.HTTP_SERVER_HOST, SysConst.HTTP_SERVER_PORT, null, null, null);
            URL url = uri.toURL();
            HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
            urlc.setRequestProperty("User-Agent", "Escort");
            urlc.setRequestProperty("Connection", "close");
            urlc.setConnectTimeout(timeout);
            urlc.connect();
            //treat it as OK if no exception is thrown
            //if (urlc.getResponseCode() == HttpURLConnection.HTTP_OK)
            urlc.disconnect();
            return true;
        } catch (MalformedURLException e) {
            Log.w("checkHttpConnection", "MalformedURLException");
        } catch (IOException e) {
            Log.w("checkHttpConnection", "IOException");
        } catch (URISyntaxException e) {
            Log.w("checkHttpConnection", "URISyntaxException:" + e.getReason());
        }

        return false;
    }

    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void showToast(final Context context, final String text, final int duration) {
        Handler handler  = new Handler(context.getMainLooper());
        final Runnable runnable = new Runnable() {
            public void run() {
                Toast.makeText(context, text, duration).show();
            }
        };
        handler.post(runnable);
    }
}
