package com.bijak.techno.oganlopian.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.TextView;

import com.bijak.techno.oganlopian.app.MyApplication;

import java.util.Calendar;

public class ConnectivityReceiver extends BroadcastReceiver {
    //public static final String API_URL="https://api.bijaktechnology.com/oganapi/index.php/";
    //public static final String API_URL="http://192.168.1.6:8080/oganapi/index.php/";
    //localhost
    public static final String API_URL="https://laporanwarga.oganlopian.purwakartakab.go.id/backend/index.php/";
    public static ConnectivityReceiverListener connectivityReceiverListener;
    public ConnectivityReceiver(){
        super();
    }
    @Override
    public void onReceive(Context context, Intent intent){
        ConnectivityManager connectivityManager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork=connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
        if(connectivityReceiverListener!=null){
            connectivityReceiverListener.onNetworkConnectionChanged(isConnected);
        }
    }
    public static boolean isConnected(){
        ConnectivityManager cm=(ConnectivityManager) MyApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo aktifNetwork = cm.getActiveNetworkInfo();
        return aktifNetwork != null && aktifNetwork.isConnectedOrConnecting();
    }

    public interface ConnectivityReceiverListener{
        void onNetworkConnectionChanged(boolean isConnected);
    }
    public static void dateToday(TextView txtTgl){

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        txtTgl.setText(new StringBuilder()
                .append(pad(day)).append("-")
                .append(pad(month + 1)).append("-")
                .append(year).append(" "));

    }
    private static String pad(int c) {
        if (c >= 10) {
            return String.valueOf(c);
        } else {
            return "0" + c;
        }
    }

}
