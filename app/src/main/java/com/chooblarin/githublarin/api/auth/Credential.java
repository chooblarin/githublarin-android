package com.chooblarin.githublarin.api.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Credential {

    public final static String PREFS_GITHUB_API = "prefs_github_api";
    public final static String PREFS_KEY_USER_NAME = "username";
    public final static String PREFS_KEY_PASSWORD = "password";

    private Context context;
    private SharedPreferences gitHubApiPrefs;

    @Inject
    public Credential(Context context) {
        this.context = context;
        gitHubApiPrefs = context.getSharedPreferences(PREFS_GITHUB_API, Context.MODE_PRIVATE);
    }

    public String username() {
        String username = gitHubApiPrefs.getString(PREFS_KEY_USER_NAME, null);

        if (TextUtils.isEmpty(username)) {
            return null;
        } else {
            // return CryptUtil.decrypt(context, PREFS_KEY_USER_NAME, username);
            return username;
        }
    }

    public String password() {
        String password = gitHubApiPrefs.getString(PREFS_KEY_PASSWORD, null);

        if (TextUtils.isEmpty(password)) {
            return null;
        } else {
            // return CryptUtil.decrypt(context, PREFS_KEY_PASSWORD, password);
            return password;
        }
    }

    public boolean save(String username, String password) {
        // String u = CryptUtil.encrypt(context, PREFS_KEY_USER_NAME, username);
        // String p = CryptUtil.encrypt(context, PREFS_KEY_PASSWORD, password);
        return gitHubApiPrefs.edit()
                .putString(PREFS_KEY_USER_NAME, username)
                .putString(PREFS_KEY_PASSWORD, password)
                .commit();
    }

    public static void save(Context context, String username, String password) {
        SharedPreferences.Editor editor
                = context.getSharedPreferences(PREFS_GITHUB_API, Context.MODE_PRIVATE).edit();
        // String u = CryptUtil.encrypt(context, PREFS_KEY_USER_NAME, username);
        // String p = CryptUtil.encrypt(context, PREFS_KEY_PASSWORD, password);
        editor.putString(PREFS_KEY_USER_NAME, username).apply();
        editor.putString(PREFS_KEY_PASSWORD, password).apply();
    }
}
