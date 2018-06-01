package com.danielsolawa.locationapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


import com.activeandroid.query.Select;
import com.danielsolawa.locationapp.model.Locality;


import static com.danielsolawa.locationapp.utils.Constants.CURRENT_FORECAST;
import static com.danielsolawa.locationapp.utils.Constants.CURRENT_INTERVAL;

/**
 * Created by NeverForgive on 2017-12-02.
 */

public final class AppManager {

    private static volatile AppManager instance = null;
    private Localization localization;
    private int [] intervals;
    private Context ctx;

    private AppManager(Context ctx){
        this.ctx = ctx;
        localization = new Localization(ctx);
        initIntervals();
    }

    private void initIntervals() {
        intervals = new int[5];
        for(int i = 0; i < intervals.length; i++){
            if(i == 0){
                intervals[i] = Constants.ONE_HOUR;
                continue;
            }
            int multiplier = i;

            multiplier = intervals.length - 1 > i ? multiplier + 1 : multiplier + 2;

            intervals[i] = Constants.ONE_HOUR * multiplier;
        }



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

    public int getCurrentForecast(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getInt(CURRENT_FORECAST, 1);
    }


    public void saveCurrentForecast(int value){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(CURRENT_FORECAST, value);
        editor.apply();
    }

    public int getCurrentIntervalIndex(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);

        return preferences.getInt(CURRENT_INTERVAL, 3);
    }


    public void saveCurrentIntervalIndex(int index){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(Constants.CURRENT_INTERVAL, index);
        editor.apply();

    }

    public int getCurrentIntervalInMillis(){
        int currentIntervalIndex = getCurrentIntervalIndex();

        return intervals[currentIntervalIndex];
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


    public boolean getAlarmState() {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getBoolean(Constants.ALARM_STATE, false);
    }


    public void setAlarmState(boolean alarmState){
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(Constants.ALARM_STATE, alarmState);
        editor.apply();
    }




    // Getters & Setters
    public Localization getLocalization() {
        return localization;
    }


    public int[] getIntervals() {
        return intervals;
    }
}
