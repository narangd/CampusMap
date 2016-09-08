package com.example.campusmap.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.campusmap.R;
import com.example.campusmap.adapter.SearchItemAdapter;
import com.example.campusmap.database.InfoLocation;
import com.example.campusmap.database.SQLiteHelperCampusInfo;

import java.util.ArrayList;

public class SearchResultActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener, DialogInterface.OnClickListener {
    private static final String TAG = "SearchResultActivity";
    public static final int RESULT_OK = 1;

    String query;
    InfoLocation infoLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

//        ProgressDialog dialog = ProgressDialog.show(this, "", "검색 중입니다.", true);

        query = getIntent().getStringExtra("query");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("\"" + query + "\"을(를) 검색한 결과입니다.");
        }

        SQLiteHelperCampusInfo helper = SQLiteHelperCampusInfo.getInstance(this);
        SQLiteDatabase db = helper.getReadableDatabase();

        ArrayList<InfoLocation> result = helper.searchResultItems(db, query);
        db.close();

        final ListView listView = (ListView)findViewById(R.id.result_list);
        if (listView != null) {
            listView.setAdapter(new SearchItemAdapter(
                    this,
                    android.R.layout.simple_list_item_2,
                    result
            ));
            listView.setOnItemClickListener(this);
        }

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        if (floatingActionButton != null) {
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listView != null) {
                        listView.setSelectionAfterHeaderView();
                    }
                }
            });
        }
    }

    // ## For ListView ##
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        infoLocation = (InfoLocation)adapterView.getItemAtPosition(position);
        Log.i(TAG, "onItemClick: " + infoLocation.toString());
        alertMessage(infoLocation.toString());
    }

    public void alertMessage(String destination) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(destination + "(으)로 이동하시겠습니까?")
                .setPositiveButton("예", this)
                .setNegativeButton("취소", null)
                .show();
    }

    // ## For AlertDialog ##
    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                Intent result = new Intent();
                result.putExtra(DrawerTestActivity.KEY_SEACH_ITEM, infoLocation);
                setResult(RESULT_OK, result);
                finish();
                break;
        }
    }
}
