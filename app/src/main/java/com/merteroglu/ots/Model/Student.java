package com.merteroglu.ots.Model;


import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.util.List;

public class Student implements Serializable {
    private String id;
    private String driverId;
    private Timestamp OffBusTime;
    private GeoPoint OffBusLocation;
    private Timestamp OnBusTime;
    private GeoPoint OnBusLocation;
    private GeoPoint address;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public Timestamp getOffBusTime() {
        return OffBusTime;
    }

    public void setOffBusTime(Timestamp offBusTime) {
        OffBusTime = offBusTime;
    }

    public GeoPoint getOffBusLocation() {
        return OffBusLocation;
    }

    public void setOffBusLocation(GeoPoint offBusLocation) {
        OffBusLocation = offBusLocation;
    }

    public Timestamp getOnBusTime() {
        return OnBusTime;
    }

    public void setOnBusTime(Timestamp onBusTime) {
        OnBusTime = onBusTime;
    }

    public GeoPoint getOnBusLocation() {
        return OnBusLocation;
    }

    public void setOnBusLocation(GeoPoint onBusLocation) {
        OnBusLocation = onBusLocation;
    }

    public GeoPoint getAddress() {
        return address;
    }

    public void setAddress(GeoPoint address) {
        this.address = address;
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
