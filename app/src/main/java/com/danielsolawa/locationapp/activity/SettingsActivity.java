package com.danielsolawa.locationapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Spinner;

import com.danielsolawa.locationapp.R;

public class SettingsActivity extends AppCompatActivity {

    private Spinner notificationsSpinner;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        init();


    }

    private void init() {
        notificationsSpinner = (Spinner) findViewById(R.id.interval_spinner);
        saveButton = (Button) findViewById(R.id.save_settings);


    }
}
