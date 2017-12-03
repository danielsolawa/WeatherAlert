package com.danielsolawa.locationapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.danielsolawa.locationapp.R;
import com.danielsolawa.locationapp.utils.AppManager;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = SettingsActivity.class.getSimpleName();
    private Spinner notificationsSpinner;
    private Button saveButton;
    private int[] intervals;
    private int currentIntervalIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        init();


    }

    private void init() {
        AppManager appManager = AppManager.getInstance(getApplicationContext());
        intervals = appManager.getIntervals();

        notificationsSpinner = (Spinner) findViewById(R.id.interval_spinner);
        notificationsSpinner.setOnItemSelectedListener(this);
        setupSpinner();
        notificationsSpinner.setSelection(appManager.getCurrentIntervalIndex());

        saveButton = (Button) findViewById(R.id.save_settings);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });
    }


    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.notifications_intervals, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        notificationsSpinner.setAdapter(adapter);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        currentIntervalIndex = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void saveData() {
        
    }

}
