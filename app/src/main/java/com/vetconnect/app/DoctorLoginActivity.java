package com.vetconnect.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class DoctorLoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister, tvForgotPassword;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_login);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etEmail = findViewById(R.id.etDoctorLoginEmail);
        etPassword = findViewById(R.id.etDoctorLoginPassword);
        btnLogin = findViewById(R.id.btnDoctorLogin);
        tvRegister = findViewById(R.id.tvDoctorRegister);
        tvForgotPassword = findViewById(R.id.tvDoctorForgotPassword);

        btnLogin.setOnClickListener(v -> loginDoctor());

        tvRegister.setOnClickListener(v ->
                startActivity(new Intent(
                        DoctorLoginActivity.this,
                        DoctorRegisterActivity.class
                ))
        );

        tvForgotPassword.setOnClickListener(v -> resetPassword());
    }

    private void loginDoctor() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (TextUtils.isEmpty(email)
                || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email address");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Enter your password");
            etPassword.requestFocus();
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("Logging in...");

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (!task.isSuccessful()) {
                        btnLogin.setEnabled(true);
                        btnLogin.setText("Login");

                        Toast.makeText(this,
                                "Login failed. Check your email and password.",
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (auth.getCurrentUser() == null) {
                        showLoginError("Unable to identify your account");
                        return;
                    }

                    String uid = auth.getCurrentUser().getUid();

                    db.collection("doctors")
                            .document(uid)
                            .get()
                            .addOnSuccessListener(document -> {
                                if (document.exists()
                                        && "doctor".equals(
                                        document.getString("role"))) {

                                    Intent intent = new Intent(
                                            DoctorLoginActivity.this,
                                            DoctorDashboardActivity.class
                                    );
                                    intent.setFlags(
                                            Intent.FLAG_ACTIVITY_NEW_TASK
                                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    );
                                    startActivity(intent);
                                    finish();

                                } else {
                                    auth.signOut();
                                    showLoginError(
                                            "No doctor profile found for this account. "
                                                    + "Please register as a doctor."
                                    );
                                }
                            })
                            .addOnFailureListener(e -> {
                                auth.signOut();
                                showLoginError(
                                        "Could not verify doctor profile. "
                                                + "Check your internet connection."
                                );
                            });
                });
    }

    private void resetPassword() {
        String email = etEmail.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter your registered email first");
            etEmail.requestFocus();
            return;
        }

        tvForgotPassword.setEnabled(false);

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    tvForgotPassword.setEnabled(true);

                    if (task.isSuccessful()) {
                        Toast.makeText(this,
                                "If the account exists, a password reset email has been sent.",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this,
                                "Could not send reset email. Please try again.",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void showLoginError(String message) {
        btnLogin.setEnabled(true);
        btnLogin.setText("Login");
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
