package com.hswatch.bluetooth;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.annotation.NonNull;

import static android.content.Context.LOCATION_SERVICE;
import static com.hswatch.bluetooth.Profile.coordenadasGPS;

public class GPSListener implements LocationListener {

    private final Context context;

    private final LocationManager locationManager;

    private boolean isUpdating = false;

    private static GPSListener INSTANCE = null;

    private double[] gpsCoordinates = new double[2];

    private GPSListener(Context context) {
        this.context = context;
        this.locationManager = (LocationManager) this.context.getSystemService(LOCATION_SERVICE);
    }

    public static synchronized GPSListener getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new GPSListener(context);
        }

        return INSTANCE;
    }

    @SuppressLint("MissingPermission")
    public void start() {
        if (locationManager != null && !isUpdating) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 50, this);
            isUpdating = true;
        }
    }

    public void stop() {
        if (locationManager != null && isUpdating) {
            locationManager.removeUpdates(this);
        }
        if (coordenadasGPS != null) {
            coordenadasGPS[0] = 0;
            coordenadasGPS[1] = 0;
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        gpsCoordinates[0] = location.getLatitude();
        gpsCoordinates[1] = location.getLongitude();
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    public double[] getGpsCoordinates() {
        return gpsCoordinates;
    }
}
