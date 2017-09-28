package com.danielsolawa.locationapp.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.danielsolawa.locationapp.R;
import com.danielsolawa.locationapp.utils.Localization;

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
                Localization loc = new Localization(getApplicationContext());
                String localizedString = loc.localizeWeatherDataString("clear sky");
                Log.d(TAG, localizedString);
            }
        });


}

}
