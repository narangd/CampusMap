package com.example.campusmap.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.campusmap.R;
import com.example.campusmap.asynctask.loader.MenuPlannerLoader;
import com.example.campusmap.form.MenuPlanner;
import com.example.campusmap.fragment.pager.MenuPlannerPagerAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MenuPlannerFragment extends Fragment implements LoaderManager.LoaderCallbacks<Pair<Integer,ArrayList<MenuPlanner>>>{
    public static final int TAP_INDEX = 1;
    private static final String TAG = "MenuPlannerFragment";
    private static final SimpleDateFormat GNTECH_DATE_FORMAT = new SimpleDateFormat("M월 d일(E)", Locale.KOREAN);
    public static final String GNTechURL = "http://www1.gntech.ac.kr/web/www/178";
    public static final String TODAY_DATE = GNTECH_DATE_FORMAT.format(new Date());

    private SharedPreferences preferences;
    private ViewPager viewPager;

    public MenuPlannerFragment() {
    }

    public static MenuPlannerFragment newInstance() {
        return new MenuPlannerFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        getLoaderManager().initLoader(1, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_menu_planner, container, false);

        viewPager = (ViewPager) rootView.findViewById(R.id.view_pager);

        return rootView;
    }

    @Override
    public Loader<Pair<Integer, ArrayList<MenuPlanner>>> onCreateLoader(int id, Bundle args) {
        return new MenuPlannerLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader<Pair<Integer, ArrayList<MenuPlanner>>> loader, Pair<Integer, ArrayList<MenuPlanner>> data) {

        if (data.first == null || data.second == null) {
            return;
        }

        viewPager.setAdapter(
                new MenuPlannerPagerAdapter(
                        getActivity().getSupportFragmentManager(),
                        data.second
                )
        );
        viewPager.setCurrentItem(data.first);
    }

    @Override
    public void onLoaderReset(Loader<Pair<Integer, ArrayList<MenuPlanner>>> loader) {
        viewPager.removeAllViews();
    }

}
