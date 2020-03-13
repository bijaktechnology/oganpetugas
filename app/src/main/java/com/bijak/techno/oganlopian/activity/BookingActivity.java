package com.bijak.techno.oganlopian.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.MediaRouteButton;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bijak.techno.oganlopian.R;
import com.bijak.techno.oganlopian.adapter.AutoCompleteAdapter;
import com.bijak.techno.oganlopian.data.HttpGetUrl;
import com.bijak.techno.oganlopian.data.SettingsAPI;
import com.bijak.techno.oganlopian.model.ProfileModel;
import com.bijak.techno.oganlopian.model.Umume;
import com.bijak.techno.oganlopian.service.ConnectivityReceiver;
import com.bijak.techno.oganlopian.util.Constants;

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
import java.util.Objects;

import static com.bijak.techno.oganlopian.fragment.MapBookingFragment.getLocationWithCheckNetworkAndGPS;

public class BookingActivity extends AppCompatActivity {
    private String tipe_booking,dataPetugas;
    private EditText nama_pasien,nik_pasien,keluhan_pasien;
    private TextView petugasID,p_lat,p_lng;
    private Spinner  layanan_pasien;
    private Button submit;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private ProgressBar progressBar;
    private ProgressDialog dialog;
    private AutoCompleteAdapter autoCompleteAdapter=null;
    private AutoCompleteTextView namapasien;
    ArrayList<ProfileModel> familylist=new ArrayList<>();
    SettingsAPI set;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        tipe_booking =getIntent().getStringExtra("tp_booking");
        dataPetugas = getIntent().getStringExtra("message");
        Toast.makeText(getApplicationContext(),tipe_booking,Toast.LENGTH_LONG).show();
        nik_pasien = findViewById(R.id.patient_data_inp_nik);
        namapasien = findViewById(R.id.namapasiene);
        nama_pasien = findViewById(R.id.patient_data_inp_name);
        keluhan_pasien = findViewById(R.id.patient_keluhan);
        layanan_pasien = findViewById(R.id.patient_data_spinner_services);
        petugasID = findViewById(R.id.petugas_id);
        p_lat = findViewById(R.id.latitude);
        p_lng = findViewById(R.id.longitude);
        submit = findViewById(R.id.btn_submit);
        progressBar =findViewById(R.id.progrebar);
        dialog = new ProgressDialog(this);
        set = new SettingsAPI(this);
        //isi dropdown layanan
        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add("UMUM");
        spinnerArray.add("BPJS");
        spinnerArray.add("JAMPIS");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        Spinner sItems = findViewById(R.id.patient_data_spinner_services);
        sItems.setAdapter(adapter);
        //end of isi dropdown
        initToolbar();
        LocationManager lm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

        Location lc = new Location(getLocationWithCheckNetworkAndGPS(this,lm));
        //tampilkan button pick from anggota keluarga jika sudah ada data anggota keluarga
        __dataPetugase(dataPetugas);
        _getAnggotaKK();
        //simpan data pasien
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Umume obj = new Umume();
                //check keluhan pasien wajib di input
                if(keluhan_pasien.getText().toString().trim().length()==0){
                    Toast.makeText(getApplicationContext(),"Keluhan Pasien harus di isi",Toast.LENGTH_LONG).show();
                    return;
                }
                obj.setUserID(set.readSetting(Constants.PREF_MY_UID));
                obj.setNikPasien(nik_pasien.getText().toString());
                obj.setNamaPasien(namapasien.getText().toString());
                obj.setKeluhanPasien(keluhan_pasien.getText().toString());
                obj.setLayananPasien(layanan_pasien.getSelectedItem().toString());
                obj.setLatiTuded(lc.getLatitude());
                obj.setLongiTuded(lc.getLongitude());
                obj.setPetugasID(petugasID.getText().toString());
                new simpan_booking().execute(obj);
            }
        });
    }
    public void initToolbar(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Booking Data");
        //getSupportActionBar().setLogo(getApplicationContext().getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }
    private void __dataPetugase(String datax){
        JSONObject jDatax = null;
        try {
            jDatax = new JSONObject(datax);
            JSONObject jD=new JSONObject(jDatax.getString("petugase"));
            petugasID.setText(jD.getString("petugas_id"));
            p_lat.setText(jD.getString("lat"));
            p_lng.setText(jD.getString("lng"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                Intent intent = new Intent(this,HomeActivity.class);
                intent.putExtra("p_booking",tipe_booking);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class simpan_booking extends AsyncTask<Umume,Void,Void>{

        @Override
        protected void onPreExecute() {
            //super.onPreExecute();
            dialog.setMessage("Simpan data in progess... please wait");
            dialog.show();
        }
        @Override
        protected Void doInBackground(Umume... umumes) {
            try{
                Umume data = umumes[0];
                String layanan="3";
                switch(data.getLayananPasien()){
                    case "BPJS":layanan="1";break;
                    case "JAMPIS":layanan="2"; break;
                    default:layanan="3";break;
                }
                URL url1 = new URL(ConnectivityReceiver.API_URL + "transaksi/booking/"+tipe_booking);
                JSONObject postData = new JSONObject();
                postData.put("user_id", data.getUserID());
                postData.put("latitude",String.valueOf(data.getLatiTuded()));
                postData.put("longitude",String.valueOf(data.getLongiTuded()));
                postData.put("nik_pasien",data.getNikPasien());
                postData.put("nama_pasien",data.getNamaPasien());
                postData.put("keterangan",data.getKeluhanPasien());
                postData.put("layanan_id",layanan);
                postData.put("petugas_id",data.getPetugasID());
                Log.e("datane update",postData.toString());
                HttpURLConnection Conn = (HttpURLConnection)url1.openConnection();
                Conn.setReadTimeout(30000 /* milliseconds */);
                Conn.setConnectTimeout(30000 /* milliseconds */);
                Conn.setRequestMethod("POST");
                Conn.setDoInput(true);
                Conn.setDoOutput(true);

                OutputStream os = Conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
                writer.write(Constants.getPostDataString(postData));

                writer.flush();
                writer.close();
                os.close();
                int responseCode = Conn.getResponseCode();
                if(responseCode==200){
                    Intent intent = new Intent(BookingActivity.this,BookmonActivity.class);
                    intent.putExtra("p_booking",tipe_booking);
                    startActivity(intent);
                    //finish();
                    if(dialog.isShowing()){
                        dialog.dismiss();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                Log.i("error",e.getMessage());
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

    private void _getAnggotaKK(){

        String where ="?custom=length(no_kk)>0&user_id="+set.readSetting(Constants.PREF_MY_UID);
        new listAnggotaKK().execute(ConnectivityReceiver.API_URL+"profile/profileKK/"+where);
    }
    public class listAnggotaKK extends AsyncTask<String,Void,String>{

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
                            ProfileModel md = new ProfileModel (obj.getString("nama_lengkap"),obj.getString("nik"),obj.getString("status_keluarga"),obj.getString("jenis_kelamin"));
                            familylist.add(md);
                        }
                        autoCompleteAdapter= new AutoCompleteAdapter(getApplicationContext(),familylist);
                        namapasien.setAdapter(autoCompleteAdapter);
                        namapasien.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                ProfileModel mp = familylist.get(position);
                                try {
                                    for (int x = 0; x < arrD.length(); x++) {
                                        JSONObject obj = arrD.getJSONObject(x);
                                        if(obj.getString("nik").equals(mp.getNIK())){
                                            nik_pasien.setText(obj.getString("nik"));
                                            keluhan_pasien.requestFocus();
                                        }
                                    }
                                } catch (JSONException e){
                                    e.printStackTrace();
                                }
                            }
                        });
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
