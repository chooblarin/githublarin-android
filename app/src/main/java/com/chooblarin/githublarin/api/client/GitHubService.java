package com.chooblarin.githublarin.api.client;

import com.chooblarin.githublarin.api.response.FeedsResponse;
import com.chooblarin.githublarin.api.response.SearchResponse;
import com.chooblarin.githublarin.model.Gist;
import com.chooblarin.githublarin.model.Notification;
import com.chooblarin.githublarin.model.Repository;
import com.chooblarin.githublarin.model.User;

import java.util.List;

import retrofit.Call;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.PUT;
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

    @GET("/gists/starred")
    Observable<List<Gist>> starredGists();

    @GET("/users/{username}")
    Observable<User> user(@Path("username") String username);

    @GET("/users/{username}/repos")
    Observable<List<Repository>> usersRepositories(@Path("username") String username);

    @GET("/users/{username}/starred")
    Observable<List<Repository>> starredRepositories(@Path("username") String username);

    @PUT("/user/starred/{owner}/{repo}")
    @Headers("Content-Length: 0")
    Call<Void> starRepository(@Path("owner") String owner, @Path("repo") String repo);

    @DELETE("/user/starred/{owner}/{repo}")
    Call<Void> unstarRepository(@Path("owner") String owner, @Path("repo") String repo);

    @GET("/notifications")
    Observable<List<Notification>> notifications();

    @GET("/notifications")
    Observable<List<Notification>> notifications(@Header("If-Modified-Since") String timestamp);

    @GET("/notifications")
    Call<List<Notification>> notificationsSync(@Header("If-Modified-Since") String timestamp);
}
