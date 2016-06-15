package com.example.campusmap.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.campusmap.R;
import com.example.campusmap.tree.branch.Parent;
import com.example.campusmap.xmlparser.BuildingInfoParser;
import com.example.campusmap.xmlparser.search.ParentAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class SearchResultActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener, DialogInterface.OnClickListener {
    public static final int RESULT_OK = 1;

    String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

//        ProgressDialog dialog = ProgressDialog.show(this, "", "검색 중입니다.", true);

        query = getIntent().getStringExtra("query");

        ((Toolbar) findViewById(R.id.toolbar) ).setTitle("\"" + query + "\"을(를) 검색한 결과입니다.");
        ArrayList<Parent> result = BuildingInfoParser.search(getBaseContext(), query);
        Collections.sort(result);

        ListView listView = (ListView)findViewById(R.id.result_listview);
        listView.setAdapter(new ParentAdapter(
                this,
                android.R.layout.simple_list_item_2,
                result
        ));
        listView.setOnItemClickListener(this);
    }

    Parent parent;
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        parent = (Parent)adapterView.getItemAtPosition(position);
        Log.i("Parent", "Parent : " + this.parent);
        Log.d("Parent Class", parent.getClass().toString());
        alertMessage(parent.toString());
    }

    public void alertMessage(String destination) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(destination + "로 이동하시겠습니까?")
                .setPositiveButton("예", this)
                .setNegativeButton("취소", this)
                .show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                Intent result = new Intent();
                result.putExtra("parent", parent);
                setResult(RESULT_OK, result);
                finish();
                break;
        }
    }
}
