package com.bijak.techno.oganlopian.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bijak.techno.oganlopian.BuildConfig;
import com.bijak.techno.oganlopian.R;
import com.bijak.techno.oganlopian.adapter.MenuDepanAdapter;
import com.bijak.techno.oganlopian.data.HttpGetUrl;
import com.bijak.techno.oganlopian.data.SettingData;
import com.bijak.techno.oganlopian.data.SettingsAPI;
import com.bijak.techno.oganlopian.model.LaporanModel;
import com.bijak.techno.oganlopian.model.MenuModel;
import com.bijak.techno.oganlopian.service.ConnectivityReceiver;
import com.bijak.techno.oganlopian.service.LocationMonitoringService;
import com.bijak.techno.oganlopian.util.Constants;
import com.bijak.techno.oganlopian.util.CustomToast;
import com.bijak.techno.oganlopian.widget.DividerItemDecoration;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MenuActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener,
        ViewPagerEx.OnPageChangeListener{
    private static final int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE =5469;
    private ActionBar actionBar;
    private Toolbar toolbar;
    ImageView setting,btnsetting;
    private TextView marque,nama_user;
    private ProgressDialog dialog;
    SettingsAPI set;
    private RecyclerView recyclerView;
    private List<MenuModel> laporanModels;
    private SwipeRefreshLayout refreshLayout;
    private MenuDepanAdapter menuDepanAdapter;
    private static final String TAG = HomeActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;//34;
    private boolean mAlreadyStartedService = false;
    SliderLayout sliderLayout ;
    private WebView webView;
    private String url = "https://purwakartakab.go.id",stikerTick="";
    Document doc;
    HashMap<String, String> HashMapForURL ;
    private Button sos112;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*setContentView(R.layout.activity_menu);*/
        setContentView(R.layout.item_mainmenu);
        marque = (TextView) findViewById(R.id.txtMarque);
        marque.setSelected(true);
        marque.setSingleLine(true);
        marque.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        webView =(WebView) findViewById(R.id.webviews);
        set = new SettingsAPI(this);
        recyclerView = findViewById(R.id.recyclerview_id);
        laporanModels = new ArrayList<>();
        dialog = new ProgressDialog(this);
        btnsetting =(ImageView) findViewById(R.id.btnSetting);
        btnsetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logoutIntent = new Intent(MenuActivity.this, SettingActivity.class);
                startActivity(logoutIntent);
                //finish();
            }
        });
        nama_user =(TextView)findViewById(R.id.usrLogin);
        nama_user.setText(set.readSetting(Constants.PREF_MY_NAME));

        sliderLayout = (SliderLayout)findViewById(R.id.slider);
        AddImagesUrlOnline();
        for(String name : HashMapForURL.keySet()){
            TextSliderView textSliderView = new TextSliderView(MenuActivity.this);
            textSliderView
                    .description(name)
                    .image(HashMapForURL.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle().putString("extra",name);
            sliderLayout.addSlider(textSliderView);
        }
        sliderLayout.setPresetTransformer(SliderLayout.Transformer.DepthPage);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.setDuration(6000);
        sliderLayout.addOnPageChangeListener(MenuActivity.this);
        getMainMenu();
        //new GetMenudepan(this).execute(ConnectivityReceiver.API_URL+"laporan/menudepan");
        __checkBookingan();
        new webData().execute();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setHorizontalScrollBarEnabled(false);
        sos112 =(Button) findViewById(R.id.main_menu_sos);
        sos112.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toDial="tel:112";
                startActivity(new Intent(Intent.ACTION_DIAL,Uri.parse(toDial)));
            }
        });
    }
    public void initToolbar(){
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle(set.readSetting(Constants.PREF_MY_NAME));
    }
    private void prepareActionBar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(false);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar_depan, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_logout: {
                Intent logoutIntent = new Intent(MenuActivity.this, SplashActivity.class);
                logoutIntent.putExtra("mode", "logout");
                startActivity(logoutIntent);
                logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
                return true;
            }
            case R.id.action_home: {
                Intent logoutIntent = new Intent(this, SettingActivity.class);
                startActivity(logoutIntent);
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private long exitTime = 0;
    public void doExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            new CustomToast(this).showInfo(getString(R.string.press_again_exit_app));
            exitTime = System.currentTimeMillis();
        } else {
            this.finish();
        }

    }
    @Override
    public void onBackPressed() {
        doExitApp();
    }
    @Override
    public void onSliderClick(BaseSliderView slider) {

    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }
    @Override
    public void onPageSelected(int position) {

    }
    @Override
    public void onPageScrollStateChanged(int state) {

    }
    private void getMainMenu(){
        JSONObject jData = new JSONObject();
        String result =loadJSONFromAsset();
        if (result != null) {
            try {
                String jsonString = result;
                String[] id_book ={};
                JSONObject jsonObject = new JSONObject(jsonString);

                if (Integer.valueOf(jsonObject.getString("totaldata")) > 0) {
                    JSONArray arrD = jsonObject.getJSONArray("message");
                    laporanModels = new ArrayList<>();
                    ArrayList<String> feedList = new ArrayList<String>();
                    for (int i = 0; i < arrD.length(); i++) {
                        JSONObject obj = arrD.getJSONObject(i);
                        MenuModel md = new MenuModel();
                        String namaIcon = obj.getString("icon");
                        int resID = getResources().getIdentifier(namaIcon, "drawable", getPackageName());
                        md.setIcone(resID);
                        md.setMenu_name(obj.getString("nama_menu"));
                        md.setMenu_urutan(obj.getString("urutan"));
                        md.setAlias(obj.getString("activity"));
                        laporanModels.add(md);
                    }
                    menuDepanAdapter = new MenuDepanAdapter(getApplicationContext(),laporanModels);
                    recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),3));
                    recyclerView.setAdapter(menuDepanAdapter);
                    recyclerView.addOnItemTouchListener(new SettingData(getApplicationContext(), recyclerView, new SettingData.ClickListener() {
                        @Override
                        public void onClick(View view, int position) {
                            MenuModel md = laporanModels.get(position);
                            switch (md.getAlias()){
                                case "Destinasi":
                                    Intent intentw = new Intent(MenuActivity.this,DestinasiActivity.class);
                                    startActivity(intentw);
                                    finish();
                                    break;
                                case "Laporan":
                                    Intent intentl = new Intent(MenuActivity.this,LaporanwargaActivity.class);
                                    intentl.putExtra("jenis_lap","1");
                                    startActivity(intentl);
                                    finish();
                                    break;
                                case "Lokasi":
                                    Intent intent = new Intent(MenuActivity.this, LokasipentingActivity.class);
                                    startActivity(intent);
                                    break;
                                case "Ambulance":
                                    Intent intentx = new Intent(MenuActivity.this,HomeActivity.class);
                                    intentx.putExtra("p_booking","Ambulance");
                                    startActivity(intentx);
                                    finish();
                                    break;
                                case "Bidan":
                                    Intent intentb = new Intent(MenuActivity.this,HomeActivity.class);
                                    intentb.putExtra("p_booking","Bidan");
                                    startActivity(intentb);
                                    finish();
                                    break;
                                case "Dokter":
                                    Intent intentd = new Intent(MenuActivity.this,HomeActivity.class);
                                    intentd.putExtra("p_booking","Dokter");
                                    startActivity(intentd);
                                    finish();
                                    break;
                                case "Tukang":
                                    Intent intentt = new Intent(MenuActivity.this,TukangActivity.class);
                                    startActivity(intentt);
                                    finish();
                                    break;
                            }
                        }

                        @Override
                        public void onLongClick(View view, int position) {

                        }
                    }));
                }
            } catch (final JSONException e) {
                Log.i("error-depan", e.getMessage());
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startStep1();
    }
    private void startStep1() {

        //Check whether this user has installed Google play service which is being used by Location updates.
        if (isGooglePlayServicesAvailable()) {

            //Passing null to indicate that it is executing for the first time.
            startStep2(null);

        } else {
            Toast.makeText(getApplicationContext(), R.string.no_google_playservice_available, Toast.LENGTH_LONG).show();
        }
    }
    /**
     * Step 2: Check & Prompt Internet connection
     */
    private Boolean startStep2(DialogInterface dialog) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            promptInternetConnect();
            return false;
        }


        if (dialog != null) {
            dialog.dismiss();
        }

        //Yes there is active internet connection. Next check Location is granted by user or not.

        if (checkPermissions()) { //Yes permissions are granted by the user. Go to the next step.
            startStep3();
        } else {  //No user has not granted the permissions yet. Request now.
            requestPermissions();
        }
        return true;
    }
    /**
     * Show A Dialog with button to refresh the internet state.
     */
    private void promptInternetConnect() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);
        builder.setTitle(R.string.title_alert_no_intenet);
        builder.setMessage(R.string.msg_alert_no_internet);

        String positiveText = getString(R.string.btn_label_refresh);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        //Block the Application Execution until user grants the permissions
                        if (startStep2(dialog)) {

                            //Now make sure about location permission.
                            if (checkPermissions()) {

                                //Step 2: Start the Location Monitor Service
                                //Everything is there to start the service.
                                startStep3();
                            } else if (!checkPermissions()) {
                                requestPermissions();
                            }

                        }
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    /**
     * Step 3: Start the Location Monitor Service
     */
    private void startStep3() {

        //And it will be keep running until you close the entire application from task manager.
        //This method will executed only once.

        if (!mAlreadyStartedService) {

            //mMsgView.setText(R.string.msg_location_service_started);

            //Start location sharing service to app server.........
            Intent intent = new Intent(this, LocationMonitoringService.class);
            startService(intent);

            mAlreadyStartedService = true;
            //Ends................................................
        }
    }
    /**
     * Return the availability of GooglePlayServices
     */
    public boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(this, status, 2404).show();
            }
            return false;
        }
        return true;
    }
    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState1 = ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION);

        int permissionState2 = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        return permissionState1 == PackageManager.PERMISSION_GRANTED && permissionState2 == PackageManager.PERMISSION_GRANTED;

    }
    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(MenuActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MenuActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(MenuActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(MenuActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * Start permissions requests.
     */
    private void requestPermissions() {

        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION);

        boolean shouldProvideRationale2 =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION);


        // Provide an additional rationale to the img_user. This would happen if the img_user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale || shouldProvideRationale2) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MenuActivity.this,
                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the img_user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MenuActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }
    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }
    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If img_user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Log.i(TAG, "Permission granted, updates requested, starting location updates");
                startStep3();

            } else {
                // Permission denied.

                // Notify the img_user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the img_user for permission (device policy or "Never ask
                // again" prompts). Therefore, a img_user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation, R.string.settings, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Build intent that displays the App settings screen.
                        Intent intent = new Intent();
                        intent.setAction(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
                        intent.setData(uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
            }
        }
    }
    /**
     * Check status booking
     */
    private void __checkBookingan() {
        String user_id = set.readSetting(Constants.PREF_MY_UID);
        String where ="user_id="+user_id;
        new bookingan(this).execute(ConnectivityReceiver.API_URL +"laporan/booking/medis/?"+where);
    }
    public class bookingan extends AsyncTask<String,Void,String>{

        public bookingan(MenuActivity activity){

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Check Booking Status....");
            dialog.show();
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
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
                    String[] id_book ={};
                    JSONObject jsonObject = new JSONObject(jsonString);
                    if (Integer.valueOf(jsonObject.getString("totaldata")) > 0) {
                        JSONArray arrD = jsonObject.getJSONArray("message");
                        ArrayList<String> feedList = new ArrayList<String>();
                        ArrayList<String> tbook = new ArrayList<String>();
                        for (int i = 0; i < arrD.length(); i++) {
                            JSONObject obj = arrD.getJSONObject(i);
                            String sts =obj.getString("booking_status");
                            if(sts.equals("W")|| sts.equals("P")){
                                feedList.add(obj.getString("booking_id"));
                                tbook.add(obj.getString("p_type"));
                            }
                        }
                        //ke halaman monitoring bookingan
                        if(feedList.size()>0) {
                            Intent intent = new Intent(MenuActivity.this, BookmonActivity.class);
                            intent.putExtra("dbooking", feedList.toString());
                            intent.putExtra("petugas",tbook.toString());
                            intent.putExtra("asal", "depan");
                            startActivity(intent);
                        }

                        if(dialog.isShowing()) {
                            dialog.dismiss();
                        }



                    }else{
                        if(dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                } catch (final JSONException e) {
                    Log.i("error-depan",e.getMessage());

                    if(dialog.isShowing()) {
                        dialog.dismiss();
                    }


                }
            }else{
                Toast.makeText(getApplicationContext(),"Check Koneksi internat",Toast.LENGTH_LONG).show();
                if(dialog.isShowing()) {
                    dialog.dismiss();
                }

            }
        }
    }
    public void AddImagesUrlOnline(){

        HashMapForURL = new HashMap<String, String>();

        HashMapForURL.put("Anti Hoax", "https://diskominfo.purwakartakab.go.id/panel/assets/images/64902f14bc5c3c9694bd6e9e36838d69.png");
        HashMapForURL.put("Smart City", "https://diskominfo.purwakartakab.go.id/panel/assets/images/40a4c3f6779acfbf1ff4d7db490de0d7.png");
        HashMapForURL.put("Call Center 112", "https://diskominfo.purwakartakab.go.id/panel/assets/images/10d670b398b9b5b85067685bc298ba44.png");
        //HashMapForURL.put("Froyo", "http://androidblog.esy.es/images/froyo-4.png");
        //HashMapForURL.put("GingerBread", "http://androidblog.esy.es/images/gingerbread-5.png");
    }
    /**
     * get data from web page
     */
    public class webData extends AsyncTask<Void,Void,Void>{
        String margue;

        @Override
        protected void  onPreExecute(){
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            try{
                Document web = Jsoup.connect(url).get();
                Elements element = web.select("li[class=newsticker__item]");
                int elemenSize = element.size();
                for(int i=0;i< elemenSize;i++){
                    Elements stik = web.select("li[class=newsticker__item]").eq(i);
                    stikerTick += stik.text()+"."+Constants.getWhiteSpace(5);
                }
                doc= web;// Jsoup.connect(url).get();
                    Elements elem = doc.select("div[class=row]");elem.remove();
                    Elements elem1 = doc.select("header[class=nav]");elem1.remove();
                    Elements elem2 = doc.select("div[class=featured-posts-grid]");elem2.remove();
                    Elements elem3 =doc.select("div[class=text-center pb-48]");elem3.remove();
                    Elements elem4 =doc.select("section[class=section mb-24]");elem4.remove();
                    Elements elem5 =doc.select("footer[class=footer]");elem5.remove();
                    Elements elem6 = doc.select("div[class=container]");elem6.remove();
                    Elements elem7 = doc.select("div[class=content-overlay]");elem7.remove();
                    Elements elem8 = doc.select("div[id=modal-sampurasun]");elem8.remove();
                    //Elements elem9 = doc.select("div[class=owl-prev]");elem9.remove();
                    //Elements elem10 = doc.select("div[class=owl-next]");elem10.remove();

            }catch (IOException e) {
                e.printStackTrace();
                Log.i("error webdata",e.getMessage());
            }
            return null;
        }
        protected void onPostExecute(Void result){
            marque.setText(stikerTick);
            webView.loadData(doc.toString(),"text/html", "utf-8");
        }
    }
    /*{
      "id": "6",
      "nama_menu": "Sampurasun Tukang",
      "urutan": "6",
      "activity": "Tukang",
      "icon": "ic_tukang",
      "row_status": "0",
      "created_time": "2019-09-14 14:47:09"
    },*/
    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getApplicationContext().getAssets().open("menu-depan.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                // You don't have permission
                checkPermission();
            } else {
                // Do as per your logic
            }

        }

    }
    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }
    }
}
