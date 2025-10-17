package com.example.redditclone;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.textfield.TextInputEditText;

public class EditProfileFragment extends Fragment {

    // Khai báo các key hằng số để giao tiếp an toàn giữa các fragment
    public static final String REQUEST_KEY = "editProfileRequest";
    public static final String KEY_DISPLAY_NAME = "displayName";
    public static final String KEY_ABOUT_YOU = "aboutYou";

    private TextInputEditText displayNameInput;
    private TextInputEditText aboutYouInput;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ các view từ layout
        ImageView closeButton = view.findViewById(R.id.close_button);
        TextView saveButton = view.findViewById(R.id.save_button);
        displayNameInput = view.findViewById(R.id.display_name_input);
        aboutYouInput = view.findViewById(R.id.about_you_input);

        // Nhận dữ liệu được gửi từ MyProfileFragment (nếu có) và hiển thị lên các ô nhập liệu
        if (getArguments() != null) {
            String currentDisplayName = getArguments().getString(KEY_DISPLAY_NAME);
            String currentAboutYou = getArguments().getString(KEY_ABOUT_YOU);
            displayNameInput.setText(currentDisplayName);
            aboutYouInput.setText(currentAboutYou);
        }

        // Gán sự kiện cho nút đóng: quay lại màn hình trước
        closeButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        // Gán sự kiện cho nút Save
        saveButton.setOnClickListener(v -> {
            // Lấy dữ liệu mới người dùng đã nhập
            String newDisplayName = displayNameInput.getText().toString();
            String newAboutYou = aboutYouInput.getText().toString();

            // Tạo một Bundle để đóng gói kết quả
            Bundle result = new Bundle();
            result.putString(KEY_DISPLAY_NAME, newDisplayName);
            result.putString(KEY_ABOUT_YOU, newAboutYou);

            // Sử dụng setFragmentResult để gửi dữ liệu về cho fragment đã gọi nó
            getParentFragmentManager().setFragmentResult(REQUEST_KEY, result);

            // Sau khi gửi, quay lại màn hình trước
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }
}

