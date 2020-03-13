package com.bijak.techno.oganlopian.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.bijak.techno.oganlopian.R;
import com.bijak.techno.oganlopian.adapter.FamilyListAdapter;
import com.bijak.techno.oganlopian.data.HttpGetUrl;
import com.bijak.techno.oganlopian.data.SettingData;
import com.bijak.techno.oganlopian.model.ProfileModel;
import com.bijak.techno.oganlopian.service.ConnectivityReceiver;
import com.bijak.techno.oganlopian.widget.DividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProfilekkActivity extends AppCompatActivity {
    private Button btnAdd;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private String no_kk;
    private ProgressDialog dialog;
    private RecyclerView recyclerView;
    private ProfileModel profileModel;
    private List<ProfileModel> familylist =new ArrayList<>();
    private FamilyListAdapter mAdapter;
    private SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilekk);
        btnAdd = findViewById(R.id.family_list_btn_add);
        no_kk = getIntent().getStringExtra("nokk");
        dialog = new ProgressDialog(this);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilekkActivity.this,ProfilekkAdd.class);
                intent.putExtra("no_kk",no_kk);
                intent.putExtra("mod","insert");
                startActivity(intent);
            }
        });
        initToolbar();
        recyclerView = findViewById(R.id.family_list_recycler);
        _loadDataKK("lod");
        refreshLayout = findViewById(R.id.family_list_swipe_refresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                        _loadDataKK("swipe");
                    }
                },5000);
            }
        });

    }
    private void _loadDataKK(String asal){
        //load data kk
        String where ="no_kk="+no_kk;
        new getDataKK(this,asal).execute(ConnectivityReceiver.API_URL+"profile/profileKK/?"+where);
    }
    public class getDataKK extends AsyncTask<String,Void,String>{
        private String darimana=null;
        private getDataKK(ProfilekkActivity context,String asal){
            darimana = asal;
        }
        @Override
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
                    familylist = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(jsonString);
                    if (Integer.valueOf(jsonObject.getString("totaldata")) > 0) {
                        JSONArray arrD = jsonObject.getJSONArray("message");
                        for (int i = 0; i < arrD.length(); i++) {
                            JSONObject obj = arrD.getJSONObject(i);
                            ProfileModel md = new ProfileModel (obj.getString("nama_lengkap"),obj.getString("nik"),obj.getString("status_keluarga"),obj.getString("jenis_kelamin"));
                            familylist.add(md);
                        }
                        mAdapter = new FamilyListAdapter(familylist);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.addOnItemTouchListener(new SettingData(getApplicationContext(),recyclerView,new SettingData.ClickListener(){

                            @Override
                            public void onClick(View view, int position) {
                                ProfileModel md = familylist.get(position);
                                //Toast.makeText(getApplicationContext(),md.getNIK(),Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(ProfilekkActivity.this,ProfilekkAdd.class);
                                intent.putExtra("mod","edit");
                                intent.putExtra("noktp",md.getNIK());
                                intent.putExtra("no_kk",no_kk);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onLongClick(View view, int position) {

                            }
                        }));
                        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),DividerItemDecoration.VERTICAL_LIST));
                        recyclerView.setAdapter(mAdapter);
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
        actionBar.setTitle("Daftar Anggota Keluarga");
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
                Intent i = new Intent(ProfilekkActivity.this,SettingActivity.class);
                startActivity(i);
                this.finish();
                break;
        }
        return true;
    }
}
