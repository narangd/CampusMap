package com.example.campusmap.xmlparser.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by rla on 2015-10-29.
 */
public class PairAdapter extends BaseAdapter {

        private  Context context;
        private ArrayList<Pair> pairs;


    public PairAdapter(Context context, ArrayList<Pair> pairs) {
        this.context = context;
        this.pairs = pairs;
    }

    @Override
    public int getCount() {
        return pairs.size();
    }

    @Override
    public Object getItem(int position) {
        return pairs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(
                    android.R.layout.simple_list_item_2, null);
        } else {
            view = convertView;
        }

        TextView text1 = (TextView)view.findViewById(android.R.id.text1);
        TextView text2 = (TextView)view.findViewById(android.R.id.text2);

        text1.setText(pairs.get(position).getMain());
        text2.setText(pairs.get(position).getSub());

        return view;
    }

}
