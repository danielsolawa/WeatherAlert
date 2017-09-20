package com.danielsolawa.locationapp.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;

import com.activeandroid.query.Select;
import com.danielsolawa.locationapp.activity.HomeActivity;
import com.danielsolawa.locationapp.activity.WizardActivity;
import com.danielsolawa.locationapp.model.Locality;
import com.danielsolawa.locationapp.model.LocationInfo;
import com.danielsolawa.locationapp.utils.Constants;

/**
 * Created by NeverForgive on 2017-09-04.
 */

public class UpdateLocationDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final HomeActivity homeActivity = (HomeActivity) getActivity();

        String msg = getArguments().getString("location");
        final LocationInfo locationInfo = getArguments().getParcelable("location_info");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Is that your current location?\n" + msg)
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveLastLocation(locationInfo, homeActivity);


                    }
                })
                .setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       homeActivity.createToast();
                       homeActivity.updateLocation();
                    }
                });

        return builder.create();
    }

    private void saveLastLocation(LocationInfo locationInfo, final HomeActivity homeActivity) {

        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(homeActivity);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.LAST_LOCATION, locationInfo.getLocality());
        editor.apply();

        AsyncTask<LocationInfo, Integer, Boolean> async =
                new AsyncTask<LocationInfo, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(LocationInfo... params) {
                LocationInfo lastLocation = params[0];


                Locality locality = new Select()
                        .from(Locality.class)
                        .where("name = ?", lastLocation.getLocality())
                        .executeSingle();

                if(locality == null){
                    locality = new Locality();
                    locality.setName(lastLocation.getLocality());
                    locality.setLongitude(lastLocation.getLongitude());
                    locality.setLatitude(lastLocation.getLatitude());
                    locality.save();
                }


                return locality.getId() > 0;
            }

            @Override
            protected void onPostExecute(Boolean result) {
               if(result){
                   Handler handler = new Handler();
                   handler.postDelayed(new Runnable() {
                       @Override
                       public void run() {
                           homeActivity.recreate();
                       }
                   }, 2000);
               }
            }
        };


        async.execute(locationInfo);

    }

}
