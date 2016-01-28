package com.chooblarin.githublarin.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Repository {

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
}
