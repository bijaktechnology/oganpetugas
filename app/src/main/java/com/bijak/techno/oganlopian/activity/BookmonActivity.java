package com.bijak.techno.oganlopian.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bijak.techno.oganlopian.BuildConfig;
import com.bijak.techno.oganlopian.R;
import com.bijak.techno.oganlopian.data.SettingsAPI;
import com.bijak.techno.oganlopian.model.Friend;
import com.bijak.techno.oganlopian.service.LocationMonitoringService;
import com.bijak.techno.oganlopian.util.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

public class BookmonActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private SettingsAPI set;
    private static final String TAG = HomeActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;//34;
    private boolean mAlreadyStartedService = false;
    private TextView complete;
    private ImageView chats,batal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmonitoring);
        //set toolbar
        toolbar = findViewById(R.id.toolbar);
        set = new SettingsAPI(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Booking Monitoring");
        getSupportActionBar().setSubtitle(set.readSetting(Constants.PREF_MY_NAME));
        //load fragment
        initComponent();
        /*complete =findViewById(R.id.txtCompleted);
        chats = findViewById(R.id.chat);
        batal =(ImageView) findViewById(R.id.dibatalkan);*/


    }
    private void initComponent() {
        androidx.fragment.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //com.bijak.techno.oganlopian.fragment.MapFragment ctf = new com.bijak.techno.oganlopian.fragment.MapFragment();
        com.bijak.techno.oganlopian.fragment.MapMonitorBooking ctf = new com.bijak.techno.oganlopian.fragment.MapMonitorBooking();

        fragmentTransaction.add(R.id.frame_container, ctf,"Booking Monitoring");
        fragmentTransaction.commit();

    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home: {
                doExitApp();
                this.finish();
                return true;
            }
        }
        return  false;
    }
    @Override
    public void onResume() {
        super.onResume();
    }
    public void doExitApp() {
        Intent inten = new Intent(BookmonActivity.this,MenuActivity.class);
        startActivity(inten);
        this.finish();
    }
    @Override
    public void onBackPressed() {
        doExitApp();
    }




}
