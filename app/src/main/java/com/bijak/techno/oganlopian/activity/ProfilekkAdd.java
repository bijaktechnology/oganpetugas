package com.bijak.techno.oganlopian.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;

import com.bijak.techno.oganlopian.R;
import com.bijak.techno.oganlopian.data.HttpGetUrl;
import com.bijak.techno.oganlopian.data.SettingsAPI;
import com.bijak.techno.oganlopian.model.ProfileModel;
import com.bijak.techno.oganlopian.service.ConnectivityReceiver;
import com.bijak.techno.oganlopian.util.Constants;
import com.bijak.techno.oganlopian.util.CustomToast;
import com.bijak.techno.oganlopian.widget.DateInputMask;
import com.google.android.material.textfield.TextInputEditText;

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

public class ProfilekkAdd extends AppCompatActivity {
    private Button btnAdd;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private ProgressDialog dialog;
    SettingsAPI settingsAPI;
    CustomToast customToast;
    private TextInputEditText tgl_lahir,tmpat_bod,ibu_kandung,nama_lengkap,nik;
    private String no_kk,modex,noktp;
    private String[] sex ={"Laki-Laki","Perempuan"};
    private String[] hubkeluarga ={"Kepala Keluarga","Suami","Istri","Anak","Menantu","Cucu","Orang Tua","Mertua","Famili Lain","Pembantu","Lainnya"};
    private String[] agama ={"Islam","Kristen","Katholik","Budha","Hindu","Konghutzu","Aliran Kepercayaan"};
    private String[] pendidikan ={"TIDAK/BLM SEKOLAH","BELUM TAMAT SD/SEDERAJAT","TAMAT SD/SEDERAJAT","SLTP/SEDERAJAT","SLTA/SEDERAJAT","DIPLOMA I/II","AKADEMI/DIPLOMA III/SARJANA MUDA","DIPLOMA IV/STRATA I","STRATA-II"};
    private String[] gol_darah={"O","A","B","AB","B+","O+","O-","A+","Ab-","Ab+","B-","Tidak Tahu","Tidak Terdefinisi"};
    private AppCompatAutoCompleteTextView txtHubkel,jenis_kelamin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilekk_add);
        txtHubkel = findViewById(R.id.add_family_inp_status);
        tmpat_bod = findViewById(R.id.add_family_inp_dobplace);
        tgl_lahir = findViewById(R.id.add_family_inp_dob);
        jenis_kelamin = findViewById(R.id.add_family_inp_sex);
        no_kk =getIntent().getStringExtra("no_kk");
        modex = getIntent().getStringExtra("mod");
        noktp = getIntent().getStringExtra("noktp");
        ibu_kandung =findViewById(R.id.add_family_inp_mother_name);
        nama_lengkap = findViewById(R.id.add_family_inp_name);
        nik = findViewById(R.id.add_family_inp_ktp);
        Constants.AutoCompleteFill(this,txtHubkel,hubkeluarga,1);
        Constants.AutoCompleteFill(this,jenis_kelamin,sex,1);
        new DateInputMask(tgl_lahir);
        customToast = new CustomToast(this);
        settingsAPI = new SettingsAPI(this);
        btnAdd = findViewById(R.id.add_family_btn_save);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileModel md = new ProfileModel();
                md.setNamaLengkap(nama_lengkap.getText().toString());
                md.setTempatLahir(tmpat_bod.getText().toString());
                md.setTglLahir(tgl_lahir.getText().toString());
                md.setJenisKelamin(jenis_kelamin.getText().toString());
                md.setHubungan(txtHubkel.getText().toString());
                md.setNamaIbuKandung(ibu_kandung.getText().toString());
                md.setNIK(nik.getText().toString());
                md.setKK(no_kk);
                new SimpanUser().execute(md);
            }
        });
        initToolbar();
        dialog = new ProgressDialog(this);
        if(modex.equals("edit")){
            String where = "no_kk="+no_kk+"&nik="+noktp;
            new loadDetailKK().execute(ConnectivityReceiver.API_URL+"profile/profilekk/?"+where);
        }
    }
    public class SimpanUser extends AsyncTask<ProfileModel,Void,Void> {
        public SimpanUser(){}
        @Override
        protected void onPreExecute() {
            dialog.setMessage("Update profile data in progess... please wait");
            dialog.show();
        }
        @Override
        protected Void doInBackground(ProfileModel... umumes) {
            try{
                ProfileModel data = umumes[0];
                URL url1 = new URL(ConnectivityReceiver.API_URL + "profile/profilekk");
                JSONObject mdData = new JSONObject();
                mdData.put("no_kk",data.getKK());
                mdData.put("nik",data.getNIK());
                mdData.put("nama_lengkap",data.getNamaLengkap());
                mdData.put("tempat_lahir",data.getTempatLahir());
                mdData.put("tgl_lahir",data.getTglLahir());
                mdData.put("jenis_kelamin",data.getJenisKelamin());
                mdData.put("nama_ibu_kandung",data.getNamaIbuKandung());
                mdData.put("status_hubungan",data.getHubungan());
                mdData.put("user_id",settingsAPI.readSetting(Constants.PREF_MY_UID));
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
                no_kk =data.getKK();
                int responseCode = Conn.getResponseCode();
                Log.i("result-api",String.valueOf(responseCode));
                if(dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (responseCode==200){
                    Intent intent = new Intent(ProfilekkAdd.this,ProfilekkActivity.class);
                    intent.putExtra("nokk",no_kk);
                    startActivity(intent);
                    finish();
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
    public void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Tambah Anggota");
        actionBar.setSubtitle("No.KK :" +no_kk);
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
                switch (modex) {
                    case "edit":
                        Intent i = new Intent(ProfilekkAdd.this, ProfilekkActivity.class);
                        i.putExtra("nokk",no_kk);
                        startActivity(i);
                        finish();
                        break;
                    default:
                        this.finish();
                        break;
                }
                break;
        }
        return true;
    }
    public class loadDetailKK extends AsyncTask<String,Void,String>{
        @Override
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
                    JSONObject jsonObject = new JSONObject(jsonString);
                    if (Integer.valueOf(jsonObject.getString("totaldata")) > 0) {
                        JSONArray arrD = jsonObject.getJSONArray("message");
                        for (int i = 0; i < arrD.length(); i++) {
                            JSONObject obj = arrD.getJSONObject(i);
                            nama_lengkap.setText(obj.getString("nama_lengkap"));
                            tmpat_bod.setText(obj.getString("tempat_lahir"));
                            nik.setText(noktp);
                            tgl_lahir.setText(customToast.getDisplayDateTime(obj.getString("tgl_lahir")));
                            jenis_kelamin.setText(obj.getString("jenis_kelamin"));
                            txtHubkel.setText(obj.getString("status_keluarga"));
                            ibu_kandung.setText(obj.getString("nama_ibu_kandung"));
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
    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }
}
