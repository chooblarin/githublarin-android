package com.chooblarin.githublarin.api.client;

import com.chooblarin.githublarin.api.auth.Credential;
import com.chooblarin.githublarin.model.Feed;
import com.chooblarin.githublarin.model.FeedConverter;
import com.chooblarin.githublarin.model.FeedParser;
import com.chooblarin.githublarin.model.Gist;
import com.chooblarin.githublarin.model.Repository;
import com.chooblarin.githublarin.model.User;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.exceptions.Exceptions;

public class GitHubApiClient {

    final OkHttpClient httpClient;
    final Credential credential;
    final GitHubService gitHubService;

    private volatile String currentUserUrl;

    @Inject
    public GitHubApiClient(OkHttpClient httpClient, GitHubService gitHubService, Credential credential) {
        this.httpClient = httpClient;
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

    private Observable<Response> getFeeds(final int page) {
        Observable<String> requestUrl;
        if (null == currentUserUrl) {
            requestUrl = gitHubService.feeds()
                    .map(feedsResponse -> feedsResponse.currentUserUrl);
        } else {
            requestUrl = Observable.just(currentUserUrl);
        }

        return requestUrl.map(url -> {
            Request request = new Request.Builder().url(url + "&page=" + page).build();
            try {
                // todo : A resource was acquired at attached stack trace but never released. See java.io.Closeable for information on avoiding resource leaks.
                return httpClient.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
                throw Exceptions.propagate(e);
            }
        }).onErrorResumeNext(throwable -> {
            currentUserUrl = null;
            return getFeeds(page);
        });
    }

    public Observable<List<Feed>> feeds(int page) {
        return getFeeds(page).map(response -> {
            String bodyText = null;
            try {
                bodyText = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bodyText;
        }).map(FeedParser::parseString)
                .flatMap(Observable::from)
                .compose(FeedConverter.expandThumbnail)
                .toList();
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
