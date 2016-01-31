package com.chooblarin.githublarin.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.threeten.bp.LocalDateTime;

import java.util.List;

public class Gist implements Parcelable {

    @Expose
    @SerializedName("id")
    public String id;

    @Expose
    @SerializedName("description")
    public String description;

    @Expose
    @SerializedName("public")
    public boolean isPublic;

    @Expose
    @SerializedName("owner")
    public User owner;

    @Expose
    @SerializedName("files")
    public List<GistFile> files;

    @Expose
    @SerializedName("comments")
    public int comments;

    @Expose
    @SerializedName("comments_url")
    public String commentsUrl;

    @Expose
    @SerializedName("html_url")
    public String htmlUrl;

    @Expose
    @SerializedName("created_at")
    public LocalDateTime createdAt;

    @Expose
    @SerializedName("updated_at")
    public LocalDateTime updatedAt;

    public String getTitle() {
        return String.format("%s/%s", owner.login, !files.isEmpty() ? files.get(0).filename : "");
    }

    protected Gist(Parcel in) {
        id = in.readString();
        description = in.readString();
        isPublic = in.readByte() != 0;
        owner = in.readParcelable(User.class.getClassLoader());
        files = in.createTypedArrayList(GistFile.CREATOR);
        comments = in.readInt();
        commentsUrl = in.readString();
        htmlUrl = in.readString();
    }

    public static final Creator<Gist> CREATOR = new Creator<Gist>() {
        @Override
        public Gist createFromParcel(Parcel in) {
            return new Gist(in);
        }

        @Override
        public Gist[] newArray(int size) {
            return new Gist[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(description);
        dest.writeByte((byte) (isPublic ? 1 : 0));
        dest.writeParcelable(owner, flags);
        dest.writeTypedList(files);
        dest.writeInt(comments);
        dest.writeString(commentsUrl);
        dest.writeString(htmlUrl);
    }
}
