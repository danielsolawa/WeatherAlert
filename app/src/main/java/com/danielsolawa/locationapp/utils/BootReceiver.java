package com.danielsolawa.locationapp.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.danielsolawa.locationapp.service.AlertIntentService;

/**
 * Created by NeverForgive on 2017-09-07.
 */

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){
            AlarmManager alarmManager = (AlarmManager)
                    context.getSystemService(Context.ALARM_SERVICE);

            Intent alarmIntent = new Intent(context, AlertIntentService.class);
            PendingIntent pendingIntent = PendingIntent.getService(context,
                    0, alarmIntent, 0);

            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + Constants.THIRTY_SECONDS,
                    Constants.SIX_HOURS,
                    pendingIntent);
        }

    }
}
