package com.example.geoapp.Controller;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Looper;

import androidx.core.app.ActivityCompat;

import com.example.geoapp.Model.LocationModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationController {
    private static final int REQUEST_LOCATION_PERMISSION = 1001;
    private Context context;
    private LocationModel locationModel;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private LocationUpdateListener listener;

    public interface LocationUpdateListener {
        void onLocationUpdated(LocationModel location);
    }

    public LocationController(Context context, LocationUpdateListener listener) {
        this.context = context;
        this.locationModel = new LocationModel();
        this.listener = listener;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        createLocationRequest();
        createLocationCallback();
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 20000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(30000)
                .setMaxUpdateDelayMillis(60000)
                .build();
    }

    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult != null && locationResult.getLastLocation() != null) {
                    Location location = locationResult.getLastLocation();
                    locationModel.updateLocation(location);

                    getAddressFromLocation(location);

                    if (listener != null) {
                        listener.onLocationUpdated(locationModel);
                    }
                }
            }
        };
    }

    public void startLocationUpdates() {
        if (checkLocationPermission()) {
            fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
            );
        } else {
            requestLocationPermission();
        }
    }

    public void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    public void getLastLocation() {
        if (checkLocationPermission()) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            locationModel.updateLocation(location);
                            getAddressFromLocation(location);
                            if (listener != null) {
                                listener.onLocationUpdated(locationModel);
                            }
                        }
                    });
        } else {
            requestLocationPermission();
        }
    }

    private void getAddressFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1
            );
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i));
                    if (i < address.getMaxAddressLineIndex()) {
                        sb.append(", ");
                    }
                }

                locationModel.setAddress(sb.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkLocationPermission() {
        return ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestLocationPermission() {
        ActivityCompat.requestPermissions(
                (Activity) context,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                },
                REQUEST_LOCATION_PERMISSION
        );
    }

    public LocationModel getLocationModel() {
        return locationModel;
    }
}