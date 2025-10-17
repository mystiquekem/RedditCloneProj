package com.example.redditclone.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.redditclone.AppExecutors;
import com.example.redditclone.Post;
import com.example.redditclone.PostAdapter;
import com.example.redditclone.R;
import com.example.redditclone.network.ApiService;
import com.example.redditclone.network.PostContainer;
import com.example.redditclone.network.RedditResponse;
import com.example.redditclone.network.RetrofitClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;
    private List<Post> apiPostList = new ArrayList<>(); // Posts from API
    private List<Post> localPosts = new ArrayList<>(); // Local posts created by user
    private List<Post> allPosts = new ArrayList<>(); // Combined list for display
    private ApiService apiService;
    private PostAdapter postAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ProgressBar loadingIndicator;
    private boolean isLoading = false;
    private String nextPageToken = "";

    // Image picker
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initViews(view);
        setupSwipeRefresh();
        setupInfiniteScroll();
        apiService = RetrofitClient.getApiService();
        setupBottomNavigation();

        // Load local posts first, then fetch API posts
        loadLocalPosts();
        fetchPosts();

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.rvPosts);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        loadingIndicator = view.findViewById(R.id.loadingIndicator);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        // Initialize with empty list, will be populated later
        postAdapter = new PostAdapter(allPosts, this::onPostClick);
        recyclerView.setAdapter(postAdapter);

        if (getActivity() != null) {
            bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
        }
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            nextPageToken = "";
            apiPostList.clear();
            allPosts.clear();
            postAdapter.notifyDataSetChanged();

            // Reload local posts and fetch new API posts
            loadLocalPosts();
            fetchPosts();
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.orange);
    }

    private void setupInfiniteScroll() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                // Only trigger infinite scroll if we have more API posts to load
                if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 5 &&
                        firstVisibleItemPosition >= 0 && totalItemCount > 0 && !nextPageToken.isEmpty()) {
                    isLoading = true;

                    AppExecutors.getInstance().mainThread().execute(() -> {
                        loadingIndicator.setVisibility(View.VISIBLE);
                    });

                    // Fetch more API posts
                    fetchMorePosts();
                }
            }
        });
    }

    private void onPostClick(Post post) {
        // Handle post click - show different message for local posts
        if (post.isLocal()) {
            Toast.makeText(getContext(), "Local Post: " + post.getTitle(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "API Post: " + post.getTitle(), Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchPosts() {
        isLoading = true;
        loadingIndicator.setVisibility(View.VISIBLE);

        apiService.getPosts().enqueue(new Callback<RedditResponse>() {
            @Override
            public void onResponse(Call<RedditResponse> call, Response<RedditResponse> response) {
                AppExecutors.getInstance().mainThread().execute(() -> {
                    if (!isAdded()) return;

                    swipeRefreshLayout.setRefreshing(false);
                    loadingIndicator.setVisibility(View.GONE);
                    isLoading = false;

                    if (response.isSuccessful() && response.body() != null) {
                        List<PostContainer> children = response.body().getData().getChildren();

                        // Clear existing API posts for fresh load
                        apiPostList.clear();

                        for (PostContainer container : children) {
                            apiPostList.add(container.getPost());
                        }
                        nextPageToken = response.body().getData().getAfter();

                        // Combine all posts and update UI
                        combineAllPosts();

                        Toast.makeText(getContext(), "Posts loaded successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to fetch posts", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<RedditResponse> call, Throwable t) {
                Log.e("HomeFragment", "Error fetching posts", t);
                AppExecutors.getInstance().mainThread().execute(() -> {
                    if (!isAdded()) return;

                    swipeRefreshLayout.setRefreshing(false);
                    loadingIndicator.setVisibility(View.GONE);
                    isLoading = false;
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void fetchMorePosts() {
        // Since your API doesn't support pagination with 'after' parameter,
        // we'll just load the same posts again for demonstration
        // In a real app, you'd implement proper pagination

        apiService.getPosts().enqueue(new Callback<RedditResponse>() {
            @Override
            public void onResponse(Call<RedditResponse> call, Response<RedditResponse> response) {
                AppExecutors.getInstance().mainThread().execute(() -> {
                    if (!isAdded()) return;

                    loadingIndicator.setVisibility(View.GONE);
                    isLoading = false;

                    if (response.isSuccessful() && response.body() != null) {
                        List<PostContainer> children = response.body().getData().getChildren();

                        // Add new posts to existing list
                        for (PostContainer container : children) {
                            apiPostList.add(container.getPost());
                        }

                        // Combine all posts and update UI
                        combineAllPosts();

                        Toast.makeText(getContext(), "More posts loaded!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to load more posts", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<RedditResponse> call, Throwable t) {
                Log.e("HomeFragment", "Error fetching more posts", t);
                AppExecutors.getInstance().mainThread().execute(() -> {
                    if (!isAdded()) return;

                    loadingIndicator.setVisibility(View.GONE);
                    isLoading = false;
                    Toast.makeText(getContext(), "Error loading more posts: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void loadLocalPosts() {
        // For now, we'll just initialize an empty list
        // In a real app, you would load from SharedPreferences or database
        localPosts.clear();

        // You can add some sample local posts for testing
        // localPosts.add(new Post("Sample Local Post", "This is a local post content", "Current User", null));

        combineAllPosts();
    }

    private void combineAllPosts() {
        allPosts.clear();

        // Add local posts (newest first)
        List<Post> sortedLocalPosts = new ArrayList<>(localPosts);
        Collections.sort(sortedLocalPosts, (p1, p2) -> Double.compare(p2.getCreatedUtc(), p1.getCreatedUtc()));
        allPosts.addAll(sortedLocalPosts);

        // Add API posts
        allPosts.addAll(apiPostList);

        // Sort all posts by timestamp (newest first)
        Collections.sort(allPosts, (p1, p2) -> Double.compare(p2.getCreatedUtc(), p1.getCreatedUtc()));

        if (postAdapter != null) {
            postAdapter.updatePosts(allPosts);
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

            bottomNavigationView.setSelectedItemId(R.id.bottom_home);
        }
    }

    public void showCreatePostDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.FullScreenDialogStyle);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_post, null);
        builder.setView(dialogView);

        final EditText etPostTitle = dialogView.findViewById(R.id.etPostTitle);
        final EditText etPostContent = dialogView.findViewById(R.id.etPostContent);
        Button btnPost = dialogView.findViewById(R.id.btnPost);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnSelectImage = dialogView.findViewById(R.id.btnSelectImage);
        Button btnRemoveImage = dialogView.findViewById(R.id.btnRemoveImage);
        ImageView ivSelectedImage = dialogView.findViewById(R.id.ivSelectedImage);

        AlertDialog dialog = builder.create();
        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        // Reset image state when dialog opens
        selectedImageUri = null;
        ivSelectedImage.setVisibility(View.GONE);
        btnRemoveImage.setVisibility(View.GONE);

        // Image selection
        btnSelectImage.setOnClickListener(v -> {
            openImagePicker();
        });

        // Image removal
        btnRemoveImage.setOnClickListener(v -> {
            selectedImageUri = null;
            ivSelectedImage.setVisibility(View.GONE);
            btnRemoveImage.setVisibility(View.GONE);
            ivSelectedImage.setImageDrawable(null);
        });

        btnPost.setOnClickListener(v -> {
            String title = etPostTitle.getText().toString().trim();
            String content = etPostContent.getText().toString().trim();

            if (!title.isEmpty()) {
                createLocalPost(title, content, selectedImageUri);
                dialog.dismiss();
            } else {
                Toast.makeText(getContext(), "Title cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

    private void openImagePicker() {
        try {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error opening image picker", Toast.LENGTH_SHORT).show();
            Log.e("HomeFragment", "Error opening image picker", e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();

            // Update the dialog UI to show selected image
            // We need to find the currently visible dialog
            View rootView = getView();
            if (rootView != null) {
                ImageView ivSelectedImage = rootView.findViewById(R.id.ivSelectedImage);
                Button btnRemoveImage = rootView.findViewById(R.id.btnRemoveImage);

                if (ivSelectedImage != null && selectedImageUri != null) {
                    Glide.with(this)
                            .load(selectedImageUri)
                            .placeholder(R.drawable.ic_image_placeholder)
                            .into(ivSelectedImage);
                    ivSelectedImage.setVisibility(View.VISIBLE);
                }

                if (btnRemoveImage != null) {
                    btnRemoveImage.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void createLocalPost(String title, String content, Uri imageUri) {
        String imageUrl = null;
        if (imageUri != null) {
            // Store the URI as string for local posts
            imageUrl = imageUri.toString();
        }

        // Get current user info
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String author = "Anonymous";
        if (user != null) {
            if (user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
                author = user.getDisplayName();
            } else if (user.getEmail() != null) {
                // Use email username part as fallback
                String email = user.getEmail();
                author = email.substring(0, email.indexOf('@'));
            }
        }

        // Create new local post
        Post localPost = new Post(title, content, author, imageUrl);
        localPosts.add(0, localPost); // Add to beginning of list

        // Update the combined list and refresh adapter
        combineAllPosts();

        Toast.makeText(getContext(), "Post created successfully!", Toast.LENGTH_SHORT).show();

        Log.d("HomeFragment", "Local post created: " + title + ", Total local posts: " + localPosts.size());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.bottom_home);
        }
    }
}