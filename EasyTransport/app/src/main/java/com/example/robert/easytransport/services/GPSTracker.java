package com.example.robert.easytransport.services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.example.robert.easytransport.data.SharedPrefs;

/**
 * Created by Robert on 11/22/2017.
 */

public class GPSTracker extends Service {

    private Location objectiveLocation;
    private static final String TAG = GPSTracker.class.getSimpleName();
    private static final String BROADCAST_ACTION = "com.example.robert.easytransport.services.GPSTracker";
    public LocationManager locationManager;
    public MyLocationListener listener;
    Intent intent;

    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service stared!");
        SharedPrefs.getsInstance(getApplicationContext()).setServiceRuning(true);
        objectiveLocation = new Location("");
        objectiveLocation.setLatitude(Double.parseDouble(intent.getStringExtra("Latitude")));
        objectiveLocation.setLongitude(Double.parseDouble(intent.getStringExtra("Longitude")));
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, listener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0, listener);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private class MyLocationListener implements LocationListener{
        @Override
        public void onLocationChanged(Location location) {
            if(isLocationCloseEnough(location, objectiveLocation)) {
                sendBroadcast(intent);
            }

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }

    private boolean isLocationCloseEnough(Location location, Location objectiveLocation) {
        if(location.distanceTo(objectiveLocation)<150){
            return true;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(listener);
    }
}
