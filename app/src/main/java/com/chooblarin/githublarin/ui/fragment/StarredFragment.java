package com.chooblarin.githublarin.ui.fragment;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chooblarin.githublarin.R;
import com.chooblarin.githublarin.databinding.FragmentStarredBinding;
import com.chooblarin.githublarin.service.GitHubApiService;
import com.chooblarin.githublarin.ui.adapter.RepositoryAdapter;
import com.trello.rxlifecycle.FragmentEvent;
import com.trello.rxlifecycle.components.support.RxFragment;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class StarredFragment extends RxFragment
        implements ServiceConnection {

    private GitHubApiService service;
    private RepositoryAdapter repositoryAdapter;

    FragmentStarredBinding binding;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        repositoryAdapter = new RepositoryAdapter(activity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_starred, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupStarredList(binding.recyclerviewStarred);
    }

    @Override
    public void onStart() {
        super.onStart();
        Context context = getActivity().getApplicationContext();
        Intent intent = new Intent(context, GitHubApiService.class);
        context.bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().getApplicationContext().unbindService(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.unbind();
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

    private void setup() {
        binding.progressLoadingStarred.setVisibility(View.VISIBLE);

        service.starredRepositories()
                .compose(bindUntilEvent(FragmentEvent.STOP))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(_repositories -> {
                    binding.progressLoadingStarred.setVisibility(View.GONE);
                    repositoryAdapter.setData(_repositories);
                }, throwable -> {
                    binding.progressLoadingStarred.setVisibility(View.GONE);
                    throwable.printStackTrace();
                });
    }

    private void setupStarredList(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(repositoryAdapter);
    }
}
