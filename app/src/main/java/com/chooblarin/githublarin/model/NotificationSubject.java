package com.chooblarin.githublarin.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotificationSubject implements Parcelable {

    @SerializedName("title")
    @Expose
    public String title;

    @SerializedName("url")
    @Expose
    public String url;

    @SerializedName("latest_comment_url")
    @Expose
    public String latestCommentUrl;

    @SerializedName("type")
    @Expose
    public String type;

    protected NotificationSubject(Parcel in) {
        title = in.readString();
        url = in.readString();
        latestCommentUrl = in.readString();
        type = in.readString();
    }

    public static final Creator<NotificationSubject> CREATOR = new Creator<NotificationSubject>() {
        @Override
        public NotificationSubject createFromParcel(Parcel in) {
            return new NotificationSubject(in);
        }

        @Override
        public NotificationSubject[] newArray(int size) {
            return new NotificationSubject[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(url);
        dest.writeString(latestCommentUrl);
        dest.writeString(type);
    }
}
