package com.example.campusmap.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.campusmap.R;
import com.example.campusmap.form.Updater;

import java.util.List;

public class UpdaterAdapter extends ArrayAdapter<Updater> {

    private List<Updater> mUpdaterList;

    public UpdaterAdapter(Context context, List<Updater> updaterList) {
        super(context, R.layout.list_item_with_number, updaterList);

        mUpdaterList = updaterList;
    }

    @NonNull
    @Override
    public View getView(int position, View rootView, ViewGroup parent) {

        if (rootView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rootView = inflater.inflate(
                    R.layout.list_item_with_number, null);
        }

        Updater currentUpdater = mUpdaterList.get(position);

        ImageView favorite = (ImageView) rootView.findViewById(R.id.favorite);
        favorite.setColorFilter(0xFFF44336);

        TextView title = (TextView) rootView.findViewById(R.id.title);
        title.setText( currentUpdater.getTitle() );

        TextView number = (TextView) rootView.findViewById(R.id.number);
        number.setText( String.valueOf(currentUpdater.getVote()) );

        return rootView;
    }
}
