package com.vetconnect.app;

public class MedicalRecord {

    private String disease;
    private String medicine;
    private String vaccination;
    private String notes;
    private String date;

    // Empty constructor (required for Firebase)
    public MedicalRecord() {
    }

    // Constructor
    public MedicalRecord(String disease, String medicine,
                         String vaccination, String notes,
                         String date) {
        this.disease = disease;
        this.medicine = medicine;
        this.vaccination = vaccination;
        this.notes = notes;
        this.date = date;
    }

    // Getters
    public String getDisease() {
        return disease;
    }

    public String getMedicine() {
        return medicine;
    }

    public String getVaccination() {
        return vaccination;
    }

    public String getNotes() {
        return notes;
    }

    public String getDate() {
        return date;
    }

    // Setters
    public void setDisease(String disease) {
        this.disease = disease;
    }

    public void setMedicine(String medicine) {
        this.medicine = medicine;
    }

    public void setVaccination(String vaccination) {
        this.vaccination = vaccination;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setDate(String date) {
        this.date = date;
    }
}