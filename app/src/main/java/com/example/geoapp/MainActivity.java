package com.example.geoapp;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
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
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationController.LocationUpdateListener {

    private static final int REQUEST_LOCATION_PERMISSION = 1001;
    private LocationController locationController;
    private GoogleMap googleMap;
    private TextView tvAddress, tvCoordinates, tvAccuracy;
    private Button btnUpdateLocation;

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

        // Inicializar el controlador de ubicación
        locationController = new LocationController(this, this);

        // Configurar el mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Configurar botón para actualizar ubicación
        btnUpdateLocation.setOnClickListener(v -> locationController.getLastLocation());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;

        // Configuración inicial del mapa
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (locationController.getLocationModel().getLatitude() != 0
                && locationController.getLocationModel().getLongitude() != 0) {
            updateMapLocation(locationController.getLocationModel());
        } else {
            // Ubicación por defecto (por ejemplo, la Ciudad de México)
            LatLng defaultLocation = new LatLng(19.4326, -99.1332);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12));
        }

        // Iniciar actualizaciones de ubicación
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

        // Actualizar la ubicación en el mapa
        updateMapLocation(location);
    }

    private void updateMapLocation(LocationModel location) {
        if (googleMap != null) {
            LatLng position = new LatLng(location.getLatitude(), location.getLongitude());

            // Limpiar marcadores anteriores
            googleMap.clear();

            // Añadir un nuevo marcador
            googleMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title("Mi ubicación")
                    .snippet(location.getAddress())
            );

            // Mover la cámara a la ubicación actual
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, iniciar actualizaciones de ubicación
                locationController.startLocationUpdates();
                locationController.getLastLocation();
            } else {
                // Permiso denegado, mostrar mensaje
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