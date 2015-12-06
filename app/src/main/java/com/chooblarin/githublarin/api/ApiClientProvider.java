package com.chooblarin.githublarin.api;

import com.chooblarin.githublarin.BuildConfig;
import com.chooblarin.githublarin.api.client.GitHubClient;
import com.chooblarin.githublarin.api.http.Header;
import com.google.gson.Gson;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.io.IOException;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

public class ApiClientProvider {

    public final static String GITHUB_BASE_URL = "https://api.github.com";

    public static GitHubClient gitHubClient(final String authorization) {

        OkHttpClient okHttpClient = new OkHttpClient();
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

    private ApiClientProvider() {
    }
}
