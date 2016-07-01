package com.example.campusmap.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.campusmap.R;
import com.example.campusmap.database.SearchResultItem;
import com.example.campusmap.fragment.pager.CampusMapPagerAdapter;
import com.example.campusmap.xmlparser.BuildingInfoParser;

import java.io.Serializable;

public class CampusMapActivity extends AppCompatActivity {
    private static final String TAG = "CampusMapActivity";
    private static BuildingInfoParser parser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campus_map);

        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);

        if (parser == null)
            parser = BuildingInfoParser.getInstance(getResources().getXml(R.xml.building_info));



        int buildingID = getIntent().getIntExtra("building_id", 0);

//        Intent intent = getIntent();
//        Serializable data;
//
//        data = intent.getSerializableExtra("building");
//        if (data != null) {
//            Log.i(TAG, "onCreate: data to buildingNumber");
//            int buildingNumber = (int)data;
//
//            FloorPagerAdapter mFloorPagerAdapter = new FloorPagerAdapter(getSupportFragmentManager(),
//                    buildingList.get(buildingNumber) );
//            Log.i(TAG, "onCreate: mFloorPagerAdapter set " + buildingList.get(buildingNumber).toString());
//            mViewPager.setAdapter(mFloorPagerAdapter);
//        } else {

        Serializable serializableExtra = getIntent().getSerializableExtra("search_item");
        SearchResultItem resultItem = (SearchResultItem) serializableExtra;
//        resultItem.

        if (mViewPager != null){
            CampusMapPagerAdapter adapter = new CampusMapPagerAdapter(getSupportFragmentManager(), buildingID);
            mViewPager.setAdapter(adapter);
        }

//        ((TabLayout) findViewById(R.id.tabs)).
//                setupWithViewPager(mViewPager);
    }
}
