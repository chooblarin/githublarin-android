package com.chooblarin.githublarin.gcm;

import android.app.Notification;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;

import com.chooblarin.githublarin.R;

import timber.log.Timber;

public class GcmListenerService extends com.google.android.gms.gcm.GcmListenerService {

    public static final String TAG = GcmListenerService.class.getSimpleName();
    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Timber.tag(TAG).d("From: " + from);
        Timber.tag(TAG).d("Message: " + message);

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }

        sendNotification(message);
    }

    private void sendNotification(String message) {
        Notification notification = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_lock_white_18dp)
                .setContentTitle("content title")
                .setContentText("content text")
                .setSubText("sub text")
                .setContentInfo(message)
                .setWhen(System.currentTimeMillis())
                .setTicker("Ticker")
                .build();
        NotificationManagerCompat.from(getApplicationContext())
                .notify(NOTIFICATION_ID, notification);
    }
}
