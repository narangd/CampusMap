package com.example.campusmap.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.campusmap.R;
import com.example.campusmap.tree.branch.Building;
import com.example.campusmap.tree.branch.Room;

public class BuildingActivity extends AppCompatActivity {
    private static final String TAG = "BuildingActivity";
    public static final String KEY_BUILDING = "building";
    public static final String KEY_ROOM = "room";

    private ViewPager mViewPager;
    private Building mBuilding;
    private Room mRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building);

        mViewPager = (ViewPager) findViewById(R.id.container);

        Intent intent = getIntent();
        mBuilding = (Building)intent.getSerializableExtra(KEY_BUILDING);
        mRoom = (Room) intent.getSerializableExtra(KEY_ROOM);

//        Serializable serializableExtra = getIntent().getSerializableExtra("search_item");
//        SearchResultItem resultItem = (SearchResultItem) serializableExtra;
//        resultItem.

//        ((TabLayout) findViewById(R.id.tabs)).
//                setupWithViewPager(mViewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
