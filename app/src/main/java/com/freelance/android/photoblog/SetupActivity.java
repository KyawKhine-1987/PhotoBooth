package com.freelance.android.photoblog;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private final static String LOG_TAG = SetupActivity.class.getName();

    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    private Toolbar tb;
    private CircleImageView mCIVSetup;

    private Uri mImageUri = null;
    private String userId;
    private EditText metSetupName;
    private Button mbtnSetup;
    private ProgressBar setupProgress;

    private boolean isChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "Test : onCreate() is called...");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        mFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        tb = (Toolbar) this.findViewById(R.id.tbSetup);
        setSupportActionBar(tb);
        getSupportActionBar().setTitle("Account Setup");

        metSetupName = findViewById(R.id.etPersonName);
        mbtnSetup = findViewById(R.id.btnSetup);
        mCIVSetup = this.findViewById(R.id.civSetup);
        setupProgress = this.findViewById(R.id.pbSetup);

        setupProgress.setVisibility(View.VISIBLE);
        mbtnSetup.setEnabled(false);
        mFirestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Log.i(LOG_TAG, "Test : onComplete() in FirebaseFirestore is called...");

                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        Toast.makeText(SetupActivity.this, "Data Exists.", Toast.LENGTH_SHORT).show();

                        String name = task.getResult().getString("name");
                        String image = task.getResult().getString("image");

                        mImageUri = Uri.parse(image);
                        metSetupName.setText(name);

                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.profile_picture);
                        Glide.with(SetupActivity.this).setDefaultRequestOptions(placeholderRequest).load(image).into(mCIVSetup);
                    }
                } else {
                    String errorMessage = task.getException().getMessage();
                    Toast.makeText(SetupActivity.this, "(FIRESTORE Retrieve Error)" + errorMessage, Toast.LENGTH_SHORT).show();
                }

                setupProgress.setVisibility(View.INVISIBLE);
                mbtnSetup.setEnabled(true);
            }
        });

        mbtnSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "Test : onClick() in btnSetup is called...");

                final String username = metSetupName.getText().toString();

                if (!TextUtils.isEmpty(username) && mImageUri != null) {
                    setupProgress.setVisibility(View.VISIBLE);

                    if (isChanged) {

                        userId = mAuth.getCurrentUser().getUid();

                        StorageReference imagePath = storageReference.child("profile_images").child(userId + ".jpg");
                        imagePath.putFile(mImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                Log.i(LOG_TAG, "Test : onComplete() in StorageReference is called...");

                                if (task.isSuccessful()) {

                                    storeFirestore(task, username);

                                } else {
                                    String errorMessage = task.getException().getMessage();
                                    Toast.makeText(SetupActivity.this, "(IMAGE Error)" + errorMessage, Toast.LENGTH_SHORT).show();
                                    setupProgress.setVisibility(View.INVISIBLE);
                                }


                            }
                        });
                    } else {
                        storeFirestore(null, username);
                    }
                }
            }
        });

        mCIVSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "Test : onClick() in CircleImageView is called...");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(SetupActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        Toast.makeText(SetupActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(SetupActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else {

                        //Toast.makeText(SetupActivity.this, "You already have permission.", Toast.LENGTH_SHORT).show();
                        BringImagePicker();
                    }
                } else {

                    BringImagePicker();
                }
            }
        });
    }

    private void storeFirestore(@NonNull Task<UploadTask.TaskSnapshot> task, String username) {
        Log.i(LOG_TAG, "Test : storeFirestore() is called...");

        Uri downloadUri;

        if (task != null) {
            downloadUri = task.getResult().getDownloadUrl();
        } else {
            downloadUri = mImageUri;
        }
        //Toast.makeText(SetupActivity.this, "The image is uploaded.", Toast.LENGTH_SHORT).show();

        Map<String, String> userMap = new HashMap<>();
        userMap.put("name", username);
        userMap.put("image", downloadUri.toString());

        mFirestore.collection("Users").document(userId).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.i(LOG_TAG, "Test : onComplete() in Firestore is called...");

                if (task.isSuccessful()) {

                    Toast.makeText(SetupActivity.this, "The user settings are updated.", Toast.LENGTH_SHORT).show();
                    Intent main = new Intent(SetupActivity.this, MainActivity.class);
                    startActivity(main);
                    finish();

                } else {
                    String errorMessage = task.getException().getMessage();
                    Toast.makeText(SetupActivity.this, "(FIRESTORE Error)" + errorMessage, Toast.LENGTH_SHORT).show();
                }
                setupProgress.setVisibility(View.INVISIBLE);

            }
        });
    }

    private void BringImagePicker() {
        Log.i(LOG_TAG, "Test : BringImagePicker() is called...");

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(SetupActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(LOG_TAG, "Test : onActivityResult() is called...");
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                this.mImageUri = result.getUri();
                this.mCIVSetup.setImageURI(mImageUri);
                this.isChanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    /*if you want to do some permissions you can do here.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }*/

}
