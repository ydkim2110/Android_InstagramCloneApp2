package com.example.anti2110.instagramcloneapp2.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.anti2110.instagramcloneapp2.Adapter.UserAdapter;
import com.example.anti2110.instagramcloneapp2.Model.User;
import com.example.anti2110.instagramcloneapp2.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anti2110 on 2018-11-18
 */
public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";

    private EditText mSearchBar;

    private RecyclerView mRecyclerView;
    private UserAdapter mUserAdapter;
    private List<User> mUserList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: started.");

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mSearchBar = view.findViewById(R.id.search_bar);

        mUserList = new ArrayList<>();

        mUserAdapter = new UserAdapter(getContext(), mUserList);
        mRecyclerView.setAdapter(mUserAdapter);

        readUsers();
        mSearchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUsers(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;
    }

    private void searchUsers(String s) {
        Query query = FirebaseDatabase.getInstance().getReference(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_user_username))
                .startAt(s)
                .endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUserList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    mUserList.add(user);
                }

                mUserAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readUsers() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.dbname_users));

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mSearchBar.getText().toString().equals("")) {
                    mUserList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        mUserList.add(user);
                    }
                    mUserAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
