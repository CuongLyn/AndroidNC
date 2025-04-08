package com.example.mypets.data.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HealthRecord {
    private String date;
    private String diagnosis;
    private String symptoms;
    private float weight;
    private int height;

    public HealthRecord(String date, String diagnosis, String symptoms, float weight, int height) {
        this.date = date;
        this.diagnosis = diagnosis;
        this.symptoms = symptoms;
        this.weight = weight;
        this.height = height;
    }

    public HealthRecord() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "HealthRecord{" +
                "date='" + date + '\'' +
                ", diagnosis='" + diagnosis + '\'' +
                ", symptoms='" + symptoms + '\'' +
                ", weight=" + weight +
                ", height=" + height +
                '}';
    }
    public long getTimestamp() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date parsedDate = sdf.parse(date);
            return parsedDate != null ? parsedDate.getTime() : 0L; // Trả về timestamp tính bằng milliseconds
        } catch (ParseException e) {
            e.printStackTrace();
            return 0L;
        }
    }
}
