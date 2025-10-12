package com.example.redditclone;

import static com.example.redditclone.MainActivity.MY_REQUEST_CODE;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import javax.annotation.Nullable;

public class MyProfileFragment extends Fragment {

    private View mview;
    private ImageView imgAvatar;
    private EditText edtFullName, edtEmail;
    private Button btnUpdateProfile;
    private Uri mUri;
    private MainActivity mMainActivity;
    private ProgressDialog progressDialog;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mview = inflater.inflate(R.layout.fragment_my_profile, container, false);

        initUI();
        mMainActivity = (MainActivity) getActivity();
        progressDialog = new ProgressDialog(getActivity());
        setUserInformation();
        initListener();


        return mview;
    }

    private void initUI() {
        imgAvatar = mview.findViewById(R.id.img_avatar);
        edtFullName = mview.findViewById(R.id.edt_full_name);
        edtEmail = mview.findViewById(R.id.edt_email);
        btnUpdateProfile = mview.findViewById(R.id.btn_update_profile);

    }

    private void setUserInformation() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        edtFullName.setText(user.getDisplayName());
        edtEmail.setText(user.getEmail());
        Glide.with(getActivity()).load(user.getPhotoUrl()).error(R.drawable.profile_default).into(imgAvatar);

    }

    private void initListener() {
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickRequestPermission();
            }
        });

        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickUpdateProfile();
            }

        });
    }

    private void onClickRequestPermission() {
        if (mMainActivity == null || getContext() == null) {
            return;
        }

        // On versions older than Android 6.0 (M), permissions are granted at install time.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mMainActivity.openGallery();
            return;
        }

        String permission;
        // For Android 13 (API 33) and above, use READ_MEDIA_IMAGES
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission = Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            // For older versions (Android 6.0 to 12L), use READ_EXTERNAL_STORAGE
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        if (getContext().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
            // Permission is already granted
            mMainActivity.openGallery();
        } else {
            // Permission has not been granted, request it.
            requestPermissions(new String[]{permission}, MY_REQUEST_CODE);
        }
    }

    public void setBitmapImageView(Bitmap bitmapImageView) {
        imgAvatar.setImageBitmap(bitmapImageView);
    }

    public void setUri(Uri mUri) {
        this.mUri = mUri;
    }

    private void onClickUpdateProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        progressDialog.show();
        String strFullname = edtFullName.getText().toString().trim();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(strFullname)
                .setPhotoUri(mUri)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Update profile successfully", Toast.LENGTH_SHORT).show();
                            mMainActivity.showUserInfomation();
                        }
                    }
                });
    }
}