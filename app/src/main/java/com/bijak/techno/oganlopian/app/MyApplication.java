package com.bijak.techno.oganlopian.app;

import android.app.Application;

import com.bijak.techno.oganlopian.service.ConnectivityReceiver;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;


public class MyApplication extends Application {
    private static MyApplication mInstance;
    @Override
    public void onCreate(){
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        mInstance=this;
    }
    public static synchronized MyApplication getInstance(){
        return mInstance;
    }
    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener){
        ConnectivityReceiver.connectivityReceiverListener=listener;
    }
}
