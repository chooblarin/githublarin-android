package com.chooblarin.githublarin.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

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

    public int following;

    public int followers;
}
