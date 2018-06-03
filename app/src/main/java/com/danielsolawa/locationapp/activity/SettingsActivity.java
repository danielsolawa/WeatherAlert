package com.danielsolawa.locationapp.activity;

import android.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.danielsolawa.locationapp.R;
import com.danielsolawa.locationapp.adapter.EmailAdapter;
import com.danielsolawa.locationapp.adapter.RowClicker;
import com.danielsolawa.locationapp.dialog.DeleteDialog;
import com.danielsolawa.locationapp.dialog.DeleteEmailDialog;
import com.danielsolawa.locationapp.utils.AlertUtils;
import com.danielsolawa.locationapp.utils.AppManager;
import com.danielsolawa.locationapp.utils.EmailUtils;

import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();
    private Spinner notificationsSpinner;
    private Spinner forecastSpinner;
    private SwitchCompat emailSwitch;
    private EditText emailEd;
    private ListView emailList;
    private Button saveButton;
    private Button addButton;

    private int[] intervals;
    private int currentIntervalIndex;
    private int currentForecast;
    private AppManager appManager;
    private EmailAdapter adapter;
    private List<String> emails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        init();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


    }

    private void init() {
        appManager = AppManager.getInstance(getApplicationContext());
        intervals = appManager.getIntervals();
        AlertUtils.init(getApplicationContext());

        initNotificationSpinner();
        initForecastSpinner();
        initEmailSection();

        saveButton = (Button) findViewById(R.id.save_settings);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });
    }

    private void initEmailSection() {
        emailSwitch = (SwitchCompat) findViewById(R.id.email_switch);
        emailEd = (EditText) findViewById(R.id.email_ed);
        emailList = (ListView) findViewById(R.id.email_lv);
        addButton = (Button) findViewById(R.id.email_button);

        initEmailSwitch();
        initEmailList();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewRecipient();
            }
        });




    }

    private void addNewRecipient() {
        String email = emailEd.getText().toString();

        if(EmailUtils.isEmailValid(email)) {
            emails.add(email);
            saveRecipient("The email has been added successfully!");
        }
        else
            createToast("The email address is invalid.");


    }

    private void saveRecipient(String msg){
        appManager.saveEmailRecipient(appManager.emailsToString(emails));
        createToast(msg);
        emailEd.setText("");
        initEmailList();
    }


    private void initEmailSwitch() {
        emailSwitch.setChecked(appManager.isEmailEnabled());
        emailSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               toggleEmailSwitch(isChecked);
                Log.d(TAG, "recipients " + "'" + appManager.getEmailRecipients() + "'");
            }
        });



    }


    private void initEmailList() {

        emails = appManager.emailsToList();
        adapter = new EmailAdapter(getApplicationContext(), emails, new RowClicker() {
            @Override
            public void onClick(long iD) {

            }

            @Override
            public void onClick(long iD, String name) {
                DialogFragment df = new DeleteEmailDialog();
                Bundle args = new Bundle();
                args.putInt("EMAIL_ID", (int)iD);
                args.putString("EMAIL_NAME", name);
                df.setCancelable(false);
                df.setArguments(args);
                df.show(getFragmentManager(), TAG);
            }
        });

        emailList.setAdapter(adapter);

    }

    public void removeEmailFromTheList(int id){
        emails.remove(id);
        saveRecipient("The email has been removed from the list.");

    }

    private void toggleEmailSwitch(boolean isChecked) {
        if(isChecked && emails.size() == 0){
            emailSwitch.setChecked(false);
            createToast("You have to add at least one email!");
            return;
        }

        appManager.setEmailEnabled(isChecked);
        emailSwitch.setChecked(isChecked);
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
