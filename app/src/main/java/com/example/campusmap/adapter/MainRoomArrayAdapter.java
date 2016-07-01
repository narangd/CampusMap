package com.example.campusmap.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by 성용 on 2016-06-30.
 */
public class MainRoomArrayAdapter extends ArrayAdapter<Pair<String,Integer[]>> {

    private List<Pair<String,Integer[]>> mList;
    private int mResource;

    public MainRoomArrayAdapter(Context context, int resource, List<Pair<String, Integer[]>> list) {
        super(context, resource, list);
        mResource = resource;
        mList = list;
    }

    public MainRoomArrayAdapter(Context context, int resource, Pair<String, Integer[]>[] list) {
        super(context, resource, list);
        mResource = resource;
        mList = new ArrayList<>();
        Collections.addAll(mList, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mResource, null);
        }

        TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
        textView.setText(mList.get(position).first);

        return convertView;
    }
}
