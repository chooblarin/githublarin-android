package com.chooblarin.githublarin.api.client;

import com.chooblarin.githublarin.api.auth.Credential;
import com.chooblarin.githublarin.model.Entry;
import com.chooblarin.githublarin.model.FeedParser;
import com.chooblarin.githublarin.model.Gist;
import com.chooblarin.githublarin.model.Repository;
import com.chooblarin.githublarin.model.User;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;

public class GitHubApiClient {

    final Credential credential;
    final GitHubService gitHubService;

    @Inject
    public GitHubApiClient(GitHubService gitHubService, Credential credential) {
        this.gitHubService = gitHubService;
        this.credential = credential;
    }

    public Observable<List<Repository>> searchRepository(String keyword, boolean reverse) {
        return gitHubService.query("repositories", keyword, "stars", reverse ? "desc" : "asc")
                .map(searchResponse -> searchResponse.items);
    }

    public Observable<User> login(final String username, final String password) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                if (credential.save(username, password)) {
                    subscriber.onNext(username);
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(new Error()); // todo
                }
            }
        }).flatMap(gitHubService::user);
    }

    public Observable<User> user() {
        String username = credential.username();
        return gitHubService.user(null != username ? username : "");
    }

    public Observable<List<Entry>> entries() {
        return gitHubService.feeds().map(feedsResponse -> feedsResponse.currentUserUrl)
                .flatMap(url -> Observable.create((Observable.OnSubscribe<Response>) subscriber -> {
                    Request request = new Request.Builder().url(url).build();
                    try {
                        // todo : A resource was acquired at attached stack trace but never released. See java.io.Closeable for information on avoiding resource leaks.
                        Response response = new OkHttpClient().newCall(request).execute();
                        subscriber.onNext(response);
                    } catch (IOException e) {
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
                .map(FeedParser::parseString);

    }

    public Observable<List<Repository>> repositories() {
        String username = credential.username();
        return gitHubService.usersRepositories(null != username ? username : "");
    }

    public Observable<List<Gist>> gists() {
        return gitHubService.gists();
    }

    public Observable<List<Repository>> starredRepositories() {
        String username = credential.username();
        return gitHubService.starredRepositories(null != username ? username : "");
    }
}
