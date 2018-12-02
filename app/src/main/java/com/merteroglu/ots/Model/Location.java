package com.merteroglu.ots.Model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;

public class Location implements Serializable {
    private Timestamp time;
    private GeoPoint location;
    private boolean action;

    public Location() {
    }

    public Location(Timestamp time, GeoPoint location, boolean action) {
        this.time = time;
        this.location = location;
        this.action = action;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public boolean isAction() {
        return action;
    }

    public void setAction(boolean action) {
        this.action = action;
    }
}
