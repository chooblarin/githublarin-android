package com.chooblarin.githublarin.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User implements Parcelable {

    @Expose
    public long id;

    @Expose
    public String login;

    @Expose
    public String name;

    @Expose
    @SerializedName("avatar_url")
    public String avatarUrl;

    @Expose
    public String location;

    @SerializedName("created_at")
    public String createdAt;

    @Expose
    @SerializedName("html_url")
    public String htmlUrl;

    @Expose
    @SerializedName("public_repos")
    public int publicRepos;

    @Expose
    @SerializedName("public_gists")
    public int publicGists;

    @Expose
    public int following;

    @Expose
    public int followers;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(login);
        dest.writeString(avatarUrl);
        dest.writeString(location);
        dest.writeString(createdAt);
        dest.writeString(htmlUrl);
        dest.writeInt(publicRepos);
        dest.writeInt(publicGists);
        dest.writeInt(following);
        dest.writeInt(followers);
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    private User(Parcel in) {
        id = in.readLong();
        login = in.readString();
        avatarUrl = in.readString();
        location = in.readString();
        createdAt = in.readString();
        htmlUrl = in.readString();
        publicRepos = in.readInt();
        publicGists = in.readInt();
        following = in.readInt();
        followers = in.readInt();
    }
}
