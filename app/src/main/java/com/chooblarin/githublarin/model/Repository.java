package com.chooblarin.githublarin.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Repository implements Parcelable {

    @Expose
    @SerializedName("id")
    public long id;

    @Expose
    @SerializedName("name")
    public String name;

    @Expose
    @SerializedName("full_name")
    public String fullName;

    @Expose
    public String language;

    @Expose
    @SerializedName("star_gazers_count")
    public int starGazersCount;

    @Expose
    @SerializedName("watchers_count")
    public int watchersCount;

    @Expose
    @SerializedName("forks_count")
    public int forksCount;

    @Expose
    public String description;

    @Expose
    @SerializedName("private")
    public boolean isPrivate;

    @Expose
    public User owner;

    protected Repository(Parcel in) {
        id = in.readLong();
        name = in.readString();
        fullName = in.readString();
        language = in.readString();
        starGazersCount = in.readInt();
        watchersCount = in.readInt();
        forksCount = in.readInt();
        description = in.readString();
        isPrivate = in.readByte() != 0;
        owner = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<Repository> CREATOR = new Creator<Repository>() {
        @Override
        public Repository createFromParcel(Parcel in) {
            return new Repository(in);
        }

        @Override
        public Repository[] newArray(int size) {
            return new Repository[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(fullName);
        dest.writeString(language);
        dest.writeInt(starGazersCount);
        dest.writeInt(watchersCount);
        dest.writeInt(forksCount);
        dest.writeString(description);
        dest.writeByte((byte) (isPrivate ? 1 : 0));
        dest.writeParcelable(owner, flags);
    }
}
