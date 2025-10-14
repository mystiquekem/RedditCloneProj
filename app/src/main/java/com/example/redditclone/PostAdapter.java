package com.example.redditclone;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    public interface OnPostClickListener {
        void onPostClick(Post post);
        void onVoteClick(Post post, boolean isUpvote);
    }

    private List<Post> postList;
    private OnPostClickListener listener;

    public PostAdapter(List<Post> postList, OnPostClickListener listener) {
        this.postList = postList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.bind(post, listener);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public void updatePosts(List<Post> newPosts) {
        postList.clear();
        postList.addAll(newPosts);
        notifyDataSetChanged();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAuthor, tvTitle, tvContent, tvScore;
        private ImageView ivPostImage;
        private ImageButton btnUpvote, btnDownvote;
        private Button btnComment, btnShare;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvScore = itemView.findViewById(R.id.tvScore);
            ivPostImage = itemView.findViewById(R.id.ivPostImage);
            btnUpvote = itemView.findViewById(R.id.btnUpvote);
            btnDownvote = itemView.findViewById(R.id.btnDownvote);
            btnComment = itemView.findViewById(R.id.btnComment);
            btnShare = itemView.findViewById(R.id.btnShare);
        }

        public void bind(final Post post, final OnPostClickListener listener) {
            tvAuthor.setText("u/" + post.getAuthor());
            tvTitle.setText(post.getTitle());
            tvContent.setText(post.getContent());
            tvScore.setText(String.valueOf(post.getScore()));

            // Handle image visibility
            if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
                ivPostImage.setVisibility(View.VISIBLE);
                // You can add Glide later for image loading
            } else {
                ivPostImage.setVisibility(View.GONE);
            }

            // Voting buttons
            btnUpvote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onVoteClick(post, true);
                }
            });

            btnDownvote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onVoteClick(post, false);
                }
            });

        }
    }
}