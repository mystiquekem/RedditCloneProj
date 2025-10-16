package com.example.redditclone.network;

import com.example.redditclone.Post;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RedditResponse {
    @SerializedName("data")
    private RedditData data;

    public RedditData getData() {
        return data;
    }
}
