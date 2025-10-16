package com.example.redditclone.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.redditclone.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MyProfileFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;

    public MyProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);

        if (bottomNavigationView != null) {
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();

                bottomNavigationView.setOnItemSelectedListener(null);

                getParentFragmentManager().popBackStack();

                bottomNavigationView.post(() -> {
                    bottomNavigationView.setSelectedItemId(itemId);
                });

                return true;
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnItemSelectedListener(null);
        }
    }
}