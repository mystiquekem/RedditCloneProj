package com.example.redditclone;

import com.google.gson.annotations.SerializedName;

public class Post {
    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("selftext")
    private String content;

    @SerializedName("author")
    private String author;

    @SerializedName("score")
    private int score;

    @SerializedName("num_comments")
    private int commentCount;

    @SerializedName("created_utc")
    private double createdUtc;

    @SerializedName("subreddit")
    private String subreddit;

    @SerializedName("thumbnail")
    private String thumbnail;

    @SerializedName("url")
    private String imageUrl;

    @SerializedName("is_video")
    private boolean isVideo;

    // Local post flag
    private boolean isLocal = false;

    // Constructor for local posts
    public Post(String title, String content, String author, String imageUrl) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.imageUrl = imageUrl;
        this.score = 0;
        this.commentCount = 0;
        this.createdUtc = System.currentTimeMillis() / 1000.0;
        this.subreddit = "local";
        this.isLocal = true;
        this.id = "local_" + System.currentTimeMillis();
    }

    // Default constructor for GSON
    public Post() {}

    // Getters
    public String getId() { return id; }
    public String getTitle() {
        if (title == null) return "";
        return title;
    }
    public String getContent() {
        if (content == null) return "";
        return content;
    }
    public String getAuthor() {
        if (author == null) return "Unknown";
        return author;
    }
    public int getScore() { return score; }
    public int getCommentCount() { return commentCount; }
    public double getCreatedUtc() { return createdUtc; }
    public String getSubreddit() {
        if (subreddit == null) return "reddit";
        return subreddit;
    }
    public String getThumbnail() { return thumbnail; }
    public String getImageUrl() { return imageUrl; }
    public boolean isVideo() { return isVideo; }
    public boolean isLocal() { return isLocal; }

    // Setters
    public void setScore(int score) { this.score = score; }
    public void setCommentCount(int commentCount) { this.commentCount = commentCount; }

    // Check if post has a displayable image

    public boolean hasImage() {
        if (isLocal && imageUrl != null && !imageUrl.isEmpty()) {
            // For local posts, check if it's a content URI
            return imageUrl.startsWith("content://") ||
                    imageUrl.startsWith("file://") ||
                    (imageUrl.startsWith("http") && !imageUrl.contains("self") && !imageUrl.contains("reddit.com"));
        }

        // Original logic for API posts
        return imageUrl != null &&
                !imageUrl.isEmpty() &&
                !imageUrl.contains("self") &&
                !imageUrl.contains("reddit.com") &&
                !isVideo;
    }
}