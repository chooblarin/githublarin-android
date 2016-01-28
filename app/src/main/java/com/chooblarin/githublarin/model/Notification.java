package com.chooblarin.githublarin.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.threeten.bp.LocalDateTime;

import java.util.List;

public class Notification {

    @SerializedName("id")
    @Expose
    public String id;

    @SerializedName("reason")
    @Expose
    public String reason;

    @SerializedName("repository")
    @Expose
    public List<Repository> repositories;

    @SerializedName("unread")
    @Expose
    public boolean unread;

    @SerializedName("update_at")
    @Expose
    public LocalDateTime updatedAt;

    @SerializedName("last_read_at")
    @Expose
    public LocalDateTime lastReadAt;

    @SerializedName("url")
    @Expose
    public String url;
}
