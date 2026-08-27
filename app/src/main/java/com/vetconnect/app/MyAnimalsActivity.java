package com.vetconnect.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class MyAnimalsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Animal> animalList;
    AnimalAdapter adapter;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_animals);

        recyclerView = findViewById(R.id.recyclerAnimals);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        animalList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        adapter = new AnimalAdapter(animalList,
                new AnimalAdapter.OnAnimalClickListener() {

                    @Override
                    public void onAnimalClick(Animal animal) {

                        Intent intent = new Intent(
                                MyAnimalsActivity.this,
                                AnimalProfileActivity.class
                        );

                        intent.putExtra("documentId",
                                animal.getDocumentId());

                        intent.putExtra("animalName",
                                animal.getAnimalName());

                        intent.putExtra("animalType",
                                animal.getAnimalType());

                        intent.putExtra("breed",
                                animal.getBreed());

                        intent.putExtra("age",
                                animal.getAge());

                        intent.putExtra("gender",
                                animal.getGender());

                        startActivity(intent);
                    }
                });

        recyclerView.setAdapter(adapter);

        FirebaseUser user =
                FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {

            Toast.makeText(
                    this,
                    "Please login again",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        String email = user.getEmail();

        db.collection("animals")
                .whereEqualTo("farmerEmail", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    animalList.clear();

                    for (QueryDocumentSnapshot document :
                            queryDocumentSnapshots) {

                        Animal animal =
                                document.toObject(Animal.class);

                        animal.setDocumentId(
                                document.getId());

                        animalList.add(animal);
                    }

                    adapter.notifyDataSetChanged();


                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                MyAnimalsActivity.this,
                                e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show());
    }
}