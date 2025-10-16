package com.example.redditclone.network;

import com.example.redditclone.Post;
import com.google.gson.annotations.SerializedName;

public class PostContainer {
    @SerializedName("data")
    private Post post;

    public Post getPost() {
        return post;
    }
}
