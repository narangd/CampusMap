package com.example.campusmap.asynctask.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.util.Pair;

import com.example.campusmap.Internet;
import com.example.campusmap.form.MenuPlanner;
import com.example.campusmap.fragment.MenuPlannerFragment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class MenuPlannerLoader extends AsyncTaskLoader<Pair<Integer,ArrayList<MenuPlanner>>> {
    private static final String TAG = "MenuPlannerLoader";
    private static final boolean DEBUG = false;
    private Pair<Integer, ArrayList<MenuPlanner>> data;

    public MenuPlannerLoader(Context context) {
        super(context);
    }

    @Override
    public Pair<Integer,ArrayList<MenuPlanner>> loadInBackground() {
        ArrayList<MenuPlanner> menuPlanners = new ArrayList<>();
        int today_index = 0;

        if (!Internet.isConnect(getContext())) {
            return new Pair<>(null, null);
        }

        try {
            // 네트워크가 않좋거나 끊어졌을시 발생할 가능성이 있는 Exception : UnknownHostException ("www1.gntech.ac.kr")
            Document document = Jsoup.connect(MenuPlannerFragment.GNTechURL).get();
            Elements tags = document.select("table");
            Elements ths = tags.get(1).select("thead>tr>th");
            ths.remove(0); // 구분 삭제
            ths.remove(ths.size()-1); // 비고 삭제

            Log.i(TAG, "parseGNTechMenuPlaner: ths : " + ths.size());
            for (int i=0; i<ths.size(); i++) {
                // date
                menuPlanners.add(
                        new MenuPlanner(ths.get(i).ownText())
                );

                if (ths.get(i).ownText().equals(MenuPlannerFragment.TODAY_DATE)) {
                    today_index = i;
                }
            }

            Element tbody = tags.get(1).select("tbody").first();
            // 조식, 중식, 석식, 교직원 (세로)
            for (int meal_index=0; meal_index<tbody.children().size(); meal_index++) {

                Elements tr_days = tbody.children().get(meal_index).children();
                // 구분 일 (월 화 수 목 금) 토
                for (int day_index=1; day_index<tr_days.size(); day_index++) {
                    String menu = tr_days.get(day_index).html().replace("&amp;","&").replaceAll("(\n| )", "");
                    menuPlanners.get(day_index-1).addMeal(
                            tr_days.get(0).ownText(),
                            menu.split("<br><br>")
                    );

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Pair<>(today_index, menuPlanners);
    }

    @Override
    public void deliverResult(Pair<Integer,ArrayList<MenuPlanner>> data) {
        if (isReset()) {
            if (DEBUG) Log.w(TAG, "+++ Warning! An async query came in while the Loader was reset! +++");

            if (data != null) {
                if (DEBUG) Log.w(TAG, "+++ data.second.clear() called! +++");
                data.second.clear();
                return;
            }
        }

        Pair<Integer,ArrayList<MenuPlanner>> oldData = this.data;
        this.data = data;

        if (isStarted()) {
            if (DEBUG) Log.i(TAG, "+++ Delivering results to the LoaderManager for" +
                    " the Fragment to display! +++");

            super.deliverResult(data);
        }

        if (oldData != null && oldData != data) {
            if (DEBUG) Log.i(TAG, "+++ Releasing any old data associated with this Loader. +++");
            if (DEBUG) Log.w(TAG, "+++ oldData.second.clear() called! +++");
            oldData.second.clear();
        }

        super.deliverResult(data);
    }

    @Override
    protected void onStartLoading() {
        if (DEBUG) Log.i(TAG, "+++ onStartLoading() called! +++");

        if (data != null) {
            if (DEBUG) Log.i(TAG, "+++ Delivering previously loaded data to the client...");
            deliverResult(data);
        } else {
            if (DEBUG) Log.i(TAG, "+++ The current data is data is null... so force load! +++");
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        if (DEBUG) Log.i(TAG, "+++ onStopLoading() called! +++");

        cancelLoad();
    }

    @Override
    protected void onReset() {
        if (DEBUG) Log.i(TAG, "+++ onReset() called! +++");

        onStopLoading();

        if (data != null) {
            if (DEBUG) Log.w(TAG, "+++ data.second.clear() called! +++");
            data.second.clear(); // release map
            data = null;
        }
    }

    @Override
    public void onCanceled(Pair<Integer,ArrayList<MenuPlanner>> data) {
        if (DEBUG) Log.i(TAG, "+++ onCanceled() called! +++");

        super.onCanceled(data);

        if (data != null)
            data.second.clear();;
    }

    @Override
    public void forceLoad() {
        if (DEBUG) Log.i(TAG, "+++ forceLoad() called! +++");
        super.forceLoad();
    }
}
