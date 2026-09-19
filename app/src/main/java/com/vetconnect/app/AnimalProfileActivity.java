package com.vetconnect.app;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class AnimalProfileActivity extends AppCompatActivity {

    // Animal details
    TextView txtAnimalName;
    TextView txtAnimalType;
    TextView txtBreed;
    TextView txtAge;
    TextView txtGender;

    // Header
    View btnBack;
    TextView tvHeaderSubtitle;

    // Animal photo
    ShapeableImageView ivAnimalPhoto;

    // Medical records
    RecyclerView recyclerMedicalRecords;
    TextView tvNoRecords;

    ArrayList<MedicalRecordModel> recordList;

    MedicalRecordAdapter adapter;

    FirebaseFirestore db;

    String animalId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_animal_profile);


        // =====================================================
        // FIND VIEWS
        // =====================================================

        txtAnimalName = findViewById(R.id.txtAnimalName);

        txtAnimalType = findViewById(R.id.txtAnimalType);

        txtBreed = findViewById(R.id.txtBreed);

        txtAge = findViewById(R.id.txtAge);

        txtGender = findViewById(R.id.txtGender);

        ivAnimalPhoto = findViewById(R.id.ivAnimalPhoto);

        btnBack = findViewById(R.id.btnBack);

        tvHeaderSubtitle = findViewById(R.id.tvHeaderSubtitle);

        recyclerMedicalRecords =
                findViewById(R.id.recyclerMedicalRecords);

        tvNoRecords =
                findViewById(R.id.tvNoRecords);


        // =====================================================
        // BACK BUTTON
        // =====================================================

        btnBack.setOnClickListener(v -> finish());


        // =====================================================
        // FIREBASE
        // =====================================================

        db = FirebaseFirestore.getInstance();


        // =====================================================
        // GET ANIMAL ID
        // =====================================================

        animalId =
                getIntent().getStringExtra("documentId");


        // =====================================================
        // GET ANIMAL DETAILS
        // =====================================================

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

        String photoUrl =
                getIntent().getStringExtra("photoUrl");


        // =====================================================
        // DEFAULT VALUES
        // =====================================================

        if (animalName == null ||
                animalName.trim().isEmpty()) {

            animalName = "Unknown Animal";
        }


        if (animalType == null ||
                animalType.trim().isEmpty()) {

            animalType = "Animal";
        }


        if (breed == null ||
                breed.trim().isEmpty()) {

            breed = "Not Available";
        }


        if (age == null ||
                age.trim().isEmpty()) {

            age = "Not Available";
        }


        if (gender == null ||
                gender.trim().isEmpty()) {

            gender = "Not Available";
        }


        // =====================================================
        // DISPLAY ANIMAL DETAILS
        // =====================================================

        txtAnimalName.setText(animalName);

        txtAnimalType.setText(animalType);

        txtBreed.setText(breed);

        txtAge.setText(age);

        txtGender.setText(gender);


        // =====================================================
        // HEADER
        // =====================================================

        tvHeaderSubtitle.setText(
                "All medical records of " + animalName
        );


        // =====================================================
        // LOAD ANIMAL PHOTO
        // =====================================================

        if (photoUrl != null &&
                !photoUrl.trim().isEmpty()) {

            Glide.with(this)
                    .load(photoUrl)
                    .placeholder(
                            R.drawable.ic_animal_placeholder
                    )
                    .error(
                            R.drawable.ic_animal_placeholder
                    )
                    .centerCrop()
                    .into(ivAnimalPhoto);

        } else {

            ivAnimalPhoto.setImageResource(
                    R.drawable.ic_animal_placeholder
            );
        }


        // =====================================================
        // RECYCLER VIEW
        // =====================================================

        recyclerMedicalRecords.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerMedicalRecords.setNestedScrollingEnabled(
                false
        );


        // =====================================================
        // MEDICAL RECORD LIST
        // =====================================================

        recordList = new ArrayList<>();


        // =====================================================
        // ADAPTER
        // =====================================================

        adapter =
                new MedicalRecordAdapter(
                        recordList,

                        new MedicalRecordAdapter
                                .OnMedicalRecordClickListener() {

                            @Override
                            public void onDeleteClick(
                                    MedicalRecordModel record) {

                                // Farmer cannot delete records.
                            }


                            @Override
                            public void onEditClick(
                                    MedicalRecordModel record) {

                                // Farmer cannot edit records.
                            }
                        }
                );


        recyclerMedicalRecords.setAdapter(adapter);


        // =====================================================
        // LOAD MEDICAL RECORDS
        // =====================================================

        loadMedicalRecords();
    }


    // =========================================================
    // LOAD MEDICAL RECORDS FROM FIRESTORE
    // =========================================================

    private void loadMedicalRecords() {

        if (animalId == null ||
                animalId.trim().isEmpty()) {

            tvNoRecords.setVisibility(View.VISIBLE);

            recyclerMedicalRecords.setVisibility(View.GONE);

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


                            if (recordList.isEmpty()) {

                                tvNoRecords.setVisibility(
                                        View.VISIBLE
                                );

                                recyclerMedicalRecords.setVisibility(
                                        View.GONE
                                );

                            } else {

                                tvNoRecords.setVisibility(
                                        View.GONE
                                );

                                recyclerMedicalRecords.setVisibility(
                                        View.VISIBLE
                                );
                            }
                        }
                )

                .addOnFailureListener(e -> {

                    tvNoRecords.setVisibility(
                            View.VISIBLE
                    );

                    recyclerMedicalRecords.setVisibility(
                            View.GONE
                    );

                    Toast.makeText(
                            AnimalProfileActivity.this,
                            "Failed to load health records",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }


    // =========================================================
    // RELOAD WHEN RETURNING TO SCREEN
    // =========================================================

    @Override
    protected void onResume() {

        super.onResume();

        if (animalId != null &&
                !animalId.trim().isEmpty()) {

            loadMedicalRecords();
        }
    }
}