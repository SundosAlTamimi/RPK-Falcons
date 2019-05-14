package com.example.restposkitchen.internet;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;

import com.example.restposkitchen.MainActivity;

public class NetworkUtil {
    public static String status = "0";

    public static String getConnectivityStatusString(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Network network = connectivityManager.getActiveNetwork();
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)){
//                    MainActivity.networkStateFollower.setText("1");
                    status = "1";//"Wifi enabled";
                    return status;
                }
            }
        } else {
//            MainActivity.networkStateFollower.setText("0");
            status = "0";//"No internet is available";
            return status;
        }


//        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//        if (activeNetwork != null) {
//            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
//                MainActivity.networkStateFollower.setText("1");
//                status = "1";//"Wifi enabled";
//                return status;
//            }
//             else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
//                status = "Mobile data enabled";
//                return status;
//            }
//        } else {
//            MainActivity.networkStateFollower.setText("0");
//            status = "0";//"No internet is available";
//            return status;
//        }
        return status;
    }

}