package com.bijak.techno.oganlopian.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bijak.techno.oganlopian.R;
import com.bijak.techno.oganlopian.data.SettingsAPI;
import com.bijak.techno.oganlopian.util.Constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TukangdetailActivity extends AppCompatActivity {

    private TextView namaTukang;
    private String s_tukangID,s_tukangNama;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private SettingsAPI settingsAPI;
    private Button ordButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tukangdetail);
        s_tukangID = getIntent().getStringExtra("id");
        s_tukangNama = getIntent().getStringExtra("nama");
        namaTukang = findViewById(R.id.nama_tukang);
        namaTukang.setText(s_tukangNama);
        //init toolbar
        toolbar = findViewById(R.id.toolbar);
        prepareActionBar(toolbar);
        settingsAPI = new SettingsAPI(this);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Profile Tukang");
        actionBar.setSubtitle(s_tukangNama);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ordButton =findViewById(R.id.btnOrder);
        ordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TukangdetailActivity.this,TukangorderActivity.class);
                intent.putExtra("tukang_id",s_tukangID);
                intent.putExtra("tukang_nama",s_tukangNama);
                startActivity(intent);
            }
        });
    }
    private void prepareActionBar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(false);
    }
}
