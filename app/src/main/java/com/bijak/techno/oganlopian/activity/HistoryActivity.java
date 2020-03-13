package com.bijak.techno.oganlopian.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bijak.techno.oganlopian.R;
import com.bijak.techno.oganlopian.adapter.HistoryListAdapter;
import com.bijak.techno.oganlopian.data.HttpGetUrl;
import com.bijak.techno.oganlopian.data.SettingsAPI;
import com.bijak.techno.oganlopian.model.HistoryModel;
import com.bijak.techno.oganlopian.service.ConnectivityReceiver;
import com.bijak.techno.oganlopian.util.Constants;
import com.bijak.techno.oganlopian.util.CustomToast;
import com.bijak.techno.oganlopian.widget.DividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swip;
    private HistoryListAdapter historiAdapter;
    private List<HistoryModel> historiList = new ArrayList<>();
    private SettingsAPI settingsAPI;
    private CustomToast customToast;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        recyclerView = findViewById(R.id.histori_booking);
        swip = findViewById(R.id.histori_swip);
        settingsAPI = new SettingsAPI(this);
        historiList = new ArrayList<>();
        customToast = new CustomToast(this);
        dialog = new ProgressDialog(this);
        _getDataHistory("lod");
        initToolbar();
        swip.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swip.setRefreshing(false);
                        _getDataHistory("res");
                    }
                },5000);
            }
        });
    }

    private void _getDataHistory(String asal){
        String where ="?user_id="+settingsAPI.readSetting(Constants.PREF_MY_UID)+"&orderby=booking_time%20desc";
        new getHistory(asal).execute(ConnectivityReceiver.API_URL+"laporan/booking/histori/"+where);
    }
    public  class getHistory extends AsyncTask<String,Void,String>{
        String darimana=null;
        public getHistory(String asal){
            darimana = asal;
        }
        protected void onPreExecute() {
            super.onPreExecute();
            if(darimana.equals("lod")) {
                dialog.setMessage("Loading detail ... please wait");
                dialog.show();
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
                    historiList = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(jsonString);
                    if (Integer.valueOf(jsonObject.getString("totaldata")) > 0) {
                        JSONArray arrD = jsonObject.getJSONArray("message");
                        for (int i = 0; i < arrD.length(); i++) {
                            JSONObject obj = arrD.getJSONObject(i);
                            HistoryModel md = new HistoryModel();
                            md.setNamaLengkap(obj.getString("nama_pasien"));
                            md.setTanggalBoking(customToast.getDisplayDateTime(obj.getString("booking_time")));
                            if(!"null".equals(obj.getString("complete_time"))) {
                                md.setTanggalProses(customToast.getDisplayDateTime(obj.getString("complete_time")));
                            }
                            switch (obj.getString("user_role")){
                                case "Bidan": case "bidan":
                                    md.setAvatar(getApplicationContext().getResources().getDrawable(R.drawable.ic_bidan));break;
                                case "Dokter": case "dokter":
                                    md.setAvatar(getApplicationContext().getResources().getDrawable(R.drawable.ic_doctor));break;
                                default:
                                    md.setAvatar(getApplicationContext().getResources().getDrawable(R.drawable.ic_ambulance));break;
                            }
                            md.setPetugase(obj.getString("nama_petugas"));
                            md.setKeluhan(obj.getString("keterangan"));
                            switch (obj.getString("layanan_id")){
                                case "1": md.setJamPis("BPJS"); break;
                                case "2": md.setJamPis("JAMPIS");break;
                                default:md.setJamPis("UMUM");break;
                            }
                            md.setPenilaian(obj.getString("penilaian"));
                            //md.setDurasi(obj.getString("durasi"));
                            historiList.add(md);
                            Log.i("data-"+i,md.toString());
                        }
                        historiAdapter = new HistoryListAdapter(historiList);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),DividerItemDecoration.VERTICAL_LIST));
                        recyclerView.setAdapter(historiAdapter);
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
    public void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Riwayat Panggilan");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_save:
                //_updateProfile();
                break;
            case android.R.id.home:
                Intent i = new Intent(HistoryActivity.this,SettingActivity.class);
                startActivity(i);
                this.finish();
                break;
        }
        return true;
    }
}
