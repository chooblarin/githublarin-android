package com.chooblarin.githublarin.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.chooblarin.githublarin.R;
import com.chooblarin.githublarin.model.User;
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
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity
        implements ServiceConnection {

    private static final String EXTRA_USER = "extra_user";

    public static Intent createIntent(Context context, User user) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(EXTRA_USER, user);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    private GitHubApiService service;
    private CompositeSubscription subscriptions;

    @InjectView(R.id.toolbar_main)
    Toolbar toolbar;

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

    private SearchView searchView;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        setupToolbar();

        user = getIntent().getParcelableExtra(EXTRA_USER);
        if (null != user) {
            bindUser(user);
        }

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem menuItem = menu.findItem(R.id.search_menu_search_view);
        searchView = (SearchView) menuItem.getActionView();
        setupSearchView();
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

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        service = ((GitHubApiService.GitHubApiBinder) iBinder).getService();
        if (null == user) {
            setupUserData();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        service = null;
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
                showFragment(new GistFragment(), false);
                break;

            case R.id.nav_item_starred:
                showFragment(new StarredFragment(), false);
                break;
        }
    }

    private void showFragment(Fragment fragment, boolean stack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (stack) transaction.addToBackStack(null);
        transaction.replace(R.id.container_content_main, fragment).commit();
    }

    private void setupUserData() {
        Subscription user = service.user()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(_user -> {
                    this.user = _user;
                    bindUser(_user);
                }, throwable -> {
                    throwable.printStackTrace();
                });
        subscriptions.add(user);
    }

    private void bindUser(User u) {
        if (null != u.avatarUrl) {
            avatarImage.setImageURI(Uri.parse(u.avatarUrl));
        }
        userLoginText.setText(u.login);
        userNameText.setText(u.name);
    }

    private void setupSearchView() {
        searchView.setQueryHint("Search Repository");
        searchView.setIconifiedByDefault(true);
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnFocusChangeListener((_view, hasFocus) -> {
            if (!hasFocus) {
                toolbar.collapseActionView();
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)) {
                    getSupportFragmentManager().popBackStack();
                    Intent intent = SearchResultActivity.createIntent(MainActivity.this, query);
                    startActivity(intent);
                }
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
}
