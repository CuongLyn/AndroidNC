package com.example.mypets.data.model.Pet;

public class GioAn {
    private String sang;
    private String trua;
    private String toi;

    public GioAn() {
    }

    public GioAn(String sang, String trua, String toi) {
        this.sang = sang;
        this.trua = trua;
        this.toi = toi;
    }

    public String getSang() {
        return sang;
    }

    public void setSang(String sang) {
        this.sang = sang;
    }

    public String getTrua() {
        return trua;
    }

    public void setTrua(String trua) {
        this.trua = trua;
    }

    public String getToi() {
        return toi;
    }

    public void setToi(String toi) {
        this.toi = toi;
    }

    @Override
    public String toString() {
        return "GioAn{" +
                "sang='" + sang + '\'' +
                ", trua='" + trua + '\'' +
                ", toi='" + toi + '\'' +
                '}';
    }
}
