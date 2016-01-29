package com.chooblarin.githublarin.api.session;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    static final private String PREFS_API = "api";
    static final private String KEY_NOTIFICATION_LAST_MODIFIED_AT = "notification_last_modified_at";

    static private SessionManager instance;

    public static synchronized SessionManager get(Context context) {
        if (null == instance) {
            instance = new SessionManager(context);
        }
        return instance;
    }

    private SharedPreferences prefs;

    private SessionManager(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_API, Context.MODE_PRIVATE);
    }

    public long getNotificationLastModifiedAt() {
        return prefs.getLong(KEY_NOTIFICATION_LAST_MODIFIED_AT, 0);
    }

    public void setNotificationLastModifiedAt(long millis) {
        prefs.edit().putLong(KEY_NOTIFICATION_LAST_MODIFIED_AT, millis).apply();
    }
}
