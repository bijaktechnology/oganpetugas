package com.bijak.techno.oganlopian.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.bijak.techno.oganlopian.R;
import com.bijak.techno.oganlopian.data.CustomMarkerAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import javax.annotation.Nullable;

public class LaporanmapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private ImageView avatar,img_kategori;
    private TextView namapelapor,judul,kategori,tanggal;
    private FloatingActionButton floatingActionButton;
    String nama,laptitle,kat,img_kat,lat,lng;
    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporanmaps);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Lokasi Laporan");
        toolbar.setSubtitle("Thread : "+getIntent().getStringExtra("judul"));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LaporanmapsActivity.this,LaporanwrgdtlActivity.class);
                i.putExtra("laporan_id",getIntent().getStringExtra("lap_id"));
                startActivity(i);
                finish();
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        img_kategori = (ImageView) findViewById(R.id.map_detail_img_category);
        kategori = (TextView) findViewById(R.id.map_detail_txt_category);
        judul = (TextView) findViewById(R.id.map_detail_txt_title);
        tanggal = (TextView) findViewById(R.id.map_detail_txt_address_time);
        laptitle = getIntent().getStringExtra("judul");
        namapelapor = (TextView) findViewById(R.id.map_detail_txt_status);
        namapelapor.setText(getIntent().getStringExtra("stat_lap"));
        judul.setText(laptitle);
        img_kat = getIntent().getStringExtra("img_kat");
        img_kategori.setImageResource(Integer.valueOf(img_kat));
        kategori.setText(getIntent().getStringExtra("kategori"));
        tanggal.setText(getIntent().getStringExtra("lokasi")+"\n"+getIntent().getStringExtra("tanggal"));
        lat = getIntent().getStringExtra("lat");
        lng = getIntent().getStringExtra("lng");
        floatingActionButton=findViewById(R.id.map_detail_btn_my_location);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMap != null)
                {
                    if(locationButton != null)
                        locationButton.callOnClick();
                }
            }
        });

    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_save:
                //_updateProfile();
                break;
            case android.R.id.home:
                Intent i = new Intent(LaporanmapsActivity.this,LaporanwrgdtlActivity.class);
                i.putExtra("laporan_id",getIntent().getStringExtra("lap_id"));
                startActivity(i);
                this.finish();
                break;
        }
        return true;
    }

    private View locationButton;
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Integer zoom =14;
        mMap.setMyLocationEnabled(true);
        CustomMarkerAdapter mAdapter = new CustomMarkerAdapter(this);
        mMap.setInfoWindowAdapter(mAdapter);
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(Double.valueOf(lat), Double.valueOf(lng));
        mMap.addMarker(new MarkerOptions()
                .position(sydney)
                .title(laptitle.toString())
                .snippet(getIntent().getStringExtra("gbr_lap"))
        );
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney,zoom));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, zoom));
        // get your maps fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        // Extract My Location View from maps fragment
        locationButton = mapFragment.getView().findViewById(R.id.map_detail_btn_my_location);
        // Change the visibility of my location button
        if(locationButton != null)
            locationButton.setVisibility(View.GONE);
    }
    public static Location getLocationWithCheckNetworkAndGPS(Context mContext) {
        LocationManager lm = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
        //assert lm != null;
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
}
