package com.bijak.techno.oganlopian.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bijak.techno.oganlopian.R;
import com.bijak.techno.oganlopian.adapter.CustomAndroidGridViewAdapter;
import com.bijak.techno.oganlopian.data.SettingsAPI;
import com.bijak.techno.oganlopian.util.Constants;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.ArrayList;

public class ProfileDepanActivity extends AppCompatActivity {
    Toolbar toolbar;
    ActionBar actionBar;
    CollapsingToolbarLayout collapsingToolbarLayoutAndroid;
    CoordinatorLayout rootLayoutAndroid;
    GridView gridView;
    ImageView imgProfile;
    Context context;
    ArrayList arrayList;
    SettingsAPI settingsAPI;

    public static String[] gridViewStrings = {
            "Android",
            "Java",
            "GridView",
            "ListView",
            "Adapter",
            "Custom GridView",
            "Material",
            "XML",
            "Code",

    };
    public static int[] gridViewImages = {
            R.drawable.ic_profile82,
            R.drawable.ic_profile82,
            R.drawable.ic_profile82,
            R.drawable.ic_profile82,
            R.drawable.ic_profile82,
            R.drawable.ic_profile82,
            R.drawable.ic_profile82,
            R.drawable.ic_profile82,
            R.drawable.ic_profile82
    };
    private String[] idene ={"1","2","3","4","5","6","7","8","9"};
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        settingsAPI = new SettingsAPI(this);
        gridView = findViewById(R.id.grid);
        gridView.setAdapter(new CustomAndroidGridViewAdapter(this, gridViewStrings, gridViewImages,idene));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Log.i("clik",String.valueOf(position));
                Toast.makeText(getApplicationContext(), String.valueOf(position),Toast.LENGTH_LONG).show();
            }
        });

        imgProfile = findViewById(R.id.image_view);
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileDepanActivity.this,ProfileActivity.class);
                startActivity(intent);
            }
        });
        initInstances();
        initToolbar();
    }

    private void initInstances() {
        rootLayoutAndroid = findViewById(R.id.android_coordinator_layout);
        collapsingToolbarLayoutAndroid = findViewById(R.id.collapsing_toolbar_android_layout);
        collapsingToolbarLayoutAndroid.setTitle(settingsAPI.readSetting(Constants.PREF_MY_NAME));
        collapsingToolbarLayoutAndroid.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);


    }
    public void initToolbar() {
        actionBar = getSupportActionBar();
        actionBar.setTitle("Setting");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return true;
    }
}
