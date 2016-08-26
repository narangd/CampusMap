package com.example.campusmap.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.campusmap.R;
import com.example.campusmap.form.MenuPlanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MenuPlannerFragment extends Fragment {
    public static final int TAP_INDEX = 1;
    private static final String GNTechURL = "http://www1.gntech.ac.kr/web/www/178";
    private static final String TAG = "MenuPlannerFragment";
    private static final SimpleDateFormat GNTECH_DATE_FORMAT = new SimpleDateFormat("M월 d일(E)", Locale.KOREAN);
    public static final String TODAY_DATE = GNTECH_DATE_FORMAT.format(new Date());

    private SharedPreferences preferences;
    private RecyclerView recyclerView;

    public MenuPlannerFragment() {
    }

    public static MenuPlannerFragment newInstance() {
        return new MenuPlannerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_menu_planer, container, false);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setPositiveButton("닫기", null);
        builder.setNegativeButton("오늘그만보기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(getString(R.string.pref_key_last_skip_date), TODAY_DATE);
                editor.putBoolean(getString(R.string.pref_key_today_menu_planner), false);
                editor.apply();
            }
        });

        recyclerView = (RecyclerView) rootView.findViewById(R.id.menu_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        if (isIntetnetConnect()) {
            new AsyncTask<Void, String, Pair<MenuPlanner,ArrayList<MenuPlanner>>>() {
                @Override
                protected Pair<MenuPlanner,ArrayList<MenuPlanner>> doInBackground(Void... params) {
                    return parseGNTechMenuPlaner();
                }

                @Override
                protected void onPostExecute(Pair<MenuPlanner,ArrayList<MenuPlanner>> result) {
                    super.onPostExecute(result);

                    if (result.first == null || result.second == null) {
                        return;
                    }

                    MenuPlanner.MealAdapter mealAdapter = new MenuPlanner.MealAdapter(result.first);
                    recyclerView.setAdapter(mealAdapter);

                    boolean is_today_show =  !preferences.getString(getString(R.string.pref_key_last_skip_date), "").equals(TODAY_DATE);
                    if (is_today_show) {
                        builder.setTitle("오늘의 학식입니다"); // breakfast lunch dinner
                        builder.setMessage(result.first.toString());
                        builder.show();
                    }
                    Log.i(TAG, "onPostExecute: length : " + result.second.size());
                }
            }.execute();
        } else {
            Log.e(TAG, "onCreateView: 인터넷에 연결이 되어 있지 않습니다...");
        }

        return rootView;
    }

    private Pair<MenuPlanner,ArrayList<MenuPlanner>> parseGNTechMenuPlaner() {
        ArrayList<MenuPlanner> menuPlanners = new ArrayList<>();
        MenuPlanner today = null;
        int today_index = 0;
        Document doc;

        try {
            doc = Jsoup.connect(GNTechURL).get();
            Elements tags = doc.select("table");
            Elements ths = tags.get(1).select("thead>tr>th");
            ths.remove(0); // 구분 삭제
            ths.remove(ths.size()-1); // 비고 삭제

            Log.i(TAG, "parseGNTechMenuPlaner: ths : " + ths.size());
            for (int i=0; i<ths.size(); i++) {
                // date
                menuPlanners.add(
                        new MenuPlanner(ths.get(i).ownText())
                );

                if (ths.get(i).ownText().equals(TODAY_DATE)) {
                    today_index = i;
                }
            }

            Element tbody = tags.get(1).select("tbody").first();
            // 조, 중, 석, 교 -- skip 조
            for (int meal_index=1; meal_index<tbody.children().size(); meal_index++) {

                Elements tr_days = tbody.children().get(meal_index).children();
                // 일 (월 화 수 목 금) 토
                for (int day_index=1; day_index<tr_days.size(); day_index++) {
                    String menu = tr_days.get(day_index).html().replace("&amp;","&");
                    menuPlanners.get(day_index-1).addMeal(
                            meal_index,
                            tr_days.get(0).ownText(),
                            menu.split("<br>")
                    );

                }
            }
            today = menuPlanners.get(today_index);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Pair<>(today, menuPlanners);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

//        alertDialog.dismiss();
    }

    private boolean isIntetnetConnect() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

}
