package com.example.mypets.data.model;

public class Schedule {
    private String scheduleId;
    private String petId;
    private String petName;
    private String activity;
    private long time;
    private String note;

    public Schedule() {}

    public Schedule(String petId, String activity, long time, String note) {
        this.petId = petId;
        this.activity = activity;
        this.time = time;
        this.note = note;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }
    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPetId() {
        return petId;
    }

    public void setPetId(String petId) {
        this.petId = petId;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
