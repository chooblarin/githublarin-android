package com.chooblarin.githublarin.di;

import com.chooblarin.githublarin.BuildConfig;
import com.chooblarin.githublarin.api.auth.AuthInterceptor;
import com.chooblarin.githublarin.api.client.GitHubService;
import com.chooblarin.githublarin.serializer.LocalDateTimeDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import org.threeten.bp.LocalDateTime;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

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
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
                .create();

        return new Retrofit.Builder()
                .client(okHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(GITHUB_BASE_URL)
                .build()
                .create(GitHubService.class);
    }
}
