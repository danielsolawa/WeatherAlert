package com.danielsolawa.locationapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.danielsolawa.locationapp.R;
import com.danielsolawa.locationapp.utils.AlertUtils;
import com.danielsolawa.locationapp.utils.AppManager;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();
    private Spinner notificationsSpinner;
    private Spinner forecastSpinner;
    private Button saveButton;
    private int[] intervals;
    private int currentIntervalIndex;
    private int currentForecast;
    private AppManager appManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        init();


    }

    private void init() {
        appManager = AppManager.getInstance(getApplicationContext());
        intervals = appManager.getIntervals();
        AlertUtils.init(getApplicationContext());

        initNotificationSpinner();
        initForecastSpinner();

        saveButton = (Button) findViewById(R.id.save_settings);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });
    }



    private void initNotificationSpinner() {
        notificationsSpinner = (Spinner) findViewById(R.id.interval_spinner);
        notificationsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentIntervalIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        setupNotificationSpinner();
    }


    private void setupNotificationSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.notifications_intervals, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        notificationsSpinner.setAdapter(adapter);
        notificationsSpinner.setSelection(appManager.getCurrentIntervalIndex());

    }


    private void initForecastSpinner() {
        forecastSpinner = (Spinner) findViewById(R.id.alert_spinner);
        forecastSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentForecast = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        setupForecastSpinner();
    }

    private void setupForecastSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.settings_forecast_key, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        forecastSpinner.setAdapter(adapter);

         forecastSpinner.setSelection(appManager.getCurrentForecast());

    }


    private void saveData() {
        saveNotificationsInterval();
        saveForecastInterval();
        createToast("Data saved");
        finish();
    }

    private void saveForecastInterval() {
        appManager.saveCurrentForecast(currentForecast);
    }

    private void saveNotificationsInterval() {
        appManager.saveCurrentIntervalIndex(currentIntervalIndex);
        if(appManager.getAlarmState()){
            changeIntervals();
        }

    }

    private void changeIntervals() {
        AlertUtils.stopJob();
        AlertUtils.scheduleJob();
    }

    private void createToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

}
