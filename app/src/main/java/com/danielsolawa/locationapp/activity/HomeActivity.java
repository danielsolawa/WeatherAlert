package com.danielsolawa.locationapp.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.danielsolawa.locationapp.R;
import com.danielsolawa.locationapp.adapter.HorizontalListViewFragment;
import com.danielsolawa.locationapp.client.OpenWeatherRestClient;
import com.danielsolawa.locationapp.model.Locality;
import com.danielsolawa.locationapp.model.LocationInfo;
import com.danielsolawa.locationapp.model.WeatherData;
import com.danielsolawa.locationapp.utils.AppManager;
import com.danielsolawa.locationapp.utils.Constants;
import com.danielsolawa.locationapp.utils.DateUtils;
import com.danielsolawa.locationapp.utils.Localization;
import com.danielsolawa.locationapp.utils.LocationResultHandler;
import com.danielsolawa.locationapp.utils.LocationUtils;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
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
    private AppManager appManager;

    //views
    private LinearLayout progressLayout;
    private LinearLayout weatherLayout;
    private LinearLayout currentWeatherLayout;
    private ConstraintLayout contentLayout;
    private TextView localityTv;
    private TextView temperatureTv;
    private TextView descriptionTv;
    private TextView pressureTv;
    private TextView visibilityTv;
    private TextView windSpeedTv;
    private TextView dateTv;
    private TextView sunriseTv;
    private TextView sunsetTv;
    private TextView minMaxTempTv;



    private TextView dateCardTv;
    private TextView tempCardTv;

    private Locality lastLocation;
    private LineChart lineChart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initialize();
        fetchLastLocation();



    }


    private void initialize() {
        appManager = AppManager.getInstance(getApplicationContext());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        contentLayout = (ConstraintLayout) findViewById(R.id.content_layout);
        progressLayout = (LinearLayout) findViewById(R.id.progress_layout);
        weatherLayout = (LinearLayout) findViewById(R.id.weather_layout);
        currentWeatherLayout = (LinearLayout) findViewById(R.id.current_weather_layout);

        dateTv = (TextView) findViewById(R.id.date_tv);
        localityTv = (TextView) findViewById(R.id.locality_tv);
        temperatureTv = (TextView) findViewById(R.id.temperature_tv);
        descriptionTv = (TextView) findViewById(R.id.desc_tv);
        pressureTv = (TextView) findViewById(R.id.pressure_tv);
        visibilityTv = (TextView) findViewById(R.id.visibility_tv);
        windSpeedTv = (TextView) findViewById(R.id.wind_speed_tv);
        sunriseTv = (TextView) findViewById(R.id.sunrise_tv);
        sunsetTv = (TextView) findViewById(R.id.sunset_tv);
        minMaxTempTv = (TextView) findViewById(R.id.max_min_temp_tv);

        dateCardTv = (TextView) findViewById(R.id.date_rv);
        tempCardTv = (TextView) findViewById(R.id.temp_rv);

        lineChart = (LineChart) findViewById(R.id.line_chart);

        initLocationUtils();
    }

    private void fetchLastLocation() {
        lastLocation = appManager.loadLastLocationFromPreferences();

        if(lastLocation != null){
            fetchCurrentWeather(lastLocation);
        }else{
            if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1){
                locationUtils.updateLocation();
            }
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
                locationUtils.updateLocation();
                return true;
            case R.id.show_condition:
                changeActivity(ConditionsActivity.class);
                return true;
            case R.id.show_settings:
                changeActivity(SettingsActivity.class);
                return true;
        }



        return super.onOptionsItemSelected(item);
    }





    private void changeActivity(Class<?> clazz) {
        Intent intent = new Intent(getApplicationContext(), clazz);
        startActivity(intent);
    }


    private void initLocationUtils() {


        locationUtils = new LocationUtils(this, new LocationResultHandler() {
            @Override
            public void handleLocationResult(String msg, LocationInfo locationInfo) {
                Locality locality = new Locality();
                locality.setName(locationInfo.getLocality());
                locality.setLatitude(locationInfo.getLatitude());
                locality.setLongitude(locationInfo.getLongitude());

                appManager.saveLastLocation(locality);



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
                    JSONObject sysObject = response.getJSONObject("sys");

                    weatherData.setDescription(weatherObject.getString("description"));
                    weatherData.setIcon("i" + weatherObject.getString("icon"));
                    weatherData.setPressure(mainObject.getDouble("pressure"));
                    weatherData.setTemp(mainObject.getDouble("temp"));
                    weatherData.setMinTemp(mainObject.getDouble("temp_min"));
                    weatherData.setMaxTemp(mainObject.getDouble("temp_max"));
                    weatherData.setVisibility(response.getDouble("visibility"));
                    weatherData.setWindSpeed(windObject.getDouble("speed"));
                    weatherData.setSunrise(sysObject.getLong("sunrise"));
                    weatherData.setSunset(sysObject.getLong("sunset"));

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
                currentWeatherLayout.setBackground(getDrawable(imgId));
                localityTv.setText(locality.getName());
                temperatureTv.setText(String.format("%s \u00b0C",
                         weatherData.getTemp()));
                descriptionTv.setText(String.format("%s",
                        weatherData.getDescription()));
                pressureTv.setText(String.format("%s hPa",
                        weatherData.getPressure()));
                String visibility = formatVisibility(weatherData.getVisibility());
                visibilityTv.setText(String.format("%s km",
                        visibility));
                String windSpeed = convertSpeed(weatherData.getWindSpeed());
                windSpeedTv.setText(String.format("%s km/h",
                        windSpeed));
                dateTv.setText(DateUtils.getCurrentDate());
                sunriseTv.setText(DateUtils.timestampToDate(weatherData.getSunrise()));
                sunsetTv.setText(DateUtils.timestampToDate(weatherData.getSunset()));
                minMaxTempTv.setText("min " + weatherData.getMinTemp() + "\u00b0C" + "\n"
                        + "max " + weatherData.getMaxTemp() + "\u00b0C" );


                fetchForecast(locality);


            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getApplicationContext(), "Failure " + statusCode, Toast.LENGTH_LONG).show();
            }
        });

    }

    private void prepareBackground(boolean darkMode) {
        String bg =  "background_color";

        if(darkMode){
            bg = "background_night_color";
            lightenFonts();
        }


        int backgroundId = getResources()
                .getIdentifier("com.danielsolawa.locationapp:drawable/"
                                + bg
                        , null, null);
        contentLayout.setBackground(getDrawable(backgroundId));
    }

    private void lightenFonts() {
        localityTv.setTextColor(Color.WHITE);
        temperatureTv.setTextColor(Color.WHITE);
        descriptionTv.setTextColor(Color.WHITE);
        pressureTv.setTextColor(Color.WHITE);
        visibilityTv.setTextColor(Color.WHITE);
        windSpeedTv.setTextColor(Color.WHITE);
        dateTv.setTextColor(Color.WHITE);
        sunriseTv.setTextColor(Color.WHITE);
        sunsetTv.setTextColor(Color.WHITE);
        minMaxTempTv.setTextColor(Color.WHITE);


    }

    private boolean isDarkModeOn(){
        return DateUtils.getCurrentHour() > 6 && DateUtils.getCurrentHour() < 20
                ? false : true;

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
                        String icon = "i" + tempIcon;
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

                boolean darkMode = isDarkModeOn();
                if(weatherForecastList.size() > 0) {
                    fillListViewWithData(weatherForecastList, darkMode);
                    fillChartWithData(weatherForecastList, darkMode);
                }

                prepareBackground(darkMode);
                progressLayout.setVisibility(View.GONE);
                weatherLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  Throwable throwable, JSONObject errorResponse) {

            }
        });


    }




    private void fillListViewWithData(List<WeatherData> weatherForecastList, boolean darkMode) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);

        if(fragment == null){
            fragment = new HorizontalListViewFragment();
            Bundle args = new Bundle();

            args.putParcelable(Constants.WEATHER_DATA_LIST, Parcels.wrap(weatherForecastList));
            args.putBoolean(Constants.DARK_MODE, darkMode);
            fragment.setArguments(args);
            fm.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
        }




    }


    private void fillChartWithData(List<WeatherData> weatherForecastList, boolean darkMode) {

        List<Entry> entries = new ArrayList<>();
        final String[] dates = new String[6];

        for(int i = 0; i < 6; i++){
            dates[i] = DateUtils.getLocalizedDateNewLine(weatherForecastList.get(i).getDate());
            entries.add(new Entry((float)(i + 1),
                    (float)weatherForecastList.get(i).getTemp()));
        }

        IAxisValueFormatter formatterX = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return dates[(int) value-1];
            }
        };

        IAxisValueFormatter formatterY = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return formatDegrees(value) + "\u00b0C";
            }
        };

        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setValueFormatter(formatterY);
        lineChart.getAxisRight().setEnabled(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(formatterX);


        LineDataSet dataSet = new LineDataSet(entries, "Temperature");
        dataSet.setColor(R.color.colorLightBlue);
        dataSet.setValueTextSize(10f);
        dataSet.setValueTextColor(R.color.colorDarkerBlue);


        Description desc = new Description();
        desc.setText("");
        LineData lineData = new LineData(dataSet);

        if(darkMode){
            lineChart.setDrawingCacheBackgroundColor(Color.WHITE);
            lineChart.setNoDataTextColor(Color.WHITE);
            lineChart.setBorderColor(Color.WHITE);
            dataSet.setColor(Color.WHITE);

            dataSet.setValueTextColor(Color.WHITE);
            yAxis.setTextColor(Color.WHITE);
            xAxis.setTextColor(Color.WHITE);
        }

        lineChart.setDescription(desc);
        lineChart.setData(lineData);
        lineChart.invalidate();
        lineChart.animateX(1500, Easing.EasingOption.EaseInBounce );
        lineChart.animateY(1500, Easing.EasingOption.EaseInBounce );

    }





    private String formatVisibility(double visibility) {
        DecimalFormat dc = new DecimalFormat("###.##");

        return dc.format((visibility / 1000));
    }

    private String formatDegrees(float degrees){
        DecimalFormat dc = new DecimalFormat("##.00");
        return dc.format(degrees);
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

            }
        }
    }



    public void createToast(){
        Toast.makeText(this, getString(R.string.updating_location_data), Toast.LENGTH_SHORT).show();
    }





}
