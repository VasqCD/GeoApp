# GeoApp

Aplicación Android para la visualización y seguimiento de ubicación en tiempo real utilizando Google Maps API.
<img src="https://github.com/user-attachments/assets/1ad1260d-5359-40da-b03b-1e7e2c1ef59c" width="250" alt="Pantalla principal de GeoApp"/>
<img src="https://github.com/user-attachments/assets/4e7d535e-958b-4f69-af28-67f1c85bf33e" width="250" alt="Opciones de capa de mapa"/>


## Descripción

GeoApp es una aplicación diseñada para mostrar la ubicación actual del usuario en un mapa interactivo. Permite visualizar información detallada de la ubicación, como dirección, coordenadas y precisión. Además, incorpora funcionalidades adicionales como cambio de tipos de mapa.

## Características

- Visualización de la ubicación actual en Google Maps
- Múltiples tipos de mapa (Normal, Satélite, Híbrido, Terreno)

## Estructura del Proyecto

La aplicación sigue el patrón arquitectónico MVC (Modelo-Vista-Controlador):

- **Modelo**: Clases que representan y gestionan los datos (LocationModel)
- **Vista**: Layouts XML y actividades que gestionan la interfaz de usuario (MainActivity)
- **Controlador**: Clases que manejan la lógica de negocio y conectan el modelo con la vista (LocationController)

## Configuración

### Clave de Google Maps API

Para utilizar esta aplicación, necesitarás una clave de API de Google Maps:

1. Obtén una clave en [Google Cloud Console](https://console.cloud.google.com/) activando Maps SDK for Android
2. Crea un archivo `secrets.xml` en la carpeta `app/src/main/res/values/` con el siguiente contenido:
3. Puedes observar leer el archivo example_secrets.xml incluido en values

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="google_maps_key">TU_CLAVE_API_AQUÍ</string>
</resources>
```

**Nota**: El archivo `secrets.xml` está incluido en `.gitignore` para proteger la clave de API.

## Permisos

La aplicación requiere los siguientes permisos:

- `ACCESS_FINE_LOCATION`: Para acceder a la ubicación precisa (GPS)
- `INTERNET`: Para conectarse a Google Maps y servicios de geolocalización

## Uso

1. Instala la aplicación en tu dispositivo
2. Concede los permisos de ubicación cuando se soliciten
3. La aplicación mostrará tu ubicación actual en el mapa
4. Utiliza el panel de control en la esquina superior derecha para cambiar el tipo de mapa 
5. El panel inferior muestra información detallada sobre tu ubicación
6. Pulsa "Actualizar Ubicación" para forzar una actualización de la ubicación actual

