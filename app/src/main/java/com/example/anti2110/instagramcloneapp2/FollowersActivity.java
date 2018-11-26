package com.example.anti2110.instagramcloneapp2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.anti2110.instagramcloneapp2.Adapter.UserAdapter;
import com.example.anti2110.instagramcloneapp2.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FollowersActivity extends AppCompatActivity {

    private static final String TAG = "FollowersActivity";
    public static final String EXTRA_ID = "id";
    public static final String EXTRA_TITLE = "title";

    private String mId, mTitle;
    private List<String> mIdList;

    private RecyclerView mRecyclerView;
    private UserAdapter mUserAdapter;
    private List<User> mUserList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);
        Log.d(TAG, "onCreate: started.");

        Intent intent = getIntent();
        mId = intent.getStringExtra(EXTRA_ID);
        mTitle = intent.getStringExtra(EXTRA_TITLE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(mTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUserList = new ArrayList<>();
        mUserAdapter = new UserAdapter(this, mUserList, false);
        mRecyclerView.setAdapter(mUserAdapter);

        mIdList = new ArrayList<>();

        switch (mTitle) {
            case "likes":
                getLikes();
                break;
            case "following":
                getFollowing();
                break;
            case "followers":
                getFollowers();
                break;
        }

    }

    private void getLikes() {
        Log.d(TAG, "getLikes: started.");

        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference(getString(R.string.dbname_likes))
                .child(mId);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mIdList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    mIdList.add(snapshot.getKey());
                }
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getFollowing() {
        Log.d(TAG, "getFollowing: started.");

        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference(getString(R.string.dbname_follow))
                .child(mId)
                .child(getString(R.string.field_follow_following));

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mIdList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    mIdList.add(snapshot.getKey());
                }
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getFollowers() {
        Log.d(TAG, "getFollowers: started.");

        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference(getString(R.string.dbname_follow))
                .child(mId)
                .child(getString(R.string.field_follow_followers));

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mIdList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    mIdList.add(snapshot.getKey());
                }
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showUsers() {
        Log.d(TAG, "showUsers: started.");

        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference(getString(R.string.dbname_users));

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUserList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    for (String id : mIdList) {
                        if (user.getId().equals(id)) {
                            mUserList.add(user);
                        }
                    }
                }
                mUserAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
