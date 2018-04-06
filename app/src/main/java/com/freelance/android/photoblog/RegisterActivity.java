package com.freelance.android.photoblog;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private final static String LOG_TAG = RegisterActivity.class.getName();

    private EditText regEmailName, regPassword, regConfirmPassword;
    private Button reg, regLogin;
    private FirebaseAuth mAuth;
    private ProgressBar regProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "Test : onCreate() is called...");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        regEmailName = (EditText) this.findViewById(R.id.etRegEmailName);
        regPassword = (EditText) this.findViewById(R.id.etRegPassword);
        regConfirmPassword = (EditText) this.findViewById(R.id.etRegConfirmPassword);
        reg = (Button) this.findViewById(R.id.btnReg);
        regLogin = (Button) this.findViewById(R.id.btnRegLogin);
        regProgress = (ProgressBar) this.findViewById(R.id.pbReg);

        regLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "Test : onClick() in btnRegLogin is called...");

                finish();
            }
        });

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "Test : onClick() in btnReg is called...");

                String email = regEmailName.getText().toString();
                String pwd = regPassword.getText().toString();
                String confirm_pwd = regConfirmPassword.getText().toString();

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pwd) & !TextUtils.isEmpty(confirm_pwd)) {

                    if (pwd.equals(confirm_pwd)) {

                        regProgress.setVisibility(View.VISIBLE);

                        mAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.i(LOG_TAG, "Test : onComplete() is called...");

                                if (task.isSuccessful()) {
                                    //sendToMain();
                                    Intent setup = new Intent(RegisterActivity.this, SetupActivity.class);
                                    startActivity(setup);
                                    finish();

                                } else {
                                    String errorMessage = task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this, "Error :" + errorMessage, Toast.LENGTH_SHORT).show();
                                }

                                regProgress.setVisibility(View.INVISIBLE);
                            }
                        });
                    } else {

                        Toast.makeText(RegisterActivity.this, "Your Password and Confirm Password doesn't match!", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }

    @Override
    protected void onStart() {
        Log.i(LOG_TAG, "Test : onStart() is called...");
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            sendToMain();
        }
    }

    private void sendToMain() {
        Log.i(LOG_TAG, "Test : sendToMain() is called...");

        Intent i = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
