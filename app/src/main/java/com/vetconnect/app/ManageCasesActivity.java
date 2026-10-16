
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

public class ManageCasesActivity extends AppCompatActivity {

    private TextView btnBack;
    private TextView tvCaseCount;
    private TextView tvNoCases;
    private RecyclerView recyclerCases;

    private FirebaseFirestore db;
    private ListenerRegistration caseListener;

    private final ArrayList<EmergencyRequestModel> caseList =
            new ArrayList<>();

    private EmergencyRequestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_cases);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            return;
        }

        btnBack = findViewById(R.id.btnBackManageCases);
        tvCaseCount = findViewById(R.id.tvCaseCount);
        tvNoCases = findViewById(R.id.tvNoCases);
        recyclerCases = findViewById(R.id.recyclerCases);

        db = FirebaseFirestore.getInstance();

        recyclerCases.setLayoutManager(new LinearLayoutManager(this));

        adapter = new EmergencyRequestAdapter(
                caseList,
                request -> Toast.makeText(
                        this,
                        "This case has already been accepted.",
                        Toast.LENGTH_SHORT
                ).show()
        );

        recyclerCases.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        loadAcceptedCases();
    }

    private void loadAcceptedCases() {
        tvNoCases.setText("Loading cases...");
        tvNoCases.setVisibility(View.VISIBLE);

        caseListener = db.collection("emergency_requests")
                .whereEqualTo(
                        "acceptedBy",
                        FirebaseAuth.getInstance().getCurrentUser().getUid()
                )
                .addSnapshotListener((snapshot, error) -> {

                    if (isFinishing() || isDestroyed()) return;

                    if (error != null) {
                        tvNoCases.setText(
                                "Unable to load cases. Check Firestore permissions."
                        );
                        tvNoCases.setVisibility(View.VISIBLE);
                        recyclerCases.setVisibility(View.GONE);
                        return;
                    }

                    caseList.clear();

                    if (snapshot != null) {
                        for (DocumentSnapshot document :
                                snapshot.getDocuments()) {

                            EmergencyRequestModel request =
                                    document.toObject(
                                            EmergencyRequestModel.class
                                    );

                            if (request != null) {
                                request.setDocumentId(document.getId());
                                caseList.add(request);
                            }
                        }
                    }

                    adapter.notifyDataSetChanged();

                    tvCaseCount.setText(
                            "My Accepted Cases: " + caseList.size()
                    );

                    if (caseList.isEmpty()) {
                        tvNoCases.setText(
                                "No accepted cases yet.\n\n" +
                                        "Cases you accept will appear here."
                        );
                        tvNoCases.setVisibility(View.VISIBLE);
                        recyclerCases.setVisibility(View.GONE);
                    } else {
                        tvNoCases.setVisibility(View.GONE);
                        recyclerCases.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (caseListener != null) {
            caseListener.remove();
        }
    }
}
