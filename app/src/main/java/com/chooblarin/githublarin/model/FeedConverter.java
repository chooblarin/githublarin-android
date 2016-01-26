package com.chooblarin.githublarin.model;

import android.net.Uri;

import java.util.regex.Pattern;

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

    final public static Transformer<Feed, Feed> discriminateAction = feed -> feed.map(_feed -> {
        String authorName = _feed.authorName;
        Pattern pattern = Pattern.compile(authorName + " made .+ public");

        if (_feed.title.startsWith(authorName + " starred")) {
            _feed.action = Action.STAR;
            return _feed;
        }

        if (_feed.title.startsWith(authorName + " forked")) {
            _feed.action = Action.FORK;
            return _feed;
        }

        if (_feed.title.startsWith(authorName + " created repository")) {
            _feed.action = Action.CREATE_REPOSITORY;
            return _feed;
        }

        if (pattern.matcher(_feed.title).find()) {
            _feed.action = Action.MAKE_PUBLIC;
            return _feed;
        }

        _feed.action = Action.NONE;
        return _feed;
    });
}
