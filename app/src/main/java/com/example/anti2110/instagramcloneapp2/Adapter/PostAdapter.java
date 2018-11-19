package com.example.anti2110.instagramcloneapp2.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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

import java.util.List;

/**
 * Created by anti2110 on 2018-11-19
 */
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Context mContext;
    private List<Post> mPostList;

    public PostAdapter(Context context, List<Post> postList) {
        mContext = context;
        mPostList = postList;
    }

    private FirebaseUser mFirebaseUser;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, viewGroup, false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Post post = mPostList.get(i);

        Glide.with(mContext).load(post.getPost_image()).into(viewHolder.mPostImage);

        if (post.getDescription().equals("")) {
            viewHolder.mDescription.setVisibility(View.GONE);
        } else {
            viewHolder.mDescription.setVisibility(View.VISIBLE);
            viewHolder.mDescription.setText(post.getDescription());
        }

        publisherInfo(viewHolder.mImageProfile, viewHolder.mUsername, viewHolder.mPublisher, post.getPublisher());

    }

    @Override
    public int getItemCount() {
        return mPostList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImageProfile, mPostImage, mLike, mComment, mSave;
        public TextView mUsername , mLikes, mPublisher, mDescription, mComments;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mImageProfile = itemView.findViewById(R.id.image_profile);
            mPostImage = itemView.findViewById(R.id.post_image);
            mLike = itemView.findViewById(R.id.like);
            mComment = itemView.findViewById(R.id.comment);
            mSave = itemView.findViewById(R.id.save);
            mUsername = itemView.findViewById(R.id.username);
            mLikes = itemView.findViewById(R.id.likes);
            mPublisher = itemView.findViewById(R.id.publisher);
            mDescription = itemView.findViewById(R.id.description);
            mComments = itemView.findViewById(R.id.comments);
        }
    }

    private void publisherInfo(final ImageView imageProfile, final TextView username, final TextView publisher, String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference(mContext.getString(R.string.dbname_users)).child(userId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                Glide.with(mContext).load(user.getImageUrl()).into(imageProfile);
                username.setText(user.getUsername());
                publisher.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
