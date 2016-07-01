package com.example.campusmap.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.campusmap.database.SQLiteHelperCampusInfo;
import com.example.campusmap.database.SearchResultItem;
import com.example.campusmap.tree.branch.Parent;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by 성용 on 2016-06-28.
 */
public class SearchItemAdapter extends ArrayAdapter<SearchResultItem> {

    private List<SearchResultItem> mList;

    public SearchItemAdapter(Context context, int resource, List<SearchResultItem> list) {
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

        SearchResultItem currentItem = mList.get(position);

        text1.setText(currentItem.toString());

        // ## Search Database ##
        if (currentItem.mTag == SearchResultItem.Tag.ROOM) {
            SQLiteHelperCampusInfo sqLiteHelperCampusInfo = SQLiteHelperCampusInfo.getInstance(getContext());
            SQLiteDatabase db = sqLiteHelperCampusInfo.getReadableDatabase();
            String path = sqLiteHelperCampusInfo.searchRoomHierarchy(db, currentItem.mParentId);
            db.close();

            text2.setText(path);

//        text2.setText(TextUtils.join(" / ", path));
        } else { // tag == building .. do noting..
            text2.setText(" - ");
        }

        return view;
    }
}
