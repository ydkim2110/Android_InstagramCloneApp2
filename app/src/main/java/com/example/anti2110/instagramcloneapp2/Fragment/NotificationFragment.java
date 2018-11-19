package com.example.anti2110.instagramcloneapp2.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.anti2110.instagramcloneapp2.LoginActivity;
import com.example.anti2110.instagramcloneapp2.R;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by anti2110 on 2018-11-18
 */
public class NotificationFragment extends Fragment {

    private static final String TAG = "NotificationFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: started.");

        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        TextView signout = view.findViewById(R.id.signout);

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
            }
        });

        return view;
    }
}
