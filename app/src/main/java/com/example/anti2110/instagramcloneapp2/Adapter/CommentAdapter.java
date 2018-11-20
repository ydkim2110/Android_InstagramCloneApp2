package com.example.anti2110.instagramcloneapp2.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.anti2110.instagramcloneapp2.MainActivity;
import com.example.anti2110.instagramcloneapp2.Model.Comment;
import com.example.anti2110.instagramcloneapp2.Model.User;
import com.example.anti2110.instagramcloneapp2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by anti2110 on 2018-11-20
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private static final String TAG = "CommentAdapter";

    public static final String EXTRA_PUBLISHER_ID = "publisher_id";

    private Context mContext;
    private List<Comment> mCommentList;

    private FirebaseUser mFirebaseUser;

    public CommentAdapter(Context context, List<Comment> commentList) {
        mContext = context;
        mCommentList = commentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Comment comment = mCommentList.get(i);

        viewHolder.mComment.setText(comment.getComment());
        getUserInfo(viewHolder.mImageProfile, viewHolder.mUsername, comment.getPublisher());

        viewHolder.mComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra(EXTRA_PUBLISHER_ID, comment.getPublisher());
                mContext.startActivity(intent);
            }
        });

        viewHolder.mImageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra(EXTRA_PUBLISHER_ID, comment.getPublisher());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCommentList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImageProfile;
        public TextView mUsername, mComment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mImageProfile = itemView.findViewById(R.id.image_profile);
            mUsername = itemView.findViewById(R.id.username);
            mComment = itemView.findViewById(R.id.comment);

        }
    }

    private void getUserInfo(final ImageView imageView, final TextView username, String publisherId) {

        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference()
                .child(mContext.getString(R.string.dbname_users))
                .child(publisherId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImageUrl()).into(imageView);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
