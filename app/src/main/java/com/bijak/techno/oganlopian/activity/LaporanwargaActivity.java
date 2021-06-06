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
                            /*if(obj.getString("foto_laporan").length()==0){
                                md.setGambarLaporan("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");
                            }else {*/
                                md.setGambarLaporan(obj.getString("foto_laporan"));
//                            }
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
                        Log.d("list-laporan",laporanModels.toString());
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
