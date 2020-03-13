package com.bijak.techno.oganlopian.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bijak.techno.oganlopian.R;
import com.bijak.techno.oganlopian.data.HttpGetUrl;
import com.bijak.techno.oganlopian.data.SettingsAPI;
import com.bijak.techno.oganlopian.model.UsersModel;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static com.bijak.techno.oganlopian.util.Constants.getPostDataString;

public class ChangepwdActivity extends AppCompatActivity {
    private EditText email,nik;
    private Button submit,batal;
    private ProgressDialog dialog;
    SettingsAPI set;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepwd);
        dialog = new ProgressDialog(this);
        set = new SettingsAPI(this);
        email = (EditText) findViewById(R.id.inpEmail);
        nik =(EditText) findViewById(R.id.nik_usere);
        submit =(Button) findViewById(R.id.btnSubmit);
        batal =(Button) findViewById(R.id.btnClear);
        batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChangepwdActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nik.getText().toString().length()!=16){
                    Toast.makeText(getApplicationContext(),"NIK harus di isi dengan 16 angka",Toast.LENGTH_LONG).show();
                    return;
                }else {
                    if(Constants.isValidEmail(email)) {
                        String where = "?nik=" + nik.getText().toString() + "&email=" + email.getText().toString();
                        new cekNIK(getApplicationContext()).execute(ConnectivityReceiver.API_URL + "laporan/ceknik" + where);
                    }else{
                        Toast.makeText(getApplicationContext(),"Email tidak valid",Toast.LENGTH_LONG).show();
                        return;
                    }
                }
            }
        });
    }
    public class KirimEmail extends AsyncTask<UsersModel, Void, Void>
    {
        private final Context contexts;
        public KirimEmail(Context c) {
            this.contexts = c;
        }
        @Override
        protected void onPreExecute(){
            /*dialog.setMessage("Submit data in progress....please wait");
            dialog.setCancelable(false);
            dialog.show();*/
        }
        @Override
        protected Void doInBackground(UsersModel... params) {
            try {
                UsersModel usri=params[0];
                URL url1 = new URL(ConnectivityReceiver.API_URL + "laporan/sendresetpwd");
                JSONObject postData = new JSONObject();
                postData.put("user_id",usri.getUserName());
                postData.put("email",usri.getEmail());
                postData.put("nama",usri.getNamaLengkap());
                Log.e("param", postData.toString());
                HttpURLConnection Conn = (HttpURLConnection) url1.openConnection();
                Conn.setReadTimeout(15000 /* milliseconds */);
                Conn.setConnectTimeout(15000 /* milliseconds */);
                Conn.setRequestMethod("POST");
                Conn.setDoInput(true);
                Conn.setDoOutput(true);

                OutputStream os = Conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
                writer.write(getPostDataString(postData));

                writer.flush();
                writer.close();
                os.close();
                int responseCode = Conn.getResponseCode();
                if(responseCode==200) {
                    dialog.dismiss();
                    ChangepwdActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder alertDialogs = new AlertDialog.Builder(ChangepwdActivity.this);
                            alertDialogs.setCancelable(false);
                            alertDialogs.setTitle("Reset Password");
                            alertDialogs.setMessage("Link Reset Passoword telah di kirim ke email\nSilahkan check email anda");
                            alertDialogs.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Toast.makeText(getApplicationContext(),"Link Reset Passoword telah di kirim ke email\nSilahkan check email anda",Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(ChangepwdActivity.this,LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            AlertDialog alertDialog = alertDialogs.create();
                            alertDialog.show();
                        }
                    });

                }

            }catch (MalformedURLException e){
                e.printStackTrace();
                //Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }catch (IOException e) {
                e.printStackTrace();
                //Toast.makeText(context,e.getMessage().toString(),Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }catch (JSONException e){
                e.printStackTrace();
                //Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }catch (Exception e){
                e.printStackTrace();
                //Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }

            return null;
        }
        protected void onPostExecute() {

        }
    }
    public class cekNIK extends AsyncTask<String,Void,String>{
        private final Context contexts;
        public cekNIK(Context context){
            this.contexts=context;
        }
        @Override
        protected void onPreExecute() {
            dialog.setMessage("Submit data in progress....please wait");
            dialog.setCancelable(false);
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
                    if (Integer.valueOf(jsonObject.getString("totaldata")) == 0) {
                        dialog.dismiss();
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ChangepwdActivity.this);
                        alertDialog.setCancelable(false);
                        alertDialog.setTitle("Reset Password");
                        alertDialog.setMessage("Gabungan NIK dan Email tidak sesuai dengan data registrasi anda\nSilahkan hubung Administrator OganLopian.");
                        alertDialog.setPositiveButton("OK", null);
                        AlertDialog dlg = alertDialog.create();
                        dlg.show();
                    }else{
                        JSONArray arrD = jsonObject.getJSONArray("message");
                        //dialog.dismiss();
                        UsersModel usr = new UsersModel();
                        for (int i = 0; i < arrD.length(); i++) {
                            JSONObject obj = arrD.getJSONObject(i);
                            usr.setUserName(obj.getString("user_id"));
                            usr.setEmail(email.getText().toString());
                            usr.setNamaLengkap(obj.getString("nama_lengkap"));
                        }
                        new KirimEmail(getApplicationContext()).execute(usr);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("error", e.getMessage());
                }
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }
    }
}
