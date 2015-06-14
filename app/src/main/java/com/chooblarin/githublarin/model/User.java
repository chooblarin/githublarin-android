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
}
