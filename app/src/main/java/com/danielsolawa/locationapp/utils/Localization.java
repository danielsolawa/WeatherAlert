package com.danielsolawa.locationapp.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.danielsolawa.locationapp.R;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by NeverForgive on 2017-09-26.
 */

public class Localization {

    public static final String TAG = Localization.class.getSimpleName();
    private List<String> defaultWeatherDataStrings;
    private List<String> localizedWeatherDataStrings;
    private List<String> defaultAlertConditionsStrings;
    private List<String> localizedAlertConditionsStrings;



    public Localization(Context context) {
        if(isLocalized()){
            initList(context);
        }

    }

    private void initList(Context context) {
        defaultWeatherDataStrings =
                Arrays.asList(getLocalizedResources(context, Locale.ENGLISH)
                        .getStringArray(R.array.weather_conditions));
        localizedWeatherDataStrings =
                Arrays.asList(getLocalizedResources(context, Locale.getDefault())
                        .getStringArray(R.array.weather_conditions));

        defaultAlertConditionsStrings =
                Arrays.asList(getLocalizedResources(context, Locale.ENGLISH)
                        .getStringArray(R.array.weather_clouds));

        localizedAlertConditionsStrings =
                Arrays.asList(getLocalizedResources(context, Locale.getDefault())
                         .getStringArray(R.array.weather_clouds));
    }

    @NonNull
    Resources getLocalizedResources(Context context, Locale locale){
        Configuration conf = context.getResources().getConfiguration();
        conf = new Configuration(conf);
        conf.setLocale(locale);
        Context localizedContext = context.createConfigurationContext(conf);

        return localizedContext.getResources();
    }

    public String localizeWeatherDataString(String text){
        int index = 0;
        for(int i = 0; i < defaultWeatherDataStrings.size(); i++){
           if(defaultWeatherDataStrings.get(i).equals(text)){
               index = i;
           }

        }



        return localizedWeatherDataStrings.get(index);
    }


    public String localizeAlertConditionString(String text){
        int index = 0;
        for(int i = 0; i < defaultAlertConditionsStrings.size(); i++){
            if(defaultAlertConditionsStrings.get(i).equals(text)){
                index = i;
            }

        }


        return localizedAlertConditionsStrings.get(index);
    }


    public String reverseLocalizeAlertConditionString(String text){
        int index = 0;
        for(int i = 0; i < localizedAlertConditionsStrings.size(); i++){
            if(localizedAlertConditionsStrings.get(i).equals(text)){
                index = i;
            }

        }


        return defaultAlertConditionsStrings.get(index);
    }


    public boolean isLocalized(){

        return Locale.getDefault() != Locale.ENGLISH;
    }





}
