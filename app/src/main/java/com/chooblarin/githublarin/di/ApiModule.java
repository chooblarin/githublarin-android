package com.chooblarin.githublarin.di;

import com.chooblarin.githublarin.BuildConfig;
import com.chooblarin.githublarin.api.auth.AuthInterceptor;
import com.chooblarin.githublarin.api.client.GitHubService;
import com.google.gson.Gson;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.RxJavaCallAdapterFactory;

@Module(includes = AppModule.class)
public class ApiModule {

    public final static String GITHUB_BASE_URL = "https://api.github.com";

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(AuthInterceptor authInterceptor) {
        OkHttpClient okHttpClient = new OkHttpClient();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(BuildConfig.DEBUG ?
                HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        okHttpClient.interceptors().add(loggingInterceptor);
        okHttpClient.interceptors().add(authInterceptor);
        return okHttpClient;
    }

    @Provides
    @Singleton
    public GitHubService provideGitHubService(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .client(okHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .baseUrl(GITHUB_BASE_URL)
                .build()
                .create(GitHubService.class);
    }
}
