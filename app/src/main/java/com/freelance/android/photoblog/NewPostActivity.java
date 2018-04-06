package com.freelance.android.photoblog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

public class NewPostActivity extends AppCompatActivity {

    private final static String LOG_TAG = NewPostActivity.class.getName();
    private Toolbar tb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "Test : onCreate() is called...");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        tb = (Toolbar) this.findViewById(R.id.tbNewPost);
        setSupportActionBar(tb);
        getSupportActionBar().setTitle("Add New Post");
    }
}
