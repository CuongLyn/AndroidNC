package com.example.mypets.data.model;

import java.io.Serializable;
import java.util.List;

public class Clinic implements Serializable {
    private String name;
    private String address;
    private double lat;
    private double lng;
    private String phone;
    private List<String> services;
    private String workingHours;
    private double distance;

    public Clinic() {
    }

    public Clinic(String address, double distance, double lat, double lng, String name, String phone, List<String> services, String workingHours) {
        this.address = address;
        this.distance = distance;
        this.lat = lat;
        this.lng = lng;
        this.name = name;
        this.phone = phone;
        this.services = services;
        this.workingHours = workingHours;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<String> getServices() {
        return services;
    }

    public void setServices(List<String> services) {
        this.services = services;
    }

    public String getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(String workingHours) {
        this.workingHours = workingHours;
    }
}
