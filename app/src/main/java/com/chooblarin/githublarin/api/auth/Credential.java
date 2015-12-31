package com.chooblarin.githublarin.api.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.chooblarin.githublarin.util.CryptUtil;

public class Credential {

    public final static String PREFS_GITHUB_API = "prefs_github_api";
    public final static String PREFS_KEY_USER_NAME = "username";
    public final static String PREFS_KEY_PASSWORD = "password";

    private Context context;
    private SharedPreferences githubApiPrefs;

    public Credential(Context context) {
        this.context = context;
        githubApiPrefs = context.getSharedPreferences(PREFS_GITHUB_API, Context.MODE_PRIVATE);
    }

    public String username() {
        String username = githubApiPrefs.getString(PREFS_KEY_USER_NAME, null);

        if (TextUtils.isEmpty(username)) {
            return null;
        } else {
            return CryptUtil.decrypt(context, PREFS_KEY_USER_NAME, username);
        }
    }

    @Nullable
    public static String username(Context context) {
        SharedPreferences prefs
                = context.getSharedPreferences(PREFS_GITHUB_API, Context.MODE_PRIVATE);
        String username = prefs.getString(PREFS_KEY_USER_NAME, null);

        if (TextUtils.isEmpty(username)) {
            return null;
        } else {
            return CryptUtil.decrypt(context, PREFS_KEY_USER_NAME, username);
        }
    }

    @Nullable
    public static String password(Context context) {
        SharedPreferences prefs
                = context.getSharedPreferences(PREFS_GITHUB_API, Context.MODE_PRIVATE);
        String password = prefs.getString(PREFS_KEY_PASSWORD, null);

        if (TextUtils.isEmpty(password)) {
            return null;
        } else {
            return CryptUtil.decrypt(context, PREFS_KEY_PASSWORD, password);
        }
    }

    public static void save(Context context, String username, String password) {
        SharedPreferences.Editor editor
                = context.getSharedPreferences(PREFS_GITHUB_API, Context.MODE_PRIVATE).edit();
        String u = CryptUtil.encrypt(context, PREFS_KEY_USER_NAME, username);
        String p = CryptUtil.encrypt(context, PREFS_KEY_PASSWORD, password);
        editor.putString(PREFS_KEY_USER_NAME, u).apply();
        editor.putString(PREFS_KEY_PASSWORD, p).apply();
    }
}
