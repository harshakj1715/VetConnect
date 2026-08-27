package com.vetconnect.app;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class AnimalProfileActivity extends AppCompatActivity {

    TextView txtAnimalName;
    TextView txtAnimalType;
    TextView txtBreed;
    TextView txtAge;
    TextView txtGender;
    TextView tvNoRecords;

    RecyclerView recyclerMedicalRecords;

    ArrayList<MedicalRecordModel> recordList;

    MedicalRecordAdapter adapter;

    FirebaseFirestore db;

    String animalId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_animal_profile);

        // -----------------------------
        // Animal Profile Views
        // -----------------------------

        txtAnimalName = findViewById(R.id.txtAnimalName);
        txtAnimalType = findViewById(R.id.txtAnimalType);
        txtBreed = findViewById(R.id.txtBreed);
        txtAge = findViewById(R.id.txtAge);
        txtGender = findViewById(R.id.txtGender);

        // -----------------------------
        // Medical Records Views
        // -----------------------------

        recyclerMedicalRecords =
                findViewById(R.id.recyclerMedicalRecords);

        tvNoRecords =
                findViewById(R.id.tvNoRecords);

        // -----------------------------
        // Firebase
        // -----------------------------

        db = FirebaseFirestore.getInstance();

        // -----------------------------
        // Get Animal ID
        // -----------------------------

        animalId =
                getIntent().getStringExtra("documentId");

        // -----------------------------
        // Get Animal Details
        // -----------------------------

        String animalName =
                getIntent().getStringExtra("animalName");

        String animalType =
                getIntent().getStringExtra("animalType");

        String breed =
                getIntent().getStringExtra("breed");

        String age =
                getIntent().getStringExtra("age");

        String gender =
                getIntent().getStringExtra("gender");

        // -----------------------------
        // Display Animal Details
        // -----------------------------

        txtAnimalName.setText(animalName);

        txtAnimalType.setText(
                "Type : " + animalType
        );

        txtBreed.setText(
                "Breed : " + breed
        );

        txtAge.setText(
                "Age : " + age
        );

        txtGender.setText(
                "Gender : " + gender
        );

        // -----------------------------
        // RecyclerView
        // -----------------------------

        recyclerMedicalRecords.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recordList = new ArrayList<>();

        // -----------------------------
        // Medical Record Adapter
        // -----------------------------

        adapter = new MedicalRecordAdapter(
                recordList,

                new MedicalRecordAdapter.OnMedicalRecordClickListener() {

                    @Override
                    public void onDeleteClick(
                            MedicalRecordModel record) {

                        // Farmer cannot delete records.
                        // Do nothing.
                    }

                    @Override
                    public void onEditClick(
                            MedicalRecordModel record) {

                        // Farmer cannot edit records.
                        // Do nothing.
                    }
                }
        );

        recyclerMedicalRecords.setAdapter(adapter);

        // -----------------------------
        // Load Medical Records
        // -----------------------------

        loadMedicalRecords();
    }

    // -----------------------------
    // Load Health Records
    // -----------------------------

    private void loadMedicalRecords() {

        if (animalId == null ||
                animalId.isEmpty()) {

            Toast.makeText(
                    this,
                    "Animal ID not found",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        db.collection("animals")
                .document(animalId)
                .collection("medical_records")
                .get()
                .addOnSuccessListener(
                        queryDocumentSnapshots -> {

                            recordList.clear();

                            for (QueryDocumentSnapshot document :
                                    queryDocumentSnapshots) {

                                MedicalRecordModel record =
                                        document.toObject(
                                                MedicalRecordModel.class
                                        );

                                record.setDocumentId(
                                        document.getId()
                                );

                                recordList.add(record);
                            }

                            adapter.notifyDataSetChanged();

                            // -----------------------------
                            // Show / Hide No Records
                            // -----------------------------

                            if (recordList.isEmpty()) {

                                tvNoRecords.setVisibility(
                                        View.VISIBLE
                                );

                                recyclerMedicalRecords
                                        .setVisibility(
                                                View.GONE
                                        );

                            } else {

                                tvNoRecords.setVisibility(
                                        View.GONE
                                );

                                recyclerMedicalRecords
                                        .setVisibility(
                                                View.VISIBLE
                                        );
                            }
                        }
                )
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            AnimalProfileActivity.this,
                            "Failed to load health records",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Reload records when returning
        // to this screen.
        if (animalId != null) {
            loadMedicalRecords();
        }
    }
}