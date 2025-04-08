package com.example.mypets.data.model;

import java.io.Serializable;

public class Vaccination implements Serializable {
    private String id;
    private String vaccineName;
    private String date;
    private String nextDate;

    public Vaccination() {}

    // Getter và Setter đúng tên trường
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getVaccineName() {
        return vaccineName;
    }

    public void setVaccineName(String vaccineName) {
        this.vaccineName = vaccineName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNextDate() {
        return nextDate;
    }

    public void setNextDate(String nextDate) {
        this.nextDate = nextDate;
    }
}