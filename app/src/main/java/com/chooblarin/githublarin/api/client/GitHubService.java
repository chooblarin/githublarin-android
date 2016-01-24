package com.chooblarin.githublarin.api.client;

import com.chooblarin.githublarin.api.response.FeedsResponse;
import com.chooblarin.githublarin.api.response.SearchResponse;
import com.chooblarin.githublarin.model.Gist;
import com.chooblarin.githublarin.model.Repository;
import com.chooblarin.githublarin.model.User;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface GitHubService {

    @GET("/search/{category}")
    Observable<SearchResponse> query(@Path("category") String category,
                                     @Query("q") String keyword,
                                     @Query("sort") String sort,
                                     @Query("order") String order);
    @GET("/feeds")
    Observable<FeedsResponse> feeds();

    @GET("/gists")
    Observable<List<Gist>> gists();

    @GET("/users/{username}")
    Observable<User> user(@Path("username") String username);

    @GET("/users/{username}/repos")
    Observable<List<Repository>> usersRepositories(@Path("username") String username);

    @GET("/users/{username}/starred")
    Observable<List<Repository>> starredRepositories(@Path("username") String username);
}
