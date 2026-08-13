package com.vetconnect.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegisterAnimalActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private Uri imageUri;
    private String currentPhotoPath;

    private Spinner spAnimalType, spGender;
    private EditText etAnimalName, etBreed, etAge;
    private Button btnSaveAnimal;

    private LinearLayout uploadBox;
    private ImageView ivAnimalPhoto;
    // Gallery Launcher
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        if (result.getResultCode() == RESULT_OK && result.getData() != null) {

            imageUri = result.getData().getData();

            if (imageUri != null) {

                ivAnimalPhoto.setVisibility(View.VISIBLE);
                ivAnimalPhoto.setImageURI(imageUri);

            }

        }

    });

    // Camera Launcher
    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        if (result.getResultCode() == RESULT_OK) {

            ivAnimalPhoto.setVisibility(View.VISIBLE);
            ivAnimalPhoto.setImageURI(imageUri);

        }

    });

    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = getCacheDir();

        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        currentPhotoPath = image.getAbsolutePath();

        return image;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_animal);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();



        etAnimalName = findViewById(R.id.etAnimalName);
        etBreed = findViewById(R.id.etBreed);
        etAge = findViewById(R.id.etAge);

        spAnimalType = findViewById(R.id.spinnerAnimalType);
        spGender = findViewById(R.id.spinnerGender);

        btnSaveAnimal = findViewById(R.id.btnSaveAnimal);

        uploadBox = findViewById(R.id.uploadBox);
        ivAnimalPhoto = findViewById(R.id.ivAnimalPhoto);
        // ================= Animal Type Spinner =================

        String[] animalTypes = {"Select Animal Type", "Cow", "Buffalo", "Goat", "Sheep", "Dog", "Cat"};

        ArrayAdapter<String> animalAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, animalTypes);
        animalAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        spAnimalType.setAdapter(animalAdapter);


        // ================= Gender Spinner =================

        String[] genders = {"Select Gender", "Male", "Female"};

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, genders);

        genderAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        spGender.setAdapter(genderAdapter);


        // ================= Upload Photo =================

        uploadBox.setOnClickListener(v -> {

            String[] options = {"📷 Take Photo", "🖼 Choose from Gallery"};

            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterAnimalActivity.this);

            builder.setTitle("Add Animal Photo");

            builder.setItems(options, (dialog, which) -> {

                if (which == 0) {

                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    try {

                        File photoFile = createImageFile();

                        imageUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", photoFile);

                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

                        cameraLauncher.launch(cameraIntent);

                    } catch (IOException e) {

                        e.printStackTrace();

                        Toast.makeText(this, "Unable to open camera", Toast.LENGTH_SHORT).show();

                    }
                } else {

                    Intent galleryIntent = new Intent(Intent.ACTION_PICK);

                    galleryIntent.setType("image/*");

                    galleryLauncher.launch(galleryIntent);

                }

            });

            builder.show();

        });
        // ================= Save Animal =================

        btnSaveAnimal.setOnClickListener(v -> {

            String animalName = etAnimalName.getText().toString().trim();
            String animalType = spAnimalType.getSelectedItem().toString();
            String breed = etBreed.getText().toString().trim();
            String age = etAge.getText().toString().trim();
            String gender = spGender.getSelectedItem().toString();

            // Validation
            if (animalName.isEmpty()) {
                etAnimalName.setError("Enter Animal Name");
                etAnimalName.requestFocus();
                return;
            }

            if (animalType.equals("Select Animal Type")) {
                Toast.makeText(this, "Please select Animal Type", Toast.LENGTH_SHORT).show();
                return;
            }

            if (breed.isEmpty()) {
                etBreed.setError("Enter Breed");
                etBreed.requestFocus();
                return;
            }

            if (age.isEmpty()) {
                etAge.setError("Enter Age");
                etAge.requestFocus();
                return;
            }

            if (gender.equals("Select Gender")) {
                Toast.makeText(this, "Please select Gender", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser user = mAuth.getCurrentUser();

            if (user == null) {
                Toast.makeText(this, "Please login first!", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> animal = new HashMap<>();

            animal.put("animalName", animalName);
            animal.put("animalType", animalType);
            animal.put("breed", breed);
            animal.put("age", age);
            animal.put("gender", gender);
            animal.put("farmerEmail", user.getEmail());

            // Image upload will be added later
            // ================= Upload Image & Save Animal =================

            if (imageUri != null) {

                StorageReference fileRef = storageReference.child("animal_photos/" + System.currentTimeMillis() + ".jpg");

                fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->

                        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {

                            animal.put("photoUrl", uri.toString());

                            db.collection("animals").add(animal).addOnSuccessListener(documentReference -> {

                                Toast.makeText(RegisterAnimalActivity.this, "Animal Registered Successfully!", Toast.LENGTH_SHORT).show();

                                etAnimalName.setText("");
                                etBreed.setText("");
                                etAge.setText("");

                                spAnimalType.setSelection(0);
                                spGender.setSelection(0);

                                ivAnimalPhoto.setImageDrawable(null);
                                ivAnimalPhoto.setVisibility(View.GONE);

                                imageUri = null;

                            }).addOnFailureListener(e ->

                                    Toast.makeText(RegisterAnimalActivity.this, e.getMessage(), Toast.LENGTH_LONG).show()

                            );

                        })

                ).addOnFailureListener(e ->

                        Toast.makeText(RegisterAnimalActivity.this, "Image Upload Failed", Toast.LENGTH_SHORT).show()

                );

            } else {

                animal.put("photoUrl", "");

                db.collection("animals")
                        .add(animal)
                        .addOnSuccessListener(documentReference -> {
                            Dialog dialog = new Dialog(RegisterAnimalActivity.this);
                            dialog.setContentView(R.layout.dialog_success);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.show();

                            new Handler().postDelayed(() -> {
                                dialog.dismiss();
                            }, 2000);
                            etAnimalName.setText("");
                            etBreed.setText("");
                            etAge.setText("");

                            spAnimalType.setSelection(0);
                            spGender.setSelection(0);

                            ivAnimalPhoto.setImageDrawable(null);
                            ivAnimalPhoto.setVisibility(View.GONE);

                            imageUri = null;

                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(
                                    RegisterAnimalActivity.this,
                                    e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        });

            }

        });   // Ends btnSaveAnimal.setOnClickListener

    }   // Ends onCreate()

}   // Ends RegisterAnimalActivity




