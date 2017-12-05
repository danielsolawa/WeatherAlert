package com.danielsolawa.locationapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


import com.activeandroid.query.Select;
import com.danielsolawa.locationapp.model.Locality;


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

            if(i < intervals.length - 1){
                multiplier += 1;
            }else{
                multiplier += 2;
            }

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

    public int getCurrentIntervalIndex(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        int currentIntervalIndex = preferences.getInt(CURRENT_INTERVAL, 3);

        return currentIntervalIndex;
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
        boolean alarmState = preferences.getBoolean(Constants.ALARM_STATE, false);


        return alarmState;
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
