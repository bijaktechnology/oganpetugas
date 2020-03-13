package com.bijak.techno.oganlopian.fragment;

import android.Manifest;
import android.animation.FloatEvaluator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bijak.techno.oganlopian.R;
import com.bijak.techno.oganlopian.activity.LoginActivity;
import com.bijak.techno.oganlopian.data.CustomInfoWindowAdapter;
import com.bijak.techno.oganlopian.data.HttpGetUrl;
import com.bijak.techno.oganlopian.data.SettingsAPI;
import com.bijak.techno.oganlopian.model.LaporanModel;
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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MapTukangFragment extends Fragment implements ConnectivityReceiver.ConnectivityReceiverListener {
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
    private String Petugase;
    private OnFragmentInteractionListener mListener;
    private Location location;

    public MapTukangFragment() {
        // Required empty public constructor
    }
    private static MapTukangFragment instance = null;

    public static MapTukangFragment getInstance() {
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        set = new SettingsAPI(getContext());
        Intent intent = new Intent();
        Petugase =getActivity().getIntent().getStringExtra("text");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map_tukang, container, false);
        LocationManager lm = (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);
        Location lc = new Location(getLocationWithCheckNetworkAndGPS(getContext(),lm));
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
        return rootView;
    }
    private GoogleMap getLokasi(final double Lat, final double Lng) {
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frg);
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
                CameraPosition nLatLong = CameraPosition.builder().target(new LatLng(Lat, Lng)).zoom(13).build();
                mMap.clear();
                vmarker =null;
                vmarker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Lat, Lng))
                        .title(set.readSetting(Constants.PREF_MY_NAME))
                        .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_marker_generic)));
                riples(mMap, new LatLng(Lat, Lng));
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(nLatLong));
                //TODO:Tampilkan marker kordinat posisi terakhir dan status online petugas
                getBooking(mMap,Petugase,vmarker);
            }
        });
        return mMap;
    }
    private void getBooking(GoogleMap mMap, String petugas,Marker vmarker) {
        if(!lagiTugas) {
            String where = "?user_type=Tukang&user_role="+petugas.replace(" ","%20");
            new getPosisi(MapTukangFragment.this, mMap, vmarker).execute(ConnectivityReceiver.API_URL + "laporan/lokasi/1" + where);
        }else{
            // _checkStatusPetugas();
        }
    }
    public class getPosisi extends AsyncTask<String, Void, String> {
        Context c;
        GoogleMap mMap;
        CustomInfoWindowAdapter mAdapter = new CustomInfoWindowAdapter(getActivity());
        public getPosisi(MapTukangFragment storeFragment, GoogleMap map, Marker mark) {
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
                    //CustomInfoWindowAdapter mAdapter = new CustomInfoWindowAdapter(getActivity());
                    if(mAdapter!=null) {
                        mMap.setInfoWindowAdapter(mAdapter);
                    }
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
                            jData.put(obj.getString("nama_lengkap"), obj);
                            LaporanModel app = new LaporanModel();
                            double lama_ol = obj.getDouble("lama_ol");
                            String sts_ol =(lama_ol <48)?"Offline":"Online";
                            allMarker[i] = mMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(obj.getDouble("lat"), obj.getDouble("lng")))
                                            .title("Tukang "+obj.getString("nama_lengkap"))
                                            .icon(bitmapDescriptorFromVector(getContext(), (lama_ol < Constants.LAMA_ONLINE)?R.drawable.ic_tukang_on:R.drawable.ic_tukang)));


                            Log.i("log", obj.toString());
                            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker markers) {
                                    String[] petugase = markers.getTitle().split("\\s");
                                    if (petugase[0].trim().toLowerCase().equals("user")) {
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
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

    // TODO: Rename method, update argument and hook method into UI event
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    public static Location getLocationWithCheckNetworkAndGPS(Context mContext,LocationManager lm) {
        //LocationManager lm = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
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
    private BitmapDescriptor bitmapDescriptorFromVector(@Nullable Context context, int vectorResId) {
        if(vectorResId <0 || context ==null){
            Log.e(TAG, "Requested vector resource was not found");
            return BitmapDescriptorFactory.defaultMarker();
        }
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
}
