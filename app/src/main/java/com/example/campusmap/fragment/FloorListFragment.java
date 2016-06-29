package com.example.campusmap.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.campusmap.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FloorListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FloorListFragment extends Fragment {
    private static final String BUILDING_ID = "buildingID";

    public FloorListFragment() {
        // Required empty public constructor
    }

    public static FloorListFragment newInstance(int buildingId) {
        FloorListFragment fragment = new FloorListFragment();
        Bundle args = new Bundle();
        args.putInt(BUILDING_ID, buildingId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            int buildingId = getArguments().getInt(BUILDING_ID);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_floor_list, container, false);
    }

}
