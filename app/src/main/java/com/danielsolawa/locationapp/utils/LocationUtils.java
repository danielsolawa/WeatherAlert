package com.danielsolawa.locationapp.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.ResultReceiver;
import android.util.Log;


import com.danielsolawa.locationapp.model.LocationInfo;
import com.danielsolawa.locationapp.service.FetchAddressIntentService;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

/**
 * Created by NeverForgive on 2017-08-24.
 */

public class LocationUtils {

    private static final String TAG = LocationUtils.class.getSimpleName();
    public static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private boolean isPermissionGranted = true;

    private Activity activity;
    private LocationResultHandler locationResultHandler;

    //Location objects
    private FusedLocationProviderClient client;
    private AddressResultReceiver resultReceiver;
    private Location lastLocation;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest = new LocationRequest();


    public LocationUtils(Activity activity, LocationResultHandler locationResultHandler) {
        this.activity = activity;
        this.locationResultHandler = locationResultHandler;

        checkLocationPermission();
        checkLocationSettings();
        start();

    }




    private void start() {
        resultReceiver = new AddressResultReceiver(new Handler());
        client = LocationServices.getFusedLocationProviderClient(activity);

        setupLocationCallback();

    }

    private void setupLocationCallback() {

        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for(Location l : locationResult.getLocations()){
                    lastLocation = l;

                }
                startIntentService();
            }
        };
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {


            } else {


                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);

            }
        }

    }

    private void checkLocationSettings() {

        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(activity);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());
        task.addOnSuccessListener(activity, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                //toast here
            }
        });

        task.addOnFailureListener(activity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                int statusCode = ((ApiException) e).getStatusCode();
                switch(statusCode){
                    case CommonStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            ResolvableApiException exception = (ResolvableApiException) e;
                            exception.startResolutionForResult(activity,
                                    REQUEST_CHECK_SETTINGS);

                        } catch (IntentSender.SendIntentException sendEx) {

                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });



    }
 /*
    public void fetchLastLocation() throws SecurityException{

        client.getLastLocation().addOnSuccessListener(activity, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    lastLocation = location;
                    startIntentService();
                }else{
                    checkLocationSettings();
                    updateLocation();
                }

            }


        });

        client.getLastLocation().addOnFailureListener(activity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "FAILURE <==============================");
            }
        });

    }*/

    public void updateLocation() throws SecurityException{
        client.requestLocationUpdates(locationRequest,
                locationCallback, null);

    }

    public void stopLocationUpdates(){
        client.removeLocationUpdates(locationCallback);
    }

    private void startIntentService() {
        Intent intent = new Intent(activity, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, lastLocation);
        activity.startService(intent);
    }

    class AddressResultReceiver extends ResultReceiver{


        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
           stopLocationUpdates();
            String addressOutPut = resultData.getString(Constants.RESULT_DATA_KEY);
            LocationInfo locationInfo = resultData.getParcelable(Constants.RESULT_DATA_OBJECT);

            Log.d(TAG, "->>>>>>>>>>>>>>>>>>>>>>>> location");
            if(locationInfo != null)
                locationResultHandler.handleLocationResult(addressOutPut, locationInfo);


        }
    }





}
