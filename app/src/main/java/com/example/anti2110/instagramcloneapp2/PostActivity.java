package com.example.anti2110.instagramcloneapp2;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class PostActivity extends AppCompatActivity {

    private static final String TAG = "PostActivity";

    private Uri mImageUri;
    private String mMyUrl = "";
    private StorageTask mUploadTask;
    private StorageReference mStorageReference;

    private ImageView mClose, mImageAdded;
    private TextView mPost;
    private EditText mDescription;

    private Bitmap compressedImageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        Log.d(TAG, "onCreate: started.");

        mClose = findViewById(R.id.close);
        mImageAdded = findViewById(R.id.image_added);
        mPost = findViewById(R.id.post);
        mDescription = findViewById(R.id.description);

        mStorageReference = FirebaseStorage.getInstance().getReference(getString(R.string.storage_posts));

        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PostActivity.this, MainActivity.class));
                finish();
            }
        });

        mPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });

        CropImage.activity()
                .setAspectRatio(1, 1)
                .start(PostActivity.this);
    }

    private String getFileExtension(Uri uri) {
        String extension;

        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(getContentResolver().getType(uri));
        } else {
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
        }

        return extension;
    }

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.dialog_upload_post_message));
        pd.show();

        if (mImageUri != null) {
            final StorageReference fileReference = mStorageReference.child(System.currentTimeMillis() + "."
                    + getFileExtension(mImageUri));

            mUploadTask = fileReference.putFile(mImageUri);

            mUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw  task.getException();
                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: getDownloadUrl: " + task.getResult().toString());

                        saveCompressedImageFile();

                        Uri downloadUri = task.getResult();
                        mMyUrl = downloadUri.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.dbname_posts));

                        String postId = reference.push().getKey();

                        Map<String, Object> postMap = new HashMap<>();
                        postMap.put(getString(R.string.field_posts_post_id), postId);
                        postMap.put(getString(R.string.field_posts_post_image), mMyUrl);
                        postMap.put(getString(R.string.field_posts_description), mDescription.getText().toString());
                        postMap.put(getString(R.string.field_posts_publisher), FirebaseAuth.getInstance().getCurrentUser().getUid());

                        reference.child(postId).setValue(postMap);

                        pd.dismiss();

                        startActivity(new Intent(PostActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(PostActivity.this, R.string.toast_upload_failed, Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, R.string.toast_image_select, Toast.LENGTH_SHORT).show();
        }
    }

    private void saveCompressedImageFile() {
        File newImageFile = new File(mImageUri.getPath());

        try {
            compressedImageFile = new Compressor(PostActivity.this)
                    .setMaxWidth(200)
                    .setMaxHeight(200)
                    .setQuality(10)
                    .compressToBitmap(newImageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] thumbData = baos.toByteArray();

        final StorageReference reference =  FirebaseStorage.getInstance().getReference("App2_posts_compressed")
                .child(System.currentTimeMillis() + ".jpg");

        final UploadTask uploadTask = reference.putBytes(thumbData);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(PostActivity.this, "압축 저장 성공!!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            mImageUri = result.getUri();

            mImageAdded.setImageURI(mImageUri);

        } else {
            Toast.makeText(this, R.string.toast_upload_failed, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PostActivity.this, MainActivity.class));
            finish();
        }

    }
}
