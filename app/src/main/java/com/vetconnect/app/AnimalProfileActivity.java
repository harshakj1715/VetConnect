package com.vetconnect.app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class AnimalProfileActivity extends AppCompatActivity {

    TextView txtAnimalName, txtAnimalType, txtBreed, txtAge, txtGender;

    Button btnHealthRecords, btnEditAnimal, btnDeleteAnimal, btnEmergencyVet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_profile);

        txtAnimalName = findViewById(R.id.txtAnimalName);
        txtAnimalType = findViewById(R.id.txtAnimalType);
        txtBreed = findViewById(R.id.txtBreed);
        txtAge = findViewById(R.id.txtAge);
        txtGender = findViewById(R.id.txtGender);

        btnHealthRecords = findViewById(R.id.btnHealthRecords);
        btnEditAnimal = findViewById(R.id.btnEditAnimal);
        btnDeleteAnimal = findViewById(R.id.btnDeleteAnimal);


        // Get data from MyAnimalsActivity
        String animalName = getIntent().getStringExtra("animalName");
        String animalType = getIntent().getStringExtra("animalType");
        String breed = getIntent().getStringExtra("breed");
        String age = getIntent().getStringExtra("age");
        String gender = getIntent().getStringExtra("gender");

        txtAnimalName.setText(animalName);
        txtAnimalType.setText("Type : " + animalType);
        txtBreed.setText("Breed : " + breed);
        txtAge.setText("Age : " + age);
        txtGender.setText("Gender : " + gender);

        btnHealthRecords.setOnClickListener(v -> {

            Intent intent = new Intent(AnimalProfileActivity.this,
                    MedicalHistoryActivity.class);

            intent.putExtra("animalId",
                    getIntent().getStringExtra("documentId"));

            startActivity(intent);

        });
    }
}