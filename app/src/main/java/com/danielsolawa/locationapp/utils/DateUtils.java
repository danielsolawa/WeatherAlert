package com.danielsolawa.locationapp.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by NeverForgive on 2017-09-25.
 */

public class DateUtils {


    private static final String DEFAULT_PATTERN = "dd/MM/yyyy HH:mm";
    private static final String LOCALIZED_PATTERN = "EEEE HH:mm";
    private static final String INPUT_PATTERN = "yyyy-MM-dd HH:mm:ss";



    public static String getCurrentDate(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat();
        dateFormat.applyPattern(DEFAULT_PATTERN);


        return dateFormat.format(cal.getTime());
    }

    public static int getCurrentHour(){
        Calendar cal = Calendar.getInstance();

        return cal.get(Calendar.HOUR_OF_DAY);
    }


    public static String getLocalizedDate(String date){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance();

        dateFormat.applyPattern(INPUT_PATTERN);

        try {
            cal.setTime(dateFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }



        dateFormat.applyLocalizedPattern(LOCALIZED_PATTERN);

        return dateFormat.format(cal.getTime());
    }

    public static String getLocalizedDateNewLine(String date){
        String[] arr = getLocalizedDate(date).split(" ");
        return new StringBuilder().append(arr[1]).toString();
    }

    public static String getDateOnly(String date){
        String[] arr = date.split(" ");
        return new StringBuilder().append(arr[0]).toString();
    }

}
