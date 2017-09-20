package com.danielsolawa.locationapp.activity;

import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.danielsolawa.locationapp.R;
import com.danielsolawa.locationapp.dialog.SimpleDialog;
import com.danielsolawa.locationapp.model.Alert;
import com.danielsolawa.locationapp.model.Locality;
import com.danielsolawa.locationapp.model.LocationInfo;
import com.danielsolawa.locationapp.service.AlertIntentService;
import com.danielsolawa.locationapp.utils.BootReceiver;
import com.danielsolawa.locationapp.utils.Constants;
import com.danielsolawa.locationapp.utils.LocationResultHandler;
import com.danielsolawa.locationapp.utils.LocationUtils;
import com.danielsolawa.locationapp.bundle.WizardBundle;

import java.util.ArrayList;
import java.util.List;

public class WizardActivity extends FragmentActivity implements View.OnClickListener {

    //constants
    private static final String TAG = WizardActivity.class.getSimpleName();
    private static final String ACTIVITY_DATA = "addLayout";

    private LocationUtils locationUtils;
    private WizardBundle wizardBundle;
    private List<RadioButton> rbList;
    private LocationInfo locInfo;

    //views
    private RadioButton rbCloud;
    private RadioButton rbRain;
    private RadioButton rbSnow;
    private RadioButton rbStorm;
    private RadioButton rbAtmosphere;
    private EditText etTemperature;
    private Button saveButton;
    private Button confirmButton;
    private Button editButton;
    private Spinner spinner;
    private EditText etSpinner;
    private LinearLayout addLayout;
    private LinearLayout confirmLayout;
    private TextView tvConfirm;

    private String weatherType;
    private String temperature;
    private boolean locationUpdated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            locationUpdated = savedInstanceState.getBoolean(Constants.LOCATION_UPDATED);
            wizardBundle = savedInstanceState.getParcelable(ACTIVITY_DATA);
        }


        setContentView(R.layout.activity_wizard);
        initViews();
        setActivityData();


        locationUtils = new LocationUtils(this, new LocationResultHandler() {
            @Override
            public void createDialog(String msg, LocationInfo locationInfo) {
                locInfo = locationInfo;


                DialogFragment dialogFragment = new SimpleDialog();
                Bundle args = new Bundle();
                args.putString("location", msg);
                args.putParcelable("location_info", locationInfo);
                dialogFragment.setArguments(args);
                dialogFragment.setCancelable(false);
                dialogFragment.show(getFragmentManager(), TAG);

            }
        });

        if(!locationUpdated){
            locationUtils.fetchLastLocation();
        }


    }

    @SuppressWarnings("ResourceType")
    private void setActivityData() {
        if(wizardBundle != null){
            confirmLayout.setVisibility(wizardBundle.getConfirmLayoutVisibility());
            addLayout.setVisibility(wizardBundle.getAddLayoutVisibility());
            etTemperature.setText(wizardBundle.getTemperature());
            tvConfirm.setText(wizardBundle.getContent());
        }else{
            wizardBundle = new WizardBundle();
            saveCurrentState(View.GONE, View.GONE, null, null, null);
            addLayout.setVisibility(wizardBundle.getAddLayoutVisibility());
            confirmLayout.setVisibility(wizardBundle.getConfirmLayoutVisibility());
        }
    }

    private void initViews() {
        addLayout = (LinearLayout) findViewById(R.id.add_layout);
        confirmLayout = (LinearLayout) findViewById(R.id.confirm_layout);
        tvConfirm = (TextView) findViewById(R.id.confirm_tv);

        rbCloud = (RadioButton) findViewById(R.id.rb_cloud);
        rbRain = (RadioButton) findViewById(R.id.rb_rain);
        rbSnow = (RadioButton) findViewById(R.id.rb_snow);
        rbStorm = (RadioButton) findViewById(R.id.rb_thunderstorm);
        rbAtmosphere = (RadioButton) findViewById(R.id.rb_atmosphere);
        etSpinner = (EditText) findViewById(R.id.spinner_tv);

        etTemperature = (EditText) findViewById(R.id.ed_temp);

        saveButton = (Button) findViewById(R.id.saveData);
        confirmButton = (Button) findViewById(R.id.confirm_button);
        editButton = (Button) findViewById(R.id.edit_button);

        saveButton.setOnClickListener(this);
        confirmButton.setOnClickListener(this);
        editButton.setOnClickListener(this);

        addRadioButtonsToList();

        spinner = (Spinner) findViewById(R.id.weather_spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                weatherType = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id){
            case R.id.saveData:
                validateData();
                break;
            case R.id.edit_button:
                saveCurrentState(View.VISIBLE, View.GONE, null, null, null);
                confirmLayout.setVisibility(View.GONE);
                addLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.confirm_button:
                confirmAndSave();
                break;
        }
    }

    private void confirmAndSave() {
        AsyncTask<Void, Integer, Boolean> task = new AsyncTask<Void, Integer, Boolean>() {
            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);

            }

            @Override
            protected Boolean doInBackground(Void... params) {
                Locality locality = new Select()
                        .from(Locality.class)
                        .where("name = ?", locInfo.getLocality())
                        .executeSingle();

                if(locality == null){
                    locality = new Locality();
                    locality.setName(locInfo.getLocality());
                    locality.setLatitude(locInfo.getLatitude());
                    locality.setLongitude(locInfo.getLongitude());
                    locality.save();
                }



                Alert alert = new Alert();
                alert.setWeatherCondition(weatherType);
                alert.setTemperatureCondition(Double.parseDouble(temperature));
                alert.setLocality(locality);
                alert.save();

                return (alert.getId() > 0 && locality.getId() > 0);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if(aBoolean){
                    savePreferences();

                    Toast.makeText(getApplicationContext(), "Saved successfully", Toast.LENGTH_SHORT)
                            .show();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(getApplicationContext(),
                                    ConditionsActivity.class);
                            finish();
                            startActivity(intent);
                        }
                    }, 2000);
                }

            }
        };

        task.execute();




    }

    private void savePreferences() {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        boolean firstTimeRun = preferences.getBoolean(Constants.FIRST_TIME, true);

        SharedPreferences.Editor edit = preferences.edit();
        if(firstTimeRun){
            edit.putBoolean(Constants.FIRST_TIME, false);
            setupAlarmManager();
            enableReceiver();
        }
        edit.putString(Constants.LAST_LOCATION, locInfo.getLocality());
        edit.apply();
    }



    private void setupAlarmManager() {
        AlarmManager alarmManager = (AlarmManager)
                getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(getApplicationContext(), AlertIntentService.class);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(),
                0, alarmIntent, 0);

        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + Constants.THIRTY_SECONDS,
                Constants.SIX_HOURS,
                pendingIntent);
    }

    private void enableReceiver() {
        ComponentName receiver = new ComponentName(getApplicationContext(),
                BootReceiver.class);
        PackageManager pm = getApplicationContext().getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    private void validateData() {
        boolean error = false;

        temperature = etTemperature.getText().toString();

        if(TextUtils.isEmpty(temperature)){
             error = true;
             etTemperature.setError("Field can't be empty");
         }

         if(weatherType == null){
            etSpinner.setError("Item should be selected");
             error = true;
         }

        if(!error){
            addLayout.setVisibility(View.GONE);
            confirmLayout.setVisibility(View.VISIBLE);

            StringBuilder builder = new StringBuilder();
            builder.append("Locality: " + wizardBundle.getLocationInfo().getLocality() + "\n");
            builder.append("Weather Type: " + weatherType + "\n");
            builder.append("Temperature: " + temperature +  "\u00b0" + "C" );
            tvConfirm.setText(builder.toString());
            saveCurrentState(View.GONE, View.VISIBLE, temperature, null, builder.toString());

        }


    }

    public void saveCurrentState(int addLayoutVisibility, int confirmLayoutVisibility,
                                  String temperature, LocationInfo locationInfo, String content){

        if(locationInfo != null){
            wizardBundle.setLocationInfo(locationInfo);
        }

        if(temperature != null){
            wizardBundle.setTemperature(temperature);
        }

        if(content != null){
            wizardBundle.setContent(content);
        }

        wizardBundle.setAddLayoutVisibility(addLayoutVisibility);
        wizardBundle.setConfirmLayoutVisibility(confirmLayoutVisibility);


    }

    private void addRadioButtonsToList() {
        rbList = new ArrayList<>();
        rbList.add(rbCloud);
        rbList.add(rbRain);
        rbList.add(rbSnow);
        rbList.add(rbStorm);
        rbList.add(rbAtmosphere);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        locationUpdated = savedInstanceState.getBoolean(Constants.LOCATION_UPDATED);
        wizardBundle = savedInstanceState.getParcelable(ACTIVITY_DATA);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(Constants.LOCATION_UPDATED, locationUpdated);
        outState.putParcelable(ACTIVITY_DATA, wizardBundle);

        super.onSaveInstanceState(outState);
    }

    /*
     * receives result from checkLocationSettings
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            Toast.makeText(getApplicationContext(), "Fetching location data...", Toast.LENGTH_LONG)
                    .show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    recreate();
                }
            }, 3000);

        }else{
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            finish();
            startActivity(intent);
        }
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        int id = view.getId();
        clearChecked();
        checkSelectedRadioButton(id);

        spinner.setVisibility(View.VISIBLE);


        switch(view.getId()) {
            case R.id.rb_cloud:
                if (checked)
                    setupSpinnerData(R.array.weather_clouds);
                    break;
            case R.id.rb_rain:
                if (checked)
                    setupSpinnerData(R.array.weather_rain);
                    break;
            case R.id.rb_snow:
                if (checked)
                    setupSpinnerData(R.array.weather_snow);
                    break;
            case R.id.rb_thunderstorm:
                if (checked)
                    setupSpinnerData(R.array.weather_thunderstorm);
                    break;
            case R.id.rb_atmosphere:
                if (checked)
                    setupSpinnerData(R.array.weather_atmosphere);
                    break;
        }
    }



    private void clearChecked() {
       for(RadioButton r : rbList){
           r.setChecked(false);
       }


    }

    private void checkSelectedRadioButton(int id) {
        for(RadioButton r : rbList){
            if(r.getId() == id){
                r.setChecked(true);
            }
        }

    }


    public void updateLocation(){
        locationUtils.updateLocation();
    }

    public void setupSpinnerData(int data) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                data, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_items);

        spinner.setAdapter(adapter);



    }


    public void setLocationUpdated(boolean locationUpdated) {
        this.locationUpdated = locationUpdated;
    }

    public LinearLayout getAddLayout() {
        return addLayout;
    }

    public LinearLayout getConfirmLayout() {
        return confirmLayout;
    }


}
