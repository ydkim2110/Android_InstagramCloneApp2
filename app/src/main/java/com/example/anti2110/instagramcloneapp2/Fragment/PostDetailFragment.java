package com.example.anti2110.instagramcloneapp2.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.anti2110.instagramcloneapp2.Adapter.PostAdapter;
import com.example.anti2110.instagramcloneapp2.Model.Post;
import com.example.anti2110.instagramcloneapp2.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anti2110 on 2018-11-21
 */
public class PostDetailFragment extends Fragment {

    private static final String TAG = "PostDetailFragment";

    private RecyclerView mRecyclerView;
    private PostAdapter mPostAdapter;
    private List<Post> mPostList;

    private String mPostId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: started.");
        View view = inflater.inflate(R.layout.fragment_post_detail, container, false);

        SharedPreferences preferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        mPostId = preferences.getString("post_id", "none");
        Log.d(TAG, "onCreateView: mPostId : " + mPostId);

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mPostList = new ArrayList<>();
        mPostAdapter = new PostAdapter(getContext(), mPostList);
        mRecyclerView.setAdapter(mPostAdapter);

        readPost();

        return view;

    }

    private void readPost() {

        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("App2_Posts")
                .child(mPostId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mPostList.clear();
                Post post = dataSnapshot.getValue(Post.class);
                mPostList.add(post);

                mPostAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
