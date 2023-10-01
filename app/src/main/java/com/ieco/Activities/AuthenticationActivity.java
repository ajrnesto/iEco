package com.ieco.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ieco.R;
import com.ieco.Utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AuthenticationActivity extends AppCompatActivity {

    FirebaseFirestore DB;
    FirebaseAuth AUTH;
    FirebaseUser USER;

    private void initializeFirebase() {
        DB = FirebaseFirestore.getInstance();
        AUTH = FirebaseAuth.getInstance();
        USER = AUTH.getCurrentUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        initializeFirebase();
        initializeViews();
        handleClickListeners();
    }
    
    // set up variables
    ConstraintLayout clLogo, clLogin;
    TextInputEditText etEmail, etPassword;
    MaterialButton btnLogin, btnBack;

    private void initializeViews() {
        // global
        clLogo = findViewById(R.id.clLogo);

        // authentication
        clLogin = findViewById(R.id.clLogin);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnBack = findViewById(R.id.btnBack);
    }

    private void handleClickListeners() {
        btnBack.setOnClickListener(view -> {
            startActivity(new Intent(AuthenticationActivity.this, MainActivity.class));
            finish();
        });

        btnLogin.setOnClickListener(view -> {
            Utils.hideKeyboard(this);

            btnLogin.setEnabled(false);
            String email = Objects.requireNonNull(etEmail.getText()).toString();
            String password = Objects.requireNonNull(etPassword.getText()).toString();

            if (TextUtils.isEmpty(email)) {
                Utils.simpleDialog(this, "Login Failed","Please enter your email.", "Okay");
                btnLogin.setEnabled(true);
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Utils.simpleDialog(this, "Login Failed","Please enter a password.", "Okay");
                btnLogin.setEnabled(true);
                return;
            }

            AUTH.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(AuthenticationActivity.this, "Signed in as "+email, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(AuthenticationActivity.this, AdminActivity.class));
                            finish();
                        }
                        else {
                            Utils.simpleDialog(this, "Login Failed", "Invalid email or password.", "Back");
                            btnLogin.setEnabled(true);
                        }
                    });
        });
    }
}