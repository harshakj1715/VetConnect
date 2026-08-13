package com.vetconnect.app;

public class Animal {

    private String animalName;
    private String animalType;
    private String breed;
    private String age;
    private String gender;
    private String farmerEmail;
    private String documentId;


    public Animal() {
        // Required empty constructor for Firestore
    }

    public Animal(String animalName, String animalType, String breed,
                  String age, String gender, String farmerEmail) {
        this.animalName = animalName;
        this.animalType = animalType;
        this.breed = breed;
        this.age = age;
        this.gender = gender;
        this.farmerEmail = farmerEmail;
    }

    public String getAnimalName() {
        return animalName;
    }

    public String getAnimalType() {
        return animalType;
    }

    public String getBreed() {
        return breed;
    }

    public String getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getFarmerEmail() {
        return farmerEmail;
    }
    public String getDocumentId() {
        return documentId;
    }


    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
