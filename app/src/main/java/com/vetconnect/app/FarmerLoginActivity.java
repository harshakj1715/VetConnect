package com.vetconnect.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.google.firebase.auth.FirebaseAuth;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
public class FarmerLoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnRegister;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_login);

        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        TextView txtForgotPassword = findViewById(R.id.txtForgotPassword);
        txtForgotPassword.setOnClickListener(v -> {

            String email = etEmail.getText().toString().trim();

            if (email.isEmpty()) {
                etEmail.setError("Enter your registered email");
                etEmail.requestFocus();
                return;
            }

            FirebaseAuth.getInstance()
                    .sendPasswordResetEmail(email)
                    .addOnSuccessListener(unused ->
                            Toast.makeText(
                                    FarmerLoginActivity.this,
                                    "Password reset link has been sent to your email.",
                                    Toast.LENGTH_LONG).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(
                                    FarmerLoginActivity.this,
                                    e.getMessage(),
                                    Toast.LENGTH_LONG).show());

        });


        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(FarmerLoginActivity.this, FarmerRegisterActivity.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(v -> {

            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty()) {
                etEmail.setError("Enter Email");
                etEmail.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                etPassword.setError("Enter Password");
                etPassword.requestFocus();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {

                            Toast.makeText(
                                    FarmerLoginActivity.this,
                                    "Login Successful!",
                                    Toast.LENGTH_SHORT
                            ).show();

                            Intent intent = new Intent(
                                    FarmerLoginActivity.this,
                                    FarmerDashboardActivity.class
                            );
                            startActivity(intent);
                            finish();

                        } else {

                            Toast.makeText(
                                    FarmerLoginActivity.this,
                                    task.getException().getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();

                        }
                    });

        });



    }
}