package com.example.redditclone.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar; // Thêm import này
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.redditclone.Post;
import com.example.redditclone.PostAdapter;
import com.example.redditclone.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements PostAdapter.OnPostClickListener {

    private RecyclerView recyclerView;
    private BottomNavigationView bottomNavigationView;
    private PostAdapter postAdapter;
    private List<Post> postList = new ArrayList<>();
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initViews(view);
        setupRecyclerView();
        setupBottomNavigation();

        loadPostsFromServer();

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.rvPosts);
        progressBar = view.findViewById(R.id.progressBar); // 2. Ánh xạ ProgressBar

        if (getActivity() != null) {
            bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
        }
    }

    private void loadPostsFromServer() {

        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            List<Post> fetchedPosts = new ArrayList<>();
            fetchedPosts.add(new Post("1", "Welcome to Reddit Clone!", "This is our first post...", "admin", System.currentTimeMillis() - 3600000, 15, 2, 3, null));
            fetchedPosts.add(new Post("2", "What's your favorite programming language?", "I'm trying to decide...", "code_newbie", System.currentTimeMillis() - 7200000, 89, 5, 32, null));
            fetchedPosts.add(new Post("3", "Android Development Tips", "Always test your app...", "android_dev", System.currentTimeMillis() - 10800000, 45, 1, 12, null));

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    postAdapter.updatePosts(fetchedPosts);
                    progressBar.setVisibility(View.GONE);
                });
            }
        }).start();// Bắt đầu chạy luồng
    }


    private void setupBottomNavigation() {
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.bottom_home || itemId == R.id.bottom_answers || itemId == R.id.bottom_chat || itemId == R.id.bottom_inbox) {
                    return true;
                } else if (itemId == R.id.bottom_create) {
                    showCreatePostDialog();
                    return true;
                }
                return false;
            });
            bottomNavigationView.setSelectedItemId(R.id.bottom_home);
        }
    }

    private void setupRecyclerView() {
        postAdapter = new PostAdapter(new ArrayList<>(), this); // Khởi tạo adapter với danh sách rỗng
        recyclerView.setAdapter(postAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onPostClick(Post post) {
        Toast.makeText(getContext(), "Clicked: " + post.getTitle(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onVoteClick(Post post, boolean isUpvote) {
        for (int i = 0; i < postList.size(); i++) {
            if (postList.get(i).getId().equals(post.getId())) {
                Post updatedPost = postList.get(i);
                if (isUpvote) {
                    updatedPost.setUpvotes(updatedPost.getUpvotes() + 1);
                } else {
                    updatedPost.setDownvotes(updatedPost.getDownvotes() + 1);
                }
                postList.set(i, updatedPost);
                postAdapter.notifyItemChanged(i);
                break;
            }
        }
    }
    private void showCreatePostDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.FullScreenDialogStyle);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_post, null);
        builder.setView(dialogView);
        final EditText etPostTitle = dialogView.findViewById(R.id.etPostTitle);
        final EditText etPostContent = dialogView.findViewById(R.id.etPostContent);
        Button btnPost = dialogView.findViewById(R.id.btnPost);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        AlertDialog dialog = builder.create();
        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        btnPost.setOnClickListener(v -> {
            String title = etPostTitle.getText().toString().trim();
            String content = etPostContent.getText().toString().trim();
            if (!title.isEmpty()) {
                createNewPost(title, content);
                dialog.dismiss();
            } else {
                Toast.makeText(getContext(), "Title cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        btnCancel.setOnClickListener(v -> dialog.dismiss());
    }

    private void createNewPost(String title, String content) {
        String newId = String.valueOf(System.currentTimeMillis());
        Post newPost = new Post(newId, title, content, "tv_name", System.currentTimeMillis(), 0, 0, 0, null);
        postList.add(0, newPost);
        postAdapter.notifyItemInserted(0);
        recyclerView.smoothScrollToPosition(0);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.bottom_home);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.bottom_home);
        }
    }
}