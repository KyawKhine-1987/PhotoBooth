package com.freelance.android.photoblog.activities;

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

import com.freelance.android.photoblog.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private final static String LOG_TAG = LoginActivity.class.getName();

    private EditText loginEmailName, loginPassword;
    private Button login, loginReg;
    private FirebaseAuth mAuth;
    private ProgressBar loginProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "Test : onCreate() is called...");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        loginEmailName = (EditText) this.findViewById(R.id.etLoginEmailName);
        loginPassword = (EditText) this.findViewById(R.id.etLoginPassword);
        login = (Button) this.findViewById(R.id.btnLogin);
        loginReg = (Button) this.findViewById(R.id.btnLoginReg);
        loginProgress = (ProgressBar) this.findViewById(R.id.pbLogin);

        loginReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "Test : onClick() in btnLoginRegister is called...");

                Intent reg = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(reg);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "Test : onClick() in btnLogin is called...");

                String email = loginEmailName.getText().toString();
                String pwd = loginPassword.getText().toString();

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pwd)) {
                    loginProgress.setVisibility(View.VISIBLE);

                    mAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.i(LOG_TAG, "Test : onComplete() is called...");

                            if (task.isSuccessful()) {
                                sendToMain();
                                //TODO LIST for success login toast message.
                                Toast.makeText(LoginActivity.this, "Log In is successful.", Toast.LENGTH_SHORT).show();

                            } else {
                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this, "Error :" + errorMessage, Toast.LENGTH_SHORT).show();
                            }

                            loginProgress.setVisibility(View.INVISIBLE);
                        }
                    });
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

        //Intent i = new Intent(LoginActivity.this, LoginActivity.class);
        Intent main = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(main);
        finish();
    }
}
