package com.danielsolawa.locationapp.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

import com.danielsolawa.locationapp.R;
import com.danielsolawa.locationapp.activity.HomeActivity;

/**
 * Created by NeverForgive on 2017-12-01.
 */

public class AlertJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        sendNotification();
        return true;
    }

    private void sendNotification() {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        builder.setContentTitle("Scheduled Notification");
        builder.setContentText("Test test");
        builder.setSmallIcon(R.drawable.i10n);
        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);

        Notification notification = builder.build();
        notificationManager.notify(1, notification);
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
