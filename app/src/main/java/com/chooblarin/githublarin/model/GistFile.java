package com.chooblarin.githublarin.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonObject;

public class GistFile implements Parcelable {

    public String filename;
    public String type;
    public String language;
    public String rawUrl;
    public long size;

    public GistFile() {
    }

    protected GistFile(Parcel in) {
        filename = in.readString();
        type = in.readString();
        language = in.readString();
        rawUrl = in.readString();
        size = in.readLong();
    }

    public static final Creator<GistFile> CREATOR = new Creator<GistFile>() {
        @Override
        public GistFile createFromParcel(Parcel in) {
            return new GistFile(in);
        }

        @Override
        public GistFile[] newArray(int size) {
            return new GistFile[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(filename);
        dest.writeString(type);
        dest.writeString(language);
        dest.writeString(rawUrl);
        dest.writeLong(size);
    }
}
