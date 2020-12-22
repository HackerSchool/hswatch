package com.hswatch.bluetooth;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import static android.content.Context.LOCATION_SERVICE;
import static com.hswatch.bluetooth.Profile.coordenadasGPS;

public class GPSListener implements LocationListener {

    private final Context context;

    private final LocationManager locationManager;

    private boolean isUpdating = false;

    public GPSListener(Context context) {
        this.context = context;
        this.locationManager = (LocationManager) this.context.getSystemService(LOCATION_SERVICE);
    }

    @SuppressLint("MissingPermission")
    public void GPSStart() {
        if (locationManager != null && !isUpdating) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 50, this);
            isUpdating = true;
        }
    }

    public void GPSStop () {
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
        if (coordenadasGPS != null) {
            coordenadasGPS[0] = location.getLatitude();
            coordenadasGPS[1] = location.getLongitude();
        }

        Toast.makeText(context, "Location in:\nLat: " + location.getLatitude() + "\nLong: "
                + location.getLongitude(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
}
