package com.danielsolawa.locationapp.utils;

import android.util.Log;

import com.danielsolawa.locationapp.model.WeatherData;

import java.util.List;

/**
 * Created by NeverForgive on 2017-09-22.
 */

public class AlertPriority {


    public static final String TAG = AlertPriority.class.getSimpleName();

    private enum Priority{
        cloud, rain, snow, thunderstorm
    }


    public static WeatherData getHighestPriorityCondition(List<WeatherData> weatherData){
        WeatherData weatherDataToNotify = new WeatherData();
        for(int i = 0; i < weatherData.size(); i++){
            if(i < 1){
                weatherDataToNotify = weatherData.get(i);
                continue;
            }

            int currentPriority = getPriority(weatherData.get(i).getDescription());
            int lastPriority = getPriority(weatherData.get(i - 1).getDescription());

            if(currentPriority > lastPriority){
                weatherDataToNotify = weatherData.get(i);
            }

        }


        return weatherDataToNotify;
    }


    private static int getPriority(String condition){
        if(condition.contains(Priority.thunderstorm.toString())){
            return 4;
        }else if(condition.contains(Priority.snow.toString())){
            return 3;
        }else if(condition.contains(Priority.rain.toString())){
            return 2;
        }else if(condition.contains(Priority.cloud.toString())){
            return 1;
        }

       return 5;
    }

}
