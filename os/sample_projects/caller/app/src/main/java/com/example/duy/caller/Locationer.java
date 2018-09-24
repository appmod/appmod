package com.example.duy.caller;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import static android.content.Context.LOCATION_SERVICE;


public class Locationer implements LocationListener {


    private Double latitude = null, longitude = null;

    public Locationer(MainActivity mainActivity) {
        LocationManager locationManager = null;
        if (ActivityCompat.checkSelfPermission
                (mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission
                        (mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mainActivity.requestPermissions(new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 1);

        } else {

            // enable location buttons

            // fetch last location if any from provider - GPS.
            locationManager = (LocationManager) mainActivity.getSystemService(LOCATION_SERVICE);
            try {
                Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                //if last known location is not available
                if (loc == null)
                {

                    LocationListener locationListener = this;

                    //update location every 10sec in 500m radius with both provider GPS and Network.
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 500, locationListener);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 500, locationListener);
                } else {
                    //do something with last known location.
                    // getting location of user
                    latitude = loc.getLatitude();
                    longitude = loc.getLongitude();
                }
            }catch (Exception e){

            }

        }
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}
