package com.example.redditclone.network;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET(".json")
    Call<RedditResponse> getPosts();
}
