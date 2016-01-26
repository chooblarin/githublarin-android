package com.chooblarin.githublarin.model;

import android.net.Uri;

import rx.Observable.Transformer;

public class FeedConverter {

    final public static Transformer<Feed, Feed> expandThumbnail = feed -> feed.map(_feed -> {
        Uri uri = Uri.parse(_feed.thumbnail);
        _feed.thumbnail = new Uri.Builder().scheme(uri.getScheme())
                .authority(uri.getAuthority())
                .path(uri.getPath())
                .appendQueryParameter("v", uri.getQueryParameter("v"))
                .appendQueryParameter("s", "120")
                .build().toString();
        return _feed;
    });
}
