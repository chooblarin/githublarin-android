package com.chooblarin.githublarin.api.response;

import com.chooblarin.githublarin.model.Repository;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResponse {

    @Expose
    @SerializedName("total_count")
    public int totalCount;

    @Expose
    @SerializedName("incomplete_results")
    public boolean incompleteResults;

    @Expose
    public List<Repository> items;
}
