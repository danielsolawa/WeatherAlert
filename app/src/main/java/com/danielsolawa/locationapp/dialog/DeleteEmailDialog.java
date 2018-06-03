package com.danielsolawa.locationapp.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.danielsolawa.locationapp.activity.SettingsActivity;

/**
 * Created by NeverForgive on 2018-06-03.
 */

public class DeleteEmailDialog extends DialogFragment{


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final SettingsActivity activity = (SettingsActivity) getActivity();
        final int id = getArguments().getInt("EMAIL_ID");
        final String name = getArguments().getString("EMAIL_NAME");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Do you really want to remove " + name + " from the list?")
                .setPositiveButton("delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.removeEmailFromTheList(id);

                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });


        return builder.create();
    }
}
