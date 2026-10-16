package com.vetconnect.app;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.FieldValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EmergencyRequestsActivity extends AppCompatActivity {

    private TextView btnBack;
    private TextView tvEmergencyRequestCount;
    private TextView tvNoEmergencyRequests;
    private RecyclerView recyclerEmergencyRequests;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    private ArrayList<EmergencyRequestModel> requestList;
    private EmergencyRequestAdapter adapter;
    private ListenerRegistration requestListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_requests);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(
                    this,
                    "Please log in to access emergency requests.",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        btnBack = findViewById(R.id.btnBackEmergencyRequests);
        tvEmergencyRequestCount = findViewById(R.id.tvEmergencyRequestCount);
        tvNoEmergencyRequests = findViewById(R.id.tvNoEmergencyRequests);
        recyclerEmergencyRequests = findViewById(R.id.recyclerEmergencyRequests);

        db = FirebaseFirestore.getInstance();
        requestList = new ArrayList<>();

        recyclerEmergencyRequests.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter = new EmergencyRequestAdapter(
                requestList,
                this::acceptEmergencyRequest
        );

        recyclerEmergencyRequests.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        loadEmergencyRequests();
    }

    private void loadEmergencyRequests() {

        tvNoEmergencyRequests.setText("Loading emergency requests...");
        tvNoEmergencyRequests.setVisibility(View.VISIBLE);
        recyclerEmergencyRequests.setVisibility(View.GONE);

        requestListener = db.collection("emergency_requests")
                .addSnapshotListener((snapshot, error) -> {

                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    if (error != null) {
                        tvEmergencyRequestCount.setText(
                                "Unable to load requests"
                        );

                        tvNoEmergencyRequests.setText(
                                "Could not load requests. Check your internet " +
                                        "connection and Firestore permissions."
                        );

                        tvNoEmergencyRequests.setVisibility(View.VISIBLE);
                        recyclerEmergencyRequests.setVisibility(View.GONE);

                        Toast.makeText(
                                EmergencyRequestsActivity.this,
                                "Error loading requests: " + error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    requestList.clear();

                    if (snapshot != null) {
                        for (DocumentSnapshot document : snapshot.getDocuments()) {

                            EmergencyRequestModel request =
                                    document.toObject(EmergencyRequestModel.class);

                            if (request != null) {
                                request.setDocumentId(document.getId());
                                requestList.add(request);
                            }
                        }
                    }

                    adapter.notifyDataSetChanged();

                    int totalRequests = requestList.size();

                    tvEmergencyRequestCount.setText(
                            "Total Emergency Requests: " + totalRequests
                    );

                    if (totalRequests == 0) {
                        tvNoEmergencyRequests.setText(
                                "No emergency requests yet.\n\n" +
                                        "Farmer requests will appear here."
                        );

                        tvNoEmergencyRequests.setVisibility(View.VISIBLE);
                        recyclerEmergencyRequests.setVisibility(View.GONE);

                    } else {
                        tvNoEmergencyRequests.setVisibility(View.GONE);
                        recyclerEmergencyRequests.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void acceptEmergencyRequest(EmergencyRequestModel request) {

        if (request.getDocumentId() == null ||
                request.getDocumentId().trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Request ID is missing.",
                    Toast.LENGTH_SHORT
            ).show();

            adapter.notifyDataSetChanged();
            return;
        }

        if (currentUser == null) {
            Toast.makeText(
                    this,
                    "Please log in again.",
                    Toast.LENGTH_SHORT
            ).show();

            adapter.notifyDataSetChanged();
            return;
        }
        Toast.makeText(
                this,
                "Doctor UID: " + currentUser.getUid(),
                Toast.LENGTH_LONG
        ).show();

        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "accepted");
        updates.put("acceptedBy", currentUser.getUid());
        updates.put("acceptedAt", FieldValue.serverTimestamp());

        db.collection("emergency_requests")
                .document(request.getDocumentId())
                .update(updates)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            EmergencyRequestsActivity.this,
                            "Emergency request accepted!",
                            Toast.LENGTH_SHORT
                    ).show();

                    // The Firestore listener refreshes the list automatically.
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            EmergencyRequestsActivity.this,
                            "Unable to accept request: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                    adapter.notifyDataSetChanged();
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (requestListener != null) {
            requestListener.remove();
            requestListener = null;
        }
    }
}