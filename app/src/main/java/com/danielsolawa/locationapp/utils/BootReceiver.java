package com.danielsolawa.locationapp.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by NeverForgive on 2017-09-07.
 */

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        AlertUtils.init(context);
        AlertUtils.scheduleJob();

    }
}
