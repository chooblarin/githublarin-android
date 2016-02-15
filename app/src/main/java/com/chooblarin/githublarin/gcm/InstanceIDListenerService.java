package com.chooblarin.githublarin.gcm;

import android.content.Intent;

public class InstanceIDListenerService extends com.google.android.gms.iid.InstanceIDListenerService {

    public static final String TAG = InstanceIDListenerService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, RegistrationService.class);
        startService(intent);
    }
}
