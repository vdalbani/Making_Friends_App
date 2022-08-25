package com.example.victor_friends.helpers;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;
import java.util.Locale;

/**
 * Making_Friends created by vitto
 */
public class LocationHelper {

    private final String TAG = this.getClass().getCanonicalName();
    public boolean locationPermissionGranted = false;
    public final int REQUEST_CODE_LOCATION = 101;
    private static final LocationHelper singletonInstance = new LocationHelper();
    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient = null;

//    mutable data
    MutableLiveData<Location> myLocation = new MutableLiveData<>();

    public static LocationHelper getInstance() {
        return singletonInstance;
    }

    private LocationHelper() {
        //Initiate location helper
        this.locationRequest = new LocationRequest();
        //set interval and priority
        this.locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        this.locationRequest.setInterval(10000); //10 secs

    }


    //Check if the permission was granted or not by the user
    public void checkPermissions(Context context) {
        this.locationPermissionGranted =
                (PackageManager.PERMISSION_GRANTED ==
                        (ContextCompat.checkSelfPermission(context.getApplicationContext(),
                                Manifest.permission.ACCESS_FINE_LOCATION)));

        Log.d(TAG, "LocationPermissionGranted: " + this.locationPermissionGranted);

        if (!this.locationPermissionGranted) {
            requestLocationPermission(context);
        }
    }


    //    Request Location permission
    public void requestLocationPermission(Context context) {
        ActivityCompat.requestPermissions((Activity) context,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                this.REQUEST_CODE_LOCATION);
    }

    // define the fused location
    public FusedLocationProviderClient getFusedLocationProviderClient(Context context) {
        if (this.fusedLocationProviderClient == null) {
            this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        }
        return this.fusedLocationProviderClient;
    }

    @SuppressLint("MissingPermission")
    public MutableLiveData<Location> getLastLocation(Context context) {
        Log.d(TAG, "getLastLocation: LAST LOCATION INITIATED");
        if (this.locationPermissionGranted) {

            try {
                this.getFusedLocationProviderClient(context)
                        .getLastLocation()
                        .addOnSuccessListener(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    myLocation.setValue(location);
                                    Log.d(TAG, "onSuccess: LAST LOCATION -- Latitude: " + myLocation.getValue().getLatitude()
                                            + " Longitude: " + myLocation.getValue().getLongitude());

                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "onFailure: Exception while accessing location" + e.getLocalizedMessage());
                            }
                        });

            } catch (Exception e) {
                Log.e(TAG, "getLastLocation: Exception while accessing last location" + e.getLocalizedMessage());
                return null;
            }

            return this.myLocation;

        } else {
            Log.e(TAG, "getLastLocation: certain features wont be available because dont have access to location");
            return null;
        }

    }

    //Define request updates
    @SuppressLint("MissingPermission") //gives an error, needs to supressLint
    public void requestLocationUpdates(Context context, LocationCallback locationCallback){
        if(this.locationPermissionGranted){
            try{
                this.getFusedLocationProviderClient(context)
                        .requestLocationUpdates(this.locationRequest, locationCallback, Looper.getMainLooper());
            }catch(Exception e){
                Log.e(TAG, "requestLocationUpdates: Exception obtained while getting the updates" + e.getLocalizedMessage() );
            }
        }
    }

    public void stopLocationUpdates(Context context, LocationCallback locationCallback){
        try{
            this.getFusedLocationProviderClient(context).removeLocationUpdates(locationCallback);
        }catch(Exception e){
            Log.e(TAG, "requestLocationUpdates: EXCEPTION WHILE GETTING LOCATION UPDATES" + e.getLocalizedMessage() );
        }
    }

    //convert postal address to location coordinates (lat & lng)
    public LatLng performReverseGeocoding(Context context, String address){
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        try{
            List<Address> locationList = geocoder.getFromLocationName(address, 1);
            LatLng obtainedLocation = new LatLng(locationList.get(0).getLatitude(), locationList.get(0).getLongitude());
            Log.d(TAG, "reverseGeocoding: ObtainedLocation : " + obtainedLocation.toString());
            return obtainedLocation;

        }catch (Exception ex){
            Log.e(TAG, "reverseGeocoding: Couldn't get the coordinates for given address " + ex.getLocalizedMessage() );
        }

        return null;
    }

}

