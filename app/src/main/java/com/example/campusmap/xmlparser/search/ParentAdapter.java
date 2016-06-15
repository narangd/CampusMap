package com.example.campusmap.xmlparser.search;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.campusmap.tree.branch.Parent;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by 연구생 on 2015-11-23.
 */
public class ParentAdapter extends ArrayAdapter<Parent> {
    private List<Parent> parents;
    public ParentAdapter(Context context, int resource, List<Parent> parents) {
        super(context, resource, parents);
        this.parents = parents;
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

        text1.setText(parents.get(position).toString());

        Parent parent = parents.get(position).getParent();
        LinkedList<String> path = new LinkedList<>();
        while (parent != null) {
            path.addFirst(parent.toString());
            parent = parent.getParent();
        }
        text2.setText(TextUtils.join(" / ", path));

        return view;
    }
}
