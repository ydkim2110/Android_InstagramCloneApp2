package com.example.anti2110.instagramcloneapp2.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.anti2110.instagramcloneapp2.Fragment.PostDetailFragment;
import com.example.anti2110.instagramcloneapp2.Fragment.ProfileFragment;
import com.example.anti2110.instagramcloneapp2.Model.Notification;
import com.example.anti2110.instagramcloneapp2.Model.Post;
import com.example.anti2110.instagramcloneapp2.Model.User;
import com.example.anti2110.instagramcloneapp2.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by anti2110 on 2018-11-23
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private static final String TAG = "NotificationAdapter";

    private Context mContext;
    private List<Notification> mNotifications;

    public NotificationAdapter(Context context, List<Notification> notifications) {
        mContext = context;
        mNotifications = notifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Log.d(TAG, "onCreateViewHolder: started.");
        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_item, viewGroup, false);
        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Notification notification = mNotifications.get(i);

        viewHolder.mComment.setText(notification.getComment());

        getUserInfo(viewHolder.mImageProfile, viewHolder.mUsername, notification.getUser_id());

        if (notification.isIs_post()) {
            viewHolder.mPostImage.setVisibility(View.VISIBLE);
            getPostImage(viewHolder.mPostImage, notification.getPost_id());
        } else {
            viewHolder.mPostImage.setVisibility(View.GONE);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (notification.isIs_post()) {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString(mContext.getString(R.string.string_post_id), notification.getPost_id());
                    editor.apply();

                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new PostDetailFragment())
                            .commit();
                } else {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString(mContext.getString(R.string.string_profile_id), notification.getUser_id());
                    editor.apply();

                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new ProfileFragment())
                            .commit();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNotifications.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImageProfile, mPostImage;
        public TextView mUsername, mComment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mImageProfile = itemView.findViewById(R.id.image_profile);
            mPostImage = itemView.findViewById(R.id.post_image);
            mUsername = itemView.findViewById(R.id.username);
            mComment = itemView.findViewById(R.id.comment);

        }
    }

    private void getUserInfo(final ImageView imageView, final TextView username, String publisherId) {
        Log.d(TAG, "getUserInfo: ");

        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference(mContext.getString(R.string.dbname_users))
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

    private void getPostImage(final ImageView imageView, String postId) {
        Log.d(TAG, "getPostImage: ");

        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference(mContext.getString(R.string.dbname_posts))
                .child(postId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);

                Glide.with(mContext).load(post.getPost_image()).into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
