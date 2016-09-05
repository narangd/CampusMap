package com.example.campusmap.fragment.pager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.campusmap.R;
import com.example.campusmap.form.MenuPlanner;

import java.util.List;

public class MenuPlannerPagerAdapter extends FragmentPagerAdapter {

    private final List<MenuPlanner> list;

    public MenuPlannerPagerAdapter(FragmentManager manager, List<MenuPlanner> list) {
        super(manager);
        this.list = list;
    }

    @Override
    public Fragment getItem(int position) {
        return MenuPlannerFragment.newInstance(list.get(position));
    }

    @Override
    public int getCount() {
        return list.size();
    }

    private static class MenuPlannerFragment extends Fragment {
        private static final String KEY_PLANNER = "menu_planner";

        private MenuPlanner menuPlanner;
        private RecyclerView recyclerView;

        public static MenuPlannerFragment newInstance(MenuPlanner menuPlanner) {
            MenuPlannerFragment fragment = new MenuPlannerFragment();
            Bundle args = new Bundle();
            args.putSerializable(KEY_PLANNER, menuPlanner);
            fragment.setArguments(args);
            return fragment;
        }

        public MenuPlannerFragment() {
            /*  */
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_menu_planner_piece, container, false);

            recyclerView = (RecyclerView) rootView.findViewById(R.id.menu_recycler_view);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager( new LinearLayoutManager(getContext()) );

            menuPlanner = (MenuPlanner) getArguments().getSerializable(KEY_PLANNER);

            MenuPlanner.MealAdapter mealAdapter = new MenuPlanner.MealAdapter(menuPlanner);
            recyclerView.setAdapter(mealAdapter);

            return rootView;
        }
    }
}
