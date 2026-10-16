
package com.vetconnect.app;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TreatmentRecordsActivity extends AppCompatActivity {

    private TextView btnBack;
    private TextView tvRecordCount;
    private TextView tvNoRecords;
    private RecyclerView recyclerTreatmentRecords;

    private FirebaseFirestore db;
    private ListenerRegistration animalListener;

    private final ArrayList<Map<String, String>> treatmentRecords =
            new ArrayList<>();

    private TreatmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treatment_records);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            return;
        }

        btnBack = findViewById(R.id.btnBackTreatmentRecords);
        tvRecordCount = findViewById(R.id.tvRecordCount);
        tvNoRecords = findViewById(R.id.tvNoRecords);
        recyclerTreatmentRecords =
                findViewById(R.id.recyclerTreatmentRecords);

        db = FirebaseFirestore.getInstance();

        recyclerTreatmentRecords.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter = new TreatmentAdapter(treatmentRecords);
        recyclerTreatmentRecords.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        loadTreatmentRecords();
    }

    private void loadTreatmentRecords() {

        tvNoRecords.setText("Loading treatment records...");
        tvNoRecords.setVisibility(View.VISIBLE);

        animalListener = db.collection("animals")
                .addSnapshotListener((snapshot, error) -> {

                    if (isFinishing() || isDestroyed()) return;

                    if (error != null) {
                        tvNoRecords.setText(
                                "Unable to load animal records. " +
                                        "Check your Firestore permissions."
                        );
                        tvNoRecords.setVisibility(View.VISIBLE);
                        recyclerTreatmentRecords.setVisibility(View.GONE);
                        return;
                    }

                    if (snapshot == null) return;

                    // Remove the previous animal listener before reloading.
                    if (recordListeners != null) {
                        for (ListenerRegistration listener : recordListeners) {
                            listener.remove();
                        }
                    }

                    recordListeners = new ArrayList<>();
                    treatmentRecords.clear();

                    if (snapshot.isEmpty()) {
                        updateRecordsUI();
                        return;
                    }

                    final int totalAnimals = snapshot.size();
                    final int[] completedAnimals = {0};

                    for (DocumentSnapshot animal : snapshot.getDocuments()) {

                        String animalId = animal.getId();
                        String animalName = animal.getString("animalName");

                        if (animalName == null || animalName.trim().isEmpty()) {
                            animalName = animal.getString("name");
                        }

                        final String finalAnimalName =
                                animalName == null ? "Animal" : animalName;

                        ListenerRegistration recordListener =
                                db.collection("animals")
                                        .document(animalId)
                                        .collection("medical_records")
                                        .addSnapshotListener((records, recordError) -> {

                                            if (isFinishing() || isDestroyed()) return;

                                            if (recordError != null) {
                                                Toast.makeText(
                                                        TreatmentRecordsActivity.this,
                                                        "Unable to load some medical records.",
                                                        Toast.LENGTH_SHORT
                                                ).show();
                                                return;
                                            }

                                            if (records != null) {

                                                // Remove older entries for this animal.
                                                for (int i = treatmentRecords.size() - 1;
                                                     i >= 0; i--) {

                                                    if (animalId.equals(
                                                            treatmentRecords.get(i)
                                                                    .get("animalId"))) {
                                                        treatmentRecords.remove(i);
                                                    }
                                                }

                                                for (DocumentSnapshot record :
                                                        records.getDocuments()) {

                                                    Map<String, String> item =
                                                            new HashMap<>();

                                                    item.put("animalId", animalId);
                                                    item.put("animalName",
                                                            finalAnimalName);
                                                    item.put("recordId",
                                                            record.getId());

                                                    item.put("diagnosis",
                                                            getFirstString(record,
                                                                    "diagnosis",
                                                                    "condition",
                                                                    "problem"));

                                                    item.put("treatment",
                                                            getFirstString(record,
                                                                    "treatment",
                                                                    "treatmentDetails",
                                                                    "notes"));

                                                    item.put("medicine",
                                                            getFirstString(record,
                                                                    "medicine",
                                                                    "medication",
                                                                    "prescribedMedicine"));

                                                    item.put("date",
                                                            getFirstString(record,
                                                                    "date",
                                                                    "recordDate",
                                                                    "createdAt"));

                                                    treatmentRecords.add(item);
                                                }

                                                adapter.notifyDataSetChanged();
                                                updateRecordsUI();
                                            }
                                        });

                        recordListeners.add(recordListener);
                    }
                });
    }

    private ArrayList<ListenerRegistration> recordListeners;

    private String getFirstString(
            DocumentSnapshot document, String... fieldNames) {

        for (String field : fieldNames) {
            Object value = document.get(field);

            if (value instanceof String) {
                String text = (String) value;

                if (!text.trim().isEmpty()) {
                    return text;
                }
            }

            if (value instanceof com.google.firebase.Timestamp) {
                return ((com.google.firebase.Timestamp) value)
                        .toDate().toString();
            }
        }

        return "Not provided";
    }

    private void updateRecordsUI() {

        tvRecordCount.setText(
                "Treatment Records: " + treatmentRecords.size()
        );

        if (treatmentRecords.isEmpty()) {
            tvNoRecords.setText(
                    "No treatment records found.\n\n" +
                            "Medical records saved for animals will appear here."
            );

            tvNoRecords.setVisibility(View.VISIBLE);
            recyclerTreatmentRecords.setVisibility(View.GONE);

        } else {
            tvNoRecords.setVisibility(View.GONE);
            recyclerTreatmentRecords.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (animalListener != null) {
            animalListener.remove();
        }

        if (recordListeners != null) {
            for (ListenerRegistration listener : recordListeners) {
                listener.remove();
            }
        }
    }

    private static class TreatmentAdapter extends
            RecyclerView.Adapter<TreatmentAdapter.RecordViewHolder> {

        private final ArrayList<Map<String, String>> records;

        TreatmentAdapter(ArrayList<Map<String, String>> records) {
            this.records = records;
        }

        @Override
        public RecordViewHolder onCreateViewHolder(
                android.view.ViewGroup parent, int viewType) {

            View view = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_treatment_record, parent, false);

            return new RecordViewHolder(view);
        }

        @Override
        public void onBindViewHolder(
                RecordViewHolder holder, int position) {

            Map<String, String> record = records.get(position);

            holder.tvAnimalName.setText(
                    "Animal: " + record.get("animalName")
            );

            holder.tvDiagnosis.setText(
                    "Diagnosis: " + record.get("diagnosis")
            );

            holder.tvTreatment.setText(
                    "Treatment: " + record.get("treatment")
            );

            holder.tvMedicine.setText(
                    "Medicine: " + record.get("medicine")
            );

            holder.tvDate.setText(
                    "Date: " + record.get("date")
            );
        }

        @Override
        public int getItemCount() {
            return records.size();
        }

        static class RecordViewHolder extends RecyclerView.ViewHolder {

            TextView tvAnimalName;
            TextView tvDiagnosis;
            TextView tvTreatment;
            TextView tvMedicine;
            TextView tvDate;

            RecordViewHolder(View itemView) {
                super(itemView);

                tvAnimalName =
                        itemView.findViewById(R.id.tvRecordAnimalName);
                tvDiagnosis =
                        itemView.findViewById(R.id.tvRecordDiagnosis);
                tvTreatment =
                        itemView.findViewById(R.id.tvRecordTreatment);
                tvMedicine =
                        itemView.findViewById(R.id.tvRecordMedicine);
                tvDate =
                        itemView.findViewById(R.id.tvRecordDate);
            }
        }
    }
}
