package com.bijak.techno.oganlopian.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.bijak.techno.oganlopian.R;
import com.bijak.techno.oganlopian.data.HttpGetUrl;
import com.bijak.techno.oganlopian.model.DestinasiModel;
import com.bijak.techno.oganlopian.service.ConnectivityReceiver;
import com.bijak.techno.oganlopian.util.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LokasipentingActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = null;
    private GoogleMap mMap;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private Spinner spinner;
    private List<DestinasiModel> lokasipenting,kategori;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lokasipenting);
        toolbar = findViewById(R.id.toolbar);
        dialog = new ProgressDialog(this);
        toolbar.setTitle("Lokasi Penting");
        //toolbar.inflateMenu(R.menu.menu_maps);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logoutIntent = new Intent(getApplicationContext(), MenuActivity.class);
                //logoutIntent.putExtra("mode", "logout");
                startActivity(logoutIntent);
                finish();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.action_logout: {
                        Intent logoutIntent = new Intent(getApplicationContext(), SplashActivity.class);
                        logoutIntent.putExtra("mode", "logout");
                        startActivity(logoutIntent);
                        finish();
                        return true;
                    }
                    case android.R.id.home:{
                        Intent logoutIntent = new Intent(getApplicationContext(), MenuActivity.class);
                        //logoutIntent.putExtra("mode", "logout");
                        startActivity(logoutIntent);
                        finish();
                        return true;
                    }
                    default:
                        return false;
                }
            }
        });
        spinner = findViewById(R.id.pilihan);
        new dataSpinner().execute(ConnectivityReceiver.API_URL+"laporan/lokasipenting/1");
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getLokasi(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        /*SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);*/

    }

    private GoogleMap getLokasi(String kategori){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                mMap = googleMap;
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMap.clear();
                // Add a marker in Sydney and move the camera
                LatLng sydney = new LatLng(Constants.defaultLat, Constants.defaultLng);
                //mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                //mMap.addMarker(new MarkerOptions().position(sydney).title("Purwakarta"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 10));
                /*CameraPosition nLatLong = CameraPosition.builder().target(new LatLng(Constants.defaultLat, Constants.defaultLng)).zoom(12).build();
                mMap.clear();*/
                _tampilkanLokasi(mMap, kategori);
            }
        });
        return mMap;
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    private void _tampilkanLokasi(GoogleMap map,String kategori){
        String where="?nama_kategori="+kategori.replace(" ","%20");
        new lokasiPenting(this,map,kategori).execute(ConnectivityReceiver.API_URL+"laporan/lokasipenting"+where);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    public class lokasiPenting extends AsyncTask<String,Void,String>{
        private GoogleMap map;
        private Context context;
        Marker marker;
        String kategori;
        public lokasiPenting(LokasipentingActivity activity,GoogleMap nMap,String lokasi){
            this.map = nMap;
            this.context = activity;
            this.kategori=lokasi;
        }
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Loading data "+kategori+" ... please wait");
            dialog.show();
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
                    ArrayList<String> list = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(jsonString);
                    //list.add("Semua Lokasi");
                    if (Integer.valueOf(jsonObject.getString("totaldata")) > 0) {
                        JSONArray arrD = jsonObject.getJSONArray("message");
                        Marker[] allMarker = new Marker[arrD.length()];
                        for (int i = 0; i < arrD.length(); i++) {
                            JSONObject obj = arrD.getJSONObject(i);
                            int markere =0;
                            switch (obj.getString("kategori_id")){
                                case "1": markere=R.drawable.mark_rs;break;
                                case "2": markere=R.drawable.mark_atm_beras;break;
                                case "3": markere=R.drawable.mark_police;break;
                                case "4": markere=R.drawable.mark_puskes;break;
                                default: markere=R.drawable.marker_24px;break;
                            }
                            DestinasiModel dm = new DestinasiModel();
                            allMarker[i] = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(obj.getDouble("lat"), obj.getDouble("lng")))
                                    .title(obj.getString("nama_lokasi"))
                                    .snippet(obj.getString("alamat"))
                                    .icon(bitmapDescriptorFromVector(getApplicationContext(), markere)));

                            allMarker[i].setTag(0);
                            Log.i("log", obj.toString());
                            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker markers) {
                                        markers.showInfoWindow();
                                        //mAdapter.onInfoWindowClick(markers);
                                        return true;
                                }
                            });
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("error-json", e.getMessage());
                }
            }
            dialog.dismiss();
        }
    }

    /**
     * Generata dropdown kategori lokasi
     * ambil data nama kategori lokasi
     */
    public class dataSpinner extends AsyncTask<String,Void,String>{

        protected void onPreExecute() {
            super.onPreExecute();
            /*dialog.setMessage("Loading data ... please wait");
            dialog.show();*/
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
                    kategori = new ArrayList<>();
                    ArrayList<String> list = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(jsonString);
                    //list.add("Semua Lokasi");
                    if (Integer.valueOf(jsonObject.getString("totaldata")) > 0) {
                        JSONArray arrD = jsonObject.getJSONArray("message");
                        for (int i = 0; i < arrD.length(); i++) {
                            JSONObject obj = arrD.getJSONObject(i);
                            DestinasiModel dm = new DestinasiModel();
                            dm.setKategoriID(obj.getString("kategori_id"));
                            list.add(obj.getString("nama_kategori"));
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.item_spinner, list);
                        adapter.setDropDownViewResource(R.layout.item_spinner);
                        spinner.setAdapter(adapter);
                        getLokasi("Rumah Sakit");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("error-json", e.getMessage());
                }
            }
            dialog.dismiss();
        }
    }
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        if (vectorDrawable == null) {
            Log.e(TAG, "Requested vector resource was not found");
            return BitmapDescriptorFactory.defaultMarker();
        }
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
