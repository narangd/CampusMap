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
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.campusmap.R;

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

    private TextView textView;
    private SharedPreferences preferences;

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

        textView = (TextView) rootView.findViewById(R.id.text);


        boolean is_today_show =  !preferences.getString(getString(R.string.pref_key_last_skip_date), "").equals(TODAY_DATE);

        if (isIntetnetConnect() && is_today_show) {
            new AsyncTask<Void, String, Pair<String,String>>() {
                @Override
                protected Pair<String,String> doInBackground(Void... params) {
                    return parseGNTechMenuPlaner();
                }

                @Override
                protected void onPostExecute(Pair<String,String> result) {
                    super.onPostExecute(result);

                    builder.setTitle("오늘의 학식입니다");
                    builder.setMessage(result.first);
                    builder.show();
                    Log.i(TAG, "onPostExecute: length : " + result.second.length());

                    textView.setText(result.second);
                }
            }.execute();
        } else {
            textView.setText("인터넷에 연결이 되어 있지 않습니다...");
        }

        return rootView;
    }

    private Pair<String,String> parseGNTechMenuPlaner() {
        ArrayList<StringBuilder> days = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        String today = "";
        int today_index = 0;

        Document doc;
        try {
            doc = Jsoup.connect(GNTechURL).get();
            Elements tags = doc.select("table");
            Elements ths = tags.get(1).select("thead>tr>th");
            ths.remove(0);
            ths.remove(ths.size()-1);

            Log.i(TAG, "parseGNTechMenuPlaner: ths : " + ths.size());
            for (int i=0; i<ths.size(); i++) {
                StringBuilder th_builder = new StringBuilder();
                th_builder.append("날짜 : ")
                        .append(ths.get(i).ownText())
                        .append("\n\n");
                days.add(th_builder);

                if (ths.get(i).ownText().equals(TODAY_DATE)) {
                    today_index = i;
                }
            }

            Element tbody = tags.get(1).select("tbody").first();
            for (Element tr : tbody.children()) {
                Elements tr_days = tr.children();
                for (int i=1; i<tr_days.size(); i++) {
                    days.get(i-1)
                            .append("<")
                            .append(tr_days.get(0).ownText())
                            .append(">\n")
                            .append(tr_days.get(i).html().replace("<br>", "\n").replace("&amp;","&"))
                            .append("\n\n");

                }
            }
            today = days.get(today_index).toString();

            for (StringBuilder day : days) {
                builder.append(day.toString());
                builder.append("=-------------=\n\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Pair<>(today, builder.toString());
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
