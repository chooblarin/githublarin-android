package com.chooblarin.githublarin.service;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.chooblarin.githublarin.R;

public class NotificationService extends IntentService {

    private static final String TAG = NotificationService.class.getSimpleName();
    private static final int NOTIFICATION_ID = 1;

    public NotificationService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Notification notification = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_lock_white_18dp)
                .setContentTitle("content title")
                .setContentText("content text")
                .setSubText("sub text")
                .setContentInfo("content info")
                .setWhen(System.currentTimeMillis())
                .setTicker("Ticker")
                .build();
        NotificationManagerCompat.from(getApplicationContext())
                .notify(NOTIFICATION_ID, notification);
    }
}
