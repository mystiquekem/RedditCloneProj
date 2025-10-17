package com.example.redditclone.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.redditclone.MainActivity;
import com.example.redditclone.R;
import com.example.redditclone.MainActivity;
import com.example.redditclone.fragment.HomeFragment;

public class CurateFragment extends Fragment {

    public CurateFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Gắn layout XML: fragment_curate.xml
        View view = inflater.inflate(R.layout.fragment_curate, container, false);

        ImageButton backButton = view.findViewById(R.id.back_button);
        if (backButton != null) {
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Kiểm tra và gọi switchFragment để chuyển về HomeFragment
                    if (getActivity() instanceof MainActivity) {
                        MainActivity mainActivity = (MainActivity) getActivity();

                        // Giả định HomeFragment là FRAGMENT_PROFILE (hoặc một hằng số khác mà bạn dùng cho Home)
                        // Cần đảm bảo FRAGMENT_PROFILE và switchFragment là public trong MainActivity
                        mainActivity.switchFragment(MainActivity.FRAGMENT_HOME, new HomeFragment());
                    }
                }
            });
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() != null) {
            View toolbar = getActivity().findViewById(R.id.toolbar);
            if (toolbar != null) {
                toolbar.setVisibility(View.VISIBLE);
            }
        }
    }
}