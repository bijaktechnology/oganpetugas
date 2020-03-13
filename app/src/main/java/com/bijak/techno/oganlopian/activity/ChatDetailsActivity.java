package com.bijak.techno.oganlopian.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;

import com.bijak.techno.oganlopian.R;
import com.bijak.techno.oganlopian.adapter.ChatDetailsListAdapter;
import com.bijak.techno.oganlopian.data.ParseFirebaseData;
import com.bijak.techno.oganlopian.data.SettingsAPI;
import com.bijak.techno.oganlopian.model.ChatMessage;
import com.bijak.techno.oganlopian.model.Friend;
import com.bijak.techno.oganlopian.util.Constants;
import com.bijak.techno.oganlopian.util.CustomToast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.bijak.techno.oganlopian.util.Constants.NODE_IS_READ;
import static com.bijak.techno.oganlopian.util.Constants.NODE_RECEIVER_ID;
import static com.bijak.techno.oganlopian.util.Constants.NODE_RECEIVER_NAME;
import static com.bijak.techno.oganlopian.util.Constants.NODE_RECEIVER_PHOTO;
import static com.bijak.techno.oganlopian.util.Constants.NODE_SENDER_ID;
import static com.bijak.techno.oganlopian.util.Constants.NODE_SENDER_NAME;
import static com.bijak.techno.oganlopian.util.Constants.NODE_SENDER_PHOTO;
import static com.bijak.techno.oganlopian.util.Constants.NODE_TEXT;
import static com.bijak.techno.oganlopian.util.Constants.NODE_TIMESTAMP;
import static com.bijak.techno.oganlopian.util.Constants.PREF_MY_NAME;
import static com.bijak.techno.oganlopian.util.Constants.PREF_MY_ROL;

public class ChatDetailsActivity extends AppCompatActivity {
    public static String KEY_FRIEND = "FRIEND";
    public static final String USERS_CHILD = "users";
    public static String ASAL_CHAT="model";
    // give preparation animation activity transition
    public static void navigate(AppCompatActivity activity, View transitionImage, Friend obj) {
        Intent intent = new Intent(activity, ChatDetailsActivity.class);
        intent.putExtra(KEY_FRIEND, obj);
        intent.putExtra(ASAL_CHAT,"normal");
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionImage, KEY_FRIEND);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    private Button btn_send;
    private EditText et_content;
    public static ChatDetailsListAdapter mAdapter;

    private ListView listview;
    private ActionBar actionBar;
    private Friend friend;
    private ArrayList<Friend> friendList;
    private List<ChatMessage> items = new ArrayList<>();
    private View parent_view;
    ParseFirebaseData pfbd;
    SettingsAPI set;
    private FirebaseAuth mAuth;

    String chatNode, chatNode_1, chatNode_2,datax, asalChat;
    private JSONObject dataxl;
    DatabaseReference ref;
    DatabaseReference ref2;
    ValueEventListener valueEventListener;
    ValueEventListener valueEventListener2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_details);
        parent_view = findViewById(android.R.id.content);
        pfbd = new ParseFirebaseData(this);
        set = new SettingsAPI(this);
        btn_send = findViewById(R.id.btn_send);
        // animation transition
        ViewCompat.setTransitionName(parent_view, KEY_FRIEND);

        // initialize conversation data
        Intent intent = getIntent();
        asalChat = intent.getStringExtra("model");
        friend = (Friend) intent.getExtras().getSerializable(KEY_FRIEND);
        if(friend==null){
           datax=intent.getStringExtra("chat-from");

           try{
               //dataxl = new JSONObject(datax);
               friend =(new Friend(intent.getStringExtra("chat-from"),intent.getStringExtra("chat-name"),Constants.IMG_URL));
               /*lakukan registrasi user ke firebase jika blm ada*/
               //registerUser(dataxl.getString("user_id"),"User",dataxl.getString("nama_user"));
               Log.i("d",datax);
           }catch(Exception e){
               e.printStackTrace();
           }
        }else{
            friend = (Friend) intent.getExtras().getSerializable(KEY_FRIEND);
        }
        initToolbar();
        iniComponen();
        chatNode_1 = set.readSetting(Constants.PREF_MY_UID) + "-" + friend.getId();
        chatNode_2 = friend.getId() + "-" + set.readSetting(Constants.PREF_MY_UID);

        valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(Constants.LOG_TAG,"Data changed from activity");
                if (dataSnapshot.hasChild(chatNode_1)) {
                    chatNode = chatNode_1;
                } else if (dataSnapshot.hasChild(chatNode_2)) {
                    chatNode = chatNode_2;
                } else {
                    chatNode = chatNode_1;
                }
                items.clear();
                items.addAll(pfbd.getMessagesForSingleUser(dataSnapshot.child(chatNode)));

                //Here we are traversing all the messages and mark all received messages read
                if(asalChat.equals("jobs")){
                    String petugas = set.readSetting(PREF_MY_ROL);
                    String namane = set.readSetting(PREF_MY_NAME);
                    //et_content.setText(petugas+" "+ namane+ " Siap melayani Anda, Mohon di tunggu.");
                    String txt = et_content.getText().toString();
                    if(txt.equals(petugas+" "+ namane+ " Siap melayani Anda, Mohon di tunggu.")) {
                        btn_send.callOnClick();
                        //et_content.setText("");
                    }

                }
                for (DataSnapshot data : dataSnapshot.child(chatNode).getChildren()) {
                    if (data.child(NODE_RECEIVER_ID).getValue().toString().equals(set.readSetting(Constants.PREF_MY_UID))) {
                        data.child(NODE_IS_READ).getRef().runTransaction(new Transaction.Handler() {
                            @NonNull
                            @Override
                            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                mutableData.setValue(true);
                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                            }
                        });
                    }
                }

                // TODO: 12/09/18 Change it to recyclerview
                mAdapter = new ChatDetailsListAdapter(ChatDetailsActivity.this, items);
                listview.setAdapter(mAdapter);
                listview.requestFocus();
                registerForContextMenu(listview);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                new CustomToast(ChatDetailsActivity.this).showError(getString(R.string.error_could_not_connect));
            }
        };

        ref = FirebaseDatabase.getInstance().getReference(Constants.MESSAGE_CHILD);
        ref.addValueEventListener(valueEventListener);

        // for system bar in lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //Tools.systemBarLolipop(this);
        }
        /*if(asalChat.equals("jobs")){
            String petugas = set.readSetting(PREF_MY_ROL);
            String namane = set.readSetting(PREF_MY_NAME);
            et_content.setText(petugas+" "+ namane+ " Siap melayani Anda, Mohon di tunggu.");
            //btn_send.callOnClick();
            hideKeyboard();
        }*/
    }

    public void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(friend.getName());
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //cek halaman sebelumnya dan harus kembali ke halaman sebelumnya
                if(asalChat.equals("jobs") || asalChat.equals("peta")) {
                    Intent i = new Intent(ChatDetailsActivity.this, HomeActivity.class);
                    startActivity(i);
                    //onBackPressed();
                }else if(asalChat.equals("tugas")){
                    Intent i = new Intent(ChatDetailsActivity.this, HomeActivity.class);
                    startActivity(i);
                    finish();
                }else {
                    Intent i = new Intent(ChatDetailsActivity.this, BookmonActivity.class);
                    startActivity(i);
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void iniComponen() {
        listview = findViewById(R.id.listview);
        btn_send = findViewById(R.id.btn_send);
        et_content = findViewById(R.id.text_content);
        if(asalChat.equals("jobs")){
            String petugas = set.readSetting(PREF_MY_ROL);
            String namane = set.readSetting(PREF_MY_NAME);
            et_content.setText(petugas+" "+ namane+ " Siap melayani Anda, Mohon di tunggu.");
        }
        btn_send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                /* ChatMessage im=new ChatMessage(et_content.getText().toString(), String.valueOf(System.currentTimeMillis()),friend.getId(),friend.getName(),friend.getPhoto()); */

                HashMap hm = new HashMap();
                hm.put(NODE_TEXT, et_content.getText().toString());
                hm.put(NODE_TIMESTAMP, String.valueOf(System.currentTimeMillis()));
                hm.put(NODE_RECEIVER_ID, friend.getId());
                hm.put(NODE_RECEIVER_NAME, friend.getName());
                hm.put(NODE_RECEIVER_PHOTO, friend.getPhoto());
                hm.put(NODE_SENDER_ID, set.readSetting(Constants.PREF_MY_UID));
                hm.put(NODE_SENDER_NAME, set.readSetting(Constants.PREF_MY_NAME));
                hm.put(NODE_SENDER_PHOTO, set.readSetting(Constants.PREF_MY_DP));
                hm.put(NODE_IS_READ, false);

                ref.child(chatNode).push().setValue(hm);
                et_content.setText("");
                hideKeyboard();
            }
        });
        et_content.addTextChangedListener(contentWatcher);
        if (et_content.length() == 0) {
            btn_send.setVisibility(View.GONE);
        }
        hideKeyboard();
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private TextWatcher contentWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable etd) {
            if (etd.toString().trim().length() == 0) {
                btn_send.setVisibility(View.GONE);
            } else {
                btn_send.setVisibility(View.VISIBLE);
            }
            //draft.setContent(etd.toString());
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }
    };

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Toast.makeText(this,"Please press back button on toolbar for back to previous page",Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            super.onKeyDown(keyCode, event);
            return true;
        }
        return false;
    }
    @Override
    protected void onDestroy() {
        //Remove the listener, otherwise it will continue listening in the background
        //We have service to run in the background
        ref.removeEventListener(valueEventListener);
        super.onDestroy();
    }
}
