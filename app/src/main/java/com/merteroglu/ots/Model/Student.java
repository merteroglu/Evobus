package com.merteroglu.ots.Model;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.util.List;

public class Student implements Serializable {
    private BusEvent OffBus;
    private BusEvent OnBus;
    private GeoPoint adress;
    private String bid;
    private GeoPoint currentLocation;
    private boolean inVehicle;
    private String name;
    private String parentName;
    private String parentPhone;
    private String password;
    private String phone;
    private String tc;
    private String vehicle;
    private List<Location> locations;

    public BusEvent getOffBus() {
        return OffBus;
    }

    public void setOffBus(BusEvent offBus) {
        OffBus = offBus;
    }

    public BusEvent getOnBus() {
        return OnBus;
    }

    public void setOnBus(BusEvent onBus) {
        OnBus = onBus;
    }

    public GeoPoint getAdress() {
        return adress;
    }

    public void setAdress(GeoPoint adress) {
        this.adress = adress;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public GeoPoint getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(GeoPoint currentLocation) {
        this.currentLocation = currentLocation;
    }

    public boolean isInVehicle() {
        return inVehicle;
    }

    public void setInVehicle(boolean inVehicle) {
        this.inVehicle = inVehicle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentPhone() {
        return parentPhone;
    }

    public void setParentPhone(String parentPhone) {
        this.parentPhone = parentPhone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTc() {
        return tc;
    }

    public void setTc(String tc) {
        this.tc = tc;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }
}
