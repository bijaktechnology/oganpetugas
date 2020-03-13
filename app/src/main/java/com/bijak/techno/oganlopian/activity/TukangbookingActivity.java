package com.bijak.techno.oganlopian.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.app.job.JobScheduler;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bijak.techno.oganlopian.R;
import com.bijak.techno.oganlopian.adapter.TukangAdapter;
import com.bijak.techno.oganlopian.data.HttpGetUrl;
import com.bijak.techno.oganlopian.data.SettingData;
import com.bijak.techno.oganlopian.model.TukangModel;
import com.bijak.techno.oganlopian.service.ConnectivityReceiver;
import com.bijak.techno.oganlopian.widget.DividerItemDecoration;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class TukangbookingActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView btnLogo;
    private TextView txtButton,deskripsi;
    private String textButton,icone,kategori;
    public FloatingActionButton fab,fab1;
    private LinearLayout mapcontainer;
    private ScrollView scrollView;
    JobScheduler mJobScheduler;
    private TextView mMsgView;
    private static final String TAG = TukangbookingActivity.class.getSimpleName();
    private LinearLayout booking;
    private ProgressDialog dialog;
    private TukangAdapter tukangAdapter;
    private List<TukangModel> tukangModelList;
    private String jenisLap="1";
    private RecyclerView recyclerView;
    /**
     * Code used in requesting runtime permissions.
     */
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;//34;
    private boolean mAlreadyStartedService = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tukangbooking);
        fab = findViewById(R.id.add1);
        dialog =new ProgressDialog(this);
        recyclerView = findViewById(R.id.list_tukang);
        toolbar = findViewById(R.id.toolbar);
        mMsgView = findViewById(R.id.msgView);
        prepareActionBar(toolbar);
        booking = findViewById(R.id.btn_booking);
        btnLogo = findViewById(R.id.petugas);
        txtButton = findViewById(R.id.book);
        mapcontainer = findViewById(R.id.frame_container);
        scrollView = findViewById(R.id.scroled);
        deskripsi = findViewById(R.id.deskripsi);
        textButton = getIntent().getStringExtra("text");
        icone =getIntent().getStringExtra("id");
        txtButton.setText("Booking "+textButton);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Monitoring "+textButton);
        getSupportActionBar().setSubtitle("Tukang terpopuler");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String where = null;
        try {
            where = URLEncoder.encode("custom=order by rating desc limit 20","utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        new ListTukang("lod").execute(ConnectivityReceiver.API_URL+"laporan/booking/tukang?"+where);

    }
    private void initComponent() {
        androidx.fragment.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        com.bijak.techno.oganlopian.fragment.MapTukangFragment ctf = new com.bijak.techno.oganlopian.fragment.MapTukangFragment();

        fragmentTransaction.add(R.id.frame_container, ctf,"Booking Tukang");
        fragmentTransaction.commit();
        scrollView.setVisibility(View.GONE);

    }
    private void prepareActionBar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(false);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_tukang, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.newlap: {
                getSupportActionBar().setSubtitle("Tukang disekitar anda");
                hideSoftKeyboard(deskripsi);
                mapcontainer.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.GONE);
                initComponent();
                return true;
            }
            case R.id.populer: {
                getSupportActionBar().setSubtitle("Tukang terpupuler");
                mapcontainer.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                showSoftKeyboard(deskripsi);
                return true;
            }
            case android.R.id.home:{
                Intent logoutIntent = new Intent(this, TukangActivity.class);
                startActivity(logoutIntent);
                this.finish();
                return  true;
            }


            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private long exitTime = 0;
    public void doExitApp() {
        Intent inten = new Intent(this,TukangActivity.class);
        startActivity(inten);
        finish();
        //System.exit(0);
    }
    @Override
    public void onBackPressed() {
        doExitApp();
    }
    public void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }
    public class ListTukang extends AsyncTask<String,Void,String>{
        private String darimana;
        public ListTukang(String asal){
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
                    tukangModelList = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(jsonString);
                    if (Integer.valueOf(jsonObject.getString("totaldata")) > 0) {
                        JSONArray arrD = jsonObject.getJSONArray("message");
                        for (int i = 0; i < arrD.length(); i++) {
                            JSONObject obj = arrD.getJSONObject(i);
                            TukangModel md = new TukangModel();
                            md.setId_Tukang(obj.getString("tukang_id"));
                            md.setNama_Tukang(obj.getString("nama_lengkap"));
                            md.setAlamat_Tukang("Alamat Lengkap");
                            md.setAvatar(obj.getString("avatar"));
                            md.setHarga_Jasa(obj.getString("jarake"));
                            md.setRating_Tukang(obj.getString("rating").toString());
                            tukangModelList.add(md);
                        }
                        Log.i("list-laporan", tukangModelList.toString());
                        tukangAdapter = new TukangAdapter(tukangModelList);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL_LIST));
                        recyclerView.setAdapter(tukangAdapter);
                        /*recyclerView.addOnItemTouchListener(new SettingData(getApplicationContext(), recyclerView,
                                new SettingData.ClickListener() {
                            @Override
                            public void onClick(View view, int position) {
                                TukangModel dm = tukangModelList.get(position);
                                *//*Intent intent = new Intent(TukangbookingActivity.this, LaporanwrgdtlActivity.class);
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
                                startActivity(intent);*//*
                                finish();
                            }

                            @Override
                            public void onLongClick(View view, int position) {

                            }
                        }));*/
                        setSupportActionBar(toolbar);
                        if (jenisLap.equals("1")) {
                            getSupportActionBar().setSubtitle("Tukang Terpopuler");
                        } else {
                            getSupportActionBar().setSubtitle("Tukang disekitar anda");
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
}
