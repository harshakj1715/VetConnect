package com.vetconnect.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import android.widget.ImageView;

public class FarmerDashboardActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private TextView txtAnimalCount;
    private TextView txtGreeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_farmer_dashboard);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Views

        txtAnimalCount = findViewById(R.id.txtAnimalCount);

        MaterialCardView btnRegisterAnimal = findViewById(R.id.cardRegisterAnimal);
        MaterialCardView btnMyAnimals = findViewById(R.id.cardMyAnimals);
        MaterialCardView btnEmergency = findViewById(R.id.cardEmergency);
        MaterialCardView btnLogout = findViewById(R.id.cardLogout);
        ImageView btnNotification = findViewById(R.id.btnNotification);
// notifications
        btnNotification.setOnClickListener(v -> {

            Intent intent = new Intent(
                    FarmerDashboardActivity.this,
                    NotificationsActivity.class);

            startActivity(intent);

        });

        // Greeting based on current time
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);


        // Register Animal
        btnRegisterAnimal.setOnClickListener(v -> {
            startActivity(new Intent(
                    FarmerDashboardActivity.this,
                    RegisterAnimalActivity.class));
        });

        // My Animals
        btnMyAnimals.setOnClickListener(v -> {
            startActivity(new Intent(
                    FarmerDashboardActivity.this,
                    MyAnimalsActivity.class));
        });

        // Emergency Vet
        btnEmergency.setOnClickListener(v -> {
            startActivity(new Intent(
                    FarmerDashboardActivity.this,
                    EmergencyVetActivity.class));
        });
// Logout
        btnLogout.setOnClickListener(v -> {

            new androidx.appcompat.app.AlertDialog.Builder(FarmerDashboardActivity.this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setIcon(R.drawable.ic_logout) // Optional (remove this line if you don't have the icon)
                    .setCancelable(false)

                    .setPositiveButton("Yes", (dialog, which) -> {

                        FirebaseAuth.getInstance().signOut();

                        Intent intent = new Intent(FarmerDashboardActivity.this,
                                FarmerLoginActivity.class);

                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        startActivity(intent);
                        finish();
                    })

                    .setNegativeButton("No", (dialog, which) -> {
                        dialog.dismiss();
                    })

                    .show();
        });

        // Load registered animal count
        loadAnimalCount();
    }

    private void loadAnimalCount() {

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {

            db.collection("animals")
                    .whereEqualTo("farmerEmail", user.getEmail())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {

                        int count = queryDocumentSnapshots.size();

                        txtAnimalCount.setText(count + " Animals Registred");

                    })
                    .addOnFailureListener(e ->
                            txtAnimalCount.setText("0 Animals Registred"));

        } else {

            txtAnimalCount.setText("0 Animals Registred");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAnimalCount();
    }
}