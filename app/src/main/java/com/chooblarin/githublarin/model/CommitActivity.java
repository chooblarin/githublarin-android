package com.chooblarin.githublarin.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CommitActivity {

    @SerializedName("days")
    @Expose
    public int[] days;

    @SerializedName("total")
    @Expose
    public int total;

    @SerializedName("weeks")
    @Expose
    public long week;
}
