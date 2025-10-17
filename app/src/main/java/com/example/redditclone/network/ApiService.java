package com.example.redditclone.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import java.util.List;
public interface ApiService {
    @GET(".json")
    Call<RedditResponse> getPosts();

    @GET("/r/{subreddit}/comments/{postId}/.json")
    Call<List<RedditResponse>> getComments(
            @Path("subreddit") String subreddit,
            @Path("postId") String postId
    );
}
