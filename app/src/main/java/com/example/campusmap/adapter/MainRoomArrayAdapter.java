package com.example.campusmap.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.campusmap.form.InfoLocation;

import java.util.List;

public class MainRoomArrayAdapter extends ArrayAdapter<InfoLocation> {

    private List<InfoLocation> mList;
    private int mResource;

    public MainRoomArrayAdapter(Context context, int resource, List<InfoLocation> list) {
        super(context, resource, list);
        mResource = resource;
        mList = list;
    }

//    public MainRoomArrayAdapter(Context context, int resource, InfoLocation[] list) {
//        super(context, resource, list);
//        mResource = resource;
//        mList = new ArrayList<>();
//        Collections.addAll(mList, list);
//    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mResource, null);
        }

        TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
        textView.setText(mList.get(position).getName());

        return convertView;
    }
}
