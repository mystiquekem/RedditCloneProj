package com.example.redditclone;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfileFragment extends Fragment {

    public interface ProfileUpdateListener {
        void onProfileUpdated();
    }
    private ProfileUpdateListener mListener;

    private TextView displayNameTextView;
    private TextView toolbarUsernameTextView;
    private ImageView backButton, searchButton, shareButton;
    private ImageView coverPhoto, editCoverPhotoButton;
    private CircleImageView profileAvatar;
    private ImageView editAvatarButton;
    private TextView editProfileButton;
    private TextView addSocialLinkButton;

    private ActivityResultLauncher<String> mGetCoverContent;
    private ActivityResultLauncher<String> mGetAvatarContent;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ProfileUpdateListener) {
            mListener = (ProfileUpdateListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement ProfileUpdateListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGetCoverContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null && isAdded()) {
                        Glide.with(this).load(uri).into(coverPhoto);
                        Toast.makeText(getContext(), "Đã cập nhật ảnh bìa!", Toast.LENGTH_SHORT).show();
                    }
                });

        mGetAvatarContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null && isAdded()) {
                        Glide.with(this).load(uri).into(profileAvatar);
                        Toast.makeText(getContext(), "Đã cập nhật avatar!", Toast.LENGTH_SHORT).show();
                    }
                });

        getParentFragmentManager().setFragmentResultListener(EditProfileFragment.REQUEST_KEY, this, (requestKey, bundle) -> {
            String newDisplayName = bundle.getString(EditProfileFragment.KEY_DISPLAY_NAME);
            if (newDisplayName != null) {
                updateFirebaseProfile(newDisplayName);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
        setupClickListeners();
        loadProfileInfo();
    }

    private void setupViews(View view) {
        displayNameTextView = view.findViewById(R.id.display_name);
        toolbarUsernameTextView = view.findViewById(R.id.toolbar_username);
        backButton = view.findViewById(R.id.toolbar_back);
        searchButton = view.findViewById(R.id.toolbar_search);
        shareButton = view.findViewById(R.id.toolbar_share);
        coverPhoto = view.findViewById(R.id.cover_photo);
        editCoverPhotoButton = view.findViewById(R.id.edit_cover_photo_icon);
        profileAvatar = view.findViewById(R.id.profile_avatar);
        editAvatarButton = view.findViewById(R.id.edit_avatar_icon);
        editProfileButton = view.findViewById(R.id.edit_profile_button);
        addSocialLinkButton = view.findViewById(R.id.add_social_link_button);
    }

    private void loadProfileInfo() {
        // Sử dụng AppExecutors để chạy tác vụ trên luồng nền
        AppExecutors.getInstance().diskIO().execute(() -> {
            // Tác vụ này chạy ở background
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null || !isAdded()) return;

            final String displayName = user.getDisplayName() == null || user.getDisplayName().isEmpty() ? "User" : user.getDisplayName();
            final Uri photoUrl = user.getPhotoUrl();

            // Gửi kết quả về luồng chính để cập nhật UI
            AppExecutors.getInstance().mainThread().execute(() -> {
                if (!isAdded()) return;
                displayNameTextView.setText(displayName);
                toolbarUsernameTextView.setText("u/" + displayName);
                Glide.with(MyProfileFragment.this).load(photoUrl).error(R.drawable.profile_default).into(profileAvatar);
            });
        });
    }

    private void updateFirebaseProfile(String newDisplayName) {
        // Sử dụng AppExecutors để chạy tác vụ trên luồng nền
        AppExecutors.getInstance().diskIO().execute(() -> {
            // Tác vụ này chạy ở background
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null || !isAdded()) return;

            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(newDisplayName)
                    .build();

            user.updateProfile(profileUpdates).addOnCompleteListener(task -> {
                // Gửi kết quả về luồng chính để cập nhật UI
                AppExecutors.getInstance().mainThread().execute(() -> {
                    if (isAdded()) {
                        if (task.isSuccessful()) {
                            Log.d("MyProfileFragment", "User profile updated.");
                            Toast.makeText(getContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                            loadProfileInfo();
                            if (mListener != null) {
                                mListener.onProfileUpdated();
                            }
                        } else {
                            Toast.makeText(getContext(), "Failed to update profile.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            });
        });
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
        searchButton.setOnClickListener(v -> Toast.makeText(getContext(), "Search clicked", Toast.LENGTH_SHORT).show());
        shareButton.setOnClickListener(v -> Toast.makeText(getContext(), "Share clicked", Toast.LENGTH_SHORT).show());
        editCoverPhotoButton.setOnClickListener(v -> mGetCoverContent.launch("image/*"));
        editAvatarButton.setOnClickListener(v -> mGetAvatarContent.launch("image/*"));
        addSocialLinkButton.setOnClickListener(v -> Toast.makeText(getContext(), "Add Social Link clicked", Toast.LENGTH_SHORT).show());

        editProfileButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                String currentDisplayName = displayNameTextView.getText().toString();
                EditProfileFragment editFragment = new EditProfileFragment();
                Bundle args = new Bundle();
                args.putString(EditProfileFragment.KEY_DISPLAY_NAME, currentDisplayName);
                editFragment.setArguments(args);

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, editFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}

