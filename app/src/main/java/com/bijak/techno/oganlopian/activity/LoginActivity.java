package com.bijak.techno.oganlopian.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bijak.techno.oganlopian.R;
import com.bijak.techno.oganlopian.data.HttpGetUrl;
import com.bijak.techno.oganlopian.data.InfoWindowAdapter;
import com.bijak.techno.oganlopian.data.ParseFirebaseData;
import com.bijak.techno.oganlopian.data.SettingsAPI;
import com.bijak.techno.oganlopian.service.ConnectivityReceiver;
import com.bijak.techno.oganlopian.util.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.bijak.techno.oganlopian.util.Constants.NODE_NAME;
import static com.bijak.techno.oganlopian.util.Constants.NODE_PHOTO;
import static com.bijak.techno.oganlopian.util.Constants.NODE_ROLE;

public class LoginActivity extends AppCompatActivity {

    EditText userEmail, userPassword;
    Button loginButton, newUserButton,lupaPwd;
    TextView txtversion;
    private FirebaseAuth mAuth;
    SettingsAPI set;
    ParseFirebaseData pdfd ;
    DatabaseReference ref;
    private ProgressDialog dialog;
    private ProgressBar progressBar;
    public static final String USERS_CHILD = "users";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.controller_login);

        //This will get the Instance of Current  Auth state
        // FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBarL);
        progressBar.setVisibility(View.GONE);
        userEmail = findViewById(R.id.Email_editText);
        userPassword = findViewById(R.id.Password_editText);
        loginButton = findViewById(R.id.Login_button);
        newUserButton = findViewById(R.id.btnDone);
        lupaPwd =(Button) findViewById(R.id.btnExpand);
        pdfd = new ParseFirebaseData(this);
        set = new SettingsAPI(this);
        dialog = new ProgressDialog(this);
        txtversion = findViewById(R.id.txt_version);
        PackageInfo pinfo = null;
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            int versionNumber = pinfo.versionCode;
            String versionName = pinfo.versionName;
            txtversion.setText("App Version : "+versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //registrasi user baru
        newUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this,RegistrationActivity.class);
                startActivity(intent);
            }
        });
        //lupa passrword
        lupaPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,ChangepwdActivity.class);
                startActivity(intent);
            }
        });
        //login proses
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = userEmail.getText().toString()+"@mail.com";
                String Password = userPassword.getText().toString();
                set.deleteAllSettings();
                if(userEmail.getText().toString().length()==16) {
                    loginUser(Email, Password);
                }else{
                    Toast.makeText(LoginActivity.this,"NIK harus di isi dengan 16 angka",Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    private void loginUser(String email, String password) {
        dialog.setMessage("Please wait...");
        dialog.show();
        //This method will authenticate the user details..
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Login in success, Direct to Dashboard page
                    //TODO: saved on preference

                    FirebaseUser users = FirebaseAuth.getInstance().getCurrentUser();
                    if (users != null) {
                        final String uid = users.getUid();

                        ref = FirebaseDatabase.getInstance().getReference(USERS_CHILD);
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                String usrNm = null;
                                String usrId = uid;
                                String usrDp = null;
                                String usrRo = null;
                                for (DataSnapshot data : snapshot.getChildren()) {
                                    String userid = data.getKey();
                                    if (userid.hashCode() == usrId.hashCode()) {
                                        usrNm = data.child(NODE_NAME).getValue().toString();
                                        usrDp = data.child(NODE_PHOTO).getValue().toString();
                                        usrRo = data.child(NODE_ROLE).getValue().toString();
                                        set.addUpdateSettings(Constants.PREF_MY_ID, usrId);
                                        set.addUpdateSettings(Constants.PREF_MY_NAME, usrNm);
                                        set.addUpdateSettings(Constants.PREF_MY_DP, usrDp);
                                        set.addUpdateSettings(Constants.PREF_MY_ROL, usrRo);
                                        __get_userID(usrId);
                                    }

                                }


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }
                } else {

                    if(dialog.isShowing()){
                        dialog.dismiss();
                    }
                    Toast.makeText(LoginActivity.this, "Authentication failed: Please Check UserName or Password", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //progressBar.setVisibility(View.GONE);
    }
    public void onResume(){
        super.onResume();
        //MyApplication.getInstance().setConnectivityListener((ConnectivityReceiver.ConnectivityReceiverListener) getApplicationContext());
    }
    private void __get_userID(String keycode){
        String where ="?keycode="+keycode;
        new detailUser(this).execute(ConnectivityReceiver.API_URL +"login/userspublic/"+where);
    }
    public class detailUser extends AsyncTask<String,Void,String>{
        public detailUser(LoginActivity activity){

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
                            set.addUpdateSettings(Constants.PREF_MY_UID, obj.getString("user_id"));
                        }
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(LoginActivity.this, "Authentication failed: Please Check UserName or Password", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("error", e.getMessage());
                }
                if(dialog.isShowing()){
                    dialog.dismiss();
                }
            }
        }
    }
}
