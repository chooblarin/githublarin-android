package com.chooblarin.githublarin.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.chooblarin.githublarin.R;
import com.chooblarin.githublarin.constant.Prefs;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import timber.log.Timber;

public class RegistrationService extends IntentService {

    public static final String TAG = RegistrationService.class.getSimpleName();
    private static final String[] TOPICS = {"global"};

    public RegistrationService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        InstanceID instanceID = InstanceID.getInstance(this);
        try {
            String token = instanceID.getToken(getString(R.string.google_project_number),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Timber.tag(TAG).i("GCM Registration Token: " + token);

            // TODO: Implement this method to send any registration to your app's servers.
            sendRegistrationToServer(token);
            subscribeTopics(token);
            sharedPreferences.edit().putBoolean(Prefs.SENT_TOKEN_TO_SERVER, true).apply();

        } catch (IOException e) {
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.

            Timber.tag(TAG).e(e, null);
            sharedPreferences.edit().putBoolean(Prefs.SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Intent registrationComplete = new Intent(Prefs.REGISTRATION_COMPLETE);
        // LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
    }

    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
}
