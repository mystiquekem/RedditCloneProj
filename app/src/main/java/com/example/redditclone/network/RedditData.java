package com.example.redditclone.network;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RedditData {
    @SerializedName("children")
    private List<PostContainer> children;

    @SerializedName("after")
    private String after;

    public List<PostContainer> getChildren() {
        return children;
    }

    public String getAfter() {
        return after != null ? after : "";
    }
}