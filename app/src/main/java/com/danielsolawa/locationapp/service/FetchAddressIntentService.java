package com.danielsolawa.locationapp.service;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.os.ResultReceiver;
import android.util.Log;

import com.danielsolawa.locationapp.R;
import com.danielsolawa.locationapp.model.LocationInfo;
import com.danielsolawa.locationapp.utils.Constants;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by NeverForgive on 2017-08-22.
 */

public class FetchAddressIntentService extends IntentService {

    private ResultReceiver mReceiver;
    private static final String TAG = FetchAddressIntentService.class.getSimpleName();


    public FetchAddressIntentService() {
        super(FetchAddressIntentService.class.getSimpleName());

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String errorMessage = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());


        mReceiver = intent.getParcelableExtra(Constants.RECEIVER);
        Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);

        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1);
        } catch (IOException e) {
            errorMessage = getString(R.string.service_not_available);
            Log.d(TAG, errorMessage, e);
        }catch (IllegalArgumentException e){
            errorMessage =  getString(R.string.invalid_long_lat);
            Log.d(TAG, errorMessage + "." +
                    "Latitude: " + location.getLatitude() + ", " +
                    "Longitude: " + location.getLongitude(),
                    e);

        }

        if(addresses == null || addresses.size() == 0){
            if(errorMessage.isEmpty()){
                errorMessage = getString(R.string.address_not_found);
                Log.d(TAG, errorMessage);
            }
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
        }else{
            Address address = addresses.get(0);
            LocationInfo locationInfo = new LocationInfo(address.getLocality(),
                    location.getLatitude(), location.getLongitude(), address.getAdminArea(),
                    address.getSubAdminArea(), address.getPostalCode(), address.getCountryName());

            StringBuilder builder = new StringBuilder();
            builder.append("miejscowość: " + address.getLocality() + "\n");
            builder.append("kod pocztowy: " + address.getPostalCode() + "\n");
            builder.append("powiat: " + address.getSubAdminArea() + "\n");
            builder.append("województwo: " + address.getAdminArea() + "\n");
            builder.append("kraj: " + address.getCountryName() + "\n");


            deliverResultToReceiver(Constants.SUCCESS_RESULT, builder.toString(),
                    locationInfo);
        }



    }

    private void deliverResultToReceiver(int result, String msg, LocationInfo locationInfo) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, msg);
        bundle.putParcelable(Constants.RESULT_DATA_OBJECT, locationInfo);
        mReceiver.send(result, bundle);
    }

    private void deliverResultToReceiver(int result, String msg) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, msg);
        mReceiver.send(result, bundle);
    }
}
