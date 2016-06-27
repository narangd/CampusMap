package com.example.campusmap.asynctask.loader;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.campusmap.database.SQLiteHelperOstacle;
import com.example.campusmap.pathfinding.graphic.Map;
import com.example.campusmap.pathfinding.graphic.Polygon;

import java.util.ArrayList;

/**
 * Created by 연구생 on 2015-11-16.
 */
public class PolygonLoader extends AsyncTaskLoader<ArrayList<Polygon>> {
    private static final String TAG = "ADP_PolygonLoader";
    private static final boolean DEBUG = true;

    private Map map;
    private ArrayList<Polygon> mPolygons;

    public PolygonLoader(Context context, Map map) {
        super(context);
        this.map = map;
    }

    @Override
    public ArrayList<Polygon> loadInBackground() {
        if (DEBUG) Log.i(TAG, "+++ loadInBackground() called! +++");

        ArrayList<Polygon> polygons = new ArrayList<>();
        Cursor cursor = SQLiteHelperOstacle.getInstance(getContext()).select();

        while (cursor.moveToNext()) {
            Polygon polygon = new Polygon(map, cursor.getString(cursor.getColumnIndex("polygon")));
            polygons.add(polygon);
        }

        map.resetPolygon();
        map.register(polygons);

        Log.i("PolygonLoader", "cursor size : " + cursor.getCount());
        Log.i("PolygonLoader", "polygons size : " + polygons.size());

        return polygons;
    }

    @Override
    public void deliverResult(ArrayList<Polygon> polygons) {
        if (isReset()) {
            if (DEBUG) Log.w(TAG, "+++ Warning! An async query came in while the Loader was reset! +++");

            if (polygons != null) {
                if (DEBUG) Log.w(TAG, "+++ polygons.clear() called! +++");
                polygons.clear();
                return;
            }
        }

        ArrayList<Polygon> oldPolygons = mPolygons;
        mPolygons = polygons;

        if (isStarted()) {
            if (DEBUG) Log.i(TAG, "+++ Delivering results to the LoaderManager for" +
                    " the Fragment to display! +++");

            super.deliverResult(polygons);
        }

        if (oldPolygons != null && oldPolygons != polygons) {
            if (DEBUG) Log.i(TAG, "+++ Releasing any old data associated with this Loader. +++");
            if (DEBUG) Log.w(TAG, "+++ oldPolygons.clear() called! +++");
            oldPolygons.clear();
        }

        super.deliverResult(polygons);
    }

    @Override
    protected void onStartLoading() {
        if (DEBUG) Log.i(TAG, "+++ onStartLoading() called! +++");

        if (mPolygons != null) {
            if (DEBUG) Log.i(TAG, "+++ Delivering previously loaded data to the client...");
            deliverResult(mPolygons);
        } else {
            if (DEBUG) Log.i(TAG, "+++ The current data is data is null... so force load! +++");
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        if (DEBUG) Log.i(TAG, "+++ onStopLoading() called! +++");

        cancelLoad();
    }

    @Override
    protected void onReset() {
        if (DEBUG) Log.i(TAG, "+++ onReset() called! +++");

        onStopLoading();

        if (mPolygons != null) {
            if (DEBUG) Log.w(TAG, "+++ mPolygons.clear() called! +++");
            mPolygons.clear();
            mPolygons = null;
        }
    }

    @Override
    public void onCanceled(ArrayList<Polygon> polygons) {
        if (DEBUG) Log.i(TAG, "+++ onCanceled() called! +++");

        super.onCanceled(polygons);

        polygons.clear();
    }

    @Override
    public void forceLoad() {
        if (DEBUG) Log.i(TAG, "+++ forceLoad() called! +++");
        super.forceLoad();
    }
}
