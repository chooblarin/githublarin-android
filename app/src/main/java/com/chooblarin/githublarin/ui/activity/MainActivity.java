package com.chooblarin.githublarin.ui.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.chooblarin.githublarin.R;
import com.chooblarin.githublarin.service.GitHubApiService;
import com.chooblarin.githublarin.ui.fragment.GistFragment;
import com.chooblarin.githublarin.ui.fragment.StarredFragment;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity
        implements ServiceConnection {

    private GitHubApiService service;
    private CompositeSubscription subscriptions;

    @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @InjectView(R.id.navigation_view)
    NavigationView navigationView;

    @InjectView(R.id.image_avatar_drawer)
    SimpleDraweeView avatarImage;

    @InjectView(R.id.text_user_name_drawer)
    TextView userNameText;

    @InjectView(R.id.text_user_login_drawer)
    TextView userLoginText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        setupToolbar();

        ButterKnife.inject(this);

        setupDrawerContent();
    }

    @Override
    protected void onStart() {
        super.onStart();
        subscriptions = new CompositeSubscription();
        Intent intent = new Intent(getApplicationContext(), GitHubApiService.class);
        getApplicationContext().bindService(intent, this, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        subscriptions.unsubscribe();
        getApplicationContext().unbindService(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        service = ((GitHubApiService.GitHubApiBinder) iBinder).getService();
        setup();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        service = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.container_nav_header})
    public void showMyPage() {
        drawerLayout.closeDrawers();
        Intent intent = new Intent(MainActivity.this, MyPageActivity.class);
        startActivity(intent);
    }

    private void setupToolbar() {
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;

        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void setupDrawerContent() {
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            menuItem.setChecked(true);
            drawerLayout.closeDrawers();
            showContent(menuItem.getItemId());
            return true;
        });
    }

    private void showContent(int menuId) {
        switch (menuId) {
            case R.id.nav_item_gist:
                showFragment(new GistFragment());
                break;

            case R.id.nav_item_starred:
                showFragment(new StarredFragment());
                break;
        }
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_content_main, fragment)
                .commit();
    }

    private void setup() {
        Subscription user = service.user()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user1 -> {
                    if (null != user1.avatarUrl) {
                        avatarImage.setImageURI(Uri.parse(user1.avatarUrl));
                    }
                    userLoginText.setText(user1.login);
                    userNameText.setText(user1.name);
                }, throwable -> {
                    throwable.printStackTrace();
                });
        subscriptions.add(user);
    }
}
