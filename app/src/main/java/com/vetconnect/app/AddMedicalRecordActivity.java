package com.vetconnect.app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddMedicalRecordActivity extends AppCompatActivity {

    EditText etDisease, etMedicine, etVaccination, etNotes, etDate;
    Button btnSaveRecord;

    FirebaseFirestore db;
    String animalId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_medical_record);

        etDisease = findViewById(R.id.etDisease);
        etMedicine = findViewById(R.id.etMedicine);
        etVaccination = findViewById(R.id.etVaccination);
        etNotes = findViewById(R.id.etNotes);
        etDate = findViewById(R.id.etDate);
        btnSaveRecord = findViewById(R.id.btnSaveRecord);

        db = FirebaseFirestore.getInstance();

        // Receive Animal ID
        animalId = getIntent().getStringExtra("animalId");

        Toast.makeText(this, "Animal ID: " + animalId, Toast.LENGTH_SHORT).show();

        btnSaveRecord.setOnClickListener(v -> {
            Toast.makeText(this, "Save button clicked", Toast.LENGTH_SHORT).show();

            String disease = etDisease.getText().toString().trim();
            String medicine = etMedicine.getText().toString().trim();
            String vaccination = etVaccination.getText().toString().trim();
            String notes = etNotes.getText().toString().trim();
            String date = etDate.getText().toString().trim();

            if (disease.isEmpty() || medicine.isEmpty() || date.isEmpty()) {
                Toast.makeText(AddMedicalRecordActivity.this,
                        "Please fill all required fields",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (animalId == null || animalId.isEmpty()) {
                Toast.makeText(AddMedicalRecordActivity.this,
                        "Animal ID not found!",
                        Toast.LENGTH_LONG).show();
                return;
            }

            Map<String, Object> medicalRecord = new HashMap<>();
            medicalRecord.put("disease", disease);
            medicalRecord.put("medicine", medicine);
            medicalRecord.put("vaccination", vaccination);
            medicalRecord.put("notes", notes);
            medicalRecord.put("date", date);

            db.collection("animals")
                    .document(animalId)
                    .collection("medical_records")
                    .add(medicalRecord)
                    .addOnSuccessListener(documentReference -> {

                        Toast.makeText(AddMedicalRecordActivity.this,
                                "Medical Record Saved Successfully!",
                                Toast.LENGTH_SHORT).show();

                        finish();

                    })
                    .addOnFailureListener(e -> {

                        Toast.makeText(AddMedicalRecordActivity.this,
                                "Failed: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();

                    });

        });
    }
}