package com.vetconnect.app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditAnimalActivity extends AppCompatActivity {

    EditText etAnimalName, etBreed, etAge;
    Button btnUpdateAnimal;

    FirebaseFirestore db;

    String documentId;
    String animalType;
    String gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_animal);

        db = FirebaseFirestore.getInstance();

        etAnimalName = findViewById(R.id.etAnimalName);
        etBreed = findViewById(R.id.etBreed);
        etAge = findViewById(R.id.etAge);
        btnUpdateAnimal = findViewById(R.id.btnUpdateAnimal);

        documentId = getIntent().getStringExtra("documentId");

        animalType = getIntent().getStringExtra("animalType");
        gender = getIntent().getStringExtra("gender");

        etAnimalName.setText(getIntent().getStringExtra("animalName"));
        etBreed.setText(getIntent().getStringExtra("breed"));
        etAge.setText(getIntent().getStringExtra("age"));

        btnUpdateAnimal.setOnClickListener(v -> {

            Map<String, Object> updates = new HashMap<>();

            updates.put("animalName", etAnimalName.getText().toString().trim());
            updates.put("breed", etBreed.getText().toString().trim());
            updates.put("age", etAge.getText().toString().trim());
            updates.put("animalType", animalType);
            updates.put("gender", gender);

            db.collection("animals")
                    .document(documentId)
                    .update(updates)
                    .addOnSuccessListener(unused -> {

                        Toast.makeText(EditAnimalActivity.this,
                                "Animal Updated Successfully",
                                Toast.LENGTH_SHORT).show();

                        finish();

                    })
                    .addOnFailureListener(e ->

                            Toast.makeText(EditAnimalActivity.this,
                                    e.getMessage(),
                                    Toast.LENGTH_LONG).show());

        });

    }
}