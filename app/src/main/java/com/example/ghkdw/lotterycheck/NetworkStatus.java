package com.example.ghkdw.lotterycheck;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by ghkdw on 2020-10-05.
 */

public class NetworkStatus {
    public static final int TYPE_WIFI = 1;
    public static final int TYPE_MOBILE = 2;
    public static final int TYPE_NOT_CONNECTED = 3;

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager manager = (ConnectivityManager)context.getSystemService(context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = manager.getActiveNetworkInfo();
        if(netInfo != null) {
            int type = netInfo.getType();
            if(type == ConnectivityManager.TYPE_MOBILE) {
                return TYPE_MOBILE;
            }
            else if(type == ConnectivityManager.TYPE_WIFI) {
                return TYPE_WIFI;
            }
        }

        return TYPE_NOT_CONNECTED;
    }
}
