package com.danielsolawa.locationapp.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.danielsolawa.locationapp.R;
import com.danielsolawa.locationapp.WeatherApp;
import com.danielsolawa.locationapp.adapter.HorizontalListViewFragment;
import com.danielsolawa.locationapp.client.OpenWeatherRestClient;
import com.danielsolawa.locationapp.model.Locality;
import com.danielsolawa.locationapp.model.LocationInfo;
import com.danielsolawa.locationapp.model.WeatherData;
import com.danielsolawa.locationapp.utils.Constants;
import com.danielsolawa.locationapp.utils.DateUtils;
import com.danielsolawa.locationapp.utils.Localization;
import com.danielsolawa.locationapp.utils.LocationResultHandler;
import com.danielsolawa.locationapp.utils.LocationUtils;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = HomeActivity.class.getSimpleName();

    private LocationUtils locationUtils;
    private WeatherApp application;

    //views
    private LinearLayout progressLayout;
    private LinearLayout weatherLayout;
    private ImageView weatherIcon;
    private TextView localityTv;
    private TextView temperatureTv;
    private TextView descriptionTv;
    private TextView pressureTv;
    private TextView visibilityTv;
    private TextView windSpeedTv;
    private TextView dateTv;
    private Locality lastLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initialize();
        fetchLastLocation();



    }


    private void initialize() {
        application = (WeatherApp) getApplication();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressLayout = (LinearLayout) findViewById(R.id.progress_layout);
        weatherLayout = (LinearLayout) findViewById(R.id.weather_layout);

        dateTv = (TextView) findViewById(R.id.date_tv);
        weatherIcon = (ImageView) findViewById(R.id.weather_image);
        localityTv = (TextView) findViewById(R.id.locality_tv);
        temperatureTv = (TextView) findViewById(R.id.temperature_tv);
        descriptionTv = (TextView) findViewById(R.id.desc_tv);
        pressureTv = (TextView) findViewById(R.id.pressure_tv);
        visibilityTv = (TextView) findViewById(R.id.visibility_tv);
        windSpeedTv = (TextView) findViewById(R.id.wind_speed_tv);


    }

    private void fetchLastLocation() {

        lastLocation = application.loadLastLocationFromPreferences();
            if(lastLocation != null){
                fetchCurrentWeather(lastLocation);
            }else{
                getCurrentLocation();
            }

    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_update:
                getCurrentLocation();
                return true;
            case R.id.show_condition:
                changeActivity(ConditionsActivity.class);
                return true;

        }



        return super.onOptionsItemSelected(item);
    }





    private void changeActivity(Class<?> clazz) {
        Intent intent = new Intent(getApplicationContext(), clazz);
        startActivity(intent);
    }


    private void getCurrentLocation() {
        Log.d(TAG, "locality -----> get curr" );
        locationUtils = new LocationUtils(this, new LocationResultHandler() {
            @Override
            public void handleLocationResult(String msg, LocationInfo locationInfo) {
                Locality locality = new Locality();
                locality.setName(locationInfo.getLocality());
                locality.setLatitude(locationInfo.getLatitude());
                locality.setLongitude(locationInfo.getLongitude());

                application.saveLastLocation(locality);



                createToast();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recreate();
                    }
                }, 2000);


            }
        });


        locationUtils.updateLocation();






    }





    private void fetchCurrentWeather(final Locality locality) {
        String url = OpenWeatherRestClient.generateUrl(locality.getLatitude(),
                locality.getLongitude(), OpenWeatherRestClient.QueryType.weather);

        OpenWeatherRestClient.get(url, null, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                WeatherData weatherData = new WeatherData();

                try {
                    JSONObject weatherObject = response.getJSONArray("weather").getJSONObject(0);
                    JSONObject mainObject = response.getJSONObject("main");
                    JSONObject windObject = response.getJSONObject("wind");

                    weatherData.setDescription(weatherObject.getString("description"));
                    weatherData.setIcon("i" + weatherObject.getString("icon"));
                    weatherData.setPressure(mainObject.getDouble("pressure"));
                    weatherData.setTemp(mainObject.getDouble("temp"));
                    weatherData.setVisibility(response.getDouble("visibility"));
                    weatherData.setWindSpeed(windObject.getDouble("speed"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                if(Locale.getDefault() != Locale.ENGLISH){
                    Localization loc = new Localization(getApplicationContext());
                    weatherData.setDescription(loc.localizeWeatherDataString(weatherData.getDescription()));
                }



                int imgId = getResources()
                        .getIdentifier("com.danielsolawa.locationapp:drawable/"
                                        + weatherData.getIcon()
                                , null, null);
                weatherIcon.setImageResource(imgId);
                localityTv.setText(locality.getName());
                temperatureTv.setText(String.format("%s \u00b0C",
                         weatherData.getTemp()));
                descriptionTv.setText(String.format("%s %s",
                        getString(R.string.description), weatherData.getDescription()));
                pressureTv.setText(String.format("%s %s hPa",
                        getString(R.string.pressure), weatherData.getPressure()));
                String visibility = formatVisibility(weatherData.getVisibility());
                visibilityTv.setText(String.format("%s %s km",
                        getString(R.string.visibility), visibility));
                String windSpeed = convertSpeed(weatherData.getWindSpeed());
                windSpeedTv.setText(String.format("%s %s km/h", getString(R.string.wind_speed),
                        windSpeed));
                dateTv.setText(DateUtils.getCurrentDate());


                fetchForecast(locality);


            }


        });

    }

    private void fetchForecast(final Locality locality) {
            String url = OpenWeatherRestClient.generateUrl(locality.getLatitude(),
                locality.getLongitude(),
                OpenWeatherRestClient.QueryType.forecast);

        OpenWeatherRestClient.get(url, null, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Locality locality = new Locality();
                locality.setName(locality.getName());
                locality.setLatitude(locality.getLatitude());
                locality.setLongitude(locality.getLongitude());
                Localization loc = new Localization(getApplicationContext());

                List<WeatherData> weatherForecastList = new ArrayList<>();
                try {
                    JSONArray forecastList = response.getJSONArray("list");
                    for(int i = 0; i < forecastList.length(); i++){
                        JSONObject listObject = forecastList.getJSONObject(i);
                        JSONObject weatherObject =
                                listObject.getJSONArray("weather").getJSONObject(0);
                        JSONObject mainObject = listObject.getJSONObject("main");
                        String date = listObject.getString("dt_txt");
                        String description = weatherObject.getString("description");
                        String tempIcon = weatherObject.getString("icon");
                        String icon = "i" + tempIcon.replace("n", "d");
                        double temperature = mainObject.getDouble("temp");
                        double pressure = mainObject.getDouble("pressure");
                        double windSpeed = listObject.getJSONObject("wind").getDouble("speed");

                        if(Locale.getDefault() != Locale.ENGLISH){
                            description = loc.localizeWeatherDataString(description);
                        }


                        WeatherData weatherData = new WeatherData();
                        weatherData.setDescription(description);
                        weatherData.setDate(date);
                        weatherData.setIcon(icon);
                        weatherData.setTemp(temperature);
                        weatherData.setPressure(pressure);
                        weatherData.setWindSpeed(windSpeed);
                        weatherData.setLocality(locality);

                        weatherForecastList.add(weatherData);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                fillListViewWithData(weatherForecastList);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG, "failure ");
            }
        });

        progressLayout.setVisibility(View.GONE);
        weatherLayout.setVisibility(View.VISIBLE);
    }



    private void fillListViewWithData(List<WeatherData> weatherForecastList) {
        if(weatherForecastList.size() > 0){
            FragmentManager fm = getSupportFragmentManager();
            Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);

            if(fragment == null){
                fragment = new HorizontalListViewFragment();
                Bundle args = new Bundle();

                args.putParcelable(Constants.WEATHER_DATA_LIST, Parcels.wrap(weatherForecastList));
                fragment.setArguments(args);
                fm.beginTransaction()
                        .add(R.id.fragmentContainer, fragment)
                        .commit();
            }

        }


    }



    private String formatVisibility(double visibility) {
        DecimalFormat dc = new DecimalFormat("###.##");

        return dc.format((visibility / 1000));
    }

    private String convertSpeed(double windSpeed) {
        double convertedSpeed = (windSpeed * 3600) / 1000;
        DecimalFormat dc = new DecimalFormat("###.##");


        return dc.format(convertedSpeed);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch(requestCode){
            case LocationUtils.MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                if(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    locationUtils.updateLocation();
                }else{
                    finishAndRemoveTask();
                }
                return;
            }
        }
    }



    public void createToast(){
        Toast.makeText(this, getString(R.string.updating_location_data), Toast.LENGTH_SHORT).show();
    }





}
