
package com.vetconnect.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DoctorProfileActivity extends AppCompatActivity {

    private TextView tvDoctorEmail;
    private TextView tvDoctorId;
    private TextView tvDoctorInitial;
    private TextView btnProfileBack;
    private Button btnProfileLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);

        tvDoctorEmail = findViewById(R.id.tvProfileDoctorEmail);
        tvDoctorId = findViewById(R.id.tvProfileDoctorId);
        tvDoctorInitial = findViewById(R.id.tvProfileDoctorInitial);
        btnProfileBack = findViewById(R.id.btnProfileBack);
        btnProfileLogout = findViewById(R.id.btnProfileLogout);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            openDoctorLogin();
            return;
        }

        String email = user.getEmail();

        if (email != null && !email.trim().isEmpty()) {
            tvDoctorEmail.setText(email);
            tvDoctorInitial.setText(
                    email.substring(0, 1).toUpperCase()
            );
        } else {
            tvDoctorEmail.setText("Email not available");
            tvDoctorInitial.setText("D");
        }

        tvDoctorId.setText(user.getUid());

        btnProfileBack.setOnClickListener(v -> finish());

        btnProfileLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();

            Toast.makeText(
                    DoctorProfileActivity.this,
                    "Logged out successfully",
                    Toast.LENGTH_SHORT
            ).show();

            openDoctorLogin();
        });
    }

    private void openDoctorLogin() {
        Intent intent = new Intent(
                DoctorProfileActivity.this,
                DoctorLoginActivity.class
        );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }
}
