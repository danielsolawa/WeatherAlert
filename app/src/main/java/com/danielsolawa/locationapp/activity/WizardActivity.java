package com.danielsolawa.locationapp.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
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

import com.danielsolawa.locationapp.R;
import com.danielsolawa.locationapp.WeatherApp;
import com.danielsolawa.locationapp.model.Alert;
import com.danielsolawa.locationapp.model.Locality;
import com.danielsolawa.locationapp.utils.AppManager;
import com.danielsolawa.locationapp.utils.Localization;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WizardActivity extends FragmentActivity implements View.OnClickListener {

    //constants
    private static final String TAG = WizardActivity.class.getSimpleName();

    private AppManager appManager;
    private Locality locality;
    private Localization loc;



    //views
    private EditText etTemperature;
    private Button saveButton;
    private Button confirmButton;
    private Button editButton;
    private Spinner spinner;
    private LinearLayout addLayout;
    private LinearLayout confirmLayout;
    private TextView tvConfirm;

    private String weatherType;
    private String temperature;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wizard);
        initialize();


    }



    private void initialize() {
        appManager = AppManager.getInstance(getApplicationContext());
        locality = appManager.loadLastLocationFromPreferences();

        addLayout = (LinearLayout) findViewById(R.id.add_layout);
        confirmLayout = (LinearLayout) findViewById(R.id.confirm_layout);
        tvConfirm = (TextView) findViewById(R.id.confirm_tv);

        etTemperature = (EditText) findViewById(R.id.ed_temp);

        saveButton = (Button) findViewById(R.id.saveData);
        confirmButton = (Button) findViewById(R.id.confirm_button);
        editButton = (Button) findViewById(R.id.edit_button);

        saveButton.setOnClickListener(this);
        confirmButton.setOnClickListener(this);
        editButton.setOnClickListener(this);

        loc = appManager.getLocalization();


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
        setupSpinnerData(R.array.weather_clouds);

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id){
            case R.id.saveData:
                validateData();
                break;
            case R.id.edit_button:
                changeLayoutVisibility(View.VISIBLE, View.GONE);
                break;
            case R.id.confirm_button:
                confirmAndSave();
                break;
        }
    }

    private void validateData() {
        boolean error = false;

        temperature = etTemperature.getText().toString();

        if(TextUtils.isEmpty(temperature)){
            error = true;
            etTemperature.setError(getString(R.string.field_cant_be_empty));
        }


        if(!error){
            StringBuilder builder = new StringBuilder();
            builder.append(getString(R.string.locality)+ " " + locality.getName() + "\n");
            builder.append(getString(R.string.weather_type)+ " "  + weatherType + "\n");
            builder.append(getString(R.string.temperature)+ " " + temperature +  "\u00b0" + "C" );
            tvConfirm.setText(builder.toString());
            changeLayoutVisibility(View.GONE, View.VISIBLE);

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
                Alert alert = new Alert();
                if(loc.isLocalized()){
                    weatherType = loc.reverseLocalizeAlertConditionString(weatherType);
                }
                alert.setWeatherCondition(weatherType);
                alert.setTemperatureCondition(Double.parseDouble(temperature));
                alert.setLocality(locality);
                alert.save();

                return (alert.getId() > 0);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                if(result){

                    Toast.makeText(getApplicationContext(),
                            getString(R.string.saved_successfully),
                            Toast.LENGTH_SHORT)
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





    public void changeLayoutVisibility(int addLayoutVisibility, int confirmLayoutVisibility){

        addLayout.setVisibility(addLayoutVisibility);
        confirmLayout.setVisibility(confirmLayoutVisibility);

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






    public void setupSpinnerData(int data) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                data, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_items);

        spinner.setAdapter(adapter);



    }



}
