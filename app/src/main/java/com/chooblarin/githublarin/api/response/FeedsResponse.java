package com.chooblarin.githublarin.api.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FeedsResponse {

    @SerializedName("current_user_url")
    @Expose
    public String currentUserUrl;
}
