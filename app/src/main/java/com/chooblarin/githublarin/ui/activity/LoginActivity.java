package com.chooblarin.githublarin.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.chooblarin.githublarin.R;
import com.chooblarin.githublarin.api.auth.Credential;
import com.chooblarin.githublarin.service.GitHubApiService;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class LoginActivity extends AppCompatActivity implements ServiceConnection {

    public static Intent createIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @InjectView(R.id.edit_text_username)
    EditText userNameText;

    @InjectView(R.id.edit_text_password)
    EditText passwordText;

    private GitHubApiService service;
    private CompositeSubscription subscriptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        subscriptions = new CompositeSubscription();
        Context context = getApplicationContext();
        Intent intent = new Intent(context, GitHubApiService.class);
        context.bindService(intent, this, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
        subscriptions.unsubscribe();
        getApplicationContext().unbindService(this);
    }

    @OnClick({R.id.text_login_button})
    protected void onLoginClick() {
        String username = userNameText.getText().toString();
        String password = passwordText.getText().toString();
        login(username, password);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        service = ((GitHubApiService.GitHubApiBinder) iBinder).getService();
        checkAuth();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        service = null;
    }

    private void checkAuth() {
        Context context = getApplicationContext();
        String username = Credential.username(context);
        String password = Credential.password(context);

        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            login(username, password);
        }
    }

    private void login(String username, String password) {
        Subscription subscription = service.login(username, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                    service.setUser(user);
                    Credential.save(getApplicationContext(), username, password);

                    startActivity(MainActivity.createIntent(LoginActivity.this, user));
                    finish();

                }, throwable -> {
                    throwable.printStackTrace();
                    Toast.makeText(getApplicationContext(), "ログイン失敗", Toast.LENGTH_SHORT).show();
                });
        subscriptions.add(subscription);
    }
}
