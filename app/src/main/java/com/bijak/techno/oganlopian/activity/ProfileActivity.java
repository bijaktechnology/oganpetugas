package com.bijak.techno.oganlopian.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.bijak.techno.oganlopian.R;
import com.bijak.techno.oganlopian.data.HttpGetUrl;
import com.bijak.techno.oganlopian.data.SettingsAPI;
import com.bijak.techno.oganlopian.model.Umume;
import com.bijak.techno.oganlopian.model.UsersModel;
import com.bijak.techno.oganlopian.service.ConnectivityReceiver;
import com.bijak.techno.oganlopian.util.Constants;
import com.bijak.techno.oganlopian.util.CustomToast;
import com.bijak.techno.oganlopian.widget.DateInputMask;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ProfileActivity extends AppCompatActivity {
    private TextInputEditText user_name,email,pwd,nama_lengkap,tgl_lahir,nama_ibu,nohp,no_ktp,no_kk;
    private SettingsAPI settingsAPI;
    ProgressDialog dialog;
    String user_id;
    CustomToast customToast;
    private ActionBar actionBar;
    private Toolbar toolbar;
    private ImageView avatar;
    String[] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE"};
    public static final String KEY_User_Document1 = "avtr";
    private String Document_img1="";
    private Button btn_kk;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile2);
        user_name = findViewById(R.id.profile_inp_username);
        email = findViewById(R.id.profile_inp_email);
        pwd = findViewById(R.id.profile_inp_password);
        nama_lengkap = findViewById(R.id.profile_inp_name);
        tgl_lahir = findViewById(R.id.profile_inp_dob);
        new DateInputMask(tgl_lahir);
        nama_ibu = findViewById(R.id.profile_inp_mother_name);
        nohp = findViewById(R.id.profile_inp_phone);
        no_ktp = findViewById(R.id.profile_inp_no_ktp);
        settingsAPI = new SettingsAPI(this);
        user_id =  settingsAPI.readSetting(Constants.PREF_MY_UID);
        avatar = findViewById(R.id.profile_img_avatar);
        no_kk = findViewById(R.id.profile_inp_no_kk);
        initToolbar();
        customToast = new CustomToast(this);
        dialog = new ProgressDialog(this);
        _getDataProfile(user_id);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //selectImage();
            }
        });
        btn_kk = findViewById(R.id.profile_btn_family_list);
        btn_kk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(no_kk.getText().length()>4) {
                    Intent intent = new Intent(ProfileActivity.this, ProfilekkActivity.class);
                    intent.putExtra("nokk",no_kk.getText().toString());
                    startActivity(intent);
                }else{
                    no_kk.findFocus();
                    no_kk.setFocusable(true);
                    Toast.makeText(getApplicationContext(),"Nomor KK belum lengkap, Silahkan lengkapi terlebih dahulu sebelum menambah data anggota",Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    private void selectImage() {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))
                {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 1);
                }
                else if (options[item].equals("Choose from Gallery"))
                {
                    Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                File f = new File(Environment.getExternalStorageDirectory().toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {
                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), bitmapOptions);
                    bitmap=getResizedBitmap(bitmap, 1024);
                    avatar.setImageBitmap(bitmap);
                    BitMapToString(bitmap);
                    String path = android.os.Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Phoenix" + File.separator + "default";
                    f.delete();
                    OutputStream outFile = null;
                    File file = new File(path, System.currentTimeMillis() + ".jpg");
                    try {
                        outFile = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                        outFile.flush();
                        outFile.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                thumbnail=getResizedBitmap(thumbnail, 2048);
                Log.w("pathofimagefromgallery", picturePath+"");
                avatar.setImageBitmap(thumbnail);
                BitMapToString(thumbnail);
            }
        }
    }
    public String BitMapToString(Bitmap userImage1) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        userImage1.compress(Bitmap.CompressFormat.PNG, 60, baos);
        byte[] b = baos.toByteArray();
        Document_img1 = Base64.encodeToString(b, Base64.DEFAULT);
        return Document_img1;
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }


    public void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Setting");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_save:
                _updateProfile();
                break;
            case android.R.id.home:
                Intent i = new Intent(ProfileActivity.this,SettingActivity.class);
                startActivity(i);
                this.finish();
                break;
        }
        return true;
    }
    private void _updateProfile(){
        UsersModel usr = new UsersModel();
        usr.setEmail(email.getText().toString());
        usr.setNamaLengkap(nama_lengkap.getText().toString());
        usr.setAlamat(pwd.getText().toString());
        usr.setNama_ibu(nama_ibu.getText().toString());
        usr.setTgl_lahir(tgl_lahir.getText().toString());
        usr.setNoTelp(nohp.getText().toString());
        usr.setNik(no_ktp.getText().toString());
        usr.setUid(Integer.valueOf(settingsAPI.readSetting(Constants.PREF_MY_UID)));
        usr.setKartu_keluarga(no_kk.getText().toString());
        new SimpanUser(this).execute(usr);
    }
    private void _getDataProfile(String user_id){
        String where ="?user_id="+user_id;
        new detailUser(this).execute(ConnectivityReceiver.API_URL +"login/userspublic/"+where);
    }
    public class detailUser extends AsyncTask<String,Void,String> {
        public detailUser(ProfileActivity activity){

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
                            user_name.setText(obj.getString("username"));
                            email.setText(obj.getString("email"));
                            pwd.setText(obj.getString("alamat"));
                            //pwd.setVisibility(View.GONE);
                            nama_lengkap.setText(obj.getString("nama_lengkap"));
                            nama_ibu.setText(obj.getString("nama_ibu_kandung"));
                            nohp.setText(obj.getString("no_hp"));
                            no_ktp.setText(obj.getString("nik"));
                            no_kk.setText(obj.getString("no_kk"));
                            String bod = customToast.getDisplayDateTime(obj.getString("tgl_lahir"));
                            byte[] decodedString = Base64.decode(obj.getString("user_img"), Base64.DEFAULT);
                            Bitmap imgBitMap = Constants.String2Bitmap(obj.getString("user_img"));
                            avatar.setImageBitmap(imgBitMap);
                            tgl_lahir.setText(bod);
                        }

                    }else{
                        Toast.makeText(ProfileActivity.this, "Data profile belum lengkap", Toast.LENGTH_SHORT).show();
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
    public class SimpanUser extends AsyncTask<UsersModel,Void,Void>{
        public SimpanUser(ProfileActivity profileActivity){}
        @Override
        protected void onPreExecute() {
            dialog.setMessage("Update profile data in progess... please wait");
            dialog.show();
        }
        @Override
        protected Void doInBackground(UsersModel... umumes) {
            try{
                UsersModel data = umumes[0];
                URL url1 = new URL(ConnectivityReceiver.API_URL + "login/userspublic");
                JSONObject postData = new JSONObject();
                postData.put("user_id", data.getUid().toString());
                postData.put("nama_lengkap",data.getNamaLengkap());
                postData.put("alamat",data.getAlamat());
                postData.put("nik",data.getNik());
                postData.put("nama_ibu_kandung",data.getNama_ibu());
                postData.put("no_hp",data.getNoTelp());
                postData.put("email",data.getEmail());
                postData.put("tgl_lahir", data.getTgl_lahir());
                postData.put("no_kk", data.getKartu_keluarga());
                postData.put("keycode",settingsAPI.readSetting(Constants.PREF_MY_ID));
                Log.e("datane update",postData.toString());
                HttpURLConnection Conn = (HttpURLConnection)url1.openConnection();
                Conn.setReadTimeout(15000 /* milliseconds */);
                Conn.setConnectTimeout(15000 /* milliseconds */);
                Conn.setRequestMethod("PUT");
                Conn.setDoInput(true);
                Conn.setDoOutput(true);

                OutputStream os = Conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
                writer.write(Constants.getPostDataString(postData));

                writer.flush();
                writer.close();
                os.close();
                int responseCode = Conn.getResponseCode();
                Log.i("result-api",String.valueOf(responseCode));
                if(dialog.isShowing()) {
                    dialog.dismiss();
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
    public Bitmap makeTransparentBitmap(Bitmap bmp, int alpha) {
        Bitmap transBmp = Bitmap.createBitmap(bmp.getWidth(),
                bmp.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(transBmp);
        final Paint paint = new Paint();
        paint.setAlpha(alpha);
        canvas.drawBitmap(bmp, 0, 0, paint);
        return transBmp;
    }
    public static Bitmap getBitmapFromURL(String link) {
        try {
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);

            return myBitmap;

        } catch (IOException e) {
            Log.e("LIFERAYSERVICE", "link " + link);
            Log.e("LIFERAYSERVICE", "bitmap download " + e.getMessage());
            return null;
        }
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }
}
