package com.chooblarin.githublarin.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.threeten.bp.LocalDateTime;

public class Notification implements Parcelable {

    @SerializedName("id")
    @Expose
    public String id;

    @SerializedName("reason")
    @Expose
    public String reason;

    @SerializedName("unread")
    @Expose
    public boolean unread;

    @SerializedName("updated_at")
    @Expose
    public LocalDateTime updatedAt;

    @SerializedName("last_read_at")
    @Expose
    public LocalDateTime lastReadAt;

    @SerializedName("subject")
    @Expose
    public NotificationSubject subject;

    @SerializedName("repository")
    @Expose
    public Repository repository;

    @SerializedName("url")
    @Expose
    public String url;

    @SerializedName("subscription_url")
    @Expose
    public String subscriptionUrl;

    protected Notification(Parcel in) {
        id = in.readString();
        reason = in.readString();
        unread = in.readByte() != 0;
        subject = in.readParcelable(NotificationSubject.class.getClassLoader());
        repository = in.readParcelable(Repository.class.getClassLoader());
        url = in.readString();
        subscriptionUrl = in.readString();
    }

    public static final Creator<Notification> CREATOR = new Creator<Notification>() {
        @Override
        public Notification createFromParcel(Parcel in) {
            return new Notification(in);
        }

        @Override
        public Notification[] newArray(int size) {
            return new Notification[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(reason);
        dest.writeByte((byte) (unread ? 1 : 0));
        dest.writeParcelable(subject, flags);
        dest.writeParcelable(repository, flags);
        dest.writeString(url);
        dest.writeString(subscriptionUrl);
    }
}
