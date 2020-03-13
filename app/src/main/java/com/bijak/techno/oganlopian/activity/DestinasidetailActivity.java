package com.bijak.techno.oganlopian.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bijak.techno.oganlopian.R;
import com.bijak.techno.oganlopian.data.HttpGetUrl;
import com.bijak.techno.oganlopian.model.DestinasiModel;
import com.bijak.techno.oganlopian.service.ConnectivityReceiver;
import com.bijak.techno.oganlopian.util.Constants;
import com.google.android.gms.common.util.Strings;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.ErrorHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DestinasidetailActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ActionBar actionBar;
    private String gambar,distanced,latlng,judul,addr,desk,lat,lng,destid;
    private ImageView imageView;
    private TextView title,alamat,jarake,deskripsi;
    private FloatingActionButton fab2Map;
    private Constants constants;
    private Location lc;
    private ProgressDialog dialog;
    private List<DestinasiModel> destinasiModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destinasidetail);
        dialog = new ProgressDialog(this);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        title = findViewById(R.id.tour_detail_txt_title);
        alamat = findViewById(R.id.tour_detail_txt_description);
        jarake = findViewById(R.id.tour_detail_txt_distance);
        imageView = findViewById(R.id.tour_detail_img_header);
        deskripsi = findViewById(R.id.tour_detail_txt_content);
        fab2Map = findViewById(R.id.tour_detail_btn_direction);
        judul = getIntent().getStringExtra("title");
        addr = getIntent().getStringExtra("alamat");
        desk = getIntent().getStringExtra("deskripsi");
        gambar = getIntent().getStringExtra("gambar");
        distanced = getIntent().getStringExtra("dst");
        latlng = getIntent().getStringExtra("latlng");
        lat = getIntent().getStringExtra("latid");
        lng = getIntent().getStringExtra("langi");
        destid = getIntent().getStringExtra("destid");
        getSupportActionBar().setTitle(judul);
        lc = new Location(latlng);
        if(!"null".equals(destid)){
            _tampilkanGambar(destid);
        }
        //imageView.setImageBitmap(Constants.String2Bitmap(gambar));
        /*title.setText(judul);
        alamat.setText(addr);
        jarak.setText(distanced);
        deskripsi.setText(desk);*/

        fab2Map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToGoogleMap(lat,lng,lc);
            }
        });
    }
    private void _tampilkanGambar(String destinID){
        String where ="?destid="+destinID;
        new detailDestinasi().execute(ConnectivityReceiver.API_URL+"laporan/destinasi/"+where);
    }
    public class detailDestinasi extends AsyncTask<String,Void,String>{
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Loading detail ... please wait");
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
                    ArrayList<String> jarak = new ArrayList<>();
                    destinasiModels = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(jsonString);
                    if (Integer.valueOf(jsonObject.getString("totaldata")) > 0) {
                        JSONArray arrD = jsonObject.getJSONArray("message");
                        for (int i = 0; i < arrD.length(); i++) {
                            JSONObject obj = arrD.getJSONObject(i);
                            imageView.setImageBitmap(Constants.String2Bitmap(obj.getString("image")));
                            title.setText(judul);
                            alamat.setText(addr);
                            jarake.setText(distanced+" KM");
                            deskripsi.setText(desk);

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }
    private void goToGoogleMap(String lat, String lng, Location location) {
        final String origin = String.format("%s,%s",
                String.valueOf(location.getLatitude()),
                String.valueOf(location.getLongitude()));

        final String dest = String.format("%s,%s", lat, lng);

        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse(String.format("http://maps.google.com/maps?saddr=%s&daddr=%s", origin, dest)));
        startActivity(intent);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                Intent intent = new Intent(DestinasidetailActivity.this, DestinasiActivity.class);
                intent.putExtra("ldg","balik");
                startActivity(intent);
                onBackPressed();
                this.finish();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }
}
