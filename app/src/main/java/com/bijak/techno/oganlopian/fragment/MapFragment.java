package com.bijak.techno.oganlopian.fragment;

import android.Manifest;
import android.animation.FloatEvaluator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.CycleInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bijak.techno.oganlopian.R;
import com.bijak.techno.oganlopian.activity.ChatDetailsActivity;
import com.bijak.techno.oganlopian.activity.HomeActivity;
import com.bijak.techno.oganlopian.activity.LoginActivity;
import com.bijak.techno.oganlopian.activity.MapsActivity;
import com.bijak.techno.oganlopian.data.HttpGetUrl;
import com.bijak.techno.oganlopian.data.InfoWindowAdapter;
import com.bijak.techno.oganlopian.data.SettingsAPI;
import com.bijak.techno.oganlopian.model.Friend;
import com.bijak.techno.oganlopian.model.LaporanModel;
import com.bijak.techno.oganlopian.model.Umume;
import com.bijak.techno.oganlopian.service.ConnectivityReceiver;
import com.bijak.techno.oganlopian.service.LocationMonitoringService;
import com.bijak.techno.oganlopian.util.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.bijak.techno.oganlopian.util.Constants.PREF_MY_ID;


public class MapFragment extends Fragment implements ConnectivityReceiver.ConnectivityReceiverListener {

    private static final String TAG = null;
    private GoogleMap mMap;
    private GroundOverlay mRedPoint = null;
    private Marker[] marker;
    private Marker vmarker;
    private Marker marker_petugas;
    SettingsAPI set;
    private ProgressBar progressBar;
    private ProgressDialog progress;
    private SupportMapFragment mapFragment;
    private boolean lagiTugas = false;
    public MapFragment() {
        // Required empty public constructor
    }
    List<Friend> friendList;
    private static MapFragment instance = null;

    public static MapFragment getInstance() {
        return instance;
    }

    //private SessionManager sesi;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        set = new SettingsAPI(getContext());
        friendList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_store, container, false);
        Location lc = getLocationWithCheckNetworkAndGPS(getActivity());
        double latitudex =(lc==null)?0: lc.getLatitude();
        double longitudex =(lc==null)?0: lc.getLongitude();
        lagiTugas=false;
        if (latitudex != 0 && longitudex != 0) {
            getLokasi(latitudex, longitudex);
        }else {
            getLokasi(Constants.defaultLat, Constants.defaultLng);
        }
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String latitude = intent.getStringExtra(LocationMonitoringService.EXTRA_LATITUDE);
                        String longitude = intent.getStringExtra(LocationMonitoringService.EXTRA_LONGITUDE);

                        if (latitude != null && longitude != null) {
                            //mMsgView.setText(getString(R.string.msg_location_service_started) + "\n Latitude : " + latitude + "\n Longitude: " + longitude);
                            Activity activity = getActivity();
                            if(activity!=null) {
                                getLokasi(Double.valueOf(latitude), Double.valueOf(longitude));
                            }
                        }
                    }
                }, new IntentFilter(LocationMonitoringService.ACTION_LOCATION_BROADCAST)
        );

        //TODO: tampilkan popup status jika sedang melayani customer (Status P)
        _checkStatusPetugas();
        return rootView;
    }

    private void _checkStatusPetugas() {
        String petugasID = set.readSetting(Constants.PREF_MY_ID);
        String petugasRole = "bidan";//set.readSetting(Constants.PREF_MY_ROL);
        String Where =petugasRole.toLowerCase()+"/1?petugas_id="+petugasID+"&booking_status=P";
        new getStatusPetugas(getContext()).execute(ConnectivityReceiver.API_URL+"posisipetugas/petugas/"+Where );
    }
    private GoogleMap getLokasi(final double Lat, final double Lng) {
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frg);  //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            //Marker vmarker;
            @Override
            public void onMapReady(final GoogleMap mMap) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                mMap.clear(); //clear old marker

                if(set.readSetting(Constants.PREF_MY_ID).equals("na")){
                    startActivity(new Intent(getContext(), LoginActivity.class));
                }
                HashMap<String, String> data;
                data = null;//sesi.getUserDetails();
                CameraPosition nLatLong = CameraPosition.builder().target(new LatLng(Lat, Lng)).zoom(14).build();
                mMap.clear();
                vmarker =null;
                switch (set.readSetting(Constants.PREF_MY_ROL)) {
                    case "BIDAN":
                    case "Bidan":
                        vmarker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Lat, Lng))
                                .title("Bidan " + set.readSetting(Constants.PREF_MY_NAME))
                                .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.nurse_female_48px)));
                        riples(mMap, new LatLng(Lat, Lng));
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(nLatLong));
                        vmarker.showInfoWindow();
                        getBooking(mMap, "bidan", vmarker);
                        break;
                    case "DOKTER":
                    case "Dokter":
                        vmarker =mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Lat, Lng))
                                .title("Dokter " + set.readSetting(Constants.PREF_MY_NAME))
                                .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.doctor_male_24px)));
                        riples(mMap, new LatLng(Lat, Lng));
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(nLatLong));
                        vmarker.showInfoWindow();
                        getBooking(mMap, "dokter", vmarker);
                        break;
                    case "ADMIN":
                    case "ADMIN SYSTEM":
                    case "ADMIN DASHBOARD":
                    case "Operator":
                    case "Admin":
                        vmarker =mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Lat, Lng))
                                .title("Admin " + set.readSetting(Constants.PREF_MY_NAME))
                                .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.admin_settings_male_24px)));

                        riples(mMap, new LatLng(Lat, Lng));
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(nLatLong));
                        vmarker.showInfoWindow();

                        getBooking(mMap, "bidan", vmarker);
                        break;
                    case "PETUGAS DINKES":
                    case "Petugas":
                    case "Ambulance":
                        vmarker =mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Lat, Lng))
                                .title("Petugas Dinkes " + set.readSetting(Constants.PREF_MY_NAME))
                                .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ambulance_24px)));
                        riples(mMap, new LatLng(Lat, Lng));
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(nLatLong));
                        vmarker.showInfoWindow();
                        getBooking(mMap, "dinkes", vmarker);
                        break;

                    case "Damkar":
                        vmarker =mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Lat, Lng))
                                .title("Petugas Dinkes " + set.readSetting(Constants.PREF_MY_NAME))
                                .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.fire_truck_24px)));
                        riples(mMap, new LatLng(Lat, Lng));
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(nLatLong));
                        vmarker.showInfoWindow();
                        getBooking(mMap, "dinkes", vmarker);
                        break;
                    default:
                        vmarker =mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Lat, Lng))
                                .title("Petugas " + set.readSetting(Constants.PREF_MY_NAME))
                                .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.policeman_male_24px)));
                        riples(mMap, new LatLng(Lat, Lng));
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(nLatLong));
                        vmarker.showInfoWindow();
                        getBooking(mMap, "petugas", vmarker);
                        break;
                }

            }

        });
        return mMap;
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
    private void getBooking(GoogleMap mMap, String petugas, Marker vmarker) {
        if(!lagiTugas) {
            String where = "/" + petugas + "?custom=jarake<=20&booking_status=W&orderby=jarake";
            new getPosisi(MapFragment.this, mMap, vmarker).execute(ConnectivityReceiver.API_URL + "laporan/booking" + where);
        }else{
            _checkStatusPetugas();
        }
    }
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
    }
    /*cek apakah yang login sedang bertugas;
    jika iya maka tampilkan dialog*/
    public  class getStatusPetugas extends AsyncTask<String,Void,String>{
        public getStatusPetugas(Context context) {
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
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
            if (result != null) {
                try {
                    String jsonString = result;
                    JSONObject jsonObject = new JSONObject(jsonString);
                    if (Integer.valueOf(jsonObject.getString("totaldata")) > 0) {
                        tampilkanDialog(jsonObject);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void tampilkanDialog(JSONObject obj) throws JSONException, ParseException {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View view =inflater.inflate(R.layout.info_dialog,null);
        TextView txt = view.findViewById(R.id.alamat);
        TextView userp= view.findViewById(R.id.pemanggil);
        TextView dari= view.findViewById(R.id.daritgl);
        TextView durasi = view.findViewById(R.id.lamawaktu);
        ImageView img = view.findViewById(R.id.iv);
        String date = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(new Date());
        JSONArray arrD = obj.getJSONArray("message");
        //for(int i=0; i < arrD.length();i++){
            JSONObject objx = arrD.getJSONObject(0);
        //}
        txt.setText("Alamat : "+objx.getString("formatted_address"));
        userp.setText("Pemanggil : " + objx.getString("nama_lengkap"));
        dari.setText("Start Date : "+ objx.getString("booking_time"));
        durasi.setText("End Date : "+ date);
        dialog.setView(view);
        lagiTugas=true;
        dialog.setNegativeButton("Chat", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                _keHalamanChat("tugas",objx.toString());
            }
        });
        dialog.setPositiveButton("Complete Jobs", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //update status job jadi complete C
                final String booking_ids,catatan_petugase;
                try {
                    booking_ids = objx.getString("booking_id");
                    _update_panggilan(booking_ids);
                    dialog.dismiss();
                    Intent i = new Intent(getContext(), HomeActivity.class);
                    getActivity().finish();
                    getActivity().overridePendingTransition(0, 0);
                    startActivity(i);
                    getActivity().overridePendingTransition(0, 0);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
        /*dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Intent i = new Intent(getContext(), HomeActivity.class);
                getActivity().finish();
                getActivity().overridePendingTransition(0, 0);
                startActivity(i);
                getActivity().overridePendingTransition(0, 0);
            }
        });*/
        dialog.show();
    }
    private void _update_panggilan(String booking_ids) {
        Umume data= new Umume();
        data.setBooking_id(booking_ids);data.setBidan_id(set.readSetting(PREF_MY_ID));
        new panggilanUpdate(this).execute(data);
    }
    private void _keHalamanChat(String asal,String datax) {
        try {
            /*JSONObject jDatax = new JSONObject(datax);
            JSONObject jD=new JSONObject(jDatax.getString("dat-p"));
            Log.i("cek",datax);*/
            JSONObject jPasienx = new JSONObject(datax);
            friendList.clear();
            friendList.add(new Friend(jPasienx.getString("user_id"),jPasienx.getString("nama_lengkap"),Constants.IMG_URL));
            Log.i("pasien",friendList.toString());
            //registerUser(jPasienx.getString("user_id"),"User",jPasienx.getString("nama_user"));
            Intent chat = new Intent(getContext(),ChatDetailsActivity.class);
            chat.putExtra("chat-from",jPasienx.toString());
            chat.putExtra("chat-pass",friendList.toString());
            chat.putExtra("model",asal);
            startActivity(chat);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.i("error-chat",e.getMessage());
        }
    }
    //get data lokasi terakhir petugas sesuai login
    public class getPosisi extends AsyncTask<String, Void, String> {
        Context c;
        GoogleMap mMap;

        public getPosisi(MapFragment storeFragment, GoogleMap map, Marker mark) {
            mMap = map;
            marker_petugas = mark;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
                    InfoWindowAdapter mAdapter = new InfoWindowAdapter(getContext());
                    mMap.setInfoWindowAdapter(mAdapter);
                    String jsonString = result;
                    JSONObject jsonObject = new JSONObject(jsonString);
                    if (Integer.valueOf(jsonObject.getString("totaldata")) > 0) {
                        JSONArray arrD = jsonObject.getJSONArray("message");
                        ArrayList<HashMap<String, String>> feedList = new ArrayList<HashMap<String, String>>();
                        HashMap<String, String> map = new HashMap<String, String>();
                        List<Marker> lstMarcadores = new ArrayList<>();

                        Marker[] allMarker = new Marker[arrD.length()];
                        for (int i = 0; i < arrD.length(); i++) {
                            JSONObject obj = arrD.getJSONObject(i);
                            jData.put(obj.getString("nama_user"), obj);
                            LaporanModel app = new LaporanModel();
                            allMarker[i] = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(obj.getDouble("lat"), obj.getDouble("lng")))
                                    .title(obj.getString("nama_user"))
                                    .snippet(obj.getString("alamat"))
                                    .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.marker_48px)));
                            //riples(mMap,new LatLng(obj.getDouble("lat"), obj.getDouble("lng")));
                            //animIcon(R.drawable.marker_48px, allMarker[i]);
                            //allMarker[i].showInfoWindow();
                            allMarker[i].setTag(0);
                            Log.i("log", obj.toString());
                            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker markers) {
                                    String[] petugase = markers.getTitle().split("\\s");
                                    if (petugase[0].trim().toLowerCase().equals("bidan")) {
                                        return false;
                                    } else {
                                        //Toast.makeText(getContext(),petugase[0],Toast.LENGTH_LONG).show();
                                        markers.showInfoWindow();
                                        //mAdapter.onInfoWindowClick(markers);
                                        return true;
                                    }

                                }
                            });
                            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                @Override
                                public void onInfoWindowClick(Marker marker) {
                                    Bundle data = new Bundle();
                                    //HashMap<String, String> aarD = new HashMap<>();

                                    JSONObject jObj = new JSONObject();
                                    try {
                                        JSONObject jdB = jData.getJSONObject(marker.getTitle());
                                        jObj.put("lat-c", String.valueOf(marker.getPosition().latitude));
                                        jObj.put("lng-c", String.valueOf(marker.getPosition().longitude));
                                        jObj.put("nam-c", marker.getTitle());
                                        jObj.put("lat-p", String.valueOf(marker_petugas.getPosition().latitude));
                                        jObj.put("lng-p", String.valueOf(marker_petugas.getPosition().longitude));
                                        jObj.put("nam-p", marker_petugas.getTitle());
                                        jObj.put("dat-p", jdB.toString());

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    Toast.makeText(getContext(), marker.getTitle(), Toast.LENGTH_LONG).show();

                                    Intent tangani = new Intent(getContext(), MapsActivity.class);
                                    tangani.putExtra("message", jObj.toString());
                                    tangani.putExtra("pasien",marker.getTitle());
                                    tangani.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(tangani);
                                }
                            });

                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("error",e.getMessage());
                }
            }

        }
    }
    public void animIcon(int bitmap, final Marker marker) {
        Bitmap markerIcon = BitmapFactory.decodeResource(getResources(), bitmap);
        try {
            this.pulseMarker(markerIcon, marker, 1000);
        }catch (Exception e){
            e.printStackTrace();

        }
    }
    public void pulseMarker(final Bitmap markerIcon, final Marker marker, final long onePulseDuration) {
        final Handler handler = new Handler();
        final long startTime = System.currentTimeMillis();
        try{
            final CycleInterpolator interpolator = new CycleInterpolator((int) 1f);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    long elapsed = System.currentTimeMillis() - startTime;
                    float t = interpolator.getInterpolation((float) elapsed / onePulseDuration);
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(scaleBitmap(markerIcon, 1f + 0.05f * t)));
                    handler.postDelayed(this, 16);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            Log.i("error-pulse",e.getMessage());
        }

    }
    public Bitmap scaleBitmap(Bitmap bitmap, float scaleFactor) {
        final int sizeX = Math.round(bitmap.getWidth() * scaleFactor);
        final int sizeY = Math.round(bitmap.getHeight() * scaleFactor);
        Bitmap bitmapResized = Bitmap.createScaledBitmap(bitmap, sizeX, sizeY, false);
        return bitmapResized;
    }
    public void riples(GoogleMap mMap, LatLng latLng) {
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                // if overlay already exists - remove it
                if (mRedPoint != null) {
                    mRedPoint.remove();
                }
                mRedPoint = showRipples(mMap, latLng, Color.RED);
            }
        });
    }
    public GroundOverlay showRipples(GoogleMap mGoogleMap, LatLng latLng, int color) {
        GradientDrawable d = new GradientDrawable();
        d.setShape(GradientDrawable.OVAL);
        d.setSize(500, 500);
        d.setColor(color);
        d.setStroke(0, Color.TRANSPARENT);

        final Bitmap bitmap = Bitmap.createBitmap(d.getIntrinsicWidth()
                , d.getIntrinsicHeight()
                , Bitmap.Config.ARGB_8888);

        // Convert the drawable to bitmap
        final Canvas canvas = new Canvas(bitmap);
        d.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        d.draw(canvas);

        // Radius of the circle for current zoom level and latitude (because Earth is sphere at first approach)
        double meters_to_pixels = (Math.cos(mGoogleMap.getCameraPosition().target.latitude * Math.PI / 180) * 2 * Math.PI * 6378137) / (256 * Math.pow(2, mGoogleMap.getCameraPosition().zoom));
        final int radius = (int) (meters_to_pixels * getResources().getDimensionPixelSize(R.dimen.dimen_40dp));

        // Add the circle to the map
        final GroundOverlay circle = mGoogleMap.addGroundOverlay(new GroundOverlayOptions()
                .position(latLng, 2 * radius).image(BitmapDescriptorFactory.fromBitmap(bitmap)));

        // Prep the animator
        PropertyValuesHolder radiusHolder = PropertyValuesHolder.ofFloat("radius", 1, radius);
        PropertyValuesHolder transparencyHolder = PropertyValuesHolder.ofFloat("transparency", 0, 1);

        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.setValues(radiusHolder, transparencyHolder);
        valueAnimator.setDuration(1500);
        valueAnimator.setEvaluator(new FloatEvaluator());
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedRadius = (float) valueAnimator.getAnimatedValue("radius");
                float animatedAlpha = (float) valueAnimator.getAnimatedValue("transparency");
                circle.setDimensions(animatedRadius * 2);
                circle.setTransparency(animatedAlpha);

            }
        });

        // start the animation
        valueAnimator.start();

        return circle;
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
    public void onDestroy(){
        super.onDestroy();
    }
    private class panggilanUpdate extends AsyncTask<Umume,Void,Void>{

        public panggilanUpdate(Fragment mapsActivity){

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressBar.setVisibility(View.VISIBLE);
        }
        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(Umume... params) {
            try{
                Umume dat = params[0];
                String petugas="";
                String role ="Bidan";//set.readSetting(Constants.PREF_MY_ROL);
                switch (role){
                    case "Ambulance": petugas="unit";break;
                    case "Damkar":
                    case "Petugas":petugas="petugas";break;
                    default:petugas=role.toLowerCase();break;
                }
                URL url1 = new URL(ConnectivityReceiver.API_URL + "posisipetugas/petugas/"+petugas);
                JSONObject postData = new JSONObject();
                postData.put("booking_id",dat.getBooking_id());
                //postData.put("bidan_id",dat.getBidan_id());
                //postData.put("keterangan",dat.getCatatan_petugas());
                postData.put("booking_status","C");
                Log.e("datane update",postData.toString());
                HttpURLConnection Conn = (HttpURLConnection)url1.openConnection();
                Conn.setReadTimeout(15000 /* milliseconds */);
                Conn.setConnectTimeout(15000 /* milliseconds */);
                Conn.setRequestMethod("PUT");
                Conn.setDoInput(true);
                Conn.setDoOutput(true);

                OutputStream os = Conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
                writer.write(Constants.getPostDataString(postData));

                writer.flush();
                writer.close();
                os.close();
                int responseCode = Conn.getResponseCode();
                if(responseCode==200){
                    /*_keHalamanChat("jobs");
                    dialog.show().dismiss();*/
                }

            }catch (MalformedURLException e){
                e.printStackTrace();
                //progressBar.setVisibility(View.GONE);
            } catch (JSONException e) {
                e.printStackTrace();
                //progressBar.setVisibility(View.GONE);
            } catch (IOException e){
                e.printStackTrace();
                //progressBar.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
                //progressBar.setVisibility(View.GONE);
            }
            return null;
        }
        protected void onPostExecute(){
            progressBar.setVisibility(View.GONE);

        }
    }
}
