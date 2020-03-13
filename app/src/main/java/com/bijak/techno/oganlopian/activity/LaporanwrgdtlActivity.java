package com.bijak.techno.oganlopian.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bijak.techno.oganlopian.R;
import com.bijak.techno.oganlopian.adapter.LaporanAdapter;
import com.bijak.techno.oganlopian.adapter.LaporanCommentAdapter;
import com.bijak.techno.oganlopian.data.HttpGetUrl;
import com.bijak.techno.oganlopian.data.SettingData;
import com.bijak.techno.oganlopian.data.SettingsAPI;
import com.bijak.techno.oganlopian.model.LaporanModel;
import com.bijak.techno.oganlopian.service.ConnectivityReceiver;
import com.bijak.techno.oganlopian.util.Constants;
import com.bijak.techno.oganlopian.widget.DividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class LaporanwrgdtlActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ActionBar actionBar;
    private ProgressDialog dialog;
    private RecyclerView recyclerView;
    private List<LaporanModel> laporanModels;
    private LaporanCommentAdapter commentAdapter;
    SettingsAPI settingsAPI;
    String laporanid;
    ImageView avatar,img_report,img_kategory,img_maps,img_bookmark;
    TextView namapelapor,kategori,deskripsi,alamat, waktu,judul,status_lap,jml_comment,jml_like,komentar;
    Button sendComment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporanwrgdtl);
        dialog = new ProgressDialog(this);
        toolbar = findViewById(R.id.toolbar);
        settingsAPI = new SettingsAPI(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Detail Laporan");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        komentar = findViewById(R.id.report_detail_inp_comment);
        sendComment = findViewById(R.id.report_detail_btn_send);
        laporanModels= new ArrayList<>();
        initializeRecyclerView(laporanModels);
        /**
         * initialize object
         */
        avatar =(ImageView)findViewById(R.id.report_detail_img_user);
        img_report =(ImageView) findViewById(R.id.report_detail_img_report);
        namapelapor = (TextView) findViewById(R.id.report_detail_txt_user);
        kategori =(TextView) findViewById(R.id.report_detail_txt_category);
        img_kategory =(ImageView) findViewById(R.id.report_detail_img_category);
        alamat =(TextView) findViewById(R.id.report_detail_txt_location);
        waktu =(TextView) findViewById(R.id.report_detail_txt_time);
        judul = (TextView) findViewById(R.id.report_detail_txt_title);
        status_lap =(TextView) findViewById(R.id.report_txt_status);
        jml_comment=(TextView) findViewById(R.id.report_detail_txt_comment);
        jml_like =(TextView) findViewById(R.id.report_detail_txt_love);
        img_bookmark =(ImageView) findViewById(R.id.report_detail_img_bookmark);
        img_maps =(ImageView) findViewById(R.id.report_detail_img_map);
        deskripsi = (TextView) findViewById(R.id.report_detail_txt_desc);
        /**end of initilize*/
        laporanid = getIntent().getStringExtra("laporan_id");
        String where=laporanid;
        new GetLaporan("lod").execute(ConnectivityReceiver.API_URL+"laporan/laporanwarga/"+where);
        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LaporanModel lm = new LaporanModel();
                lm.setLaporanID(laporanid);
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
                Intent i = new Intent(LaporanwrgdtlActivity.this,LaporanwargaActivity.class);
                i.putExtra("jenis_lap",getIntent().getStringExtra("jenis_lap"));
                startActivity(i);
                finish();
                break;
        }
        return true;
    }
    public class GetLaporan extends AsyncTask<String,Void,String> {
        private String darimana;
        public GetLaporan(String asal){
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
                            Bitmap gbrAvtr = Constants.String2Bitmap(obj.getString("user_img"));
                            Bitmap gbrLap = Constants.String2Bitmap(obj.getString("foto_laporan"));
                            String durasi = Constants.LamaWaktu(obj.getString("report_time"));
                            String namaIcon = obj.getString("icon").replace(".png","");
                            int resID = getResources().getIdentifier(namaIcon, "drawable", getPackageName());
                            avatar.setImageBitmap(gbrAvtr);
                            img_report.setImageBitmap(gbrLap);
                            waktu.setText(durasi);
                            namapelapor.setText(obj.getString("nama_lengkap"));
                            alamat.setText(obj.getString("formatted_address"));
                            judul.setText(obj.getString("judul"));
                            kategori.setText(obj.getString("nama_kategori"));
                            img_kategory.setImageResource(resID);
                            deskripsi.setText(obj.getString("keterangan"));
                            String sts_txt="";
                            switch (obj.getString("status_laporan")){
                                case "D": sts_txt="Menunggu Proses";break;
                                case "C": sts_txt="Selesai";break;
                                case "P": sts_txt="Dalam Proses";break;
                                default:sts_txt="Menunggu di proses";break;
                            }
                            status_lap.setText(sts_txt);
                            jml_comment.setText(obj.getString("jumlah_comment"));
                            jml_like.setText(obj.getString("jumlah_like"));
                            String lap_id = obj.getString("laporan_id");
                            String lat = obj.getString("lat");
                            String lng = obj.getString("lng");
                            String jdl = obj.getString("judul");
                            String imgkat = String.valueOf(resID);
                            String kate = obj.getString("nama_kategori");
                            String lokasi = obj.getString("street");
                            String tagal = durasi;
                            String gbr = obj.getString("foto_laporan");
                            jml_comment.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(LaporanwrgdtlActivity.this,LaporancommentActivity.class);
                                    intent.putExtra("laporan_id",lap_id);
                                    intent.putExtra("judul",jdl);
                                    startActivity(intent);
                                    //finish();
                                }
                            });
                            String finalSts_txt = sts_txt;
                            //icon maps klik
                            img_maps.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(LaporanwrgdtlActivity.this,LaporanmapsActivity.class);
                                    intent.putExtra("lat",lat);
                                    intent.putExtra("lng",lng);
                                    intent.putExtra("judul",jdl);
                                    intent.putExtra("lokasi",lokasi);
                                    intent.putExtra("tanggal",tagal);
                                    intent.putExtra("kategori",kate);
                                    intent.putExtra("img_kat",imgkat);
                                    intent.putExtra("lap_id",lap_id);
                                    intent.putExtra("stat_lap", finalSts_txt);
                                    intent.putExtra("gbr_lap",gbr);
                                    startActivity(intent);
                                    //finish();
                                }
                            });
                            jml_like.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int jmllike= Integer.valueOf((String) jml_like.getText());
                                    jmllike = jmllike+1;
                                    jml_like.setText(String.valueOf(jmllike));
                                    __simpanLike(lap_id);
                                }
                            });
                            img_bookmark.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    __simpanBookmark(lap_id);
                                }
                            });
                            new GetComment("xx",lap_id,jdl).execute(ConnectivityReceiver.API_URL+"laporan/commentlap/"+lap_id+"/5");
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

    private void __simpanLike(String lap_id) {
        LaporanModel md = new LaporanModel();
        md.setLaporanID(lap_id);
        new SimpanAction("transaksi/addlike").execute(md);
    }

    private void __simpanBookmark(String lap_id) {
        LaporanModel md = new LaporanModel();
        md.setLaporanID(lap_id);
        new SimpanAction("transaksi/addbookmark").execute(md);
        Toast.makeText(this,"Laporan berhasil ditambahkan ke favorit",Toast.LENGTH_LONG).show();
    }

    public class GetComment extends AsyncTask<String,Void,String>{
        private String darimana,LaporanID,Judul;
        public GetComment(String asal,String laporanid,String judul){
            this.LaporanID = laporanid;
            this.darimana  = asal;
            this.Judul = judul;
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
                        recyclerView.addOnItemTouchListener(new SettingData(getApplicationContext(), recyclerView, new SettingData.ClickListener() {
                            @Override
                            public void onClick(View view, int position) {
                                LaporanModel dm = laporanModels.get(position);
                                Intent intent = new Intent(LaporanwrgdtlActivity.this, LaporancommentActivity.class);
                                intent.putExtra("laporan_id",LaporanID);
                                intent.putExtra("judul",Judul);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onLongClick(View view, int position) {

                            }
                        }));
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
            dialog.setMessage("Update data.. please wait");
            //dialog.show();
        }
        @Override
        protected Void doInBackground(LaporanModel... umumes) {
            try{
                LaporanModel data = umumes[0];
                URL url1 = new URL(ConnectivityReceiver.API_URL + validUrl);
                JSONObject mdData = new JSONObject();
                mdData.put("laporan_id",data.getLaporanID());
                mdData.put("user_id",settingsAPI.readSetting(Constants.PREF_MY_UID));
                if(validUrl=="transaksi/addcomment"){
                    mdData.put("comment",data.getComment());
                }
                Log.e("datane update",mdData.toString());
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
                    if (validUrl == "transaksi/addcomment") {
                        Intent intent = new Intent(LaporanwrgdtlActivity.this, LaporancommentActivity.class);
                        intent.putExtra("laporan_id", laporanid);
                        intent.putExtra("judul", judul.getText().toString());
                        startActivity(intent);
                    }
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
