package com.vetconnect.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class MedicalAnimalListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Animal> animalList;
    MedicalAnimalAdapter adapter;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_animal_list);

        recyclerView = findViewById(R.id.recyclerAnimals);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        animalList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        adapter = new MedicalAnimalAdapter(animalList, animal -> {

            Intent intent = new Intent(this, MedicalHistoryActivity.class);
            intent.putExtra("animalId", animal.getDocumentId());
            startActivity(intent);

            intent.putExtra("documentId", animal.getDocumentId());
            intent.putExtra("animalName", animal.getAnimalName());

            startActivity(intent);

        });

        recyclerView.setAdapter(adapter);

        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        db.collection("animals")
                .whereEqualTo("farmerEmail", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    animalList.clear();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                        Animal animal = document.toObject(Animal.class);
                        animal.setDocumentId(document.getId());
                        animalList.add(animal);

                    }

                    adapter.notifyDataSetChanged();

                });

    }
}