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
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chooblarin.githublarin.R;
import com.chooblarin.githublarin.databinding.FragmentGistBinding;
import com.chooblarin.githublarin.service.GitHubApiService;
import com.chooblarin.githublarin.ui.adapter.GistAdapter;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class GistFragment extends Fragment implements ServiceConnection {

    private GitHubApiService service;
    private GistAdapter gistAdapter;
    private CompositeSubscription subscriptions;

    FragmentGistBinding binding;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        gistAdapter = new GistAdapter(activity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_gist, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupGistListView(binding.recyclerviewGist);
    }

    @Override
    public void onStart() {
        super.onStart();
        subscriptions = new CompositeSubscription();
        Context context = getActivity().getApplicationContext();
        Intent intent = new Intent(context, GitHubApiService.class);
        context.bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        subscriptions.unsubscribe();
        getActivity().getApplicationContext().unbindService(this);
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
        binding.progressLoadingGist.setVisibility(View.VISIBLE);

        Subscription gists = service.gists()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(_gists -> {
                    binding.progressLoadingGist.setVisibility(View.GONE);
                    gistAdapter.setData(_gists);
                }, throwable -> {
                    binding.progressLoadingGist.setVisibility(View.GONE);
                    throwable.printStackTrace();
                });
        subscriptions.add(gists);
    }

    private void setupGistListView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(gistAdapter);
    }
}
