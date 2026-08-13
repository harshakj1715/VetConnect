package com.vetconnect.app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class EmergencyVetActivity extends AppCompatActivity {

    Button btnCurrentLocation, btnNearbyVets, btnEmergencyCall, btnShareLocation;
    TextView tvLocation;

    FusedLocationProviderClient fusedLocationClient;

    ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (isGranted) {
                            getCurrentLocation();
                        } else {
                            Toast.makeText(EmergencyVetActivity.this,
                                    "Location permission denied",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_vet);

        btnCurrentLocation = findViewById(R.id.btnCurrentLocation);
        btnNearbyVets = findViewById(R.id.btnNearbyVets);
        btnEmergencyCall = findViewById(R.id.btnEmergencyCall);
        btnShareLocation = findViewById(R.id.btnShareLocation);
        tvLocation = findViewById(R.id.tvLocation);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Current Location
        btnCurrentLocation.setOnClickListener(v -> {

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                getCurrentLocation();

            } else {

                requestPermissionLauncher.launch(
                        Manifest.permission.ACCESS_FINE_LOCATION);
            }

        });

        // Nearby Veterinary Hospitals
        btnNearbyVets.setOnClickListener(v -> {

            Uri uri = Uri.parse("https://www.google.com/maps/search/veterinary+hospital");

            Intent intent = new Intent(Intent.ACTION_VIEW, uri);

            startActivity(intent);

        });

        // Emergency Call (Coming Next)
        btnEmergencyCall.setOnClickListener(v -> {

            String emergencyNumber = "1962";   // Change this to any number you want

            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + emergencyNumber));

            startActivity(intent);

        });

        // Share Location (Coming Next)
        btnShareLocation.setOnClickListener(v -> {

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissionLauncher.launch(
                        Manifest.permission.ACCESS_FINE_LOCATION);

                return;
            }

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {

                        if (location != null) {

                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            String message =
                                    "🚨 Emergency! I need veterinary assistance.\n\n"
                                            + "My current location:\n"
                                            + "https://www.google.com/maps?q="
                                            + latitude + "," + longitude;

                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_TEXT, message);

                            startActivity(Intent.createChooser(
                                    shareIntent,
                                    "Share Location"));

                        } else {

                            Toast.makeText(EmergencyVetActivity.this,
                                    "Unable to get current location.",
                                    Toast.LENGTH_SHORT).show();

                        }

                    });

        });
    }

    private void getCurrentLocation() {

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {

                    if (location != null) {

                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        tvLocation.setText(
                                "Latitude: " + latitude +
                                        "\nLongitude: " + longitude);

                    } else {

                        Toast.makeText(EmergencyVetActivity.this,
                                "Unable to get location. Turn ON GPS.",
                                Toast.LENGTH_LONG).show();
                    }

                });
    }
}