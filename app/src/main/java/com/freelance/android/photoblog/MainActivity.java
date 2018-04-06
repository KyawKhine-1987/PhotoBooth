package com.freelance.android.photoblog;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private final static String LOG_TAG = MainActivity.class.getName();
    private Toolbar tb;
    private FirebaseAuth mAuth;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "Test : onCreate() is called...");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        tb = (Toolbar) this.findViewById(R.id.tbMain);
        setSupportActionBar(tb);
        getSupportActionBar().setTitle("Photo Blog");

        fab = findViewById(R.id.fabAddPost);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "Test : onClick() in FloatingActionButton is called...");

                Intent newPost = new Intent(MainActivity.this, NewPostActivity.class);
                startActivity(newPost);
            }
        });
    }

    @Override
    protected void onStart() {
        Log.i(LOG_TAG, "Test : onStart() is called...");
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            sendToLogin();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(LOG_TAG, "Test : onCreateOptionsMenu() is called...");

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(LOG_TAG, "Test : onOptionsItemSelected() is called...");

        switch (item.getItemId()) {

            case R.id.mnActionLogOut:
                logOut();
                return true;

            case R.id.mnActionAccountSettings:
                accountSettings();
                return true;

            default:
                return false;
        }
    }

    private void accountSettings() {
        Log.i(LOG_TAG, "Test : accountSettings() is called...");

        Intent settings = new Intent(MainActivity.this, SetupActivity.class);
        startActivity(settings);
    }

    private void logOut() {
        Log.i(LOG_TAG, "Test : logOut() is called...");

        mAuth.signOut();
        sendToLogin();
    }


    private void sendToLogin() {
        Log.i(LOG_TAG, "Test : sendToLogin() is called...");

        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }
}
