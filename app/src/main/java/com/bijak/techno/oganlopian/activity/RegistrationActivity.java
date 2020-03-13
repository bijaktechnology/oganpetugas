package com.bijak.techno.oganlopian.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bijak.techno.oganlopian.R;
import com.bijak.techno.oganlopian.data.SettingsAPI;
import com.bijak.techno.oganlopian.model.UsersModel;
import com.bijak.techno.oganlopian.service.ConnectivityReceiver;
import com.bijak.techno.oganlopian.util.Constants;
import com.bijak.techno.oganlopian.util.CustomToast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

import static com.bijak.techno.oganlopian.util.Constants.NODE_ID;
import static com.bijak.techno.oganlopian.util.Constants.NODE_NAME;
import static com.bijak.techno.oganlopian.util.Constants.NODE_PHOTO;
import static com.bijak.techno.oganlopian.util.Constants.NODE_ROLE;
import static com.bijak.techno.oganlopian.util.Constants.getPostDataString;


public class RegistrationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

//    EditText userEmail, userPassword,userNama,rulese;
    TextInputEditText userEmail, userPassword,userNama,rulese,emailasli;
    Button registerButton;
    private FirebaseAuth mAuth;
    DatabaseReference ref;
    SettingsAPI set;
    private ActionBar actionBar;
    CustomToast customToast;
    private final int SPLASH_DISPLAY_LENGTH = 3000;
    public static final String USERS_CHILD = "users";
    private ProgressBar progressBar;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.controller_register);
        dialog = new ProgressDialog(this);
        //This will get the Instance of Current  Auth state
        //FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        rulese = findViewById(R.id.otorisasi);
        userEmail = findViewById(R.id.userEmail_editText);
        emailasli = findViewById(R.id.userEmailText);
        userPassword = findViewById(R.id.userPassword_editText);
        registerButton = findViewById(R.id.registerButton);
        userNama = findViewById(R.id.userNamaText);
        progressBar = findViewById(R.id.progressBarR);
        String[] jabatan = getResources().getStringArray(R.array.jabatan) ;// new String[]{"Bidan","Dokter","Petugas","Ambulance","Damkar","Operator","Admin"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,jabatan);
        progressBar.setVisibility(View.GONE);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Email = userEmail.getText().toString()+"@mail.com";
                String Password = userPassword.getText().toString();
                String rule =rulese.getText().toString();
                String nama = userNama.getText().toString();
                if(rule.length()==0){
                    Toast.makeText(RegistrationActivity.this,"Jabatan harus di isi",Toast.LENGTH_LONG).show();
                    return;
                }
                if(nama.length()==0){
                    Toast.makeText(RegistrationActivity.this,"Nama Lengkap harus di isi",Toast.LENGTH_LONG).show();
                    return;
                }

                if(userEmail.getText().toString().length()==16) {
                    if(userEmail.getText().toString().substring(0,4).equals("3214")){
                        registerUser(Email, Password,rule, nama);
                    }else{
                        Toast.makeText(RegistrationActivity.this,"NIK bukan wilayah Kabupaten Purwakarta",Toast.LENGTH_LONG).show();
                        return;
                    }

                }else{
                    Toast.makeText(RegistrationActivity.this,"NIK harus di isi dengan 16 angka",Toast.LENGTH_LONG).show();
                }

            }
        });
        initToolbar();
    }

    private void registerUser(String email, String password, final String jabatan, final String Nama) {
        dialog.setMessage("please wait");
        dialog.show();
        //This method will create new User on firebase console...
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    // SignUp in success, Direct to the dashboard Page...
                    ref = FirebaseDatabase.getInstance().getReference(USERS_CHILD);
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            final String usrNm = Nama;
                            final String usrRo = jabatan;
                            final String usrId = mAuth.getUid();
                            final String usrDp = Constants.IMG_URL;

                            if (!snapshot.hasChild(usrId)) {
                                ref.child(usrId + "/" + NODE_NAME).setValue(usrNm);
                                ref.child(usrId + "/" + NODE_PHOTO).setValue(usrDp);
                                ref.child(usrId + "/" + NODE_ID).setValue(usrId);
                                ref.child(usrId + "/" + NODE_ROLE).setValue(usrRo);
                            }
                            _simpanUser(usrId,usrNm,usrRo,email,password);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });


                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(RegistrationActivity.this, "Authentication failed User already exits",
                            Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    /*dialog.setMessage("update user data ... please wait");
                    final String usrNmc = Nama;
                    final String usrRoc = jabatan;
                    final String usrIdc = mAuth.getUid();
                    final String usrDpc = Constants.IMG_URL;
                    _simpanUser(usrIdc,usrNmc,usrRoc,email,password);*/
                }

            }
        });
    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    public void initToolbar() {
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Register Form");
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(RegistrationActivity.this,LoginActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void _simpanUser(String usrId, String usrNm, String usrRo, String email, String password) {
        UsersModel users =new UsersModel();
        String Mail=emailasli.getText().toString();
        Mail =(Mail.length()==0)?email:Mail;
        users.setEmail(Mail);
        users.setPassword(password);
        users.setNamaLengkap(usrNm);
        users.setUserName(usrId);
        users.setUserRole(usrRo);
        users.setNik(usrId);
        new PostDataOPT(this).execute(users);
        progressBar.setVisibility(View.GONE);
    }
    private class PostDataOPT extends AsyncTask<UsersModel, Void, Void>
    {
        private final Context context;
        public PostDataOPT(Context c) {
            this.context = c;
        }
        @Override
        protected void onPreExecute(){

        }
        @Override
        protected Void doInBackground(UsersModel... params) {
            try {
                UsersModel usri=params[0];
                URL url1 = new URL(ConnectivityReceiver.API_URL + "login/users/1");
                JSONObject postData = new JSONObject();
                postData.put("keycode",usri.getUserName());
                postData.put("nama_lengkap",usri.getNamaLengkap());
                postData.put("email",usri.getEmail());
                postData.put("user_role",usri.getUserRole());
                postData.put("password",usri.getPassword());
                postData.put("nik",usri.getNik());
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
                    //Toast.makeText(RegistrationActivity.this, "Authentication Successfully completed", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                    startActivity(intent);
                    dialog.dismiss();
                    finish();
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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
