package com.example.anti2110.instagramcloneapp2.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.anti2110.instagramcloneapp2.Adapter.MyFotoAdapter;
import com.example.anti2110.instagramcloneapp2.EditProfileActivity;
import com.example.anti2110.instagramcloneapp2.FollowersActivity;
import com.example.anti2110.instagramcloneapp2.Model.Post;
import com.example.anti2110.instagramcloneapp2.Model.User;
import com.example.anti2110.instagramcloneapp2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by anti2110 on 2018-11-18
 */
public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private ImageView mImageProfile, mOptions;
    private TextView mPosts, mFollowers, mFollowing, mFullname, mBio, mUsername;
    private Button mEditProfile;

    private FirebaseUser mFirebaseUser;
    private String mProfileId;

    private ImageButton mMyFotos, mSavedFotos;

    private RecyclerView mRecyclerView;
    private MyFotoAdapter mMyFotoAdapter;
    private List<Post> mPostList;

    private RecyclerView mRecyclerViewSaves;
    private MyFotoAdapter mMyFotoAdapterSaves;
    private List<Post> mPostListSaves;

    private List<String> mMySaves;

    private TextView mSignOut;


    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: started.");
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mSignOut = view.findViewById(R.id.singout);
        mSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                getActivity().finish();
            }
        });

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences preferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        mProfileId = preferences.getString(getString(R.string.string_profile_id), "none");

        mImageProfile = view.findViewById(R.id.image_profile);
        mOptions = view.findViewById(R.id.options);
        mPosts = view.findViewById(R.id.posts);
        mFollowers = view.findViewById(R.id.followers);
        mFollowing = view.findViewById(R.id.following);
        mFullname = view.findViewById(R.id.fullname);
        mBio = view.findViewById(R.id.bio);
        mUsername = view.findViewById(R.id.username);
        mEditProfile = view.findViewById(R.id.edit_profile);
        mMyFotos = view.findViewById(R.id.my_fotos);
        mSavedFotos = view.findViewById(R.id.saved_fotos);

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 3);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mPostList = new ArrayList<>();
        mMyFotoAdapter = new MyFotoAdapter(getContext(), mPostList);
        mRecyclerView.setAdapter(mMyFotoAdapter);

        mRecyclerViewSaves = view.findViewById(R.id.recycler_view_save);
        mRecyclerViewSaves.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManagerSaves = new GridLayoutManager(getContext(), 3);
        mRecyclerViewSaves.setLayoutManager(linearLayoutManagerSaves);
        mPostListSaves = new ArrayList<>();
        mMyFotoAdapterSaves = new MyFotoAdapter(getContext(), mPostListSaves);
        mRecyclerViewSaves.setAdapter(mMyFotoAdapterSaves);

        mRecyclerView.setVisibility(View.VISIBLE);
        mRecyclerViewSaves.setVisibility(View.GONE);

        userInfo();
        getFollowers();
        getNrPosts();
        myFotos();
        mySaves();

        if (mProfileId.equals(mFirebaseUser.getUid())) {
            mEditProfile.setText("Edit Profile");
        } else {
            checkFollow();
            mSavedFotos.setVisibility(View.GONE);
        }

        mEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btn = mEditProfile.getText().toString();

                if (btn.equals("Edit Profile")) {
                    startActivity(new Intent(getActivity(), EditProfileActivity.class));
                } else if (btn.equals("follow")) {

                    FirebaseDatabase.getInstance().getReference()
                            .child("App2_Follow")
                            .child(mFirebaseUser.getUid())
                            .child("following")
                            .child(mProfileId)
                            .setValue(true);
                    FirebaseDatabase.getInstance().getReference()
                            .child("App2_Follow")
                            .child(mProfileId)
                            .child("followers")
                            .child(mFirebaseUser.getUid())
                            .setValue(true);

                    addNotification();

                }else if (btn.equals("following")) {

                    FirebaseDatabase.getInstance().getReference()
                            .child("App2_Follow")
                            .child(mFirebaseUser.getUid())
                            .child("following")
                            .child(mProfileId)
                            .removeValue();
                    FirebaseDatabase.getInstance().getReference()
                            .child("App2_Follow")
                            .child(mProfileId)
                            .child("followers")
                            .child(mFirebaseUser.getUid())
                            .removeValue();

                }
            }
        });

        mMyFotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecyclerView.setVisibility(View.VISIBLE);
                mRecyclerViewSaves.setVisibility(View.GONE);
            }
        });

        mSavedFotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecyclerView.setVisibility(View.GONE);
                mRecyclerViewSaves.setVisibility(View.VISIBLE);
            }
        });

        mFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra(FollowersActivity.EXTRA_ID, mProfileId);
                intent.putExtra(FollowersActivity.EXTRA_TITLE, "followers");
                startActivity(intent);
            }
        });

        mFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra(FollowersActivity.EXTRA_ID, mProfileId);
                intent.putExtra(FollowersActivity.EXTRA_TITLE, "following");
                startActivity(intent);
            }
        });

        return view;
    }

    private void addNotification() {

        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("App2_Notifications")
                .child(mProfileId);

        Map<String, Object> notiMap = new HashMap<>();
        notiMap.put("user_id", mFirebaseUser.getUid());
        notiMap.put("comment", "started following you");
        notiMap.put("post_id", "");
        notiMap.put("is_post", false);

        reference.push().setValue(notiMap);

    }


    private void userInfo() {

        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("App2_Users").child(mProfileId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (getContext() == null) {
                    return;
                }

                User user = dataSnapshot.getValue(User.class);

                Glide.with(getContext()).load(user.getImageUrl()).into(mImageProfile);
                mUsername.setText(user.getUsername());
                mFullname.setText(user.getFullname());
                mBio.setText(user.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void checkFollow() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("App2_Follow")
                .child(mFirebaseUser.getUid())
                .child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(mProfileId).exists()) {
                    mEditProfile.setText("following");
                } else {
                    mEditProfile.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getFollowers() {

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference()
                .child("App2_Follow")
                .child(mProfileId)
                .child("followers");

        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mFollowers.setText("" + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference()
                .child("App2_Follow")
                .child(mProfileId)
                .child("following");

        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mFollowing.setText("" + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getNrPosts() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(getActivity().getString(R.string.dbname_posts));

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(mProfileId)) {
                        i++;
                    }
                }
                mPosts.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void myFotos() {

        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference(getActivity().getString(R.string.dbname_posts));

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mPostList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(mProfileId)) {
                        mPostList.add(post);
                    }

                }

                Collections.reverse(mPostList);
                mMyFotoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void mySaves() {

        mMySaves = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("App2_Saves")
                .child(mFirebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    mMySaves.add(snapshot.getKey());
                }

                readSaves();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readSaves() {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("App2_Posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mPostListSaves.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);

                    for (String id : mMySaves) {
                        if (post.getPost_id().equals(id)) {
                            mPostListSaves.add(post);
                        }
                    }
                }
                mMyFotoAdapterSaves.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
