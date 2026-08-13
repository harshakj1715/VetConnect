package com.vetconnect.app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditMedicalRecordActivity extends AppCompatActivity {

    EditText etDisease, etMedicine, etVaccination, etNotes, etDate;
    Button btnUpdateRecord;

    FirebaseFirestore db;

    String animalId, documentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_medical_record);

        etDisease = findViewById(R.id.etDisease);
        etMedicine = findViewById(R.id.etMedicine);
        etVaccination = findViewById(R.id.etVaccination);
        etNotes = findViewById(R.id.etNotes);
        etDate = findViewById(R.id.etDate);

        btnUpdateRecord = findViewById(R.id.btnUpdateRecord);

        db = FirebaseFirestore.getInstance();

        animalId = getIntent().getStringExtra("animalId");
        documentId = getIntent().getStringExtra("documentId");

        etDisease.setText(getIntent().getStringExtra("disease"));
        etMedicine.setText(getIntent().getStringExtra("medicine"));
        etVaccination.setText(getIntent().getStringExtra("vaccination"));
        etNotes.setText(getIntent().getStringExtra("notes"));
        etDate.setText(getIntent().getStringExtra("date"));

        btnUpdateRecord.setOnClickListener(v -> {

            Map<String, Object> updatedRecord = new HashMap<>();

            updatedRecord.put("disease", etDisease.getText().toString().trim());
            updatedRecord.put("medicine", etMedicine.getText().toString().trim());
            updatedRecord.put("vaccination", etVaccination.getText().toString().trim());
            updatedRecord.put("notes", etNotes.getText().toString().trim());
            updatedRecord.put("date", etDate.getText().toString().trim());

            db.collection("animals")
                    .document(animalId)
                    .collection("medical_records")
                    .document(documentId)
                    .update(updatedRecord)
                    .addOnSuccessListener(unused -> {

                        Toast.makeText(EditMedicalRecordActivity.this,
                                "Medical Record Updated Successfully!",
                                Toast.LENGTH_SHORT).show();

                        finish();

                    })
                    .addOnFailureListener(e ->

                            Toast.makeText(EditMedicalRecordActivity.this,
                                    e.getMessage(),
                                    Toast.LENGTH_LONG).show()

                    );

        });
    }
}