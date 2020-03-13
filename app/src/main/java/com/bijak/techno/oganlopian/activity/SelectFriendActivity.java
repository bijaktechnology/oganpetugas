package com.bijak.techno.oganlopian.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bijak.techno.oganlopian.R;
import com.bijak.techno.oganlopian.adapter.FriendsListAdapter;
import com.bijak.techno.oganlopian.data.ParseFirebaseData;
import com.bijak.techno.oganlopian.model.Friend;
import com.bijak.techno.oganlopian.util.CustomToast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SelectFriendActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private RecyclerView recyclerView;
    private FriendsListAdapter mAdapter;
    List<Friend> friendList;

    public static final String USERS_CHILD = "users";
    ParseFirebaseData pfbd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_chat);
        initToolbar();
        initComponent();
        friendList = new ArrayList<>();
        pfbd = new ParseFirebaseData(this);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(USERS_CHILD);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // TODO: 25-05-2017 if number of items is 0 then show something else
                mAdapter = new FriendsListAdapter(SelectFriendActivity.this, pfbd.getAllUser(dataSnapshot));
                recyclerView.setAdapter(mAdapter);

                mAdapter.setOnItemClickListener(new FriendsListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, Friend obj, int position) {
                        ChatDetailsActivity.navigate(SelectFriendActivity.this, findViewById(R.id.lyt_parent), obj);
                    }
                });

                bindView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                new CustomToast(SelectFriendActivity.this).showError(getString(R.string.error_could_not_connect));
            }
        });

        // for system bar in lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //Tools.systemBarLolipop(this);
        }
    }

    private void initComponent() {
        recyclerView = findViewById(R.id.recyclerView);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    public void initToolbar() {
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
//        actionBar.setSubtitle(Constant.getFriendsData(this).size()+" friends");
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(SelectFriendActivity.this,MainActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void bindView() {
        try {
            mAdapter.notifyDataSetChanged();
        } catch (Exception e) {
        }
    }
}
