package com.example.redditclone.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.redditclone.Post;
import com.example.redditclone.PostAdapter;
import com.example.redditclone.R;
import com.example.redditclone.network.ApiService;
import com.example.redditclone.network.PostContainer;
import com.example.redditclone.network.RedditResponse;
import com.example.redditclone.network.RetrofitClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment{

    private BottomNavigationView bottomNavigationView;
    private final List<Post> postList = new ArrayList<>();
    private ApiService apiService;
    private PostAdapter postAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initViews(view);
        setupBottomNavigation();
        apiService = RetrofitClient.getApiService();
        fetchPosts();

        return view;
    }

    private void fetchPosts() {
        apiService.getPosts().enqueue(new Callback<RedditResponse>() {
            @Override
            public void onResponse(Call<RedditResponse> call, Response<RedditResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    postList.clear();
                    for (PostContainer container : response.body().getData().getChildren()) {
                        postList.add(container.getPost());
                    }
                    postAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Failed to fetch posts", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RedditResponse> call, Throwable t) {
                Log.e("HomeFragment", "Error fetching posts", t);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.rvPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postAdapter = new PostAdapter(postList);
        recyclerView.setAdapter(postAdapter);

        // Get the bottom navigation from the parent activity
        if (getActivity() != null) {
            bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
        }
    }

    private void setupBottomNavigation() {
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();

                if (itemId == R.id.bottom_home
                        || itemId == R.id.bottom_answers
                        || itemId == R.id.bottom_chat
                        || itemId == R.id.bottom_inbox) {
                    return true;
                }

                else if (itemId == R.id.bottom_create) {
                    showCreatePostDialog();
                    return true;
                }

                return false;
            });

            // Set Home as selected when fragment loads
            bottomNavigationView.setSelectedItemId(R.id.bottom_home);
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

        // Make it fullscreen
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        btnPost.setOnClickListener(v -> {
            String title = etPostTitle.getText().toString().trim();
            String content = etPostContent.getText().toString().trim();

            if (!title.isEmpty()) {
                // Create a new Post object with the provided title and content

                dialog.dismiss();
            } else {
                Toast.makeText(getContext(), "Title cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
    }



    @Override
    public void onResume() {
        super.onResume();
        // Ensure Home is selected when returning to this fragment
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.bottom_home);
        }
    }

}
