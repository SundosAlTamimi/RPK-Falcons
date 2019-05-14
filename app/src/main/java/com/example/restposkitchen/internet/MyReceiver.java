package com.example.restposkitchen.internet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.restposkitchen.MainActivity;

public class MyReceiver extends BroadcastReceiver {

    public static String status = "0";

    @Override
    public void onReceive(final Context context, Intent intent) {
        status = NetworkUtil.getConnectivityStatusString(context);
        Log.e("status.isEmpty()", "" + status.isEmpty());
//        if (status.isEmpty()) {
//            MainActivity.networkStateFollower.setText("0");
//            status = "0";//"No Internet Connection";
//            Log.e("status" , "" + status);
//        }
//        Toast.makeText(context, status, Toast.LENGTH_LONG).show();

    }
}