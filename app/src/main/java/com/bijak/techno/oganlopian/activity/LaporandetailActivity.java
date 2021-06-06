package com.bijak.techno.oganlopian.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bijak.techno.oganlopian.R;
import com.bijak.techno.oganlopian.data.SettingsAPI;
import com.bijak.techno.oganlopian.model.LaporanModel;
import com.bijak.techno.oganlopian.service.ConnectivityReceiver;
import com.bijak.techno.oganlopian.util.Constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static android.view.View.GONE;

public class LaporandetailActivity extends AppCompatActivity {

    private Button btnCapture,btnSend;
    private ImageView imgCapture;
    private TextView textView,kat_id,judul,deskripsi;
    private static final int Image_Capture_Code = 1;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private String kategori_nam,kategori_id,imgId;
    private FloatingActionButton fab1;
    private ProgressDialog dialog;
    private SettingsAPI settingsAPI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporandetail);
        dialog = new ProgressDialog(this);
        btnCapture =(Button)findViewById(R.id.button);
        btnSend =(Button)findViewById(R.id.button2);
        deskripsi =(TextView)findViewById(R.id.editText);
        judul =(TextView) findViewById(R.id.judulaporan);
        kat_id =(TextView)findViewById(R.id.kat_id);
        imgCapture = (ImageView) findViewById(R.id.imageView);
        kategori_nam = getIntent().getStringExtra("text");
        kategori_id = getIntent().getStringExtra("kategori_id");
        imgId = getIntent().getStringExtra("id");
        kat_id.setText(kategori_id);
        settingsAPI = new SettingsAPI(this);
        fab1 = findViewById(R.id.capture);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cInt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cInt,Image_Capture_Code);
                fab1.hide();
            }
        });
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Buat Laporan Baru");
        getSupportActionBar().setSubtitle("Kategori "+kategori_nam);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Location lc = getLocationWithCheckNetworkAndGPS(getApplicationContext());
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LaporanModel md = new LaporanModel();
                md.setComment(deskripsi.getText().toString());
                md.setJudul(judul.getText().toString());
                md.setKategori(kategori_id);
                md.setKeterangan(deskripsi.getText().toString());
                imgCapture.buildDrawingCache();
                Bitmap bmplap = imgCapture.getDrawingCache();
                md.setLatitud(String.valueOf(lc.getLatitude()));
                md.setLongitud(String.valueOf(lc.getLongitude()));
                md.setLoc_id(settingsAPI.readSetting(Constants.PREF_MY_UID));
                md.setGambarLaporan(Constants.bitmapToBase64String(bmplap,80));
                Log.i("laporan",md.toString());
                new KirimLaporan().execute(md);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Image_Capture_Code) {
            if (resultCode == RESULT_OK) {
                Bitmap bp = (Bitmap) data.getExtras().get("data");
                imgCapture.setImageBitmap(bp);
                btnSend.setEnabled(true);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                //btnCapture.setVisibility(View.VISIBLE);
                fab1.show();
            }
        } else {
            fab1.show();
            //btnCapture.setVisibility(View.VISIBLE);
        }
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_save:
                //_updateProfile();
                break;
            case android.R.id.home:
                Intent i = new Intent(LaporandetailActivity.this,LaporanActivity.class);
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
    public class KirimLaporan extends AsyncTask<LaporanModel,Void,Void>{
        public KirimLaporan(){

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setCancelable(false);
            dialog.setMessage("Kirim Laporan ... please wait");
            dialog.show();
        }
        @Override
        protected Void doInBackground(LaporanModel... laporanModels) {
            try{
                LaporanModel md=laporanModels[0];
                URL url1 = new URL(ConnectivityReceiver.API_URL + "transaksi/laporanwarga/");
                JSONObject postData = new JSONObject();
                postData.put("kategori_id",md.getKategori());
                postData.put("user_id",md.getLoc_id());
                postData.put("judul",md.getJudul());
                postData.put("keterangan",md.getKeterangan());
                postData.put("lat",md.getLatitud());
                postData.put("lng",md.getLongitud());
                postData.put("foto_laporan",md.getGambarLaporan());
                Log.e("datane update",postData.toString());
                HttpURLConnection Conn = (HttpURLConnection)url1.openConnection();
                Conn.setReadTimeout(15000 /* milliseconds */);
                Conn.setConnectTimeout(15000 /* milliseconds */);
                Conn.setRequestMethod("POST");
                Conn.setDoInput(true);
                Conn.setDoOutput(true);
                //Conn.setFixedLengthStreamingMode(postData.toString().getBytes().length);
                //Conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                OutputStream os = Conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
                writer.write(Constants.getPostDataString(postData));
                writer.flush();
                writer.close();
                os.close();
                int responseCode = Conn.getResponseCode();
                //if(responseCode==200){
                    dialog.dismiss();
                    Intent intent = new Intent(LaporandetailActivity.this,LaporanwargaActivity.class);
                    intent.putExtra("jenis_lap","2");
                    startActivity(intent);
                //}
            } catch (Exception e){
                e.printStackTrace();
            }
            if(dialog.isShowing()){
                dialog.dismiss();
            }
            return null;
        }
        protected void onPostExecute(){
            if(dialog.isShowing()){
                dialog.dismiss();
            }

        }
    }
}
