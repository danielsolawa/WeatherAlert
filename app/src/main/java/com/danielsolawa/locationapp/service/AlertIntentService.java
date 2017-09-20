package com.danielsolawa.locationapp.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.danielsolawa.locationapp.R;
import com.danielsolawa.locationapp.activity.HomeActivity;
import com.danielsolawa.locationapp.client.OpenWeatherRestClient;
import com.danielsolawa.locationapp.model.Alert;
import com.danielsolawa.locationapp.model.Locality;
import com.danielsolawa.locationapp.model.WeatherData;
import com.danielsolawa.locationapp.utils.Constants;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

/**
 * Created by NeverForgive on 2017-09-07.
 */

public class AlertIntentService extends IntentService {

    private static final String TAG = AlertIntentService.class.getSimpleName();
    public static final int ID = 1;
    private Calendar cal;
    private String dateAsString;
    private Locality locality;

    public AlertIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        initCalendar();
        locality = fetchLocationData(getLastLocation());
        String url = OpenWeatherRestClient.generateUrl(locality.getLatitude(),
                locality.getLongitude(),
                OpenWeatherRestClient.QueryType.forecast);
        fetchForecast(url);


    }

    // sets tomorrow date
    private void initCalendar() {
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
        cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        dateAsString = format.format(cal.getTime());

    }


    private void fetchForecast(String url) {

        OpenWeatherRestClient.getSynchronous(url, null, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                List<WeatherData> weatherDataList = new ArrayList<>();
                try {
                    JSONArray forecastList = response.getJSONArray("list");
                    for(int i = 0; i < forecastList.length(); i++){
                        JSONObject listObject = forecastList.getJSONObject(i);
                        JSONObject weatherObject =
                                listObject.getJSONArray("weather").getJSONObject(0);
                        JSONObject mainObject = listObject.getJSONObject("main");
                        String date = listObject.getString("dt_txt");
                        if(isDateEqual(date)){
                            String description = weatherObject.getString("description");
                            String tempIcon = weatherObject.getString("icon");
                            String icon = "i" + tempIcon.replace("n", "d");
                            double temperature = mainObject.getDouble("temp");
                            double pressure = mainObject.getDouble("pressure");
                            double windSpeed = listObject.getJSONObject("wind").getDouble("speed");

                            WeatherData weatherData = new WeatherData();
                            weatherData.setDescription(description);
                            weatherData.setDate(date);
                            weatherData.setIcon(icon);
                            weatherData.setTemp(temperature);
                            weatherData.setPressure(pressure);
                            weatherData.setWindSpeed(windSpeed);
                            weatherData.setLocality(locality);

                            weatherDataList.add(weatherData);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                saveWeatherData(weatherDataList);
                List<WeatherData> fullFilledConditions =  filterMatchingConditions(weatherDataList);

                if(fullFilledConditions.size() > 0){
                    sendNotifications(fullFilledConditions);
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG, "failure ");
            }
        });
    }

    private void saveWeatherData(List<WeatherData> weatherDataList) {
        clearWeatherData();
        ActiveAndroid.beginTransaction();
        try {
            for(WeatherData w : weatherDataList){
                w.save();
            }
            ActiveAndroid.setTransactionSuccessful();
        }finally {
            ActiveAndroid.endTransaction();
        }


    }

    private void clearWeatherData() {
        new Delete().from(WeatherData.class).execute();
    }


    private void sendNotifications(List<WeatherData> fullFilledConditions) {
        WeatherData weather = fullFilledConditions.get(0);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0, intent, PendingIntent.FLAG_CANCEL_CURRENT);


            Notification.Builder builder = new Notification.Builder(getApplicationContext());
            int imgId = getResources()
                    .getIdentifier("com.danielsolawa.locationapp:drawable/"
                                    + weather.getIcon()
                            , null, null);
            Bitmap largeIcon = generateBitmap(imgId);


            builder.setContentTitle("Weather Alert!");
            builder.setContentText(weather.getDescription() +"\n" + "starts at " + weather.getDate());
            builder.setLargeIcon(largeIcon);
            builder.setSmallIcon(imgId);
            builder.setAutoCancel(true);
            builder.setContentIntent(pendingIntent);

            Notification notification = builder.build();
            notification.defaults |= Notification.DEFAULT_SOUND;
            notificationManager.notify(ID, notification);


    }

    private Bitmap generateBitmap(int imgId){
        Resources res = getApplicationContext().getResources();
        BitmapDrawable largeIcon = (BitmapDrawable)
                res.getDrawable(imgId, getTheme());
        Bitmap weatherIcon = largeIcon.getBitmap();


        int height = (int) res.getDimension(android.R.dimen.notification_large_icon_height);
        int width = (int) res.getDimension(android.R.dimen.notification_large_icon_width);
        weatherIcon = Bitmap.createScaledBitmap(weatherIcon, width, height, false);


        return weatherIcon;
    }


    private List<WeatherData> filterMatchingConditions(List<WeatherData> weatherDataList) {
        List<Alert> alerts = getAlerts();
        List<WeatherData> alertsToNotify = new ArrayList<>();
        for(int i = 0; i < alerts.size(); i++){
            String weatherCondition = alerts.get(i).getWeatherCondition();
            for(int j = 0; j < weatherDataList.size(); j++){
                String currentWeather = weatherDataList.get(j).getDescription();
                String forecastDate = weatherDataList.get(j).getDate();
                if(currentWeather.contains(weatherCondition) && forecastDate.contains(dateAsString)){
                    alertsToNotify.add(weatherDataList.get(j));
                }
            }
        }

        return alertsToNotify;

    }


    private List<Alert> getAlerts(){
        List<Alert> alerts = new Select()
                .from(Alert.class)
                .where("locality = ?", locality.getId())
                .execute();

        return alerts;
    }


    private Locality fetchLocationData(String lastLocation){
       Locality locality = new Select()
                .from(Locality.class)
                .where("name = ?", lastLocation)
                .executeSingle();

        return locality;
    }

    private String getLastLocation() {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());

        return preferences.getString(Constants.LAST_LOCATION, "");
    }


    private boolean isDateEqual(String date){
        String pattern = "yyyy-MM-dd hh:mm:ss";
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
        Calendar compareDate = Calendar.getInstance();

        try {
            compareDate.setTime(format.parse(date));
        } catch (ParseException e) {
            Log.d(TAG, e.getMessage());
        }


        int dayToCompare = compareDate.get(Calendar.DAY_OF_MONTH);
        int tomorrow = cal.get(Calendar.DAY_OF_MONTH);
        int today = cal.get(Calendar.DAY_OF_MONTH) - 1;



        if(today == dayToCompare || tomorrow == dayToCompare){
            return true;
        }


        return false;
    }
}
