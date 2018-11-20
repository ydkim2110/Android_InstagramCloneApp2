package com.example.anti2110.instagramcloneapp2.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.anti2110.instagramcloneapp2.Model.Post;
import com.example.anti2110.instagramcloneapp2.R;

import java.util.List;

/**
 * Created by anti2110 on 2018-11-20
 */
public class MyFotoAdapter extends RecyclerView.Adapter<MyFotoAdapter.ViewHolder> {

    private static final String TAG = "MyFotoAdapter";

    private Context mContext;
    private List<Post> mPostList;

    public MyFotoAdapter(Context context, List<Post> postList) {
        mContext = context;
        mPostList = postList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.fotos_item, viewGroup, false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        Post post = mPostList.get(i);

        Glide.with(mContext).load(post.getPost_image()).into(viewHolder.mPostImage);

    }

    @Override
    public int getItemCount() {
        return mPostList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView mPostImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mPostImage = itemView.findViewById(R.id.post_image);
        }
    }

}
