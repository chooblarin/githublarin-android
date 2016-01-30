package com.chooblarin.githublarin.api;

import android.content.Context;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import retrofit.HttpException;
import rx.Observable;
import rx.functions.Func1;

public class RetryWithConnectivityIncremental implements Func1<Observable<? extends Throwable>, Observable<?>> {

    private final long maxTimeout;
    private final TimeUnit timeUnit;
    private final Observable<Boolean> isConnected;
    private final long startTimeOut;
    private long timeout;


    public RetryWithConnectivityIncremental(Context context, long startTimeOut, long maxTimeout, TimeUnit timeUnit) {
        this.startTimeOut = startTimeOut;
        this.maxTimeout = maxTimeout;
        this.timeUnit = timeUnit;
        this.timeout = startTimeOut;
        isConnected = getConnectivityObservable(context);
    }

    @Override
    public Observable<?> call(Observable<? extends Throwable> observable) {
        return observable.flatMap((Throwable throwable) -> {

            if (throwable instanceof HttpException) {
                return isConnected;
            } else {
                return Observable.error(throwable);
            }
        }).compose(attachIncrementalTimeout());
    }

    private Observable.Transformer<Boolean, Boolean> attachIncrementalTimeout() {
        return observable -> observable.timeout(timeout, timeUnit)
                .doOnError(throwable -> {
                    if (throwable instanceof TimeoutException) {
                        timeout = timeout > maxTimeout ? maxTimeout : timeout + startTimeOut;
                    }
                });
    }

    private Observable<Boolean> getConnectivityObservable(Context context) {
        return ConnectivityObservable.from(context)
                .distinctUntilChanged()
                .filter(isConnected -> isConnected);
    }
}
