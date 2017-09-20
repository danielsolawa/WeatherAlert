package com.danielsolawa.locationapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.danielsolawa.locationapp.utils.Constants;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent= null;

        if(!(IsFirstTime())){
            intent = new Intent(getApplicationContext(), HomeActivity.class);

        }else{
            intent = new Intent(getApplicationContext(), MainActivity.class);
        }

        finish();
        startActivity(intent);

    }



    private boolean IsFirstTime() {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        return preferences.getBoolean(Constants.FIRST_TIME, true);

    }
}
