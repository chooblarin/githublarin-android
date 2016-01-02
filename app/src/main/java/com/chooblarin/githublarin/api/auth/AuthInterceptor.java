package com.chooblarin.githublarin.api.auth;

import com.chooblarin.githublarin.api.http.Header;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import javax.inject.Inject;

public class AuthInterceptor implements Interceptor {

    private String authorization;

    final Credential credential;

    @Inject
    public AuthInterceptor(Credential credential) {
        this.credential = credential;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder builder = request.newBuilder();
        builder.addHeader(Header.ACCEPT, "application/json");
        if (null == authorization) {
            String username = credential.username();
            String password = credential.password();
            authorization = Credentials.basic(username, password);
        }
        builder.addHeader(Header.AUTHORIZATION, authorization);
        return chain.proceed(builder.build());
    }
}
