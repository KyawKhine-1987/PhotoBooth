package com.freelance.android.photoblog.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.freelance.android.photoblog.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class NewPostActivity extends AppCompatActivity {

    private final static String LOG_TAG = NewPostActivity.class.getName();

    private Toolbar tb;

    private ImageView ivNewPost;
    private EditText etNewPostDesp;
    private Button btnNewPost;
    private ProgressBar newPostProgress;
    private Uri postImageUri = null;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String currentUserId;
    private Bitmap compressedImageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "Test : onCreate() is called...");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        currentUserId = firebaseAuth.getCurrentUser().getUid();

        tb = (Toolbar) this.findViewById(R.id.tbNewPost);
        setSupportActionBar(tb);
        getSupportActionBar().setTitle("Add New Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ivNewPost = findViewById(R.id.ivNewPost);
        etNewPostDesp = findViewById(R.id.etNewPostDesp);
        btnNewPost = findViewById(R.id.btnNewPost);
        newPostProgress = this.findViewById(R.id.pbNewPost);

        ivNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "Test : onClick() in ivNewPost is called...");

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(512, 512)
                        .setAspectRatio(1, 1)
                        .start(NewPostActivity.this);
            }
        });

        btnNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "Test : onClick() in btnNewPost is called...");

                final String desc = etNewPostDesp.getText().toString();

                if (!TextUtils.isEmpty(desc) && postImageUri != null) {

                    newPostProgress.setVisibility(View.VISIBLE);

                    final String randomName = UUID.randomUUID().toString();
                    //String randomName = FieldValue.serverTimestamp().toString();

                    StorageReference filePath = storageReference.child("post_images").child(randomName + ".jpg");
                    filePath.putFile(postImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            Log.i(LOG_TAG, "Test : onComplete() in StorageReference is called...");

                            final String downloadUri = task.getResult().getDownloadUrl().toString();

                            if (task.isSuccessful()) {

                                File newImageFile = new File(postImageUri.getPath());
                                try {
                                    compressedImageFile = new Compressor(NewPostActivity.this)
                                            .setMaxHeight(100)
                                            .setMaxWidth(100)
                                            .setQuality(2)
                                            .compressToBitmap(newImageFile);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                byte[] thumbData = baos.toByteArray();

                                UploadTask uploadTask = storageReference.child("post_images/thumbs").child(randomName + ".jpg").putBytes(thumbData);
                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Log.i(LOG_TAG, "Test : onSuccess() in UploadTask is called...");

                                        String downloadthumbUri = taskSnapshot.getDownloadUrl().toString();

                                        Map<String, Object> postMap = new HashMap<>();
                                        postMap.put("user_id", currentUserId);
                                        postMap.put("image_url", downloadUri);
                                        postMap.put("desc", desc);
                                        postMap.put("image_thumb", downloadthumbUri);
                                        postMap.put("timestamp", FieldValue.serverTimestamp());

                                        firebaseFirestore.collection("Posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                Log.i(LOG_TAG, "Test : onComplete() in FirebaseFirestore is called...");

                                                if (task.isSuccessful()) {

                                                    Toast.makeText(NewPostActivity.this, "Post was added.", Toast.LENGTH_SHORT).show();
                                                    Intent main = new Intent(NewPostActivity.this, MainActivity.class);
                                                    startActivity(main);
                                                    finish();
                                                } else {

                                                    //this is custom code from me.
                                                    String errorMessage = task.getException().getMessage();
                                                    Toast.makeText(NewPostActivity.this, "(FIRESTORE Error)" + errorMessage, Toast.LENGTH_SHORT).show();

                                                }

                                                newPostProgress.setVisibility(View.INVISIBLE);
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.i(LOG_TAG, "Test : onFailure() in UploadTask is called...");

                                        //Error Handling
                                    }
                                });
                            } else {
                                newPostProgress.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
            }
        });
    }

    /*
    private static final int MAX_LENGTH = 100;
    private static String random() {
        Log.i(LOG_TAG, "Test : random() is called...");

        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(MAX_LENGTH);
        char tempChar;

        for (int i = 0; i < randomLength; i++) {
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(LOG_TAG, "Test : onActivityResult() is called...");
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                postImageUri = result.getUri();
                ivNewPost.setImageURI(postImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


}
