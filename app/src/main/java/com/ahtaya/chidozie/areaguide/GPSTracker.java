package com.ahtaya.chidozie.areaguide;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import static android.content.Context.LOCATION_SERVICE;

public class GPSTracker implements LocationListener {

    private final Activity mContext;

    private Location location; // location
    private double latitude; // latitude
    private double longitude; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60; // 1 minute

    //Class constructor
    GPSTracker(Activity context) {
        this.mContext = context;
        getLocation();
    }

    //Method to get Location
    private void getLocation() {
        try {
            //creates location from system service
            LocationManager locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            //Flag for GPS and Network
            boolean isGPSEnabled = false;
            boolean isNetworkEnabled = false;

            if (locationManager != null) {
                // getting GPS status
                isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                // getting network status
                isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            }


            if (!isGPSEnabled && !isNetworkEnabled) {
                //Location disabled, turn on GPS
                showSettingsAlert(mContext);
            } else {
                //Check for app Location permission
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    //No permission, Request permission, location request code is 123
                    ActivityCompat.requestPermissions(mContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
                    //exit getLocation
                    return;
                }
                //Permission exist, First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    //check success
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        //check success
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Function to get latitude
     */
    double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     */
    double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }
        // return longitude
        return longitude;
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     */
    private void showSettingsAlert(final Context context) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(R.string.gps_alert_title);
        alertDialog.setMessage(R.string.gps_alert_msg);
        alertDialog.setPositiveButton(R.string.gps_alert_positive, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //create seetings intent to open location settings
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });

        alertDialog.setNegativeButton(R.string.gps_alert_negative, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

}