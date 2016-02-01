package com.chooblarin.githublarin.api.client;

import android.content.Context;

import com.chooblarin.githublarin.api.auth.Credential;
import com.chooblarin.githublarin.api.session.SessionManager;
import com.chooblarin.githublarin.model.Feed;
import com.chooblarin.githublarin.model.FeedConverter;
import com.chooblarin.githublarin.model.FeedParser;
import com.chooblarin.githublarin.model.Gist;
import com.chooblarin.githublarin.model.Notification;
import com.chooblarin.githublarin.model.Repository;
import com.chooblarin.githublarin.model.User;
import com.chooblarin.githublarin.util.DateTimeUtils;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.threeten.bp.LocalDateTime;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Observable.Transformer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.Exceptions;
import rx.schedulers.Schedulers;

public class GitHubApiClient {

    final Context context;
    final OkHttpClient httpClient;
    final GitHubService gitHubService;
    final Credential credential;

    private volatile String currentUserUrl;

    @Inject
    public GitHubApiClient(Context context,
                           OkHttpClient httpClient,
                           GitHubService gitHubService,
                           Credential credential) {
        this.context = context;
        this.httpClient = httpClient;
        this.gitHubService = gitHubService;
        this.credential = credential;
    }

    public Observable<List<Repository>> searchRepository(String keyword, boolean reverse) {
        return gitHubService.query("repositories", keyword, "stars", reverse ? "desc" : "asc")
                .map(searchResponse -> searchResponse.items)
                .compose(applySchedulers());
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
        }).flatMap(gitHubService::user).compose(applySchedulers());
    }

    public Observable<User> user() {
        String username = credential.username();
        return gitHubService
                .user(null != username ? username : "")
                .compose(applySchedulers());
    }

    public Observable<List<Notification>> notifications() {
        long millis = SessionManager.get(context).getNotificationLastModifiedAt();
        final Observable<List<Notification>> observable;
        if (0 == millis) {
            observable = gitHubService.notifications();
        } else {
            LocalDateTime dateTime = DateTimeUtils.localDateTimeFrom(millis);
            observable = gitHubService.notifications(DateTimeUtils.timeStamp(dateTime));
        }
        return observable.compose(applySchedulers());
    }

    public Observable<List<Feed>> feeds(int page) {
        Observable<String> requestUrl;
        if (null == currentUserUrl) {
            requestUrl = gitHubService.feeds()
                    .map(feedsResponse -> {
                        currentUserUrl = feedsResponse.currentUserUrl;
                        return feedsResponse.currentUserUrl;
                    });
        } else {
            requestUrl = Observable.just(currentUserUrl);
        }
        return requestUrl.flatMap(url -> execRequest(url + "&page=" + page))
                .map(FeedParser::parseString)
                .flatMap(Observable::from)
                .compose(FeedConverter.expandThumbnail)
                .compose(FeedConverter.discriminateAction)
                .toList()
                .compose(this.<List<Feed>>applySchedulers());
    }

    public Observable<List<Repository>> repositories() {
        String username = credential.username();
        return gitHubService.usersRepositories(null != username ? username : "")
                .compose(applySchedulers());
    }

    public Observable<List<Gist>> gists() {
        return gitHubService.gists()
                .compose(applySchedulers());
    }

    public Observable<List<Gist>> starredGists() {
        return gitHubService.starredGists()
                .compose(applySchedulers());
    }

    public Observable<List<Repository>> starredRepositories() {
        String username = credential.username();
        return gitHubService.starredRepositories(null != username ? username : "")
                .compose(applySchedulers());
    }

    private Observable<String> execRequest(String url) {
        Request request = new Request.Builder().url(url).build();
        return Observable.create((Subscriber<? super Response> subscriber) -> {
            try {
                // todo : A resource was acquired at attached stack trace but never released. See java.io.Closeable for information on avoiding resource leaks.
                Response response = httpClient.newCall(request).execute();
                subscriber.onNext(response);
                subscriber.onCompleted();
            } catch (IOException e) {
                subscriber.onError(e);
            }
        }).map(response -> {
            try {
                return response.body().string();
            } catch (IOException e) {
                throw Exceptions.propagate(e);
            }
        }).compose(applySchedulers());
    }

    private <T> Transformer<T, T> applySchedulers() {
        return new Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> observable) {
                return observable
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }
}
