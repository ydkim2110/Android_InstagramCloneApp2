package com.example.anti2110.instagramcloneapp2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText mUsername, mFullname, mEmail, mPassword;
    private Button mRegister;
    private TextView mTxt_login;

    private FirebaseAuth mAuth;
    private DatabaseReference mReference;
    private ProgressDialog mPd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mUsername = findViewById(R.id.username);
        mFullname = findViewById(R.id.fullname);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mRegister = findViewById(R.id.register);
        mTxt_login = findViewById(R.id.txt_login);

        mAuth = FirebaseAuth.getInstance();

        mTxt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_username = mUsername.getText().toString();
                String str_fullname = mFullname.getText().toString();
                String str_email = mEmail.getText().toString();
                String str_password = mPassword.getText().toString();

                if (TextUtils.isEmpty(str_username) || TextUtils.isEmpty(str_fullname) ||
                        TextUtils.isEmpty(str_email) ||TextUtils.isEmpty(str_password)) {
                    Toast.makeText(RegisterActivity.this, R.string.toast_field_enter_all, Toast.LENGTH_SHORT).show();
                } else if (str_password.length() < 6) {
                    Toast.makeText(RegisterActivity.this, R.string.toast_password_length, Toast.LENGTH_SHORT).show();
                } else {
                    mPd = new ProgressDialog(RegisterActivity.this);
                    mPd.setMessage(getString(R.string.string_please_wait));
                    mPd.show();
                    register(str_username, str_fullname, str_email, str_password);
                }

            }
        });
    }

    private void register(final String username, final String fullname, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            String userId = user.getUid();

                            mReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.dbname_users)).child(userId);

                            HashMap<String, Object> userMap = new HashMap<>();

                            userMap.put(getString(R.string.field_user_id), userId);
                            userMap.put(getString(R.string.field_user_username), username.toLowerCase());
                            userMap.put(getString(R.string.field_user_fullname), fullname);
                            userMap.put(getString(R.string.field_user_bio), "");
                            userMap.put(getString(R.string.field_user_imageUrl), getString(R.string.default_user_imageUrl));

                            mReference.setValue(userMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                mPd.dismiss();
                                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                            }
                                        }
                                    });
                        } else {
                            mPd.dismiss();
                            Toast.makeText(RegisterActivity.this, R.string.toast_signup_failed, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
