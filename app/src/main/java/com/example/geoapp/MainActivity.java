package com.example.geoapp;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.geoapp.Controller.LocationController;
import com.example.geoapp.Model.LocationModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationController.LocationUpdateListener {

    private static final int REQUEST_LOCATION_PERMISSION = 1001;
    private LocationController locationController;
    private GoogleMap googleMap;
    private TextView tvAddress, tvCoordinates, tvAccuracy;
    private Button btnUpdateLocation;
    private RadioGroup rgMapType;
    private boolean mapUIConfigured = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Configurar insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar vistas
        tvAddress = findViewById(R.id.tvAddress);
        tvCoordinates = findViewById(R.id.tvCoordinates);
        tvAccuracy = findViewById(R.id.tvAccuracy);
        btnUpdateLocation = findViewById(R.id.btnUpdateLocation);
        rgMapType = findViewById(R.id.rgMapType);

        locationController = new LocationController(this, this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        btnUpdateLocation.setOnClickListener(v -> locationController.getLastLocation());

        rgMapType.setOnCheckedChangeListener((group, checkedId) -> {
            if (googleMap != null) {
                if (checkedId == R.id.rbNormal) {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                } else if (checkedId == R.id.rbSatellite) {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                } else if (checkedId == R.id.rbHybrid) {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                } else if (checkedId == R.id.rbTerrain) {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);

        googleMap.getUiSettings().setCompassEnabled(true);

        try {
            googleMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        if (locationController.getLocationModel().getLatitude() != 0
                && locationController.getLocationModel().getLongitude() != 0) {
            updateMapLocation(locationController.getLocationModel());
        } else {
            LatLng defaultLocation = new LatLng(15.506186111111, -88.024897222222);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12));
        }

        locationController.startLocationUpdates();
    }

    @Override
    public void onLocationUpdated(LocationModel location) {
        // Actualizar la interfaz con la nueva ubicación
        tvAddress.setText(location.getAddress().isEmpty() ?
                "Dirección no disponible" : location.getAddress());

        tvCoordinates.setText(String.format(
                "Latitud: %.6f, Longitud: %.6f",
                location.getLatitude(),
                location.getLongitude()
        ));

        tvAccuracy.setText(String.format(
                "Precisión: %.1f m",
                location.getAccuracy()
        ));

        updateMapLocation(location);
    }

    private void updateMapLocation(LocationModel location) {
        if (googleMap != null) {
            LatLng position = new LatLng(location.getLatitude(), location.getLongitude());

            googleMap.clear();

            googleMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title("Mi ubicación")
                    .snippet(location.getAddress())
            );

            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationController.startLocationUpdates();
                locationController.getLastLocation();
            } else {
                Toast.makeText(this, "Se requiere permiso de ubicación para usar esta aplicación", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationController.startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationController.stopLocationUpdates();
    }
}