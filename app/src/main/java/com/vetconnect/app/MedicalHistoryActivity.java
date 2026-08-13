package com.vetconnect.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import android.widget.TextView;
import android.view.View;

public class MedicalHistoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;


    ArrayList<MedicalRecordModel> recordList;
    MedicalRecordAdapter adapter;

    FirebaseFirestore db;

    String animalId;
    TextView tvNoRecords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_history);

        recyclerView = findViewById(R.id.recyclerMedicalRecords);
        tvNoRecords = findViewById(R.id.tvNoRecords);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recordList = new ArrayList<>();

        db = FirebaseFirestore.getInstance();

        animalId = getIntent().getStringExtra("animalId");

        adapter = new MedicalRecordAdapter(recordList,
                new MedicalRecordAdapter.OnMedicalRecordClickListener() {

                    @Override
                    public void onDeleteClick(MedicalRecordModel record) {

                        db.collection("animals")
                                .document(animalId)
                                .collection("medical_records")
                                .document(record.getDocumentId())
                                .delete()
                                .addOnSuccessListener(unused -> {

                                    Toast.makeText(MedicalHistoryActivity.this,
                                            "Medical Record Deleted",
                                            Toast.LENGTH_SHORT).show();

                                    loadMedicalRecords();

                                });

                    }

                    @Override
                    public void onEditClick(MedicalRecordModel record) {

                        Intent intent = new Intent(MedicalHistoryActivity.this,
                                EditMedicalRecordActivity.class);

                        intent.putExtra("animalId", animalId);
                        intent.putExtra("documentId", record.getDocumentId());

                        intent.putExtra("disease", record.getDisease());
                        intent.putExtra("medicine", record.getMedicine());
                        intent.putExtra("vaccination", record.getVaccination());
                        intent.putExtra("notes", record.getNotes());
                        intent.putExtra("date", record.getDate());

                        startActivity(intent);
                    }
                });

        recyclerView.setAdapter(adapter);




        loadMedicalRecords();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMedicalRecords();
    }

    private void loadMedicalRecords() {

        db.collection("animals")
                .document(animalId)
                .collection("medical_records")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    recordList.clear();

                    for (var document : queryDocumentSnapshots) {

                        MedicalRecordModel record =
                                document.toObject(MedicalRecordModel.class);

                        record.setDocumentId(document.getId());

                        recordList.add(record);
                    }

                    adapter.notifyDataSetChanged();
                    if (recordList.isEmpty()) {
                        tvNoRecords.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        tvNoRecords.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }

                });
    }
}