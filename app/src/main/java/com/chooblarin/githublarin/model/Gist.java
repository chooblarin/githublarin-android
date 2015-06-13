package com.chooblarin.githublarin.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Gist {

    @Expose
    public String id;

    @Expose
    public String description;

    @Expose
    @SerializedName("created_at")
    public String createdAt;

    @Expose
    @SerializedName("html_url")
    private String htmlUrl;

    @Expose
    @SerializedName("public")
    public boolean isPublic;

    @Expose
    public User owner;
}
