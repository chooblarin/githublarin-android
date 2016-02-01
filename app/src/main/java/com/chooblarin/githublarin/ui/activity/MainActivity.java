package com.chooblarin.githublarin.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
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
import com.chooblarin.githublarin.ui.fragment.FeedFragment;
import com.chooblarin.githublarin.ui.fragment.GistFragment;
import com.chooblarin.githublarin.ui.fragment.StarredRepositoryFragment;
import com.facebook.drawee.view.SimpleDraweeView;
import com.trello.rxlifecycle.ActivityEvent;

import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends BaseActivity {

    private static final String EXTRA_USER = "extra_user";

    public static Intent createIntent(Context context, User user) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(EXTRA_USER, user);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    SimpleDraweeView avatarImage;
    TextView userNameText;
    TextView userLoginText;

    GitHubApiClient apiClient;

    private ActivityMainBinding binding;
    private int selectedPageId = R.id.nav_item_feed;

    @Override
    protected void setupComponent() {
        apiClient = Application.get(this).getAppComponent().apiClient();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setupToolbar(binding.toolbarMain);
        setupDrawerContent();
        setupNavigationView(binding.navigationView);
        showContentPage(selectedPageId);

        User user = getIntent().getParcelableExtra(EXTRA_USER);
        setupUserData(user);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.unbind();
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
                binding.drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.notification_menu:
                startActivity(NotificationListActivity.createIntent(this));
                return true;
            case R.id.search_menu:
                startActivity(SearchActivity.createIntent(this));
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
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
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
            selectedPageId = menuItem.getItemId();
            showContentPage(selectedPageId);
            return true;
        });
    }

    private void showContentPage(int menuId) {
        switch (menuId) {
            case R.id.nav_item_feed:
                showFragment(new FeedFragment(), false);
                break;

            case R.id.nav_item_gist:
                showFragment(new GistFragment(), false);
                break;

            case R.id.nav_item_starred:
                showFragment(new StarredRepositoryFragment(), false);
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
                    .subscribe(_user -> {
                        bindUser(_user);
                    }, throwable -> {
                        Timber.e(throwable, null);
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
