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

    private Spinner notificationsSpinner;
    private Button saveButton;
    private int[] intervals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        init();


    }

    private void init() {
        notificationsSpinner = (Spinner) findViewById(R.id.interval_spinner);
        saveButton = (Button) findViewById(R.id.save_settings);
        AppManager appManager = AppManager.getInstance(getApplicationContext());
        intervals = appManager.getIntervals();
        setupSpinner();

    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.notifications_intervals, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        notificationsSpinner.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
