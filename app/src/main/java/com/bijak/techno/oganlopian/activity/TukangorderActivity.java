package com.bijak.techno.oganlopian.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


import com.bijak.techno.oganlopian.R;

public class TukangorderActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private String tukang_name,tukang_ID;
    private ActionBar actionBar;
    private Button btnOrder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //prepareActionBar(toolbar);
        setContentView(R.layout.activity_tukangorder);
        toolbar =(Toolbar) findViewById(R.id.toolbar);
        btnOrder = findViewById(R.id.btn_order);
        tukang_name = getIntent().getStringExtra("tukang_nama");
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        getSupportActionBar().setTitle("Order Tukang");
        getSupportActionBar().setSubtitle(tukang_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private void prepareActionBar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(false);
        //actionBar.setHomeButtonEnabled(false);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:{
                Intent logoutIntent = new Intent(this, TukangbookingActivity.class);
                startActivity(logoutIntent);
                this.finish();
                return  true;
            }
        }
        return false;
    }
}
