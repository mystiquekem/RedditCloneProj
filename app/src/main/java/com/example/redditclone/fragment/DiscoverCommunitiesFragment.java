package com.example.redditclone.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SearchView; // Import for SearchView
import android.view.View; // Import for View
import android.widget.Toast; // Import for Toast

import com.example.redditclone.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DiscoverCommunitiesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiscoverCommunitiesFragment extends Fragment {
//nah
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DiscoverCommunitiesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.asdasdasdasdasd
     * @param param2 Parameter 2.asdasdasd
     * @return A new instance of fragment DiscoverCommunitiesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DiscoverCommunitiesFragment newInstance(String param1, String param2) {
        DiscoverCommunitiesFragment fragment = new DiscoverCommunitiesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discover_communities, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        SearchView searchView = view.findViewById(R.id.search_view_communities);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Toast.makeText(getContext(), "Searching for: " + query, Toast.LENGTH_SHORT).show();


                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });
    }
}