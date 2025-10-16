package com.example.redditclone.network;

import com.example.redditclone.Post;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RedditData {
    @SerializedName("children")
    private List<PostContainer> children;

    public List<PostContainer> getChildren() {
        return children;
    }
}
