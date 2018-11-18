package com.example.anti2110.instagramcloneapp2.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.anti2110.instagramcloneapp2.Fragment.ProfileFragment;
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

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by anti2110 on 2018-11-18
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private static final String TAG = "UserAdapter";

    private Context mContext;
    private List<User> mUserList;

    private FirebaseUser mUser;

    public UserAdapter(Context context, List<User> userList) {
        mContext = context;
        mUserList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        final User user = mUserList.get(i);

        viewHolder.mBtnFollow.setVisibility(View.VISIBLE);

        viewHolder.mUsername.setText(user.getUsername());
        viewHolder.mFullname.setText(user.getFullname());
        Glide.with(mContext).load(user.getImageUrl()).into(viewHolder.mImageProfile);

        isFollowing(user.getId(), viewHolder.mBtnFollow);

        if (user.getId().equals(mUser.getUid())) {
            viewHolder.mBtnFollow.setVisibility(View.GONE);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString(mContext.getString(R.string.string_profile_id), user.getId());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ProfileFragment())
                        .commit();
            }
        });

        viewHolder.mBtnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewHolder.mBtnFollow.getText().toString().equals(mContext.getString(R.string.string_follow))) {
                    FirebaseDatabase.getInstance().getReference()
                            .child(mContext.getString(R.string.dbname_follow))
                            .child(mUser.getUid())
                            .child(mContext.getString(R.string.string_following))
                            .child(user.getId())
                            .setValue(true);
                    FirebaseDatabase.getInstance().getReference()
                            .child(mContext.getString(R.string.dbname_follow))
                            .child(user.getId())
                            .child(mContext.getString(R.string.string_followers))
                            .child(mUser.getUid())
                            .setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference()
                            .child(mContext.getString(R.string.dbname_follow))
                            .child(mUser.getUid())
                            .child(mContext.getString(R.string.string_following))
                            .child(user.getId())
                            .removeValue();
                    FirebaseDatabase.getInstance().getReference()
                            .child(mContext.getString(R.string.dbname_follow))
                            .child(user.getId())
                            .child(mContext.getString(R.string.string_followers))
                            .child(mUser.getUid())
                            .removeValue();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mUsername, mFullname;
        public CircleImageView mImageProfile;
        public Button mBtnFollow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mUsername = itemView.findViewById(R.id.username);
            mFullname = itemView.findViewById(R.id.fullname);
            mImageProfile = itemView.findViewById(R.id.image_profile);
            mBtnFollow = itemView.findViewById(R.id.btn_follow);
        }
    }

    private void isFollowing(final String userid, final Button button) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(mContext.getString(R.string.dbname_follow))
                .child(mUser.getUid())
                .child(mContext.getString(R.string.string_following));

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(userid).exists()) {
                    button.setText(mContext.getString(R.string.string_following));
                } else {
                    button.setText(mContext.getString(R.string.string_follow));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}