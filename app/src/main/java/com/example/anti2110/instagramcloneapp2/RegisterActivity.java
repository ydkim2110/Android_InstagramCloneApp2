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
                    Toast.makeText(RegisterActivity.this, "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if (str_password.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "비밀번호는 6자리 이상입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    mPd = new ProgressDialog(RegisterActivity.this);
                    mPd.setMessage("잠시만 기달려주세요...");
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

                            mReference = FirebaseDatabase.getInstance().getReference().child("App2_Users").child(userId);

                            HashMap<String, Object> userMap = new HashMap<>();

                            userMap.put("id", userId);
                            userMap.put("username", username.toLowerCase());
                            userMap.put("fullname", fullname);
                            userMap.put("bio", "");
                            userMap.put("imageUrl", "https://firebasestorage.googleapis.com/v0/b/instagramcloneapp-839b2.appspot.com/o/avatar.jpeg?alt=media&token=ee90e77c-ab48-4280-8e53-3501abfe6770");

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
                            Toast.makeText(RegisterActivity.this, "회원가입에 실패하셨습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
