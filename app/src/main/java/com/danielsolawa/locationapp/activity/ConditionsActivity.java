package com.danielsolawa.locationapp.activity;

import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.danielsolawa.locationapp.R;
import com.danielsolawa.locationapp.WeatherApp;
import com.danielsolawa.locationapp.adapter.ConditionsAdapter;
import com.danielsolawa.locationapp.adapter.RowClicker;
import com.danielsolawa.locationapp.dialog.DeleteDialog;
import com.danielsolawa.locationapp.dialog.WarningDialog;
import com.danielsolawa.locationapp.model.Alert;
import com.danielsolawa.locationapp.model.Locality;
import com.danielsolawa.locationapp.service.AlertIntentService;
import com.danielsolawa.locationapp.utils.AlarmReceiver;
import com.danielsolawa.locationapp.utils.BootReceiver;
import com.danielsolawa.locationapp.utils.Constants;
import com.danielsolawa.locationapp.utils.Localization;

import java.util.ArrayList;
import java.util.List;

public class ConditionsActivity extends AppCompatActivity {
    private static final String TAG = ConditionsActivity.class.getSimpleName();

    private ConditionsAdapter adapter;
    private AlarmManager alarmManager;
    private PendingIntent pendingAlarmIntent;
    private List<Alert> alerts;
    private Localization localization;

    //views
    private ListView conditionsListView;
    private Switch alarmSwitch;
    private Button addAlertButton;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conditions);

        initialize();

        fetchAlerts();



    }

    private void initialize() {
        WeatherApp app = (WeatherApp) getApplication();
        initializeAlarmManager();
        localization = app.getLocalization();
        alarmSwitch = (Switch) findViewById(R.id.alarm_switch);
        alarmSwitch.setChecked(getAlarmState());
        alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    startAlarmManager();
                }else{
                    stopAlarmManager();
                }

            }
        });
        textView = (TextView) findViewById(R.id.alertDate);
        addAlertButton = (Button) findViewById(R.id.add_alert_btn);
        addAlertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WizardActivity.class);
                finish();
                startActivity(intent);
            }
        });

        conditionsListView = (ListView) findViewById(R.id.conditions_lv);

        alerts = new ArrayList<>();

        String nextAlertDate = app.getNextAlertDate();
        textView.setText(nextAlertDate);

    }

    private void fetchAlerts() {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String lastLocation = preferences.getString(Constants.LAST_LOCATION, "");

        AsyncTask<String, Integer, Boolean> asyncTask = new AsyncTask<String, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                String location = params[0];
                Locality locality = new Select()
                        .from(Locality.class)
                        .where("name = ?", location)
                        .executeSingle();

                if(locality == null){
                    return false;
                }

                alerts = new Select()
                        .from(Alert.class)
                        .where("locality = ?", locality.getId())
                        .execute();


                return alerts.size() > 0;
            }


            @Override
            protected void onPostExecute(Boolean result) {
                if(result){
                    fillAdapter();
                }
            }
        };

        asyncTask.execute(lastLocation);

    }

    private void fillAdapter() {
        adapter = new ConditionsAdapter(getApplicationContext(),localization, alerts, new RowClicker() {
            @Override
            public void onClick(long iD) {
                DialogFragment deleteDialog = new DeleteDialog();
                Bundle args = new Bundle();
                args.putLong("ALERT_ID", iD);
                deleteDialog.setCancelable(false);
                deleteDialog.setArguments(args);
                deleteDialog.show(getFragmentManager(), TAG);

            }
        });

        conditionsListView.setAdapter(adapter);
    }

    private void initializeAlarmManager(){
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent alarmIntent = new Intent(getApplicationContext(), AlarmReceiver.class);

        pendingAlarmIntent = PendingIntent.getBroadcast(getApplicationContext(),
                0, alarmIntent, 0);
    }

    private void startAlarmManager() {
        if(alerts.size() > 0){
            setAlarmState(true);

            /*
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime() + Constants.THIRTY_SECONDS,
                    Constants.SIX_HOURS,
                    pendingAlarmIntent);*/

            enableReceiver();
        }else{
            alarmSwitch.setChecked(false);
            setAlarmState(false);

            DialogFragment dialog = new WarningDialog();
            dialog.setCancelable(false);
            dialog.show(getFragmentManager(), TAG);
        }

    }


    private void stopAlarmManager(){
        setAlarmState(false);
        disableReceiver();
       /* if(alarmManager != null){
            alarmManager.cancel(pendingAlarmIntent);

        }*/
    }



    private void enableReceiver() {
        ComponentName receiver = new ComponentName(getApplicationContext(),
                BootReceiver.class);
        PackageManager pm = getApplicationContext().getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }


    private void disableReceiver(){
        ComponentName receiver = new ComponentName(getApplicationContext(), BootReceiver.class);
        PackageManager pm = getApplicationContext().getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }




    public void delete(long id){
        AsyncTask<Long, Integer, Boolean> asyncTask = new AsyncTask<Long, Integer, Boolean>() {

            @Override
            protected Boolean doInBackground(Long... params) {
                long id = params[0];
                Alert.delete(Alert.class, id);


                return true;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                recreate();
            }
        };
        asyncTask.execute(id);

    }


    private boolean getAlarmState() {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean alarmState = preferences.getBoolean(Constants.ALARM_STATE, false);


        return alarmState;
    }


    private void setAlarmState(boolean alarmState){
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(Constants.ALARM_STATE, alarmState);
        editor.apply();
    }
}
