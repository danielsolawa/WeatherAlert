package com.danielsolawa.locationapp.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.danielsolawa.locationapp.activity.WizardActivity;
import com.danielsolawa.locationapp.model.LocationInfo;

/**
 * Created by NeverForgive on 2017-08-23.
 */

public class SimpleDialog extends DialogFragment {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final WizardActivity wizardActivity = (WizardActivity) getActivity();



        String msg = getArguments().getString("location");
        final LocationInfo locationInfo = getArguments().getParcelable("location_info");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Is that your current location?\n" + msg)
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        wizardActivity.setLocationUpdated(true);
                        wizardActivity.getAddLayout().setVisibility(View.VISIBLE);
                        wizardActivity.saveCurrentState(wizardActivity.getAddLayout().getVisibility(),
                                wizardActivity.getConfirmLayout().getVisibility(),
                                null,
                                locationInfo,
                                null);

                    }
                })
                .setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        wizardActivity.updateLocation();
                    }
                });

        return builder.create();
    }


    <T> T getMe(Class<T> clazz){
        Activity activity = getActivity();

        return clazz.cast(activity);
    }


}
