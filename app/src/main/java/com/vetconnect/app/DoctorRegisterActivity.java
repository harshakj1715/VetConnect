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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DoctorRegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPhone, etQualification;
    private EditText etSpecialization, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvGoToLogin;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_register);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etName = findViewById(R.id.etDoctorName);
        etEmail = findViewById(R.id.etDoctorEmail);
        etPhone = findViewById(R.id.etDoctorPhone);
        etQualification = findViewById(R.id.etDoctorQualification);
        etSpecialization = findViewById(R.id.etDoctorSpecialization);
        etPassword = findViewById(R.id.etDoctorPassword);
        etConfirmPassword = findViewById(R.id.etDoctorConfirmPassword);

        btnRegister = findViewById(R.id.btnDoctorRegister);
        tvGoToLogin = findViewById(R.id.tvDoctorGoToLogin);

        btnRegister.setOnClickListener(v -> registerDoctor());

        tvGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(
                    DoctorRegisterActivity.this,
                    DoctorLoginActivity.class
            ));
            finish();
        });
    }

    private void registerDoctor() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim()
                .toLowerCase(Locale.ROOT);
        String phone = etPhone.getText().toString().trim();
        String qualification =
                etQualification.getText().toString().trim();
        String specialization =
                etSpecialization.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmPassword =
                etConfirmPassword.getText().toString();

        if (TextUtils.isEmpty(name)
                || TextUtils.isEmpty(email)
                || TextUtils.isEmpty(phone)
                || TextUtils.isEmpty(qualification)
                || TextUtils.isEmpty(specialization)
                || TextUtils.isEmpty(password)
                || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Please fill in all fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email");
            etEmail.requestFocus();
            return;
        }

        if (!phone.matches("[0-9]{10}")) {
            etPhone.setError("Enter a valid 10-digit phone number");
            etPhone.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must have at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }

        btnRegister.setEnabled(false);
        btnRegister.setText("Creating account...");

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (!task.isSuccessful()) {
                        btnRegister.setEnabled(true);
                        btnRegister.setText("Create Doctor Account");

                        String message = task.getException() != null
                                ? task.getException().getMessage()
                                : "Registration failed";

                        Toast.makeText(this, message,
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (auth.getCurrentUser() == null) {
                        btnRegister.setEnabled(true);
                        btnRegister.setText("Create Doctor Account");
                        Toast.makeText(this,
                                "Account created. Please log in.",
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    String uid = auth.getCurrentUser().getUid();

                    Map<String, Object> doctor = new HashMap<>();
                    doctor.put("uid", uid);
                    doctor.put("name", name);
                    doctor.put("email", email);
                    doctor.put("phone", phone);
                    doctor.put("qualification", qualification);
                    doctor.put("specialization", specialization);
                    doctor.put("role", "doctor");
                    doctor.put("createdAt",
                            System.currentTimeMillis());

                    db.collection("doctors")
                            .document(uid)
                            .set(doctor)
                            .addOnSuccessListener(unused -> {
                                auth.signOut();

                                Toast.makeText(this,
                                        "Registration successful! Please log in.",
                                        Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(
                                        DoctorRegisterActivity.this,
                                        DoctorLoginActivity.class
                                );
                                intent.setFlags(
                                        Intent.FLAG_ACTIVITY_NEW_TASK
                                                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                );
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                // Avoid leaving an authenticated account
                                // without a corresponding doctor profile.
                                auth.getCurrentUser().delete()
                                        .addOnCompleteListener(deleteTask -> {
                                            auth.signOut();
                                            btnRegister.setEnabled(true);
                                            btnRegister.setText(
                                                    "Create Doctor Account");

                                            Toast.makeText(this,
                                                    "Could not save doctor profile: "
                                                            + e.getMessage()
                                                            + ". Please try again.",
                                                    Toast.LENGTH_LONG).show();
                                        });
                            });
                });
    }
}
