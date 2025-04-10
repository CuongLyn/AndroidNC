package com.example.mypets.data.model;

import java.io.Serializable;

public class MedicalRecord implements Serializable {
    private String medicalRecordId;
    private String date;
    private String vetName;
    private String clinicName;
    private String reason;
    private String diagnosis;
    private String symptoms;
    private String treatment;
    private String prescription;
    private String note;

    public MedicalRecord() {
    }

    public MedicalRecord(String medicalRecordId, String date, String vetName, String clinicName, String reason, String diagnosis, String symptoms, String treatment, String prescription, String note) {
        this.medicalRecordId = medicalRecordId;
        this.date = date;
        this.vetName = vetName;
        this.clinicName = clinicName;
        this.reason = reason;
        this.diagnosis = diagnosis;
        this.symptoms = symptoms;
        this.treatment = treatment;
        this.prescription = prescription;
        this.note = note;
    }

    public String getMedicalRecordId() {
        return medicalRecordId;
    }

    public void setMedicalRecordId(String medicalRecordId) {
        this.medicalRecordId = medicalRecordId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getVetName() {
        return vetName;
    }

    public void setVetName(String vetName) {
        this.vetName = vetName;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "MedicalRecord{" +
                "MedicalRecordId='" + medicalRecordId + '\'' +
                ", date='" + date + '\'' +
                ", vetName='" + vetName + '\'' +
                ", clinicName='" + clinicName + '\'' +
                ", reason='" + reason + '\'' +
                ", diagnosis='" + diagnosis + '\'' +
                ", symptoms='" + symptoms + '\'' +
                ", treatment='" + treatment + '\'' +
                ", prescription='" + prescription + '\'' +
                ", note='" + note + '\'' +
                '}';
    }
}
