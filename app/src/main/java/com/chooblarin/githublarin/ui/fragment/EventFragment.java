package com.chooblarin.githublarin.ui.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chooblarin.githublarin.R;
import com.chooblarin.githublarin.api.ApiClientProvider;
import com.chooblarin.githublarin.api.auth.Credential;
import com.chooblarin.githublarin.api.client.GitHubClient;
import com.chooblarin.githublarin.databinding.FragmentEventBinding;
import com.chooblarin.githublarin.model.Entry;
import com.chooblarin.githublarin.model.FeedParser;
import com.chooblarin.githublarin.ui.adapter.EventAdapter;
import com.chooblarin.githublarin.ui.listener.OnItemClickListener;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.trello.rxlifecycle.FragmentEvent;
import com.trello.rxlifecycle.components.support.RxFragment;

import java.io.IOException;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class EventFragment extends RxFragment implements OnItemClickListener {

    EventAdapter eventAdapter;
    GitHubClient gitHubClient;
    FragmentEventBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventAdapter = new EventAdapter(getActivity());
        eventAdapter.setOnItemClickListener(this);

        Context context = getActivity().getApplicationContext();
        String username = Credential.username(context);
        String password = Credential.password(context);

        String authorization = null;
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            authorization = Credentials.basic(username, password);
        }
        gitHubClient = ApiClientProvider.gitHubClient(authorization);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_event, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupEventListView(binding.recyclerviewEvent);
        gitHubClient.feeds()
                .map(feedsResponse -> feedsResponse.currentUserUrl)
                .flatMap(url -> Observable.create((Observable.OnSubscribe<Response>) subscriber -> {
                    Request request = new Request.Builder().url(url).build();
                    try {
                        Response response = new OkHttpClient().newCall(request).execute(); // todo
                        subscriber.onNext(response);
                    } catch (IOException e) {
                        subscriber.onError(e);
                    }
                    subscriber.onCompleted();
                }))
                .map(response -> {
                    String bodyText = null;
                    try {
                        bodyText = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return bodyText;
                })
                .map(FeedParser::parseString)
                .compose(this.<List<Entry>>bindUntilEvent(FragmentEvent.STOP))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Entry>>() {
                    @Override
                    public void call(List<Entry> entries) {
                        eventAdapter.addAll(entries);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.unbind();
    }

    @Override
    public void onItemClick(View view, int position) {
        // todo
    }

    private void setupEventListView(RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(eventAdapter);
    }
}
