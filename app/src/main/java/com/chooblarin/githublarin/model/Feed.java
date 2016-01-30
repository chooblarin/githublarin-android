package com.chooblarin.githublarin.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.threeten.bp.LocalDateTime;

public class Feed implements Parcelable {

    public String entryId;
    public LocalDateTime published;
    public String updated;
    public String title;
    public String link;
    public String authorName;
    public String authorUrl;
    public String thumbnail;

    public Action action;

    public Feed() {
    }

    protected Feed(Parcel in) {
        entryId = in.readString();
        updated = in.readString();
        title = in.readString();
        link = in.readString();
        authorName = in.readString();
        authorUrl = in.readString();
        thumbnail = in.readString();
    }

    public static final Creator<Feed> CREATOR = new Creator<Feed>() {
        @Override
        public Feed createFromParcel(Parcel in) {
            return new Feed(in);
        }

        @Override
        public Feed[] newArray(int size) {
            return new Feed[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(entryId);
        dest.writeString(updated);
        dest.writeString(title);
        dest.writeString(link);
        dest.writeString(authorName);
        dest.writeString(authorUrl);
        dest.writeString(thumbnail);
    }
}
