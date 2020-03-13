package com.bijak.techno.oganlopian.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bijak.techno.oganlopian.R;
import com.bijak.techno.oganlopian.adapter.SettingAdapter;
import com.bijak.techno.oganlopian.data.HttpGetUrl;
import com.bijak.techno.oganlopian.data.SettingData;
import com.bijak.techno.oganlopian.data.SettingsAPI;
import com.bijak.techno.oganlopian.model.SettingModel;
import com.bijak.techno.oganlopian.service.ConnectivityReceiver;
import com.bijak.techno.oganlopian.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends AppCompatActivity {
    private ActionBar actionBar;
    private Toolbar toolbar;
    ImageView setting;
    TextView nokk;
    SettingsAPI set;
    private RecyclerView recyclerView;
    private List<SettingModel> menuList = new ArrayList<>();
    private SettingAdapter mAdapter;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        set = new SettingsAPI(this);
        initToolbar();
        dialog = new ProgressDialog(this);
        nokk = findViewById(R.id.no_kkne);
        _getNoKK();
        recyclerView = findViewById(R.id.settings_recyclerview);
        mAdapter = new SettingAdapter(menuList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        prepareMenudata();
        recyclerView.addOnItemTouchListener(new SettingData(getApplicationContext(), recyclerView, new SettingData.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                SettingModel model =menuList.get(position);
                //Toast.makeText(getApplicationContext(),model.getMenuTitle(),Toast.LENGTH_LONG).show();
                switch (model.getMenuTitle()){
                    case "Profile":
                        Intent intent = new Intent(SettingActivity.this,ProfileActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case "Daftar Anggota Keluarga":
                        if(nokk.getText().length()>0) {
                            Intent i = new Intent(SettingActivity.this, ProfilekkActivity.class);
                            i.putExtra("nokk",nokk.getText());
                            startActivity(i);
                            finish();
                        }else{
                            Toast.makeText(getApplicationContext(),"No. Kartu keluarga belum ada\n\rSilahkan update terlebih dahulu dari menu profie",Toast.LENGTH_LONG).show();
                        }
                        break;
                    case "Riwayat Panggilan":
                        Intent i = new Intent(SettingActivity.this,HistoryActivity.class);
                        startActivity(i);
                        finish();
                        break;
                    default:

                            break;
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

     public void initToolbar() {
         toolbar = findViewById(R.id.toolbar);
         setSupportActionBar(toolbar);
         actionBar = getSupportActionBar();
         actionBar.setTitle(set.readSetting(Constants.PREF_MY_NAME));
         getSupportActionBar().setDisplayHomeAsUpEnabled(true);

     }
    private void prepareMenudata(){
        SettingModel m = new SettingModel();
        m.setMenuTitle("Profile");
        menuList.add(m);
        m = new SettingModel();
        m.setMenuTitle("Daftar Anggota Keluarga");
        menuList.add(m);
        m = new SettingModel();
        m.setMenuTitle("Riwayat Panggilan");
        menuList.add(m);
    }
    private void initComponent() {
        recyclerView = findViewById(R.id.recyclerView);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new com.bijak.techno.oganlopian.widget.DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_setting, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_logout: {
                Intent logoutIntent = new Intent(SettingActivity.this, SplashActivity.class);
                logoutIntent.putExtra("mode", "logout");
                startActivity(logoutIntent);
                logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
                return true;
            }
            case R.id.action_home: {
                Intent logoutIntent = new Intent(this, MenuActivity.class);
                startActivity(logoutIntent);
                finish();
                return true;
            }
            case android.R.id.home:{
                this.finish();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void _getNoKK(){
        String where = "?user_id="+ set.readSetting(Constants.PREF_MY_UID);
        new detailUser(this).execute(ConnectivityReceiver.API_URL+"login/userspublic/"+where);
    }
    public class detailUser extends AsyncTask<String,Void,String> {
        public detailUser(SettingActivity activity){

        }
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
                            nokk.setText(obj.getString("no_kk"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("error", e.getMessage());
                }

            }
            if(dialog.isShowing()){
                dialog.dismiss();
            }
        }
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }
}
