package com.example.mypets.data.model;

public class Pet {
    private String id;
    private String name;
    private String loai;
    private int tuoi;
    private String imageUrl;
    private String lichTiem;
    private String lichKiemTraSucKhoe;

    public Pet() {
    }

    public Pet(String id, String name, String loai, int tuoi, String imageUrl, String lichTiem, String lichKiemTraSucKhoe) {
        this.id = id;
        this.name = name;
        this.loai = loai;
        this.tuoi = tuoi;
        this.imageUrl = imageUrl;
        this.lichTiem = lichTiem;
        this.lichKiemTraSucKhoe = lichKiemTraSucKhoe;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLoai() {
        return loai;
    }

    public void setLoai(String loai) {
        this.loai = loai;
    }

    public int getTuoi() {
        return tuoi;
    }

    public void setTuoi(int tuoi) {
        this.tuoi = tuoi;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLichTiem() {
        return lichTiem;
    }

    public void setLichTiem(String lichTiem) {
        this.lichTiem = lichTiem;
    }

    public String getLichKiemTraSucKhoe() {
        return lichKiemTraSucKhoe;
    }

    public void setLichKiemTraSucKhoe(String lichKiemTraSucKhoe) {
        this.lichKiemTraSucKhoe = lichKiemTraSucKhoe;
    }

    @Override
    public String toString() {
        return "Pet{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", loai='" + loai + '\'' +
                ", tuoi=" + tuoi +
                ", imageUrl='" + imageUrl + '\'' +
                ", lichTiem='" + lichTiem + '\'' +
                ", lichKiemTraSucKhoe='" + lichKiemTraSucKhoe + '\'' +
                '}';
    }
}
