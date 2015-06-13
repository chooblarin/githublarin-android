package com.chooblarin.githublarin.model;

import com.google.gson.annotations.SerializedName;

public class Repository {

    public long id;
    public String name;

    @SerializedName("full_name")
    public String fullName;

    public String language;

    @SerializedName("star_gazers_count")
    public int starGazersCount;

    @SerializedName("watchers_count")
    public int watchersCount;

    @SerializedName("forks_count")
    public int forksCount;

    public String description;

    @SerializedName("private")
    public boolean isPrivate;

    public User owner;
}
