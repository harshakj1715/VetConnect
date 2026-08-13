package com.vetconnect.app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.TextView;
import android.graphics.Color;
import androidx.core.view.WindowCompat;

import com.google.firebase.auth.FirebaseAuth;

public class FarmerRegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPhone, etPassword, etConfirmPassword;
    private Button btnRegister;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make background extend behind the status bar
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_farmer_register);

        mAuth = FirebaseAuth.getInstance();

        // Initialize Views
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);

        TextView txtLogin = findViewById(R.id.txtLogin);

        txtLogin.setOnClickListener(v -> finish());

        // Register Button Click
        btnRegister.setOnClickListener(v -> {

            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (name.isEmpty()) {
                etName.setError("Enter Name");
                return;
            }

            if (email.isEmpty()) {
                etEmail.setError("Enter Email");
                return;
            }

            if (phone.isEmpty()) {
                etPhone.setError("Enter Phone Number");
                return;
            }

            if (password.isEmpty()) {
                etPassword.setError("Enter Password");
                return;
            }

            if (password.length() < 6) {
                etPassword.setError("Password must be at least 6 characters");
                return;
            }

            if (!password.equals(confirmPassword)) {
                etConfirmPassword.setError("Passwords do not match");
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(
                                    FarmerRegisterActivity.this,
                                    "Account Created Successfully!",
                                    Toast.LENGTH_SHORT
                            ).show();
                            finish();
                        } else {
                            Toast.makeText(
                                    FarmerRegisterActivity.this,
                                    task.getException().getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    });
        });

    }
}