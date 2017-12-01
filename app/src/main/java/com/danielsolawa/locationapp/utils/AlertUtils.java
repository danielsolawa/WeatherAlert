package com.danielsolawa.locationapp.utils;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.nfc.Tag;
import android.util.Log;

import com.danielsolawa.locationapp.service.AlertJobService;

/**
 * Created by NeverForgive on 2017-12-01.
 */

public class AlertUtils {

    public static final int JOB_ID = 1;
    public static final String TAG = AlertUtils.class.getSimpleName();

    private static  JobScheduler jobScheduler;
    private static Context ctx;

    public static void init(Context ctx){
        AlertUtils.ctx = ctx;
        jobScheduler = (JobScheduler) ctx.getSystemService(Context.JOB_SCHEDULER_SERVICE);
    }


    public static void scheduleJob(){
        ComponentName componentName = new ComponentName(ctx, AlertJobService.class);
        JobInfo.Builder builder =
                new JobInfo.Builder(JOB_ID, componentName);
        builder.setPersisted(true);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setPeriodic(1000 * 60 * 30);
        builder.setRequiresDeviceIdle(false);
        builder.setRequiresCharging(false);

        int result = jobScheduler.schedule(builder.build());

        if(result <= 0){
            Log.d(TAG, "something went wrong");
        }
    }


    public static  void stopJob(){
        jobScheduler.cancel(JOB_ID);
    }
}
