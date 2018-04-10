package com.freelance.android.photoblog.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.freelance.android.photoblog.R;
import com.freelance.android.photoblog.fragments.AccountFragment;
import com.freelance.android.photoblog.fragments.HomeFragment;
import com.freelance.android.photoblog.fragments.NotificationFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private final static String LOG_TAG = MainActivity.class.getName();
    private Toolbar tb;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    private String currentUserId;
    private FloatingActionButton fab;
    private BottomNavigationView bnvMain;

    private HomeFragment homeFragment;
    private NotificationFragment notificationFragment;
    private AccountFragment accountFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "Test : onCreate() is called...");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        tb = (Toolbar) this.findViewById(R.id.tbMain);
        setSupportActionBar(tb);
        getSupportActionBar().setTitle("Photo Blog");

        if (mAuth.getCurrentUser() != null) {

            bnvMain = this.findViewById(R.id.bnvMain);

            // FRAGMENTS
            homeFragment = new HomeFragment();
            notificationFragment = new NotificationFragment();
            accountFragment = new AccountFragment();

            replaceFragment(homeFragment);

            bnvMain.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Log.i(LOG_TAG, "Test : onClick() in onNavigationItemReselected is called...");

                    switch (item.getItemId()) {

                        case R.id.action_home:
                            replaceFragment(homeFragment);
                            return true;

                        case R.id.action_notification:
                            replaceFragment(notificationFragment);
                            return true;

                        case R.id.action_account:
                            replaceFragment(accountFragment);
                            return true;

                        default:
                            return false;
                    }
                }
            });

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
    }

    @Override
    protected void onStart() {
        Log.i(LOG_TAG, "Test : onStart() is called...");
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            sendToLogin();
        } else {

            currentUserId = mAuth.getCurrentUser().getUid();
            mFirestore.collection("Users").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    Log.i(LOG_TAG, "Test : onComplete() in onStart method is called...");

                    if (task.isSuccessful()) {
                        if (!task.getResult().exists()) {

                            Intent setUp = new Intent(MainActivity.this, SetupActivity.class);
                            startActivity(setUp);
                            finish();
                        }
                    } else {
                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(MainActivity.this, "Error :" + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
            });
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

            case R.id.action_logOut:
                logOut();
                return true;

            case R.id.action_accountSettings:
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

        Intent login = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(login);
        finish();
    }

    private void replaceFragment(Fragment fragment) {
        Log.i(LOG_TAG, "Test : replaceFragment() is called...");

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.flMainContainer, fragment);
        fragmentTransaction.commit();
    }
}
