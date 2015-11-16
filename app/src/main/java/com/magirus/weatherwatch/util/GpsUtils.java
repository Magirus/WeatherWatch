package com.magirus.weatherwatch.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.widget.Toast;

import com.magirus.weatherwatch.R;


public class GpsUtils {

    private final Context context;
    private LocationListener locationListener;
    public static int GPS_INTERVAL_TIME_INTENSE = 0;
    public static int GPS_INTERVAL_TIME_NORMAL = 600000; // 1 hour
    public static int GPS_DISTANCE_INTENSE = 0;
    public static int GPS_DISTANCE_NORMAL = 10000; // 10 km

    public GpsUtils(Context context) {
        this.context = context;
    }

    public LocationManager getLocationManager() {
        return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public String getBestLocationProvider() {
        Criteria criteria = new Criteria();
        return getLocationManager().getBestProvider(criteria, false);
    }

    public boolean isGpsEnabled() {
        LocationManager manager = getLocationManager();
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public Location getUserLocation() {
        LocationManager manager = getLocationManager();
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            try {
                String provider = getBestLocationProvider();
                return manager.getLastKnownLocation(provider);
            } catch (SecurityException e) {
                e.printStackTrace();
                Toast.makeText(context, R.string.permission_required, Toast.LENGTH_LONG);
            }
        }
        return null;
    }

    public void setUserLocationRequestInterval(final LocationListener listener, final int intervalTime, final int distance) {
        this.locationListener = listener;
        try {
            getLocationManager().requestLocationUpdates(getBestLocationProvider(), intervalTime, distance, locationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
            Toast.makeText(context, context.getString(R.string.permission_required), Toast.LENGTH_LONG);
        }
    }

    public void removeUserLocationRequestInterval() {
        try {
            getLocationManager().removeUpdates(locationListener);
            locationListener = null;
        } catch (SecurityException e) {
            e.printStackTrace();
            Toast.makeText(context, context.getString(R.string.permission_required), Toast.LENGTH_LONG);
        }
    }

    public boolean isLocationListenerExists() {
        return locationListener != null;
    }
}
