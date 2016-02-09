package com.chooblarin.githublarin.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.chooblarin.githublarin.Application;
import com.chooblarin.githublarin.api.client.GitHubApiClient;
import com.chooblarin.githublarin.di.AppComponent;
import com.chooblarin.githublarin.model.Repository;

import java.io.IOException;

import retrofit.Response;
import timber.log.Timber;

public class StarRepositoryService extends IntentService {

    private static final String TAG = StarRepositoryService.class.getSimpleName();
    private static final String EXTRA_REPOSITORY = "extra_repository";
    private static final String EXTRA_FLAG_STAR = "extra_flag_star";

    public static Intent createIntent(Context context, Repository repository, boolean star) {
        Intent intent = new Intent(context, StarRepositoryService.class);
        intent.putExtra(EXTRA_REPOSITORY, repository);
        intent.putExtra(EXTRA_FLAG_STAR, star);
        return intent;
    }

    public StarRepositoryService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final Repository repository = intent.getParcelableExtra(EXTRA_REPOSITORY);
        boolean star = intent.getBooleanExtra(EXTRA_FLAG_STAR, false);
        assert null != repository;

        AppComponent appComponent = Application.get(this).getAppComponent();
        GitHubApiClient apiClient = appComponent.apiClient();

        try {
            Response<Void> response
                    = (star ? apiClient.star(repository) : apiClient.unstar(repository)).execute();
            int code = response.code();
            if (204 == code) {
                // todo: success
            } else {
                // todo: fail
            }
        } catch (IOException e) {
            Timber.tag(TAG).e(e, null);
        }
    }
}
