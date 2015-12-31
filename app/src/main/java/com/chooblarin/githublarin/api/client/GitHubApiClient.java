package com.chooblarin.githublarin.api.client;

import com.chooblarin.githublarin.api.auth.Credential;
import com.chooblarin.githublarin.model.User;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;

public class GitHubApiClient {

    private User user;
    final Credential credential;
    final GitHubService gitHubService;

    @Inject
    public GitHubApiClient(GitHubService gitHubService, Credential credential) {
        this.gitHubService = gitHubService;
        this.credential = credential;
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

    public void setUser(User user) {
        this.user = user;
    }
}
