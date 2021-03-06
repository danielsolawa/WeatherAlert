package com.danielsolawa.locationapp.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.danielsolawa.locationapp.R;
import com.danielsolawa.locationapp.activity.ConditionsActivity;

/**
 * Created by NeverForgive on 2017-09-08.
 */

public class DeleteDialog extends DialogFragment {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final ConditionsActivity activity  = (ConditionsActivity) getActivity();

        final long id = getArguments().getLong("ALERT_ID");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.delete_dialog))
                .setPositiveButton(getString(R.string.delete_dialog_remove), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.delete(id);
            }
        })
                .setNegativeButton(getString(R.string.delete_dialog_cancel),
                        new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder.create();
    }
}
