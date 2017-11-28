package com.danielsolawa.locationapp;


import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.activeandroid.app.Application;
import com.activeandroid.query.Select;
import com.danielsolawa.locationapp.model.Locality;
import com.danielsolawa.locationapp.utils.Constants;
import com.danielsolawa.locationapp.utils.Localization;

/**
 * Created by NeverForgive on 2017-08-31.
 */

public class WeatherApp extends Application {

    private Localization localization;

    @Override
    public void onCreate() {
        super.onCreate();
        localization = new Localization(getApplicationContext());

    }

    public Locality loadLastLocationFromPreferences(){
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String lastLocation = preferences.getString(Constants.LAST_LOCATION, "");


        return getLocality(lastLocation);
    }

    public String getNextAlertDate(){
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String date = preferences.getString("ALERT_DATE", "");

        return date;
    }

    public void saveAlertDate(String date){
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("ALERT_DATE", date);
        editor.apply();
    }

    public void saveLastLocation(Locality localityToSave){
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.LAST_LOCATION, localityToSave.getName());
        editor.apply();

        Locality locality = getLocality(localityToSave.getName());

        if(locality == null){
            localityToSave.save();
        }


    }

    private Locality getLocality(String lastLocation) {
        return new Select()
                .from(Locality.class)
                .where("name = ?", lastLocation)
                .executeSingle();
    }

    public Localization getLocalization() {
        return localization;
    }
}
