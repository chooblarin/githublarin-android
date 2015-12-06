package com.chooblarin.githublarin.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.chooblarin.githublarin.api.ApiClientProvider;
import com.chooblarin.githublarin.api.auth.Credential;
import com.chooblarin.githublarin.api.client.GitHubClient;
import com.chooblarin.githublarin.api.response.SearchResponse;
import com.chooblarin.githublarin.model.Gist;
import com.chooblarin.githublarin.model.Repository;
import com.chooblarin.githublarin.model.User;
import com.squareup.okhttp.Credentials;

import java.util.List;

import rx.Observable;

public class GitHubApiService extends Service {

    private final static String TAG = "GitHubApiService";

    private IBinder binder = new GitHubApiBinder();
    private GitHubClient gitHubClient;
    private User user;

    public class GitHubApiBinder extends Binder {
        public GitHubApiService getService() {
            return GitHubApiService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (null == gitHubClient) {
            Context context = getApplicationContext();

            String username = Credential.username(context);
            String password = Credential.password(context);

            String authorization = null;
            if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                authorization = Credentials.basic(username, password);
            }
            gitHubClient = ApiClientProvider.gitHubClient(authorization);
        }
    }

    public Observable<User> login(String username, String password) {
        String authCredential = Credentials.basic(username, password);
        gitHubClient = ApiClientProvider.gitHubClient(authCredential);
        return gitHubClient.user(username);
    }

    public Observable<SearchResponse> searchRepository(String keyword, boolean reverse) {
        return gitHubClient.query("repositories", keyword, "stars", reverse ? "desc" : "asc");
    }

    public Observable<User> user() {
        return gitHubClient.user(null != user ? user.login : "");
    }

    public Observable<List<Repository>> starredRepositories() {
        return gitHubClient.starredRepositories(null != user ? user.login : "");
    }

    public Observable<List<Repository>> repositories() {
        return gitHubClient.usersRepositories(null != user ? user.login : "");
    }

    public Observable<List<Gist>> gists() {
        return gitHubClient.gists();
    }

    public void setUser(User user) {
        this.user = user;
    }
}
