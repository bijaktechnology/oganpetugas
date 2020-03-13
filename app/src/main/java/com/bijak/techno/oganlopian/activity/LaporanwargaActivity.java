package com.bijak.techno.oganlopian.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.bijak.techno.oganlopian.R;
import com.bijak.techno.oganlopian.adapter.LaporanAdapter;
import com.bijak.techno.oganlopian.data.HttpGetUrl;
import com.bijak.techno.oganlopian.data.SettingData;
import com.bijak.techno.oganlopian.model.LaporanModel;
import com.bijak.techno.oganlopian.service.ConnectivityReceiver;
import com.bijak.techno.oganlopian.widget.DividerItemDecoration;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.bijak.techno.oganlopian.activity.DestinasiActivity.getLocationWithCheckNetworkAndGPS;

public class LaporanwargaActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ActionBar actionBar;
    private ProgressDialog dialog;
    private RecyclerView recyclerView;
    private List<LaporanModel> laporanModels;
    private LaporanAdapter laporanAdapter;
    private FloatingActionButton fab;
    private String jenisLap="1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporanwarga);
        toolbar = findViewById(R.id.toolbar);
        laporanModels= new ArrayList<>();
        dialog = new ProgressDialog(this);
        jenisLap=getIntent().getStringExtra("jenis_lap");
        if(jenisLap==null){
            jenisLap="1";
        }
        fab =findViewById(R.id.buatbaru);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Laporan Warga");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initializeRecyclerView(laporanModels);
        if(jenisLap.equals("1")) {
            getSupportActionBar().setSubtitle("Laporan Terpopuler");
            new GetLaporan("lod").execute(ConnectivityReceiver.API_URL + "laporan/laporanwarga?limit=20");
        }
        if(jenisLap.endsWith("2")) {
            //getSupportActionBar().setSubtitle("");
            getSupportActionBar().setSubtitle("Laporan Terbaru");
            new GetLaporan("lod").execute(ConnectivityReceiver.API_URL+"laporan/laporanwarga?limit=20&orderby=laporan_id");
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LaporanwargaActivity.this,LaporanActivity.class);
                intent.putExtra("jenis_laporan",jenisLap);
                startActivity(intent);
                finish();
            }
        });
    }
    private void initializeRecyclerView(List<LaporanModel> dm) {

        recyclerView = findViewById(R.id.laporanlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new LaporanAdapter(dm));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_laporan, menu);
        return super.onCreateOptionsMenu(menu);
    }
    public class GetLaporan extends AsyncTask<String,Void,String>{
        private String darimana;
        public GetLaporan(String asal){
            this.darimana  = asal;
        }
        protected void onPreExecute() {
            super.onPreExecute();
            if(darimana.equals("lod")) {
                dialog.setMessage("Loading data ... please wait");
                if(!dialog.isShowing()) {
                    dialog.show();
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                }
            }
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
                    laporanModels = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(jsonString);
                    //Location lc = getLocationWithCheckNetworkAndGPS(getApplicationContext());
                    if (Integer.valueOf(jsonObject.getString("totaldata")) > 0) {
                        JSONArray arrD = jsonObject.getJSONArray("message");
                        for (int i = 0; i < arrD.length(); i++) {
                            JSONObject obj = arrD.getJSONObject(i);
                            LaporanModel md = new LaporanModel();
                            md.setGambarLaporan(obj.getString("foto_laporan"));
                            md.setNamaLengkap(obj.getString("nama_lengkap"));
                            md.setKategori(obj.getString("nama_kategori"));
                            md.setTanggal(obj.getString("report_time"));
                            md.setLatitud(obj.getString("lat"));
                            md.setLongitud(obj.getString("lng"));
                            md.setJudul(obj.getString("judul"));
                            md.setComment(obj.getString("jumlah_comment"));
                            md.setLikes(obj.getString("jumlah_like"));
                            md.setAlamat(obj.getString("formatted_address"));
                            laporanModels.add(md);
                        }
                        Log.i("list-laporan",laporanModels.toString());
                        laporanAdapter = new LaporanAdapter(laporanModels);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),DividerItemDecoration.VERTICAL_LIST));
                        recyclerView.setAdapter(laporanAdapter);
                        recyclerView.addOnItemTouchListener(new SettingData(getApplicationContext(), recyclerView, new SettingData.ClickListener() {
                            @Override
                            public void onClick(View view, int position) {
                                LaporanModel dm = laporanModels.get(position);
                                Intent intent = new Intent(LaporanwargaActivity.this, LaporanwrgdtlActivity.class);
                                JSONObject obj = new JSONObject();
                                try {
                                    obj = arrD.getJSONObject(position);
                                    intent.putExtra("laporan_id",obj.getString("laporan_id"));
                                    intent.putExtra("jenis_lap",jenisLap);
                                    startActivity(intent);
                                    finish();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onLongClick(View view, int position) {

                            }
                        }));
                        setSupportActionBar(toolbar);
                        if(jenisLap.equals("1")){
                            getSupportActionBar().setSubtitle("Laporan Terpopuler");
                        }else{
                            getSupportActionBar().setSubtitle("Laporan Terbaru");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("error", e.getMessage());
                }

            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.newlap:
                jenisLap="2";
                new GetLaporan("lod").execute(ConnectivityReceiver.API_URL+"laporan/laporanwarga?limit=20&orderby=laporan_id");
                break;
            case R.id.populer:
                jenisLap="1";
                new GetLaporan("lod").execute(ConnectivityReceiver.API_URL+"laporan/laporanwarga?limit=20");
                break;
            case android.R.id.home:
                Intent i = new Intent(LaporanwargaActivity.this,MenuActivity.class);
                startActivity(i);
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }
}
