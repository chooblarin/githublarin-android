package com.chooblarin.githublarin.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.chooblarin.githublarin.BuildConfig;
import com.chooblarin.githublarin.api.auth.Credential;
import com.chooblarin.githublarin.api.client.GitHubClient;
import com.chooblarin.githublarin.api.http.Header;
import com.chooblarin.githublarin.api.response.SearchResponse;
import com.chooblarin.githublarin.model.Gist;
import com.chooblarin.githublarin.model.Repository;
import com.chooblarin.githublarin.model.User;
import com.google.gson.Gson;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.io.IOException;
import java.util.List;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class GitHubApiService extends Service {

    private final static String TAG = "GitHubApiService";

    public final static String GITHUB_BASE_URL = "https://api.github.com";

    private IBinder binder = new GitHubApiBinder();
    private OkHttpClient okHttpClient;
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
            gitHubClient = createGitHubApiClient(authorization);
        }
    }

    public Observable<User> login(String username, String password) {
        String authCredential = Credentials.basic(username, password);
        gitHubClient = createGitHubApiClient(authCredential);
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

    public void feeds() {
        gitHubClient.feeds()
                .map(feedsResponse -> feedsResponse.currentUserUrl)
                .flatMap(url -> Observable
                        .create((Observable.OnSubscribe<Response>) subscriber -> {
                            Request request = new Request.Builder().url(url).build();
                            try {
                                Response response = okHttpClient.newCall(request).execute();
                                subscriber.onNext(response);
                            } catch (IOException e) {
                                e.printStackTrace();
                                subscriber.onError(e);
                            }
                            subscriber.onCompleted();
                        }))
                .map(response -> {
                    String bodyText = null;
                    try {
                        bodyText = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return bodyText;
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.d("mimic", "kore -> " + s);
                    }
                });
    }

    private GitHubClient createGitHubApiClient(@Nullable String authorization) {
        okHttpClient = new OkHttpClient();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(BuildConfig.DEBUG ?
                HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        okHttpClient.interceptors().add(interceptor);
        okHttpClient.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Request.Builder builder = request.newBuilder();
                builder.addHeader(Header.ACCEPT, "application/json");
                if (null != authorization) {
                    builder.addHeader(Header.AUTHORIZATION, authorization);
                }
                return chain.proceed(builder.build());
            }
        });

        return new Retrofit.Builder()
                .client(okHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .baseUrl(GITHUB_BASE_URL)
                .build()
                .create(GitHubClient.class);
    }
}
