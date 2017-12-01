package com.danielsolawa.locationapp.utils;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

import com.danielsolawa.locationapp.service.AlertJobService;

/**
 * Created by NeverForgive on 2017-12-01.
 */

public class AlertUtils {


    public static void scheduleJob(Context ctx){
        ComponentName serviceComponent = new ComponentName(ctx, AlertJobService.class);

        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        builder.setMinimumLatency(1 * 1000);
        builder.setOverrideDeadline(1000 * 60 * 2);


        JobScheduler jobScheduler = ctx.getSystemService(JobScheduler.class);
        jobScheduler.schedule(builder.build());
    }
}
