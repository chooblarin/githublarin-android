package com.chooblarin.githublarin.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.chooblarin.githublarin.R;
import com.chooblarin.githublarin.api.auth.Credential;
import com.chooblarin.githublarin.service.GitHubApiService;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class StartupActivity extends AppCompatActivity {

    final private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            service = ((GitHubApiService.GitHubApiBinder) iBinder).getService();
            checkAuth();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private GitHubApiService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        Context context = getApplicationContext();
        Intent intent = new Intent(context, GitHubApiService.class);
        context.bindService(intent, connection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getApplicationContext().unbindService(connection);
    }

    private void checkAuth() {
        Context context = getApplicationContext();
        String username = Credential.username(context);
        String password = Credential.password(context);

        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            login(username, password);
        } else {
            startActivity(LoginActivity.createIntent(this));
            finish();
        }
    }

    private void login(String username, String password) {
        service.login(username, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                    service.setUser(user);
                    Credential.save(getApplicationContext(), username, password);

                    startActivity(MainActivity.createIntent(this, user));
                    finish();

                }, throwable -> {
                    Toast.makeText(getApplicationContext(), "ログイン失敗", Toast.LENGTH_SHORT).show();
                    startActivity(LoginActivity.createIntent(this));
                });
    }
}
