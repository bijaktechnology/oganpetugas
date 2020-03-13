package com.bijak.techno.oganlopian.fragment;

import android.Manifest;
import android.animation.FloatEvaluator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bijak.techno.oganlopian.R;
import com.bijak.techno.oganlopian.activity.ChatDetailsActivity;
import com.bijak.techno.oganlopian.activity.LoginActivity;
import com.bijak.techno.oganlopian.activity.MenuActivity;
import com.bijak.techno.oganlopian.data.CustomInfoWindowAdapter;
import com.bijak.techno.oganlopian.data.HttpGetUrl;
import com.bijak.techno.oganlopian.data.ParseFirebaseData;
import com.bijak.techno.oganlopian.data.SettingsAPI;
import com.bijak.techno.oganlopian.model.ChatMessage;
import com.bijak.techno.oganlopian.model.Friend;
import com.bijak.techno.oganlopian.model.LaporanModel;
import com.bijak.techno.oganlopian.model.Umume;
import com.bijak.techno.oganlopian.service.ConnectivityReceiver;
import com.bijak.techno.oganlopian.service.LocationMonitoringService;
import com.bijak.techno.oganlopian.util.Constants;
import com.bijak.techno.oganlopian.util.CustomToast;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.bijak.techno.oganlopian.util.Constants.NODE_IS_READ;
import static com.bijak.techno.oganlopian.util.Constants.NODE_RECEIVER_ID;
import static com.bijak.techno.oganlopian.util.Constants.NODE_TEXT;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapMonitorBooking extends Fragment implements ConnectivityReceiver.ConnectivityReceiverListener {
    private static final String TAG = null;
    private static final int OVERLAY_PERMISSION_CODE =5463  ;
    private GoogleMap mMap;
    private GroundOverlay mRedPoint = null;
    private Marker[] marker;
    private Marker vmarker;
    private Marker marker_petugas;
    private ProgressBar progressBar;
    private ProgressDialog progress;
    private SupportMapFragment mapFragment;
    private boolean lagiTugas = false, respon=false;
    SettingsAPI set;
    private String Petugas_ID,newMessage,chatNode_1,chatNode_2,petugase,chatNode,datax,NamaPetugas,booking_id;
    private List<ChatMessage> items = new ArrayList<>();
    private OnFragmentInteractionListener mListener;
    private Location location;
    private PopupWindow popupWindow;
    double posLat1,posLat2,posLang1,posLang2;
    private CustomToast tos;
    private DatabaseReference dbreff;
    private LayoutInflater inflater;
    private ParseFirebaseData pfbd;
    private TextView chatCount,txtComplete;
    private ImageView chate,complete,batal;
    private ProgressDialog dialogProgresse;
    public MapMonitorBooking() {}
    private static MapMonitorBooking instance = null;
    ValueEventListener valueEventListener;
    List<Friend> friendList;
    public static MapMonitorBooking getInstance() {
        return instance;
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        set = new SettingsAPI(getContext());
        Intent intent = new Intent();
        tos =new CustomToast(getContext());

        inflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        pfbd = new ParseFirebaseData(getContext());
        friendList = new ArrayList<>();

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map_monitor_booking, container, false);
        Location lc =  new Location(getLocationWithCheckNetworkAndGPS(getContext()));
        double latitudex =(lc==null)?0: lc.getLatitude();
        double longitudex =(lc==null)?0: lc.getLongitude();
        String kiriman = getActivity().getIntent().getStringExtra("dbooking");
        String ptgs = getActivity().getIntent().getStringExtra("petugas");
        if(kiriman!= null) {
            ArrayList<String> feedList = new ArrayList<String>(Arrays.asList(kiriman));
            ArrayList<String> petugas = new ArrayList<String>(Arrays.asList(ptgs));
            if (feedList.size() > 0) {
                booking_id = feedList.get(0).toString().replace("[", "").replace("]", "");
                petugase = petugas.get(0).replace("[", "").replace("]", "");
            }
        }
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
        chate = getActivity().findViewById(R.id.chat);
        //chate.setEnabled(false);
        txtComplete = getActivity().findViewById(R.id.txtCompleted);
        txtComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(petugase==null){
                    return;
                }
                Animation anim = AnimationUtils.loadAnimation(getContext(),R.anim.animbtn);
                txtComplete.startAnimation(anim);
                LayoutInflater frm = LayoutInflater.from(getContext());
                View prompt =frm.inflate(R.layout.form_complete_dialog,null);
                AlertDialog.Builder dialogs = new AlertDialog.Builder(getContext());
                dialogs.setView(prompt);
                final EditText pesan = prompt.findViewById(R.id.txtPesan);
                final TextView judul = prompt.findViewById(R.id.textView1);
                final RatingBar rtb = prompt.findViewById(R.id.simpleRatingBar);
                judul.setText("Anda sudah selesai dilayani "+petugase+" "+NamaPetugas+". Silahkan berikan penilaian dengan klik bintang di bawah ini");
                rtb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        pesan.setText(String.valueOf(rating));
                    }
                });
                dialogs
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        new AlertDialog.Builder(getContext())
                                                .setTitle("Confirm")
                                                .setMessage("Anda yakin akan mengakhiri layanan ini")
                                                .setCancelable(false)
                                                .setPositiveButton("Yakin", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogx, int which) {
                                                        _UpdateStatusBooking(booking_id,pesan.getText().toString(),"selesai");
                                                    }
                                                })
                                                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogx, int which) {
                                                        dialogx.cancel();
                                                    }
                                                })
                                                .show();
                                    }
                                })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog dialoge = dialogs.create();
                dialoge.show();

            }
        });
        dialogProgresse = new ProgressDialog(getContext());
        batal = (ImageView)getActivity().findViewById(R.id.dibatalkan);
        if(petugase != null){
            batal.setVisibility(View.GONE);
        }
        batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(getContext(),R.anim.animbtn);
                batal.startAnimation(anim);
                LayoutInflater frm = LayoutInflater.from(getContext());
                View prompt =frm.inflate(R.layout.form_complete_dialog,null);
                AlertDialog.Builder dialogs = new AlertDialog.Builder(getContext());
                dialogs.setView(prompt);
                final EditText pesan = prompt.findViewById(R.id.txtPesan);
                final TextView judul = prompt.findViewById(R.id.textView1);
                final RatingBar rtb = prompt.findViewById(R.id.simpleRatingBar);
                rtb.setVisibility(View.GONE);
                judul.setText("Anda yakin akan membatalkan pesanan ini?");
                dialogs
                        .setCancelable(false)
                        .setPositiveButton("Batalkan",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        new AlertDialog.Builder(getContext())
                                                .setTitle("Confirm")
                                                .setMessage("Anda yakin akan membatalkan Pesanan ini")
                                                .setCancelable(false)
                                                .setPositiveButton("Yakin", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogx, int which) {
                                                        _UpdateStatusBooking(booking_id,pesan.getText().toString(),"batal");
                                                    }
                                                })
                                                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogx, int which) {
                                                        dialogx.cancel();
                                                    }
                                                })
                                                .show();
                                    }
                                })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog dialoge = dialogs.create();
                dialoge.show();
            }
        });
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
                mMap.clear();
                vmarker =null;
                vmarker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Lat, Lng))
                        .title(set.readSetting(Constants.PREF_MY_NAME))
                        .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.ic_marker_generic)));
                posLat1=Lat; posLang1 = Lng;
                CameraPosition nLatLong = CameraPosition.builder().target(new LatLng(Lat, Lng)).zoom(14).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(nLatLong));
                //TODO:Tampilkan marker kordinat posisi terakhir dan status online
                //cek petugas apakah sudah ada yang ambil bokingannnya
                //berdasakan userid dengan status booking P
                    __cek_petugas(mMap,vmarker,posLat1,posLang1);
            }
        });
        return mMap;
    }
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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
    @Override
    public void onDestroy(){
        super.onDestroy();
    }
    private BitmapDescriptor bitmapDescriptorFromVector(@NonNull Context context, int vectorResId) {
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
    private void __cek_petugas(GoogleMap map,Marker marker,double pos1,double lg1){
        String where ="custom=length(petugas_id)>0&booking_status=P&user_id="+set.readSetting(Constants.PREF_MY_UID);
        //where +="&booking_status"
        new bookingan(this,map,marker,pos1,lg1).execute(ConnectivityReceiver.API_URL + "laporan/booking/medis/?" + where);
    }
    public class bookingan extends AsyncTask<String,Void,String>{
        Context c;
        GoogleMap mMap;
        public bookingan(MapMonitorBooking storeFragment, GoogleMap map, Marker mark,double lat1,double lg1) {
            mMap = map;
            marker_petugas = mark;
            posLat1=lat1;posLang1=lg1;
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
                    String jsonString = result;
                    JSONObject jsonObject = new JSONObject(jsonString);
                    View popupView = inflater.inflate(R.layout.popup_layout, null);
                    TextView txt= popupView.findViewById(R.id.tv);
                    int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                    int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                    boolean focusable = true; // lets taps outside the popup also dismiss it
                    final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                    dialog.setIcon(R.drawable.ic_laporan);
                    dialog.setTitle("Alert Message");
                    dialog.setCancelable(true);

                    if (Integer.valueOf(jsonObject.getString("totaldata")) > 0) {
                        JSONArray arrD = jsonObject.getJSONArray("message");
                        ArrayList<HashMap<String, String>> feedList = new ArrayList<HashMap<String, String>>();
                        HashMap<String, String> map = new HashMap<String, String>();
                        List<Marker> lstMarcadores = new ArrayList<>();
                        Log.i("result",arrD.toString());
                        Marker[] allMarker = new Marker[arrD.length()];
                        //LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);

                        for (int i = 0; i < arrD.length(); i++) {
                            //Petugase = "";
                            JSONObject obj = arrD.getJSONObject(i);
                            Petugas_ID =obj.getString("petugas_id");
                            //booking_id = obj.getString("booking_id");
                            if(Petugas_ID.equals("null")){
                                dialog.setMessage("Harap tunggu petugas meresponse panggilan anda");
                                batal.setVisibility(View.VISIBLE);
                            }else if(Petugas_ID.length()>0 && !"null".equals(Petugas_ID)){
                                batal.setVisibility(View.GONE);
                                if(respon==false) {
                                    dialog.setMessage("Petugas sudah merespons panggilan anda, Silahkan lihat message");
                                    respon=true;
                                }
                                chatNode_1 = set.readSetting(Constants.PREF_MY_UID) + "-" + Petugas_ID;
                                chatNode_2 = Petugas_ID + "-" + set.readSetting(Constants.PREF_MY_UID);
                                _cek_posisi_petugas(Petugas_ID, mMap, vmarker);
                                valueEventListener=new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Log.d(Constants.LOG_TAG,"Data changed from activity");
                                        if (dataSnapshot.hasChild(chatNode_1)) {
                                            chatNode = chatNode_1;
                                        } else if (dataSnapshot.hasChild(chatNode_2)) {
                                            chatNode = chatNode_2;
                                        } else {
                                            chatNode = chatNode_1;
                                        }
                                        int msg=0;
                                        items.clear();
                                        items.addAll(pfbd.getMessagesForSingleUser(dataSnapshot.child(chatNode)));
                                        for(DataSnapshot data : dataSnapshot.child(chatNode).getChildren()){
                                            if (data.child(NODE_RECEIVER_ID).getValue().toString().equals(set.readSetting(Constants.PREF_MY_UID))) {
                                                newMessage=(data.child(NODE_TEXT).getValue().toString());
                                                boolean isRead = Boolean.valueOf(data.child(NODE_IS_READ).getValue().toString());
                                                if(lagiTugas==false && isRead==false) {
                                                    txt.setText(newMessage);
                                                    //popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                                                    //_cek_posisi_petugas(petugase, mMap, vmarker);
                                                }
                                                if(isRead==false){
                                                    msg++;
                                                    //chate.setImageResource(R.drawable.topic_push_notification_48px);
                                                    chate.setImageDrawable(getResources().getDrawable(R.drawable.important_mail_48px));
                                                }
                                                chate.setEnabled(true);

                                            }
                                        }
                                        lagiTugas=true;
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                };
                                dbreff = FirebaseDatabase.getInstance().getReference(Constants.MESSAGE_CHILD);
                                dbreff.addValueEventListener(valueEventListener);

                            }
                        }

                    }
                    else {
                        dialog.setMessage("Harap menunggu petugas meresponse panggilan anda.");

                    }
                    AlertDialog alert = dialog.create();
                    if (alert.isShowing()) {
                        alert.dismiss();
                    } else {

                        if (Build.VERSION.SDK_INT >= 23) {
                            if (!Settings.canDrawOverlays(getActivity())) {

                                // Open the permission page
                                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                        Uri.parse("package:" + getActivity().getPackageName()));
                                startActivityForResult(intent, OVERLAY_PERMISSION_CODE);
                                return;
                            }
                            alert.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
                        }else{
                            alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                        }
                        //alert.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
                        if(respon==false) {
                            alert.show();
                        }
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("error", e.getMessage());
                }
            }
        }
    }
    private void _cek_posisi_petugas(String petugas_id,GoogleMap mMap, Marker vmarker){
        String where ="?booking_status=P&petugas_id="+petugas_id;
        new getPosisi(MapMonitorBooking.this, mMap, vmarker).execute(ConnectivityReceiver.API_URL + "posisipetugas/petugas/bidan/1/1" + where);
    }
    /**
     * Check Posisi lokasi bidan yang ambil panggilan saat ini
     * tampilkan di map
     * TODO: buat route dan estimasi jarak antara bidan dan pemanggil
     */
    public class getPosisi extends AsyncTask<String, Void, String> {
        Context c;
        int icone = 0;
        GoogleMap mMap;
        CustomInfoWindowAdapter mAdapter = new CustomInfoWindowAdapter(getActivity());
        public getPosisi(MapMonitorBooking storeFragment, GoogleMap map, Marker mark) {
            mMap = map;
            //marker_petugas = mark;
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
                            jData.put(obj.getString("nama_petugas"), obj);
                            LaporanModel app = new LaporanModel();
                            petugase =obj.getString("p_type");
                            switch (petugase) {
                                case "BIDAN":
                                case "Bidan":
                                case "bidan":
                                    icone = R.drawable.ic_bidan;
                                    break;
                                case "Dokter":
                                case "dokter":
                                    icone = R.drawable.ic_doctor;
                                    break;
                                case "Ambulance":
                                case "ambulance":
                                    icone = R.drawable.ic_semar;
                                    break;
                                default:
                                    icone = R.drawable.circled_user_male_24px;
                                    break;
                            }
                            posLat2 =obj.getDouble("lat");
                            posLang2= obj.getDouble("lng");
                            float jarak= tos.calculateDistance(posLat1,posLang1,posLat2,posLang2);
                            double zooms = calcZoom((int)jarak,256);
                            marker_petugas = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(obj.getDouble("lat"), obj.getDouble("lng")))
                                    .title(petugase+" "+obj.getString("nama_petugas"))
                                    .snippet("xx-Siap bertugas untuk anda,\nJarak dari lokasi anda ± "+String.format("%.2f",(jarak/1000))+" km")
                                    .icon(bitmapDescriptorFromVector(getContext(), icone)));
                            CameraPosition nLatLong = CameraPosition.builder().target(new LatLng(obj.getDouble("lat"), obj.getDouble("lng"))).zoom((float)zooms).build();
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(nLatLong));
                            riples(mMap, new LatLng(obj.getDouble("lat"), obj.getDouble("lng")));
                            Log.i("log", obj.toString());
                            //marker_petugas.showInfoWindow();
                            Petugas_ID =obj.getString("petugas_id");
                            NamaPetugas = obj.getString("nama_petugas");
                            booking_id = obj.getString("booking_id");
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


                        }

                        chate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //chatCount.setVisibility(View.GONE);
                                _keHalamanChat("monitor",Petugas_ID,NamaPetugas);
                            }
                        });
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("error",e.getMessage());
                }
            }

        }
    }

    /**
     * menghitung jarak antara 2 lokasi
     * @param visible_distance
     * @param img_width
     * @return
     */
    public double calcZoom(int visible_distance, int img_width)
    {
        // visible_distance -> in meters
        // img_width -> in pixels

        visible_distance = Math.abs(visible_distance);
        double equator_length = 40075016; // in meters

        // for an immage of 256 pixel pixel
        double zoom256 = Math.log(equator_length/visible_distance)/Math.log(2);

        // adapt the zoom to the image size
        int x = (int) (Math.log(img_width/256)/Math.log(2));
        double zoom = zoom256 + x;

        return zoom;
    }

    private void _keHalamanChat(String asal,String petugas_id,String namapetugas) {
        friendList.clear();
        friendList.add(new Friend(petugas_id, namapetugas, Constants.IMG_URL));
        Log.i("pasien", friendList.toString());
        //registerUser(jPasienx.getString("user_id"),"User",jPasienx.getString("nama_user"));
        Intent chat = new Intent(getContext(), ChatDetailsActivity.class);
        chat.putExtra("chat-from", petugas_id);
        chat.putExtra("chat-name", namapetugas);
        //chat.putExtra("chat-pass", friendList.toString());
        chat.putExtra("model", asal);
        startActivity(chat);


    }
    private void ImgBlink(ImageView img){
        Animation animation = new AlphaAnimation(1, 0); //to change visibility from visible to invisible
        animation.setDuration(1000); //1 second duration for each animation cycle
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE); //repeating indefinitely
        animation.setRepeatMode(Animation.REVERSE); //animation will start from end point once ended.
        img.startAnimation(animation); //to start animation
    }
    private void _UpdateStatusBooking(String id_booking,String pesan,String batal){
        Umume obj = new Umume();
        obj.setBooking_id(id_booking);
        obj.setPenilaian(pesan);
        obj.setKeterangan(batal);
        new update_booking().execute(obj);
    }
    private class update_booking extends AsyncTask<Umume,Void,Void>{

        @Override
        protected void onPreExecute() {
            //super.onPreExecute();
            dialogProgresse.setMessage("Update data in progess... please wait");
            dialogProgresse.show();
        }
        @Override
        protected Void doInBackground(Umume... umumes) {
            try{
                Umume data = umumes[0];
                URL url1 = new URL(ConnectivityReceiver.API_URL + "transaksi/bookingcomplete/"+petugase);
                JSONObject postData = new JSONObject();
                postData.put("booking_id", data.getBooking_id());
                if(data.getKeterangan()=="batal"){
                    //postData.put("booking_status","C");
                    postData.put("keterangan","dibatalkan");
                }
                postData.put("penilaian",data.getPenilaian());
                Log.e("datane update",postData.toString());
                HttpURLConnection Conn = (HttpURLConnection)url1.openConnection();
                Conn.setReadTimeout(30000 /* milliseconds */);
                Conn.setConnectTimeout(30000 /* milliseconds */);
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
                    Intent intent = new Intent(getActivity(), MenuActivity.class);
                    startActivity(intent);

                    if(dialogProgresse.isShowing()){
                        dialogProgresse.dismiss();
                    }
                }
            }catch (Exception e){
                Log.i("error",e.getMessage());
                if(dialogProgresse.isShowing()){
                    dialogProgresse.dismiss();
                }
                Toast.makeText(getContext(), e.getMessage(),Toast.LENGTH_LONG).show();
            }
            return null;
        }
        protected void onPostExecute(){
            if(dialogProgresse.isShowing()){
                dialogProgresse.dismiss();
            }
        }
    }
}
