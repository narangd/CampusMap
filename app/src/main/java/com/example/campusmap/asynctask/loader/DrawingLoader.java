package com.example.campusmap.asynctask.loader;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.widget.ImageView;

import com.example.campusmap.database.SQLiteHelperObstacle;
import com.example.campusmap.pathfinding.Drawing;
import com.example.campusmap.pathfinding.Map;
import com.example.campusmap.pathfinding.Polygon;

import java.util.ArrayList;

public class DrawingLoader extends AsyncTaskLoader<Drawing> {
    private static final String TAG = "DrawingLoader";
    private static final boolean DEBUG = false;

    private Context context;
    private ImageView imageView;
    private Drawing drawing;

    public DrawingLoader(Context context, ImageView imageView) {
        super(context);
        this.context = context;
        this.imageView = imageView;
    }

    @Override
    public Drawing loadInBackground() {
        if (DEBUG) Log.i(TAG, "+++ loadInBackground() called! +++");

        if (DEBUG) Log.i(TAG, "+++ create new Map +++");
        Drawing drawing = new Drawing(context, imageView);
        Map map = drawing.getMap();

        ArrayList<Polygon> polygons = new ArrayList<>();
        Cursor cursor = SQLiteHelperObstacle.getInstance(getContext()).select();

        while (cursor.moveToNext()) {
            Polygon polygon = new Polygon(map, cursor.getString(cursor.getColumnIndex("polygon")));
            polygons.add(polygon);
        }

        map.resetPolygon();
        map.register(polygons);

        Log.i("PolygonLoader", "cursor size : " + cursor.getCount());
        Log.i("PolygonLoader", "polygons size : " + polygons.size());

        return drawing;
    }

    @Override
    public void deliverResult(Drawing drawing) {
        if (isReset()) {
            if (DEBUG) Log.w(TAG, "+++ Warning! An async query came in while the Loader was reset! +++");

            if (drawing != null) {
                if (DEBUG) Log.w(TAG, "+++ polygons.clear() called! +++");
                drawing.getMap().resetPolygon();
                return;
            }
        }

        Drawing oldDrawing = this.drawing;
        this.drawing = drawing;

        if (isStarted()) {
            if (DEBUG) Log.i(TAG, "+++ Delivering results to the LoaderManager for" +
                    " the Fragment to display! +++");

            super.deliverResult(drawing);
        }

        if (oldDrawing != null && oldDrawing != drawing) {
            if (DEBUG) Log.i(TAG, "+++ Releasing any old data associated with this Loader. +++");
            if (DEBUG) Log.w(TAG, "+++ oldMap.resetPolygon() called! +++");
            oldDrawing.getMap().resetPolygon();
        }

        super.deliverResult(drawing);
    }

    @Override
    protected void onStartLoading() {
        if (DEBUG) Log.i(TAG, "+++ onStartLoading() called! +++");

        if (drawing != null) {
            if (DEBUG) Log.i(TAG, "+++ Delivering previously loaded data to the client...");
            deliverResult(drawing);
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

        if (drawing != null) {
            if (DEBUG) Log.w(TAG, "+++ mMap.resetPolygon() called! +++");
            drawing.getMap().resetPolygon(); // release map
            drawing = null;
        }
    }

    @Override
    public void onCanceled(Drawing drawing) {
        if (DEBUG) Log.i(TAG, "+++ onCanceled() called! +++");

        super.onCanceled(drawing);

        if (drawing != null)
            drawing.getMap().resetPolygon();
    }

    @Override
    public void forceLoad() {
        if (DEBUG) Log.i(TAG, "+++ forceLoad() called! +++");
        super.forceLoad();
    }
}
