package com.example.redditclone;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<Post> postList;
    private OnPostClickListener onPostClickListener;

    public PostAdapter(List<Post> postList, OnPostClickListener onPostClickListener) {
        this.postList = postList;
        this.onPostClickListener = onPostClickListener;
    }

    public void updatePosts(List<Post> newPosts) {
        this.postList = new ArrayList<>(newPosts);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view, onPostClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvContent, tvAuthor, tvSubreddit, tvScore, tvComments, tvTime;
        private ImageView ivImage;
        private ImageButton btnUpvote, btnDownvote;
        private Post currentPost;
        private OnPostClickListener onPostClickListener;

        private boolean isUpvoted = false;
        private boolean isDownvoted = false;

        public PostViewHolder(@NonNull View itemView, OnPostClickListener onPostClickListener) {
            super(itemView);
            this.onPostClickListener = onPostClickListener;

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvSubreddit = itemView.findViewById(R.id.tvSubreddit);
            tvScore = itemView.findViewById(R.id.tvScore);
            tvComments = itemView.findViewById(R.id.tvComments);
            tvTime = itemView.findViewById(R.id.tvTime);
            ivImage = itemView.findViewById(R.id.ivImage);

            btnUpvote = itemView.findViewById(R.id.btnUpvote);
            btnDownvote = itemView.findViewById(R.id.btnDownvote);
        }

        public void bind(Post post) {
            this.currentPost = post;

            // Reset vote states
            isUpvoted = false;
            isDownvoted = false;

            // Set basic info
            tvTitle.setText(post.getTitle());
            tvAuthor.setText("u/" + post.getAuthor());
            tvSubreddit.setText("r/" + post.getSubreddit());
            updateScoreDisplay();
            tvComments.setText(formatNumber(post.getCommentCount()) + " comments");

            // Set content
            if (post.getContent() != null && !post.getContent().isEmpty()) {
                tvContent.setVisibility(View.VISIBLE);
                tvContent.setText(post.getContent());
            } else {
                tvContent.setVisibility(View.GONE);
            }

            // Set time
            String timeAgo = getTimeAgo(post.getCreatedUtc());
            tvTime.setText(timeAgo);

            // Load image
            if (post.hasImage()) {
                ivImage.setVisibility(View.VISIBLE);
                Glide.with(itemView.getContext())
                        .load(post.getImageUrl())
                        .placeholder(R.drawable.ic_image_placeholder)
                        .into(ivImage);
            } else {
                ivImage.setVisibility(View.GONE);
            }

            // Setup vote buttons
            setupVoteButtons();

            itemView.setOnClickListener(v -> {
                if (onPostClickListener != null) {
                    onPostClickListener.onPostClick(post);
                }
            });
        }

        private void setupVoteButtons() {
            // Set initial colors
            updateVoteButtonColors();

            btnUpvote.setOnClickListener(v -> {
                handleUpvote();
            });

            btnDownvote.setOnClickListener(v -> {
                handleDownvote();
            });
        }

        private void handleUpvote() {
            if (isUpvoted) {
                // Remove upvote
                currentPost.setScore(currentPost.getScore() - 1);
                isUpvoted = false;
            } else {
                // Add upvote
                currentPost.setScore(currentPost.getScore() + 1);
                isUpvoted = true;
                isDownvoted = false; // Remove downvote if exists
            }
            updateVoteButtonColors();
            updateScoreDisplay();
        }

        private void handleDownvote() {
            if (isDownvoted) {
                // Remove downvote
                currentPost.setScore(currentPost.getScore() + 1);
                isDownvoted = false;
            } else {
                // Add downvote
                currentPost.setScore(currentPost.getScore() - 1);
                isDownvoted = true;
                isUpvoted = false; // Remove upvote if exists
            }
            updateVoteButtonColors();
            updateScoreDisplay();
        }

        private void updateVoteButtonColors() {
            int defaultColor = Color.parseColor("#878A8C"); // Gray
            int upvotedColor = Color.parseColor("#FF4500"); // Orange
            int downvotedColor = Color.parseColor("#7193FF"); // Blue

            // Update upvote button
            if (isUpvoted) {
                btnUpvote.setColorFilter(upvotedColor);
            } else {
                btnUpvote.setColorFilter(defaultColor);
            }

            // Update downvote button
            if (isDownvoted) {
                btnDownvote.setColorFilter(downvotedColor);
            } else {
                btnDownvote.setColorFilter(defaultColor);
            }
        }

        private void updateScoreDisplay() {
            tvScore.setText(formatNumber(currentPost.getScore()));

            // Update score text color based on vote state
            if (isUpvoted) {
                tvScore.setTextColor(Color.parseColor("#FF4500")); // Orange
            } else if (isDownvoted) {
                tvScore.setTextColor(Color.parseColor("#7193FF")); // Blue
            } else {
                tvScore.setTextColor(Color.parseColor("#1c1c1c")); // Dark gray
            }
        }

        private String getTimeAgo(double createdUtc) {
            long now = System.currentTimeMillis() / 1000;
            long diff = (long) (now - createdUtc);

            if (diff < 60) return diff + "s ago";
            if (diff < 3600) return (diff / 60) + "m ago";
            if (diff < 86400) return (diff / 3600) + "h ago";
            if (diff < 2592000) return (diff / 86400) + "d ago";
            return (diff / 2592000) + "mo ago";
        }

        private String formatNumber(int number) {
            if (number < 1000) {
                return String.valueOf(number);
            } else if (number < 1000000) {
                return String.format("%.1fk", number / 1000.0);
            } else {
                return String.format("%.1fm", number / 1000000.0);
            }
        }
    }

    public interface OnPostClickListener {
        void onPostClick(Post post);
    }
}