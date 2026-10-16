package com.vetconnect.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DoctorDashboardActivity extends AppCompatActivity {

    private TextView tvDoctorWelcome;
    private TextView tvPendingRequests;
    private TextView tvActiveCases;
    private TextView tvCompletedCases;
    private TextView tvTreatedAnimals;

    private LinearLayout cardEmergencyRequests;
    private LinearLayout cardManageCases;
    private LinearLayout cardTreatmentRecords;
    private LinearLayout cardDoctorProfile;

    private Button btnDoctorLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_dashboard);

        // Check doctor login
        FirebaseUser user =
                FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            openLogin();
            return;
        }

        // Find dashboard views
        tvDoctorWelcome = findViewById(R.id.tvDoctorWelcome);
        tvPendingRequests = findViewById(R.id.tvPendingRequests);
        tvActiveCases = findViewById(R.id.tvActiveCases);
        tvCompletedCases = findViewById(R.id.tvCompletedCases);
        tvTreatedAnimals = findViewById(R.id.tvTreatedAnimals);

        cardEmergencyRequests =
                findViewById(R.id.cardEmergencyRequests);

        cardManageCases =
                findViewById(R.id.cardManageCases);

        cardTreatmentRecords =
                findViewById(R.id.cardTreatmentRecords);

        cardDoctorProfile =
                findViewById(R.id.cardDoctorProfile);

        btnDoctorLogout =
                findViewById(R.id.btnDoctorLogout);

        // Welcome message
        String email = user.getEmail();

        if (email != null && !email.trim().isEmpty()) {
            tvDoctorWelcome.setText("Welcome, " + email);
        } else {
            tvDoctorWelcome.setText("Welcome, Doctor!");
        }

        // Placeholder statistics.
        // These will be loaded from Firestore later.
        tvPendingRequests.setText("--");
        tvActiveCases.setText("--");
        tvCompletedCases.setText("--");
        tvTreatedAnimals.setText("--");

        // Emergency requests
        cardEmergencyRequests.setOnClickListener(v -> {
            openFeature(
                    EmergencyRequestsActivity.class,
                    "Emergency Requests"
            );
        });

        // Manage cases
        cardManageCases.setOnClickListener(v -> {
            openFeature(
                    ManageCasesActivity.class,
                    "Manage Cases"
            );
        });

        // Treatment records
        cardTreatmentRecords.setOnClickListener(v -> {
            openFeature(
                    TreatmentRecordsActivity.class,
                    "Treatment Records"
            );
        });

        // Doctor profile
        cardDoctorProfile.setOnClickListener(v -> {
            openFeature(
                    DoctorProfileActivity.class,
                    "Doctor Profile"
            );
        });

        // Logout
        btnDoctorLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            openLogin();
        });
    }

    private void openFeature(
            Class<?> activityClass,
            String featureName) {

        try {
            Intent intent = new Intent(
                    DoctorDashboardActivity.this,
                    activityClass
            );

            startActivity(intent);

        } catch (Exception e) {
            Toast.makeText(
                    DoctorDashboardActivity.this,
                    featureName + " could not be opened.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void openLogin() {
        Intent intent = new Intent(
                DoctorDashboardActivity.this,
                DoctorLoginActivity.class
        );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }
}
