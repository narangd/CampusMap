package com.example.campusmap.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.campusmap.R;
import com.example.campusmap.activity.BuildingActivity;
import com.example.campusmap.database.SQLiteHelperCampusInfo;
import com.example.campusmap.tree.branch.Building;
import com.example.campusmap.tree.branch.BuildingLocation;
import com.example.campusmap.view.TouchImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CampusMapFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static final String TAG = "CampusMapFragment";
    public static final int TAP_INDEX = 0;
    private static CampusMapFragment fragment = null;

    private Context context;
    private ListView mListView;
    private ArrayAdapter<Building> mAdapter;
    private Toast mToast;
    private ViewGroup mImageHeader;
    private TouchImageView mTouchImageView;
    private ArrayList<BuildingLocation> mTrigerBoxes = new ArrayList<>();

    /**
     * return only one CampusMapFragment.
     * @return
     */
    public static CampusMapFragment newInstance() {
        if (fragment == null) {
            fragment = new CampusMapFragment();
        }
        return fragment;
    }

    public CampusMapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_campus_map, container, false);
        context = rootView.getContext();

        mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);

        mListView = (ListView) rootView.findViewById(R.id.building_list);
        mImageHeader = (ViewGroup) inflater.inflate(R.layout.header_building, mListView, false);
        if (mImageHeader != null) {
            mTouchImageView = (TouchImageView) mImageHeader.findViewById(R.id.campus_map_view);
        }
        if (mListView != null) {
            mListView.addHeaderView(mImageHeader, null, false);
        }

        if (getArguments() != null) {
            int building = getArguments().getInt("building");
            onItemClick(null, null, building, 0);
        }

        InputStream inputStream = context.getResources().openRawResource(R.raw.building_location);
        InputStreamReader isReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(isReader);
//        JsonReader jsonReader  = new JsonReader(stream.toString());

        try {
            String in = "";
            String buffer;
            while ((buffer=reader.readLine())!=null) {
                in += buffer;
            }
            Log.i(TAG, "onCreateView: JsonReader->" + in);
            JSONArray jsonArray = new JSONArray(in);
            for (int i=0; i<jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                mTrigerBoxes.add(new BuildingLocation(
                        object.getInt("id"),
                        new Rect(
                                object.getInt("left"),
                                object.getInt("top"),
                                object.getInt("right"),
                                object.getInt("bottom")
                        )
                ));
            }
            Log.i(TAG, "onCreateView: TrigerBoes->" + mTrigerBoxes.size());
            reader.close();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }


        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        SQLiteHelperCampusInfo helper = SQLiteHelperCampusInfo.getInstance(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        if (mTouchImageView != null) {
            mTouchImageView.setMaxZoom(4.0f);
            mTouchImageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int perX = (int) (event.getX() / v.getWidth() * 100);
                    int perY = (int) (event.getY() / v.getHeight() * 100);
//                    for (BuildingLocation bLoc : mTrigerBoxes) {
//                        if (bLoc.contains(perX, perY)) {
//                            mToast.setText(bLoc.getID()+"-건물로 떠납니다.");
//                            mToast.show();
//                            break;
//                        }
//                    }
                    // view 기준으로 검색하므로 확대를 하였을 때 적용되지 않는다.
                    return true;
                }
            });
        }

        if (mListView != null) {
            mAdapter = new ArrayAdapter<>(
                    context,
                    android.R.layout.simple_list_item_1,
                    helper.getBuildingList(db)
            );
            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(context, BuildingActivity.class);
        intent.putExtra(BuildingActivity.BUILDING_TAG, mAdapter.getItem(position-1)); // id to index
        startActivity(intent);
    }
}
