package com.danielsolawa.locationapp.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.danielsolawa.locationapp.service.AlertIntentService;

/**
 * Created by NeverForgive on 2017-11-25.
 */

public class AlarmReceiver extends WakefulBroadcastReceiver {

    private static final String TAG = AlarmReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "-------------------------> Receiver");
        ComponentName comp = new ComponentName(context.getPackageName(),
                AlertIntentService.class.getName());
        intent.setComponent(comp);

        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}
