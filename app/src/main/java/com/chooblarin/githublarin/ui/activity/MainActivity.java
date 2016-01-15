package com.chooblarin.githublarin.ui.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.chooblarin.githublarin.Application;
import com.chooblarin.githublarin.R;
import com.chooblarin.githublarin.api.client.GitHubApiClient;
import com.chooblarin.githublarin.databinding.ActivityMainBinding;
import com.chooblarin.githublarin.model.User;
import com.chooblarin.githublarin.ui.fragment.EventFragment;
import com.chooblarin.githublarin.ui.fragment.GistFragment;
import com.chooblarin.githublarin.ui.fragment.StarredFragment;
import com.facebook.drawee.view.SimpleDraweeView;
import com.trello.rxlifecycle.ActivityEvent;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends BaseActivity {

    private static final String EXTRA_USER = "extra_user";

    public static Intent createIntent(Context context, User user) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(EXTRA_USER, user);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    SearchView searchView;
    SimpleDraweeView avatarImage;
    TextView userNameText;
    TextView userLoginText;

    @Inject
    GitHubApiClient apiClient;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setupToolbar(binding.toolbarMain);
        setupDrawerContent();
        setupNavigationView(binding.navigationView);
        showContent(R.id.nav_item_event);

        User user = getIntent().getParcelableExtra(EXTRA_USER);
        setupUserData(user);
        handleIntent(getIntent());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.unbind();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        MenuItem menuItem = menu.findItem(R.id.search_menu_search_view);
        searchView = (SearchView) menuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                binding.drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(binding.navigationView)) {
            binding.drawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void setupComponent() {
        apiClient = Application.get(this).getAppComponent().apiClient();
    }

    private void handleIntent(@NonNull Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            startActivity(SearchResultActivity.createIntent(this, query));
        }
    }

    public void showMyPage() {
        binding.drawerLayout.closeDrawers();
        Intent intent = new Intent(MainActivity.this, MyPageActivity.class);
        startActivity(intent);
    }

    private void setupToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;

        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void setupNavigationView(NavigationView navigationView) {
        View headerView = navigationView.inflateHeaderView(R.layout.nav_header);
        avatarImage = (SimpleDraweeView) headerView.findViewById(R.id.image_avatar_drawer);
        userNameText = (TextView) headerView.findViewById(R.id.text_user_name_drawer);
        userLoginText = (TextView) headerView.findViewById(R.id.text_user_login_drawer);

        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMyPage();
            }
        });
    }

    private void setupDrawerContent() {
        binding.navigationView.setNavigationItemSelectedListener(menuItem -> {
            menuItem.setChecked(true);
            binding.drawerLayout.closeDrawers();
            showContent(menuItem.getItemId());
            return true;
        });
    }

    private void showContent(int menuId) {
        switch (menuId) {
            case R.id.nav_item_event:
                showFragment(new EventFragment(), false);
                break;

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

    private void setupUserData(@Nullable User user) {
        if (null != user) {
            bindUser(user);
        } else {
            apiClient.user()
                    .compose(this.<User>bindUntilEvent(ActivityEvent.STOP))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(_user -> {
                        bindUser(_user);
                    }, throwable -> {
                        throwable.printStackTrace();
                    });
        }
    }

    private void bindUser(User u) {
        if (null != u.avatarUrl) {
            avatarImage.setImageURI(Uri.parse(u.avatarUrl));
        }
        userLoginText.setText(u.login);
        userNameText.setText(u.name);
    }
}
