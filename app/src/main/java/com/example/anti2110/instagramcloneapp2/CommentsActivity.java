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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.anti2110.instagramcloneapp2.Adapter.CommentAdapter;
import com.example.anti2110.instagramcloneapp2.Model.Comment;
import com.example.anti2110.instagramcloneapp2.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentsActivity extends AppCompatActivity {

    private static final String TAG = "CommentsActivity";

    public static final String EXTRA_POST_ID = "post_id";
    public static final String EXTRA_PUBLISHER_ID = "publisher_id";

    private EditText mAddComment;
    private ImageView mImageProfile;
    private TextView mPost;

    private String mPostId, mPublisherId;

    private FirebaseUser mFirebaseUser;

    private RecyclerView mRecyclerView;
    private CommentAdapter mCommentAdapter;
    private List<Comment> mCommentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        Log.d(TAG, "onCreate: started.");

        initToolbar();
        getIncomingIntent();

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mCommentList = new ArrayList<>();
        mCommentAdapter = new CommentAdapter(this, mCommentList);
        mRecyclerView.setAdapter(mCommentAdapter);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mAddComment = findViewById(R.id.add_comment);
        mImageProfile = findViewById(R.id.image_profile);
        mPost = findViewById(R.id.post);

        mPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAddComment.getText().toString().equals("")) {
                    Toast.makeText(CommentsActivity.this, R.string.toast_please_enter_message, Toast.LENGTH_SHORT).show();
                } else {
                    addComment();
                }
            }
        });

        getImage();
        readComments();
    }

    private void getIncomingIntent() {
        Log.d(TAG, "getIncomingIntent: started.");
        Intent intent = getIntent();
        mPostId = intent.getStringExtra(EXTRA_POST_ID);
        mPublisherId = intent.getStringExtra(EXTRA_PUBLISHER_ID);
    }

    private void initToolbar() {
        Log.d(TAG, "initToolbar: started.");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.toolbar_title_comments);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void addComment() {
        Log.d(TAG, "addComment: started.");
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference(getString(R.string.dbname_comments))
                .child(mPostId);

        Map<String, Object> commentMap = new HashMap<>();
        commentMap.put(getString(R.string.field_comments_comment), mAddComment.getText().toString());
        commentMap.put(getString(R.string.field_comments_publisher), mFirebaseUser.getUid());

        reference.push().setValue(commentMap);
        mAddComment.setText("");

    }

    private void getImage() {
        Log.d(TAG, "getImage: started.");
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference(getString(R.string.dbname_users))
                .child(mFirebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(getApplicationContext()).load(user.getImageUrl()).into(mImageProfile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void readComments() {
        Log.d(TAG, "readComments: started.");
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference(getString(R.string.dbname_comments))
                .child(mPostId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mCommentList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Comment comment = snapshot.getValue(Comment.class);
                    mCommentList.add(comment);
                }

                mCommentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
