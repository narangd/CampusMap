package com.example.campusmap.tree;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by 연구생 on 2015-07-20.
 */
public class ListPartner {
    private ListView listView;
    private TextView textView;
    private int clickedIndes;

    public ListPartner(@NonNull ListView listView, @Nullable TextView textView) {
        this.listView = listView;
        this.textView = textView;
        this.clickedIndes = -1;
    }

    public ListView getListView() {
        return listView;
    }

    public TextView getTextView() {
        return textView;
    }

    public void click(int index) {
        this.clickedIndes = index;
    }

    public int getClickedIndes() { return this.clickedIndes; }
}
