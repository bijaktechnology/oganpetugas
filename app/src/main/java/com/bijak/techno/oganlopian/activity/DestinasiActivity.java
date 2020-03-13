package com.bijak.techno.oganlopian.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bijak.techno.oganlopian.R;
import com.bijak.techno.oganlopian.adapter.DestinasiAdapter;
import com.bijak.techno.oganlopian.data.HttpGetUrl;
import com.bijak.techno.oganlopian.data.SettingData;
import com.bijak.techno.oganlopian.model.DestinasiModel;
import com.bijak.techno.oganlopian.service.ConnectivityReceiver;
import com.bijak.techno.oganlopian.util.CustomToast;
import com.bijak.techno.oganlopian.widget.DividerItemDecoration;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.services.concurrency.AsyncTask;

public class DestinasiActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ActionBar actionBar;
    private ProgressDialog dialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private List<DestinasiModel> destinasiModels;
    private DestinasiAdapter destinasiAdapter;
    private CustomToast customToast;
    private TabLayout mTabLayout;
    private String asal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destinasi);
        toolbar = findViewById(R.id.toolbar);
        dialog = new ProgressDialog(this);
        asal = getIntent().getStringExtra("ldg");
        destinasiModels = new ArrayList<>();
        mTabLayout = findViewById(R.id.tabs);
        recyclerView = findViewById(R.id.destinasi_list);
        swipeRefreshLayout = findViewById(R.id.destinasi_swip);
        customToast = new CustomToast(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Destinasi Wisata");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupTabLayout();
        initializeRecyclerView(destinasiModels);

        _getDestinasi("lod", 0);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        _getDestinasi("swipe",mTabLayout.getSelectedTabPosition());
                    }
                },5000);
            }
        });

    }
    private void initializeRecyclerView(List<DestinasiModel> dm) {

        recyclerView = findViewById(R.id.destinasi_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new DestinasiAdapter(dm));
    }
    private void setupTabLayout() {

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                onTabTapped(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                onTabTapped(tab.getPosition());
            }
        });
    }

    private void onTabTapped(int position) {
        _getDestinasi("lod",position);
    }
    private void _getDestinasi(String asal,int pos){
        String where;
        switch (pos){
            case 2: where ="?kategori=akomodasi";break;
            case 1: where ="?kategori=kuliner";break;
            default: where ="?kategori=wisata";break;
        }
        new destinasiW(asal,this).execute(ConnectivityReceiver.API_URL+"laporan/destinasi"+where);
    }

    public class destinasiW extends AsyncTask<String,Void,String>{
        String darimana=null;

        public destinasiW(String asal,DestinasiActivity context){
            darimana = asal;
        }
        protected void onPreExecute() {
            super.onPreExecute();
            if(darimana.equals("lod")) {
                dialog.setMessage("Loading detail ... please wait");
                dialog.show();
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
            }
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                HttpGetUrl getUrl = new HttpGetUrl();
                return getUrl.HttpGet(params[0]);

            } catch (IOException e) {
                Log.i("error-api",e.getMessage());
                return null;
            }
        }
        @Override
        protected void onPostExecute(String result) {
            JSONObject jData = new JSONObject();
            if (result != null) {
                try {
                    String jsonString = result;
                    ArrayList<String> jarak =new ArrayList<>();
                    destinasiModels = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(jsonString);
                    Location lc = getLocationWithCheckNetworkAndGPS(getApplicationContext());
                    if (Integer.valueOf(jsonObject.getString("totaldata")) > 0) {
                        JSONArray arrD = jsonObject.getJSONArray("message");
                        for (int i = 0; i < arrD.length(); i++) {
                            JSONObject obj = arrD.getJSONObject(i);
                            double lati =(lc==null)?0: lc.getLatitude();
                            double langi=(lc==null)?0: lc.getLongitude();
                            Float dst = customToast.calculateDistance(Double.valueOf(obj.getString("lat")),Double.valueOf(obj.getString("lng")),
                                    lati,langi);
                            String distInKm = String.format("%.2f",(dst/1000));
                            DestinasiModel md = new DestinasiModel(obj.getString("nama_destinasi_wisata"),obj.getString("alamat"),
                                    distInKm +" KM", obj.getString("image"),obj.getString("id"));

                            destinasiModels.add(md);
                            jarak.add(i,distInKm);
                            Log.i("data-"+i,md.toString());
                        }
                        destinasiAdapter = new DestinasiAdapter(destinasiModels);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),DividerItemDecoration.VERTICAL_LIST));
                        recyclerView.setAdapter(destinasiAdapter);
                        recyclerView.addOnItemTouchListener(new SettingData(getApplicationContext(), recyclerView, new SettingData.ClickListener() {
                            @Override
                            public void onClick(View view, int position) {
                                DestinasiModel dm = destinasiModels.get(position);
                                Intent intent = new Intent(DestinasiActivity.this,DestinasidetailActivity.class);
                                JSONObject obj = new JSONObject();
                                try {
                                    obj = arrD.getJSONObject(position);
                                    intent.putExtra("title",obj.getString("nama_destinasi_wisata"));
                                    intent.putExtra("alamat",obj.getString("alamat"));
                                    intent.putExtra("deskripsi",obj.getString("deskripsi"));
                                    intent.putExtra("destid",obj.getString("id"));
                                    intent.putExtra("latid",obj.getString("lat"));
                                    intent.putExtra("langi",obj.getString("lng"));
                                    intent.putExtra("dst", jarak.get(position));
                                    intent.putExtra("latlng",lc.toString());
                                    startActivity(intent);
                                    finish();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onLongClick(View view, int position) {

                            }
                        }));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("error", e.getMessage());
                }

            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_save:
                //_updateProfile();
                break;
            case android.R.id.home:
                Intent i = new Intent(DestinasiActivity.this,MenuActivity.class);
                startActivity(i);
                this.finish();
                break;
        }
        return true;
    }
    public static Location getLocationWithCheckNetworkAndGPS(Context mContext) {
        LocationManager lm = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
        assert lm != null;
        Boolean isGpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        Boolean isNetworkLocationEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Location networkLoacation = null, gpsLocation = null, finalLoc = null;
        if (isGpsEnabled)
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return null;
            }
        gpsLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (isNetworkLocationEnabled)
            networkLoacation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (gpsLocation != null && networkLoacation != null) {

            //smaller the number more accurate result will
            if (gpsLocation.getAccuracy() > networkLoacation.getAccuracy())
                return finalLoc = networkLoacation;
            else
                return finalLoc = gpsLocation;

        } else {

            if (gpsLocation != null) {
                return finalLoc = gpsLocation;
            } else if (networkLoacation != null) {
                return finalLoc = networkLoacation;
            }
        }
        return finalLoc;
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }
}
