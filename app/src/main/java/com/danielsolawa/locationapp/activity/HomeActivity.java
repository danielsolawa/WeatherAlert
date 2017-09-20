package com.danielsolawa.locationapp.activity;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.danielsolawa.locationapp.R;
import com.danielsolawa.locationapp.adapter.HorizontalListViewFragment;
import com.danielsolawa.locationapp.client.OpenWeatherRestClient;
import com.danielsolawa.locationapp.dialog.UpdateLocationDialog;
import com.danielsolawa.locationapp.model.Locality;
import com.danielsolawa.locationapp.model.LocationInfo;
import com.danielsolawa.locationapp.model.WeatherData;
import com.danielsolawa.locationapp.utils.Constants;
import com.danielsolawa.locationapp.utils.LocationResultHandler;
import com.danielsolawa.locationapp.utils.LocationUtils;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = HomeActivity.class.getSimpleName();

    private Locality locality;
    private List<WeatherData> weatherData = new ArrayList<>();
    private LocationUtils locationUtils;

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initialize();
        fetchData();


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
                initLocationUpdate();
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

    private void initLocationUpdate() {
        createToast();
        locationUtils = new LocationUtils(this, new LocationResultHandler() {
            @Override
            public void createDialog(String msg, LocationInfo locationInfo) {
                DialogFragment dialog = new UpdateLocationDialog();
                Bundle args = new Bundle();
                args.putString("location", msg);
                args.putParcelable("location_info", locationInfo);
                dialog.setArguments(args);
                dialog.setCancelable(false);
                dialog.show(getFragmentManager(),TAG);
            }
        });


        locationUtils.updateLocation();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            /*Toast.makeText(getApplicationContext(), "Fetching location data...", Toast.LENGTH_LONG)
                    .show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    recreate();
                }
            }, 3000);
        */
        }
    }


    private void initialize() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressLayout = (LinearLayout) findViewById(R.id.progress_layout);
        weatherLayout = (LinearLayout) findViewById(R.id.weather_layout);

        weatherIcon = (ImageView) findViewById(R.id.weather_image);
        localityTv = (TextView) findViewById(R.id.locality_tv);
        temperatureTv = (TextView) findViewById(R.id.temperature_tv);
        descriptionTv = (TextView) findViewById(R.id.desc_tv);
        pressureTv = (TextView) findViewById(R.id.pressure_tv);
        visibilityTv = (TextView) findViewById(R.id.visibility_tv);
        windSpeedTv = (TextView) findViewById(R.id.wind_speed_tv);






    }




    private void fetchData() {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());

        String lastLocation = preferences.getString(Constants.LAST_LOCATION, "");

        AsyncTask<String, Integer, Boolean> task = new AsyncTask<String, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                String location = params[0];

                locality = new Select()
                        .from(Locality.class)
                        .where("name = ?", location)
                        .executeSingle();

                if(locality == null){
                    return false;
                }


                weatherData = new Select()
                        .from(WeatherData.class)
                        .where("locality = ?", locality.getId())
                        .execute();

                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if(result){
                    fetchCurrentWeather();
                    setupForecast();
                }
            }
        };

        task.execute(lastLocation);

    }

    private void setupForecast() {
        if(weatherData.size() > 0){
            FragmentManager fm = getSupportFragmentManager();
            Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);

            if(fragment == null){
                fragment = new HorizontalListViewFragment();
                Bundle args = new Bundle();

                args.putParcelable(Constants.WEATHER_DATA_LIST, Parcels.wrap(weatherData));
                fragment.setArguments(args);
                fm.beginTransaction()
                        .add(R.id.fragmentContainer, fragment)
                        .commit();
            }

        }


    }

    private void fetchCurrentWeather() {
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


                int imgId = getResources()
                        .getIdentifier("com.danielsolawa.locationapp:drawable/"
                                        + weatherData.getIcon()
                                , null, null);
                weatherIcon.setImageResource(imgId);
                localityTv.setText(locality.getName());
                temperatureTv.setText(String.format("%s \u00b0C",
                         weatherData.getTemp()));
                descriptionTv.setText(String.format("%s: %s",
                        "Description", weatherData.getDescription()));
                pressureTv.setText(String.format("%s: %s hPa",
                        "Pressure", weatherData.getPressure()));
                String visibility = formatVisibility(weatherData.getVisibility());
                visibilityTv.setText(String.format("%s: %s km",
                        "Visibility", visibility));
                String windSpeed = convertSpeed(weatherData.getWindSpeed());
                windSpeedTv.setText(String.format("%s: %s km/h", "Wind speed",
                        windSpeed));


                progressLayout.setVisibility(View.GONE);
                weatherLayout.setVisibility(View.VISIBLE);

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

            }
        });

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


    public void createToast(){
        Toast.makeText(this, "Updating your location...", Toast.LENGTH_SHORT).show();
    }


    public void updateLocation(){
        locationUtils.updateLocation();
    }


}
