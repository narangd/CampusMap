package com.example.campusmap.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.TextViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.campusmap.R;
import com.example.campusmap.database.SQLiteHelperCampusInfo;

public class BuildingDetailFragment extends Fragment {

    public BuildingDetailFragment() {
        // Required empty public constructor
    }
    public static BuildingDetailFragment newInstance() {
        BuildingDetailFragment fragment = new BuildingDetailFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_building_detail, container, false);

        TextView textView = (TextView) view.findViewById(R.id.description);

        Bundle bundle = getArguments();
        if (bundle != null) {
            int building = bundle.getInt("building");
            SQLiteHelperCampusInfo.getInstance(getContext()).getReadableDatabase().execSQL("SELECT desc");
            textView.setText("");
        } else {
            textView.setText("설명들...");
        }
        return view;
    }
}
