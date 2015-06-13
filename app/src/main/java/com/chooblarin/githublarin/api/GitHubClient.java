package com.chooblarin.githublarin.api;

import com.chooblarin.githublarin.model.Gist;
import com.chooblarin.githublarin.model.Repository;
import com.chooblarin.githublarin.model.User;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

public interface GitHubClient {

    @GET("/gists")
    Observable<List<Gist>> gists();

    @GET("/users/{username}")
    Observable<User> user(@Path("username") String username);

    @GET("/users/{username}/starred")
    Observable<List<Repository>> starredRepositories(@Path("username") String username);
}
