package com.example.mypets.data.model.Pet;

import java.io.Serializable;
import java.util.List;

public class Pet implements Serializable {
    private String id;
    private String name;
    private String loai;
    private int tuoi;
    private String gioiTinh;
    private String lichTiem;
    private String lichKiemTraSucKhoe;
    private GioAn gioAn;
    private String ownerId;
    private List<String> imageUrls;


    public Pet() {
    }

    public Pet(String id, String name, String loai, int tuoi, String gioiTinh, String lichTiem, String lichKiemTraSucKhoe, GioAn gioAn) {
        this.id = id;
        this.name = name;
        this.loai = loai;
        this.tuoi = tuoi;
        this.gioiTinh = gioiTinh;
        this.lichTiem = lichTiem;
        this.lichKiemTraSucKhoe = lichKiemTraSucKhoe;
        this.gioAn = gioAn;
    }

    public Pet(String id, String name, String loai, int tuoi, String gioiTinh, String lichTiem, String lichKiemTraSucKhoe) {
        this.id = id;
        this.name = name;
        this.loai = loai;
        this.tuoi = tuoi;
        this.gioiTinh = gioiTinh;
        this.lichTiem = lichTiem;
        this.lichKiemTraSucKhoe = lichKiemTraSucKhoe;
    }
    public Pet(String id, String name, String loai, int tuoi, String gioiTinh, String lichTiem, String lichKiemTraSucKhoe, String ownerId, List<String> imageUrls) {
        this.id = id;
        this.name = name;
        this.loai = loai;
        this.tuoi = tuoi;
        this.gioiTinh = gioiTinh;
        this.lichTiem = lichTiem;
        this.lichKiemTraSucKhoe = lichKiemTraSucKhoe;
        this.ownerId = ownerId;
        this.imageUrls = imageUrls;

    }

    public Pet(String name, String ownerId) {
        this.name = name;
        this.ownerId = ownerId;
    }


    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

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

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
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

    public GioAn getGioAn() {
        return gioAn;
    }

    public void setGioAn(GioAn gioAn) {
        this.gioAn = gioAn;
    }


    @Override
    public String toString() {
        return "Pet{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", loai='" + loai + '\'' +
                ", tuoi=" + tuoi +
                ", gioiTinh='" + gioiTinh + '\'' +
                ", lichTiem='" + lichTiem + '\'' +
                ", lichKiemTraSucKhoe='" + lichKiemTraSucKhoe + '\'' +
                ", gioAn=" + (gioAn != null ?
                "[sáng=" + gioAn.getSang() +
                        ", trưa=" + gioAn.getTrua() +
                        ", tối=" + gioAn.getToi() + "]" : "null") +
                '}';
    }
}
