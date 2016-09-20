package com.example.campusmap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.campusmap.database.InfoLocation;
import com.example.campusmap.database.SQLiteHelperCampusInfo;

import java.util.List;

public class SearchItemAdapter extends ArrayAdapter<InfoLocation> {

    private List<InfoLocation> mList;

    public SearchItemAdapter(Context context, int resource, List<InfoLocation> list) {
        super(context, resource, list);
        mList = list;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(
                    android.R.layout.simple_list_item_2, null);
        }

        TextView text1 = (TextView) view.findViewById(android.R.id.text1);
        TextView text2 = (TextView) view.findViewById(android.R.id.text2);

        InfoLocation currentItem = mList.get(position);

        text1.setText(currentItem.toString());

        // ## Search Database ##
        if (currentItem.getRoomID() != InfoLocation.NONE) { // room
            SQLiteHelperCampusInfo sqLiteHelperCampusInfo = SQLiteHelperCampusInfo.getInstance(getContext());
            String path = sqLiteHelperCampusInfo.getRoomPath(currentItem.getRoomID());

            text2.setText(path);

        } else { // tag == building .. do noting..
            text2.setText(" - ");
        }

        return view;
    }
}
