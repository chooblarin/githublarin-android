package com.chooblarin.githublarin.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.chooblarin.githublarin.Application;
import com.chooblarin.githublarin.R;
import com.chooblarin.githublarin.api.auth.Credential;
import com.chooblarin.githublarin.api.client.GitHubApiClient;
import com.chooblarin.githublarin.di.AppComponent;
import com.chooblarin.githublarin.model.User;
import com.trello.rxlifecycle.ActivityEvent;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class StartupActivity extends BaseActivity {

    GitHubApiClient apiClient;
    Credential credential;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        checkAuth();
    }

    @Override
    protected void setupComponent() {
        AppComponent appComponent = Application.get(this).getAppComponent();
        apiClient = appComponent.apiClient();
        credential = appComponent.credential();
    }

    private void checkAuth() {
        String username = credential.username();
        String password = credential.password();

        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            login(username, password);
        } else {
            startActivity(LoginActivity.createIntent(this));
            finish();
        }
    }

    private void login(String username, String password) {
        apiClient.login(username, password)
                .compose(this.<User>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                    Credential.save(getApplicationContext(), username, password);

                    startActivity(MainActivity.createIntent(this, user));
                    finish();

                }, throwable -> {
                    Toast.makeText(getApplicationContext(), "ログイン失敗", Toast.LENGTH_SHORT).show();
                    startActivity(LoginActivity.createIntent(this));
                });
    }
}
