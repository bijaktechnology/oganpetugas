package com.bijak.techno.oganlopian.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.CycleInterpolator;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bijak.techno.oganlopian.R;
import com.bijak.techno.oganlopian.data.DirectionJSONParser;
import com.bijak.techno.oganlopian.data.HttpGetUrl;
import com.bijak.techno.oganlopian.data.SettingsAPI;
import com.bijak.techno.oganlopian.data.Tools;
import com.bijak.techno.oganlopian.model.Friend;
import com.bijak.techno.oganlopian.model.Umume;
import com.bijak.techno.oganlopian.service.ConnectivityReceiver;
import com.bijak.techno.oganlopian.util.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static com.bijak.techno.oganlopian.util.Constants.NODE_ID;
import static com.bijak.techno.oganlopian.util.Constants.NODE_NAME;
import static com.bijak.techno.oganlopian.util.Constants.NODE_PHOTO;
import static com.bijak.techno.oganlopian.util.Constants.NODE_ROLE;
import static com.bijak.techno.oganlopian.util.Constants.PREF_MY_ID;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {
    private GoogleMap mMap;
    private GroundOverlay mRedPoint = null;
    private Marker[] marker;
    private Marker vmarker;
    private Marker marker_petugas;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    List<Friend> friendList;
    public MapsActivity() {
        // Required empty public constructor
    }
    private static MapsActivity instance = null;
    public static MapsActivity getInstance() {
        return instance;
    }

    /*@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }*/
    private Boolean isFabOpen = false;
    private FloatingActionButton fab,fab1,fab2;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private ActionBar actionBar;
    //private GoogleMap mMap;
    private String datax,pasien,kode_booking;
    private ArrayList markerPoints= new ArrayList();
    private Double lat,lng,lat2,lng2;
    DatabaseReference ref2;
    private SettingsAPI set;
    private AlertDialog.Builder dialog;
    private LayoutInflater inflater;
    private View dialogView;
    ValueEventListener valueEventListener2;
    private EditText jks;
    private EditText nama_pasien,nik,keluhan,noted;
    private TextView alamat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.setRetainInstance(true);
        set = new SettingsAPI(this);
        datax = getIntent().getStringExtra("message");
        pasien = getIntent().getStringExtra("pasien");
        fab = findViewById(R.id.fab2);
        fab1= findViewById(R.id.fab3);
        fab2= findViewById(R.id.fab1);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);
        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Panggilan Process");
        toolbar.setSubtitle(pasien);

        toolbar.inflateMenu(R.menu.menu_maps);
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
                    case R.id.action_home:{
                        Intent logoutIntent = new Intent(getApplicationContext(), HomeActivity.class);
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
        friendList = new ArrayList<>();


    }
    private void prepareActionBar(Toolbar toolbar) {
        ((AppCompatActivity)getApplicationContext()).setSupportActionBar(toolbar);
        actionBar = ((AppCompatActivity)getApplicationContext()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setTitle("Panggilan");
    }

    /**
     * Create menu di toolbar
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_maps, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Ketika menu di klik
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_logout: {
                Intent logoutIntent = new Intent(this, SplashActivity.class);
                logoutIntent.putExtra("mode", "logout");
                startActivity(logoutIntent);
                finish();
                return true;
            }
            case R.id.action_home:{
                Intent logoutIntent = new Intent(this, HomeActivity.class);
                //logoutIntent.putExtra("mode", "logout");
                startActivity(logoutIntent);
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Fab button click
     * @param v
     */
    @Override
    public void onClick(View v){
        int id =v.getId();
        animaFAB(v);
        switch(id) {
            case R.id.fab2:
                Toast.makeText(this,"Ambil Layanan Ini",Toast.LENGTH_LONG).show();
                //munculkan dialog booking
                initDialog();
                break;
            case R.id.fab3:
                /**
                 * Buka tab chat dan otomatis chat with pemanggil (user booking)
                 */
                _keHalamanChat("peta");
                break;
            case R.id.fab1:
                //animaFAB(v);
                MapsActivity.this.finish();
                break;
        }
    }

    private void _keHalamanChat(String asal) {
        try {
            JSONObject jDatax = new JSONObject(datax);
            JSONObject jD=new JSONObject(jDatax.getString("dat-p"));
            Log.i("cek",datax);
            JSONObject jPasienx = new JSONObject(jDatax.getString("dat-p"));
            friendList.clear();
            friendList.add(new Friend(jPasienx.getString("user_id"),jPasienx.getString("nama_lengkap"),Constants.IMG_URL));
            Log.i("pasien",friendList.toString());
            registerUser(jPasienx.getString("user_id"),"User",jPasienx.getString("nama_user"));
            Intent chat = new Intent(MapsActivity.this,ChatDetailsActivity.class);
            chat.putExtra("chat-from",jPasienx.toString());
            chat.putExtra("chat-pass",friendList.toString());
            chat.putExtra("model",asal);
            startActivity(chat);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initDialog() {
        JSONObject jPasien = null;
        dialog = new AlertDialog.Builder(MapsActivity.this);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.form_dialog, null);
        nama_pasien = dialogView.findViewById(R.id.patient_data_inp_name);
        nik = dialogView.findViewById(R.id.patient_data_inp_nik);
        alamat = dialogView.findViewById(R.id.address_pasien);
        keluhan = dialogView.findViewById(R.id.data_keluhan);
        noted = dialogView.findViewById(R.id.catat_petugas);
        jks = dialogView.findViewById(R.id.patient_data_spinner_services);
        progressBar = dialogView.findViewById(R.id.progressBare);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("Panggilan dari "+ Tools.capitalize(pasien));
        kosong();
        try {
            JSONObject jData = new JSONObject(datax);
            jPasien = new JSONObject(jData.getString("dat-p"));
            kode_booking = jPasien.getString("booking_id");
            String role =set.readSetting(Constants.PREF_MY_ROL);
            String url="/"+role.toLowerCase()+"/?booking_id="+kode_booking;
            new getDetailPanggilan(this).execute(ConnectivityReceiver.API_URL+"laporan/booking"+url);
        }catch(Exception e){
            Log.i("error-d",e.getMessage());
        }
        dialog.setPositiveButton("Tangani Pasien", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                final String booking_ids,catatan_petugase;
                booking_ids = kode_booking;
                catatan_petugase = noted.getText().toString();
                _update_panggilan(booking_ids,catatan_petugase);
                dialog.dismiss();
                //goto chat page with auto send message to pasien
                _keHalamanChat("jobs");
            }

        });

        dialog.setNegativeButton("Batal", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(MapsActivity.this,HomeActivity.class);
                startActivity(intent);
            }
        });
        dialog.show();

    }
    private void _update_panggilan(String booking_ids, String catatan_petugase) {
        //String param ="?booking_id="+booking_ids+"&catatan="+catatan_petugase;
        Umume data= new Umume();
        data.setBooking_id(booking_ids);
        data.setCatatan_petugas(catatan_petugase);
        data.setBidan_id(set.readSetting(PREF_MY_ID));
        new panggilanUpdate(this).execute(data);
    }
    private void kosong() {}
    private void registerUser(String userID, final String jabatan, final String Nama) {

        valueEventListener2 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                final String usrNm = Nama;
                final String usrRo = jabatan;
                final String usrId = userID;
                final String usrDp = Constants.IMG_URL;

                if (!snapshot.hasChild(usrId)) {
                    ref2.child(usrId + "/" + NODE_NAME).setValue(usrNm);
                    ref2.child(usrId + "/" + NODE_PHOTO).setValue(usrDp);
                    ref2.child(usrId + "/" + NODE_ID).setValue(usrId);
                    ref2.child(usrId + "/" + NODE_ROLE).setValue(usrRo);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        ref2 = FirebaseDatabase.getInstance().getReference("users");
        ref2.addValueEventListener(valueEventListener2);

    }
    private void animaFAB(View v){
        int id =v.getId();
        if (isFabOpen){
            switch (id) {
                case R.id.fab2:
                    fab.startAnimation(rotate_backward);
                    isFabOpen=false;
                    break;
                case R.id.fab1:
                    fab2.startAnimation(rotate_backward);
                    isFabOpen=false;
                    break;
                case R.id.fab3:
                    fab1.startAnimation(rotate_backward);
                    isFabOpen=false;
                    break;
            }
        }else{
            switch (id) {
                case R.id.fab2:
                    fab.startAnimation(rotate_forward);
                    isFabOpen=true;
                    break;
                case R.id.fab1:
                    fab2.startAnimation(rotate_forward);
                    isFabOpen=true;
                    break;
                case R.id.fab3:
                    fab1.startAnimation(rotate_forward);
                    isFabOpen=true;
                    break;
            }
        }
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        String Petugas="";
        int icone = 0;
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        JSONObject jPasien = null;
        //ArrayList jPasien = new ArrayList();
        Marker marker,marker1;
        try {
            JSONObject jData = new JSONObject(datax);
            lat = jData.getDouble("lat-p");
            lng = jData.getDouble("lng-p");
            lat2 = jData.getDouble("lat-c");
            lng2 = jData.getDouble("lng-c");


            //Petugas =jData.getString("dat-p").replace("\\","");
            JSONObject jD=new JSONObject(jData.getString("dat-p"));
            Log.i("cek",datax);
            jPasien= new JSONObject(jData.getString("dat-p"));
            Petugas = jPasien.getString("user_role");
            //toolbar.setSubtitle(jPasien.getString("nama_user"));
            switch (Petugas) {
                case "BIDAN":
                case "Bidan":
                    icone = R.drawable.nurse_female_48px;
                    break;
                case "Dokter":
                    icone = R.drawable.doctor_male_48px;
                    break;
                case "Ambulance":
                    icone = R.drawable.fire_truck_24px;
                    break;
                default:
                    icone = R.drawable.fireman_male_48px;
                    break;
            }
            Log.i("data dari intent", jPasien.toString());

            LatLng sydney = new LatLng(lat, lng);
            //petugas data
            marker1=mMap.addMarker(new MarkerOptions()
                    .position(sydney)
                    .icon(bitmapDescriptorFromVector(this, icone))
            );
            animIcon(icone,marker1);
            //Float[] distance =0f;
            float[] results = null;
            //Location.distanceBetween(lat,lat2,lng,lng2,results);
            Integer maxZoom = 19;
            Integer zooml = 10;
            zooml = getBoundsZoomLevel(new LatLng(lat, lng), new LatLng(lat2, lng2), 256, 256);
            //Toast.makeText(this, "zoom level " + String.valueOf(zooml), Toast.LENGTH_LONG).show();
            zooml = (zooml > maxZoom) ? maxZoom : zooml;
            zooml = (zooml <= 10) ? 16 : (zooml + 1);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, zooml));
            if (markerPoints.size() > 2) {
                markerPoints.clear();
                mMap.clear();
            }
            marker =mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat2, lng2))
                    .icon(bitmapDescriptorFromVector(this,R.drawable.marker_48px))
                    .title(jPasien.getString("nama_user"))
                    .snippet(jPasien.getString("booking_time"))
            );
            marker.showInfoWindow();
            animIcon(R.drawable.marker_48px,marker);
            // Checks, whether start and end locations are captured
            //if (markerPoints.size() >= 2) {
            LatLng origin = new LatLng(lat, lng);//LatLng) markerPoints.get(0);
            LatLng dest = new LatLng(lat2, lng2);//(LatLng) markerPoints.get(1);

            // Getting URL to the Google Directions API
            String url = getDirectionsUrl(origin, dest);

            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    final static int GLOBE_WIDTH = 256; // a constant in Google's map projection
    final static int ZOOM_MAX = 21;
    public static int getBoundsZoomLevel(LatLng northeast,LatLng southwest,int width, int height) {
        double latFraction = (latRad(northeast.latitude) - latRad(southwest.latitude)) / Math.PI;
        double lngDiff = northeast.longitude - southwest.longitude;
        double lngFraction = ((lngDiff < 0) ? (lngDiff + 360) : lngDiff) / 360;
        double latZoom = zoom(height, GLOBE_WIDTH, Math.abs(latFraction));
        double lngZoom = zoom(width, GLOBE_WIDTH, lngFraction);
        double zoom = Math.min(Math.min(latZoom, lngZoom),ZOOM_MAX);
        return (int)(zoom);
    }
    private static double latRad(double lat) {
        double sin = Math.sin(lat * Math.PI / 180);
        double radX2 = Math.log((1 + sin) / (1 - sin)) / 2;
        return Math.max(Math.min(radX2, Math.PI), -Math.PI) / 2;
    }
    private static double zoom(double mapPx, double worldPx, double fraction) {
        final double LN2 = .693147180559945309417;
        return (Math.log(mapPx / worldPx / fraction) / LN2);
    }
    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);

        }
    }
    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionJSONParser parser = new DirectionJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            if(result!=null) {
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList();
                    lineOptions = new PolylineOptions();

                    List<HashMap<String, String>> path = result.get(i);

                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    lineOptions.addAll(points);
                    lineOptions.width(12);
                    lineOptions.color(Color.RED);
                    lineOptions.geodesic(true);

                }

                // Drawing polyline in the Google Map for the i-th route
                if (result.size() > 0) {
                    mMap.addPolyline(lineOptions);
                }
            }
        }
    }
    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String map_key="key=AIzaSyDItUB-2cEm_o7sSKnwoEpWU1mjfeRGSec";//+getString(R.string.google_maps_key);
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode+"&"+map_key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }
    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        if (vectorDrawable == null) {
            Log.e("error", "Requested vector resource was not found");
            return BitmapDescriptorFactory.defaultMarker();
        }
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
    public void animIcon(int bitmap, final Marker marker){
        Bitmap markerIcon = BitmapFactory.decodeResource(getResources(), bitmap);
        this.pulseMarker(markerIcon, marker, 1000);
    }
    public void pulseMarker(final Bitmap markerIcon, final Marker marker, final long onePulseDuration) {
        final Handler handler = new Handler();
        final long startTime = System.currentTimeMillis();

        final Interpolator interpolator = new CycleInterpolator(1f);
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = System.currentTimeMillis() - startTime;
                float t = interpolator.getInterpolation((float) elapsed / onePulseDuration);
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(scaleBitmap(markerIcon, 1f + 0.05f * t)));
                handler.postDelayed(this, 16);
            }
        });
    }
    public Bitmap scaleBitmap(Bitmap bitmap, float scaleFactor) {
        final int sizeX = Math.round(bitmap.getWidth() * scaleFactor);
        final int sizeY = Math.round(bitmap.getHeight() * scaleFactor);
        Bitmap bitmapResized = Bitmap.createScaledBitmap(bitmap, sizeX, sizeY, false);
        return bitmapResized;
    }
    private class getDetailPanggilan extends AsyncTask<String,Void,String>{

        public getDetailPanggilan(MapsActivity mapsActivity) {

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                HttpGetUrl getUrl = new HttpGetUrl();
                return getUrl.HttpGet(params[0]);

            } catch (IOException e) {
                return null;
            }
        }
        @Override
        protected void onPostExecute(String result) {
            JSONObject jData = new JSONObject();
            if(result!=null){
                try{
                    String jsonString = result;
                    JSONObject jsonObject = new JSONObject(jsonString);
                    if (Integer.valueOf(jsonObject.getString("totaldata")) > 0) {
                        JSONArray arrD = jsonObject.getJSONArray("message");
                        for(int i=0; i < arrD.length();i++){
                            JSONObject obj = arrD.getJSONObject(i);
                            nama_pasien.setText("Nama Pasien : "+obj.getString("nama_pasien"));
                            nik.setText("NIK : "+obj.getString("nik_pasien"));
                            alamat.setText("Alamat : "+obj.getString("formatted_address"));
                            keluhan.setText("Keterangan : "+obj.getString("catatan"));
                            String xx =(obj.getString("layanan_id"));
                            switch(xx){
                                case "1": jks.setText("Layanan kesehatan : BPJS");break;
                                case "2": jks.setText("Layanan kesehatan : JAMPIS");break;
                                default:jks.setText("Layanan kesehatan : UMUM");break;
                            }
                            noted.requestFocus();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("error",e.getMessage());
                }
                progressBar.setVisibility(View.GONE);
            }
        }
    }
    private class panggilanUpdate extends AsyncTask<Umume,Void,Void>{

        public panggilanUpdate(MapsActivity mapsActivity){

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected Void doInBackground(Umume... params) {
            try{
                Umume dat = params[0];
                String petugas="";
                String role =set.readSetting(Constants.PREF_MY_ROL);
                switch (role){
                    case "Ambulance": petugas="unit";break;
                    case "Damkar":
                    case "Petugas":petugas="petugas";break;
                    default:petugas=role.toLowerCase(Locale.getDefault());break;
                }
                URL url1 = new URL(ConnectivityReceiver.API_URL + "posisipetugas/petugas/"+petugas);
                JSONObject postData = new JSONObject();
                postData.put("booking_id",dat.getBooking_id());
                postData.put("bidan_id",dat.getBidan_id());
                postData.put("keterangan",dat.getCatatan_petugas());
                postData.put("booking_status","P");
                Log.e("datane update",postData.toString());
                HttpURLConnection Conn = (HttpURLConnection)url1.openConnection();
                Conn.setReadTimeout(15000 /* milliseconds */);
                Conn.setConnectTimeout(15000 /* milliseconds */);
                Conn.setRequestMethod("PUT");
                Conn.setDoInput(true);
                Conn.setDoOutput(true);

                OutputStream os = Conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
                writer.write(getPostDataString(postData));

                writer.flush();
                writer.close();
                os.close();
                int responseCode = Conn.getResponseCode();
                if(responseCode==200){
                    /*_keHalamanChat("jobs");
                    dialog.show().dismiss();*/
                }
                MapsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                    }
                });

            }catch (MalformedURLException e){
                MapsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e){
                MapsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(){
            progressBar.setVisibility(View.GONE);

        }
    }
    private String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while (itr.hasNext()) {

            String key = itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }

        return result.toString();
    }
}
