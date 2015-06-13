package com.chooblarin.githublarin.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.chooblarin.githublarin.R;
import com.chooblarin.githublarin.api.GitHubClient;
import com.chooblarin.githublarin.model.Gist;
import com.chooblarin.githublarin.model.Repository;
import com.chooblarin.githublarin.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit.RestAdapter;
import rx.Observable;

public class GitHubApiService extends Service {

    public final static String GITHUB_BASE_URL = "https://api.github.com";

    private IBinder binder = new GitHubApiBinder();
    private GitHubClient gitHubClient;

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
            Gson gson = new GsonBuilder().create();
            gitHubClient = new RestAdapter.Builder()
                    .setRequestInterceptor(request -> {
                        String token = getString(R.string.github_access_token);
                        request.addHeader("Authorization", "token " + token);
                    })
                    .setEndpoint(GITHUB_BASE_URL)
                    // .setConverter(new GsonConverter(gson))
                    .build()
                    .create(GitHubClient.class);
        }
    }

    public Observable<User> user() {
        return gitHubClient.user("chooblarin");
    }

    public Observable<List<Repository>> starredRepositories() {
        return gitHubClient.starredRepositories("chooblarin");
    }

    public Observable<List<Gist>> gists() {
        return gitHubClient.gists();
    }
}
