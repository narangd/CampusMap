package com.example.campusmap.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.campusmap.R;
import com.example.campusmap.activity.NMTestActivity;


public class PathFindingFragment extends Fragment { //  implements LoaderManager.LoaderCallbacks<MapManager>
    private static final String TAG = "PathFindingFragment";
    private static final boolean DEBUG = false;
    public static final int TAP_INDEX = 2;

    public static PathFindingFragment newInstance() {
        return new PathFindingFragment();
    }

    public PathFindingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (DEBUG) Log.i(TAG, "+++ onCreateView() called! +++");

        View rootView = inflater.inflate(R.layout.fragment_path_finding, container, false);
        final FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        getContext(), NMTestActivity.class
                );
                startActivity(intent);
            }
        });

        return rootView;
    }

}