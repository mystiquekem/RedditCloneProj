package com.example.redditclone.network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "https://www.reddit.com";

    private static Retrofit retrofit = null;

    public static ApiService getApiService() {
        if (retrofit == null) {
            // Create an OkHttpClient to add a User-Agent header
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    Request request = original.newBuilder()
                            .header("User-Agent", "android:com.example.redditclone:v1.0.0 (by /u/your_reddit_username)")
                            .method(original.method(), original.body())
                            .build();
                    return chain.proceed(request);
                }
            });

            OkHttpClient client = httpClient.build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client) // Set the custom client
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}
