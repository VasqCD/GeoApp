package com.example.geoapp.Model;

import android.location.Location;

public class LocationModel {
    private double latitude;
    private double longitude;
    private float accuracy;
    private String address;

    public LocationModel() {
        this.latitude = 0.0;
        this.longitude = 0.0;
        this.accuracy = 0.0f;
        this.address = "";
    }

    public void updateLocation(Location location) {
        if (location != null) {
            this.latitude = location.getLatitude();
            this.longitude = location.getLongitude();
            this.accuracy = location.getAccuracy();
        }
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}