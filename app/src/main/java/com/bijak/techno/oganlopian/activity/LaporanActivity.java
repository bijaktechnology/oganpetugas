package com.bijak.techno.oganlopian.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bijak.techno.oganlopian.R;
import com.bijak.techno.oganlopian.adapter.CustomAndroidGridViewAdapter;
import com.bijak.techno.oganlopian.data.HttpGetUrl;
import com.bijak.techno.oganlopian.data.SettingsAPI;
import com.bijak.techno.oganlopian.service.ConnectivityReceiver;
import com.bijak.techno.oganlopian.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class LaporanActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ActionBar actionBar;
    private SettingsAPI settingsAPI;
    GridView gridView;
    ImageView imgProfile;
    Context context;
    private ProgressDialog dialog;
    public static String[] gridViewStrings = {};
    public static String[] gridViewImages = {};
    public static String[] gridViewID = {};
    public static int[] gridImages={};

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan);
        toolbar = findViewById(R.id.toolbar);
        settingsAPI = new SettingsAPI(this);
        dialog = new ProgressDialog(this);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Laporan Warga");
        actionBar.setSubtitle(settingsAPI.readSetting(Constants.PREF_MY_NAME));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        gridView = findViewById(R.id.grid);
        _getkategory();


        /*imgProfile = findViewById(R.id.image_view);
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(ProfileDepanActivity.this,ProfileActivity.class);
                //startActivity(intent);
            }
        });*/
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                Intent logoutIntent = new Intent(LaporanActivity.this, LaporanwargaActivity.class);
                logoutIntent.putExtra("jenis_lap",getIntent().getStringExtra("jenis_laporan"));
                startActivity(logoutIntent);
                break;
        }
        return true;
    }
    private void _getkategory(){
        new DataCategory().execute(ConnectivityReceiver.API_URL+"laporan/kategorilaporan?is_active=1");
    }
    public class DataCategory extends AsyncTask<String,Void,String>{

       public DataCategory(){

       }
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Loading data... please wait");
            //dialog.show();
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
                    ArrayList<String> img = new ArrayList<>();
                    ArrayList<String> ktg = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(jsonString);
                    //list.add("Semua Lokasi");
                    if (Integer.valueOf(jsonObject.getString("totaldata")) > 0) {
                        JSONArray arrD = jsonObject.getJSONArray("message");
                        for (int i = 0; i < arrD.length(); i++) {
                            JSONObject obj = arrD.getJSONObject(i);
                            list.add(obj.getString("nama_kategori"));
                            ktg.add(obj.getString("kategori_id"));
                            String namaIcon = obj.getString("icon").replace(".png","");
                            int resID = getResources().getIdentifier(namaIcon, "drawable", getPackageName());
                            img.add(String.valueOf(resID));

                        }
                        gridViewStrings = list.toArray(new String[list.size()]);
                        gridViewImages = img.toArray(new String[img.size()]);
                        gridViewID = ktg.toArray(new String[ktg.size()]);
                        gridImages = convert(gridViewImages);

                        gridView.setAdapter(new CustomAndroidGridViewAdapter(LaporanActivity.this, gridViewStrings, gridImages,gridViewID));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("error-json", e.getMessage());
                }
            }
            dialog.dismiss();
        }
    }
    private int[] convert(String[] string) {
        int number[] = new int[string.length];

        for (int i = 0; i < string.length; i++) {
            number[i] = Integer.parseInt(string[i]); // error here
        }
        return number;
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }
}
