package com.bijak.techno.oganlopian.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bijak.techno.oganlopian.R;
import com.bijak.techno.oganlopian.adapter.LaporanCommentAdapter;
import com.bijak.techno.oganlopian.data.HttpGetUrl;
import com.bijak.techno.oganlopian.data.SettingsAPI;
import com.bijak.techno.oganlopian.model.LaporanModel;
import com.bijak.techno.oganlopian.service.ConnectivityReceiver;
import com.bijak.techno.oganlopian.util.Constants;
import com.bijak.techno.oganlopian.widget.DividerItemDecoration;

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
import java.util.List;

public class LaporancommentActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ActionBar actionBar;
    private ProgressDialog dialog;
    private RecyclerView recyclerView;
    private List<LaporanModel> laporanModels;
    private LaporanCommentAdapter commentAdapter;
    private SwipeRefreshLayout refreshLayout;
    private String judule,laporan_id;
    private TextView komentar;
    private Button sendKomment;
    SettingsAPI settingsAPI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporancomment);
        dialog = new ProgressDialog(this);
        toolbar = findViewById(R.id.toolbar);
        refreshLayout = findViewById(R.id.comment_swipe_refresh);
        judule = getIntent().getStringExtra("judul");
        laporan_id = getIntent().getStringExtra("laporan_id");
        settingsAPI = new SettingsAPI(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Chat");
        getSupportActionBar().setSubtitle("Thread : "+judule);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        laporanModels= new ArrayList<>();
        initializeRecyclerView(laporanModels);
        new GetComment("lod",laporan_id).execute(ConnectivityReceiver.API_URL+"laporan/commentlap/"+laporan_id);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                        new GetComment("xx",laporan_id).execute(ConnectivityReceiver.API_URL+"laporan/commentlap/"+laporan_id);
                    }
                },5000);
            }
        });
        komentar = (TextView) findViewById(R.id.comment_inp);
        sendKomment = (Button) findViewById(R.id.comment_btn_send);
        sendKomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LaporanModel lm = new LaporanModel();
                lm.setLaporanID(laporan_id);
                lm.setComment(komentar.getText().toString());
                new SimpanAction("transaksi/addcomment").execute(lm);
            }
        });
    }
    private void initializeRecyclerView(List<LaporanModel> dm) {

        recyclerView = findViewById(R.id.comment_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new LaporanCommentAdapter(dm));
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_save:
                //_updateProfile();
                break;
            case android.R.id.home:
                Intent i = new Intent(LaporancommentActivity.this,LaporanwrgdtlActivity.class);
                i.putExtra("laporan_id",laporan_id);
                startActivity(i);
                this.finish();
                //finish();
        }
        return true;
    }
    public class GetComment extends AsyncTask<String,Void,String> {
        private String darimana,LaporanID;
        public GetComment(String asal,String laporanid){
            this.LaporanID = laporanid;
            this.darimana  = asal;
        }
        protected void onPreExecute() {
            super.onPreExecute();
            if(darimana.equals("lod")) {
                dialog.setMessage("Loading detail ... please wait");
                dialog.show();
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
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
                            md.setGambarLaporan(obj.getString("user_img"));
                            md.setNamaLengkap(obj.getString("nama_lengkap"));
                            md.setComment(obj.getString("comment"));
                            String durasi = Constants.LamaWaktu(obj.getString("timestamp"));
                            md.setTanggal(durasi);
                            laporanModels.add(md);
                        }
                        Log.i("list-laporan",laporanModels.toString());
                        commentAdapter = new LaporanCommentAdapter(laporanModels);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),DividerItemDecoration.VERTICAL_LIST));
                        recyclerView.setAdapter(commentAdapter);

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
    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

    public class SimpanAction extends AsyncTask<LaporanModel,Void,Void> {
        String validUrl;
        public SimpanAction(String uri){
            this.validUrl=uri;
        }
        @Override
        protected void onPreExecute() {
            dialog.setMessage("komentar dikirim... please wait");
            dialog.show();
        }
        @Override
        protected Void doInBackground(LaporanModel... umumes) {
            try{
                LaporanModel data = umumes[0];
                URL url1 = new URL(ConnectivityReceiver.API_URL + validUrl);
                JSONObject mdData = new JSONObject();
                mdData.put("laporan_id",data.getLaporanID());
                mdData.put("user_id",settingsAPI.readSetting(Constants.PREF_MY_UID));
                mdData.put("comment",data.getComment());
                HttpURLConnection Conn = (HttpURLConnection)url1.openConnection();
                Conn.setReadTimeout(15000 /* milliseconds */);
                Conn.setConnectTimeout(15000 /* milliseconds */);
                Conn.setRequestMethod("POST");
                Conn.setDoInput(true);
                Conn.setDoOutput(true);

                OutputStream os = Conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
                writer.write(Constants.getPostDataString(mdData));

                writer.flush();
                writer.close();
                os.close();
                int responseCode = Conn.getResponseCode();
                Log.i("result-api",String.valueOf(responseCode));
                if(dialog.isShowing()) {
                    dialog.dismiss();

                }
                if(responseCode==200) {
                    //refreshLayout.setRefreshing(true);
                    //new GetComment("xx",laporan_id).execute(ConnectivityReceiver.API_URL+"laporan/commentlap/"+laporan_id);
                    //komentar.setText("");
                    finish();
                    startActivity(getIntent());
                }else{
                    Toast.makeText(getApplicationContext(),"Komentar gagal disimpan, Silahkan dicoba lagi",Toast.LENGTH_LONG).show();
                }

            }catch (Exception e){
                Log.i("error",e.getMessage());
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
