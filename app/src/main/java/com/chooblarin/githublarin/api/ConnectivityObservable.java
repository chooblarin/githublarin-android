package com.chooblarin.githublarin.api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

public class ConnectivityObservable implements Observable.OnSubscribe<Boolean> {

    private final Context context;

    public static Observable<Boolean> from(Context context) {
        return Observable.create(new ConnectivityObservable(context)).share();
    }

    public ConnectivityObservable(Context context) {
        this.context = context;
    }

    @Override
    public void call(Subscriber<? super Boolean> subscriber) {
        final BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                subscriber.onNext(isConnectedToInternet());
            }
        };
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(receiver, intentFilter);
        subscriber.add(unsubscribeOnUiThread(() -> context.unregisterReceiver(receiver)));
    }

    private boolean isConnectedToInternet() {
        ConnectivityManager manager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private static Subscription unsubscribeOnUiThread(final Action0 unsubscribe) {
        return Subscriptions.create(() -> {
            if (Looper.getMainLooper() == Looper.myLooper()) {
                unsubscribe.call();
            } else {
                final Scheduler.Worker inner = AndroidSchedulers.mainThread().createWorker();
                inner.schedule(() -> {
                    unsubscribe.call();
                    inner.unsubscribe();
                });
            }
        });
    }
}
