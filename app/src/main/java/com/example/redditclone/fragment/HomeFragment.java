package com.example.redditclone.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.redditclone.AppExecutors;
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

public class HomeFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;
    private List<Post> postList = new ArrayList<>();
    private ApiService apiService;
    private PostAdapter postAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ProgressBar loadingIndicator;
    private boolean isLoading = false;
    private String nextPageToken = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initViews(view);
        setupSwipeRefresh();
        setupInfiniteScroll();
        apiService = RetrofitClient.getApiService();
        setupBottomNavigation();

        // Fetch posts on background thread
        AppExecutors.getInstance().diskIO().execute(() -> fetchPosts(""));

        return view;
    }


    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.rvPosts);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        loadingIndicator = view.findViewById(R.id.loadingIndicator);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        postAdapter = new PostAdapter(postList, this::onPostClick);

        recyclerView.setAdapter(postAdapter);

        if (getActivity() != null) {
            bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
        }
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            nextPageToken = "";
            postList.clear();
            postAdapter.notifyDataSetChanged();

            // Fetch on background thread
            AppExecutors.getInstance().diskIO().execute(() -> fetchPosts(""));
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

                if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 5 &&
                        firstVisibleItemPosition >= 0 && totalItemCount > 0) {
                    isLoading = true;

                    AppExecutors.getInstance().mainThread().execute(() -> {
                        loadingIndicator.setVisibility(View.VISIBLE);
                    });

                    // Fetch on background thread
                    AppExecutors.getInstance().diskIO().execute(() -> fetchPosts(nextPageToken));
                }
            }
        });
    }
    private void onPostClick(Post post) {
        // Handle post click - for now just show a toast
        Toast.makeText(getContext(), "Clicked: " + post.getTitle(), Toast.LENGTH_SHORT).show();

    }
    private void fetchPosts(String after) {
        apiService.getPosts().enqueue(new Callback<RedditResponse>() {
            @Override
            public void onResponse(Call<RedditResponse> call, Response<RedditResponse> response) {
                // Process response on background thread then update UI on main thread
                AppExecutors.getInstance().diskIO().execute(() -> {
                    if (!isAdded()) return;

                    AppExecutors.getInstance().mainThread().execute(() -> {
                        if (!isAdded()) return;

                        swipeRefreshLayout.setRefreshing(false);
                        loadingIndicator.setVisibility(View.GONE);

                        if (response.isSuccessful() && response.body() != null) {
                            List<PostContainer> children = response.body().getData().getChildren();
                            for (PostContainer container : children) {
                                postList.add(container.getPost());
                            }
                            nextPageToken = response.body().getData().getAfter();
                            postAdapter.notifyDataSetChanged();
                            isLoading = false;
                        } else {
                            Toast.makeText(getContext(), "Failed to fetch posts", Toast.LENGTH_SHORT).show();
                            isLoading = false;
                        }
                    });
                });
            }

            @Override
            public void onFailure(Call<RedditResponse> call, Throwable t) {
                Log.e("HomeFragment", "Error fetching posts", t);

                AppExecutors.getInstance().mainThread().execute(() -> {
                    if (!isAdded()) return;

                    swipeRefreshLayout.setRefreshing(false);
                    loadingIndicator.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    isLoading = false;
                });
            }
        });
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

        AlertDialog dialog = builder.create();
        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        btnPost.setOnClickListener(v -> {
            String title = etPostTitle.getText().toString().trim();
            String content = etPostContent.getText().toString().trim();

            if (!title.isEmpty()) {
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
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.bottom_home);
        }
    }
}