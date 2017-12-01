package com.danielsolawa.locationapp.service;

import android.app.job.JobParameters;
import android.app.job.JobService;

/**
 * Created by NeverForgive on 2017-12-01.
 */

public class AlertJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
