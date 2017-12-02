package com.danielsolawa.locationapp.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import com.activeandroid.query.Select;
import com.danielsolawa.locationapp.R;
import com.danielsolawa.locationapp.WeatherApp;
import com.danielsolawa.locationapp.activity.HomeActivity;
import com.danielsolawa.locationapp.client.OpenWeatherRestClient;
import com.danielsolawa.locationapp.model.Alert;
import com.danielsolawa.locationapp.model.Locality;
import com.danielsolawa.locationapp.model.WeatherData;
import com.danielsolawa.locationapp.utils.AlertPriority;
import com.danielsolawa.locationapp.utils.DateUtils;
import com.danielsolawa.locationapp.utils.Localization;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

/**
 * Created by NeverForgive on 2017-12-01.
 */

public class AlertJobService extends JobService implements Runnable{

    private static final String TAG = AlertJobService.class.getSimpleName();
    private static final int NOTIFICATION_ID = 1;

    //forecast constants
    private static final String DRAWABLE = "com.danielsolawa.locationapp:drawable/";
    private static final String LIST = "list";
    private static final String WEATHER = "weather";
    private static final String MAIN = "main";
    private static final String DATE = "dt_txt";
    private static final String DESCRIPTION = "description";
    private static final String ICON = "icon";
    private static final String TEMPERATURE = "temp";
    private static final String PRESSURE = "pressure";
    private static final String WIND = "wind";
    private static final String SPEED = "speed";
    private static final String NIGHT = "n";
    private static final String DAY = "d";


    private WeatherApp weatherApp;
    private Calendar cal;
    private Locality locality;
    private Localization localization;
    private List<Alert> alerts;

    private JobParameters params;




    @Override
    public boolean onStartJob(JobParameters params) {
        this.params = params;
        initialize();
        new Thread(this).start();

        return true;
    }

    private void initialize() {
        weatherApp = (WeatherApp) getApplication();
        localization = new Localization(getApplicationContext());
        locality = weatherApp.loadLastLocationFromPreferences();
        alerts = getAlerts();


        setupCalendar();
    }

    private void setupCalendar() {
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
        cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        //dateAsString = format.format(cal.getTime());
    }


    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    @Override
    public void run() {
        fetchForecast();
        jobFinished(params, false);
    }

    private void fetchForecast() {
        if(alerts.size() <= 0)
            return;



        String url = OpenWeatherRestClient.generateUrl(locality.getLatitude(),
                locality.getLongitude(),
                OpenWeatherRestClient.QueryType.forecast);


        OpenWeatherRestClient.getSynchronous(url, null, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
               List<WeatherData> weatherDataList = new ArrayList<WeatherData>();
               try{
                   JSONArray forecastList = response.getJSONArray(LIST);

                   for(int i = 0; i < forecastList.length(); i++){
                       JSONObject listObject = forecastList.getJSONObject(i);
                       JSONObject weatherObject =
                               listObject.getJSONArray(WEATHER).getJSONObject(0);
                       JSONObject targetObject = listObject.getJSONObject(MAIN);

                       String forecastDate = listObject.getString(DATE);

                       if(isDateEqual(forecastDate)){
                           String description = weatherObject.getString(DESCRIPTION);
                           String tempIcon = weatherObject.getString(ICON);
                           String icon = "i" + tempIcon.replace(NIGHT, DAY);
                           double temperature = targetObject.getDouble(TEMPERATURE);
                           double pressure = targetObject.getDouble(PRESSURE);
                           double windSpeed = listObject.getJSONObject(WIND).getDouble(SPEED);

                           WeatherData weatherData = createWeatherData(
                                   description,
                                   icon,
                                   temperature,
                                   pressure,
                                   windSpeed,
                                   forecastDate);

                            weatherDataList.add(weatherData);
                       }
                   }
               }catch (JSONException e){
                   e.printStackTrace();
               }
               
               List<WeatherData> fullFilledConditions = matchConditions(weatherDataList);
               prepareNotification(fullFilledConditions);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG, "Fetch forecast failed, status code " + statusCode);
            }
        });
    }

    private void prepareNotification(List<WeatherData> forecastList) {
        if(forecastList.size() > 0){
            WeatherData weatherData = AlertPriority.getHighestPriorityCondition(forecastList);

            sendNotification(weatherData);

        }
    }

    private void sendNotification(WeatherData weatherData) {
        Notification notification = generateNotification(weatherData);
        notification.defaults |= Notification.DEFAULT_SOUND;

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(NOTIFICATION_ID, notification);

    }

    private Notification generateNotification(WeatherData weatherData) {


        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        int imgId = getResources()
                .getIdentifier(DRAWABLE + weatherData.getIcon(),
                null,
                null);
        Bitmap largeIcon = genIcon(imgId);

        if(Locale.getDefault() != Locale.ENGLISH){
            weatherData.setDescription(
                    localization.localizeWeatherDataString(weatherData.getDescription()));
        }

        builder.setContentTitle(weatherData.getDescription() + " " +
                DateUtils.getLocalizedDate(weatherData.getDate()) );
        builder.setContentText(getString(R.string.temperature) + " " +
                weatherData.getTemp() + "\u00b0C");
        builder.setLargeIcon(largeIcon);
        builder.setSmallIcon(imgId);
        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);
        
        
        return builder.build();
    }

    private Bitmap genIcon(int imgId) {
        Resources res = getApplicationContext().getResources();
        BitmapDrawable largeIcon = (BitmapDrawable)
                res.getDrawable(imgId, getTheme());
        Bitmap weatherIcon = largeIcon.getBitmap();


        int height = (int) res.getDimension(android.R.dimen.notification_large_icon_height);
        int width = (int) res.getDimension(android.R.dimen.notification_large_icon_width);
        weatherIcon = Bitmap.createScaledBitmap(weatherIcon, width, height, false);


        return weatherIcon;
    }

    private List<WeatherData> matchConditions(List<WeatherData> weatherDataList) {
        List<WeatherData> alertsToNotify = new ArrayList<>();
        for(int i = 0; i < alerts.size(); i++){
            String weatherCondition = alerts.get(i).getWeatherCondition();
            for(int j = 0; j < weatherDataList.size(); j++){
                String forecastWeather = weatherDataList.get(j).getDescription();
                if(forecastWeather.contains(weatherCondition)){
                    alertsToNotify.add(weatherDataList.get(j));
                }
            }
        }

        return alertsToNotify;

    }

    // generates new weather data object
    private WeatherData createWeatherData(String description,
                                          String icon,
                                          double temperature,
                                          double pressure,
                                          double windSpeed,
                                          String forecastDate) {

        WeatherData weatherData = new WeatherData();
        weatherData.setDescription(description);
        weatherData.setDate(forecastDate);
        weatherData.setIcon(icon);
        weatherData.setPressure(pressure);
        weatherData.setTemp(temperature);
        weatherData.setWindSpeed(windSpeed);
        weatherData.setLocality(locality);

        return  weatherData;
    }

    private boolean isDateEqual(String forecastDate) {
        String pattern = "yyyy-MM-dd hh:mm:ss";
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
        Calendar compareDate = Calendar.getInstance();

        try{
            compareDate.setTime(format.parse(forecastDate));
        }catch (ParseException e){
            Log.d(TAG, e.getMessage());
        }

        int dayToComapare = compareDate.get(Calendar.DAY_OF_MONTH);
        int tomorrow = cal.get(Calendar.DAY_OF_MONTH);

        boolean isEqual = dayToComapare == tomorrow ? true : false;

        return  isEqual;

    }

    public List<Alert> getAlerts() {
        List<Alert> alerts = new Select()
                .from(Alert.class)
                .where("locality = ?", locality.getId())
                .execute();

        return alerts;
    }
}
