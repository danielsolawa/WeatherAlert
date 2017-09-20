package com.danielsolawa.locationapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.danielsolawa.locationapp.R;
import com.danielsolawa.locationapp.utils.Constants;

public class MainActivity extends FragmentActivity {

    private static final String TAG = MainActivity.class.getSimpleName();



   private Button startButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        startButton = (Button) findViewById(R.id.startWizardButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WizardActivity.class);
                finish();
                startActivity(intent);
            }
        });


}

}
