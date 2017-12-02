package com.danielsolawa.locationapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.activeandroid.query.Select;
import com.danielsolawa.locationapp.model.Locality;

/**
 * Created by NeverForgive on 2017-12-02.
 */

public final class AppManager {

    private static volatile AppManager instance = null;
    private Localization localization;
    private Context ctx;

    private AppManager(Context ctx){
        this.ctx = ctx;
        localization = new Localization(ctx);
    }

    public static AppManager getInstance(Context ctx){
        if(instance == null){
            synchronized (AppManager.class){
                if(instance == null){
                    instance = new AppManager(ctx);
                }
            }
        }

        return instance;
    }

    public void saveLastLocation(Locality localityToSave){
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.LAST_LOCATION, localityToSave.getName());
        editor.apply();

        Locality locality = getLocality(localityToSave.getName());

        if(locality == null){
            localityToSave.save();
        }


    }

    public Locality loadLastLocationFromPreferences(){
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(ctx);
        String lastLocation = preferences.getString(Constants.LAST_LOCATION, "");


        return getLocality(lastLocation);
    }

    private Locality getLocality(String lastLocation) {
        return new Select()
                .from(Locality.class)
                .where("name = ?", lastLocation)
                .executeSingle();
    }




    // Getters & Setters
    public Localization getLocalization() {
        return localization;
    }
}
