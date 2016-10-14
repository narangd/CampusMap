package com.example.campusmap.asynctask.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.widget.ImageView;

import com.example.campusmap.pathfinding.MapManager;

public class DrawingLoader extends AsyncTaskLoader<MapManager> {
    private static final String TAG = "DrawingLoader";
    private static final boolean DEBUG = false;

    private Context context;
    private ImageView imageView;
    private MapManager mapManager;

    public DrawingLoader(Context context, ImageView imageView) {
        super(context);
        this.context = context;
        this.imageView = imageView;
    }

    @Override
    public MapManager loadInBackground() {
        if (DEBUG) Log.i(TAG, "+++ loadInBackground() called! +++");

        if (DEBUG) Log.i(TAG, "+++ create new Map +++");
//        MapManager mapManager = new MapManager(context, imageView);
//        Map map = mapManager.getMap();
//
//        ArrayList<Polygon> polygons = new ArrayList<>();
//        Cursor cursor = SQLiteHelperObstacle.getInstance(getContext()).select();
//
//        while (cursor.moveToNext()) {
//            Polygon polygon = new Polygon(map, cursor.getString(cursor.getColumnIndex("polygon")));
//            polygons.add(polygon);
//        }
//
//        map.resetPolygon();
//        map.register(polygons);
//
//        Log.i("PolygonLoader", "cursor size : " + cursor.getCount());
//        Log.i("PolygonLoader", "polygons size : " + polygons.size());

        return mapManager;
    }

    @Override
    public void deliverResult(MapManager mapManager) {
        if (isReset()) {
            if (DEBUG) Log.w(TAG, "+++ Warning! An async query came in while the Loader was reset! +++");

            if (mapManager != null) {
                if (DEBUG) Log.w(TAG, "+++ polygons.clear() called! +++");
//                mapManager.getMap().resetPolygon();
                return;
            }
        }

        MapManager oldMapManager = this.mapManager;
        this.mapManager = mapManager;

        if (isStarted()) {
            if (DEBUG) Log.i(TAG, "+++ Delivering results to the LoaderManager for" +
                    " the Fragment to display! +++");

            super.deliverResult(mapManager);
        }

        if (oldMapManager != null && oldMapManager != mapManager) {
            if (DEBUG) Log.i(TAG, "+++ Releasing any old data associated with this Loader. +++");
            if (DEBUG) Log.w(TAG, "+++ oldMap.resetPolygon() called! +++");
//            oldMapManager.getMap().resetPolygon();
        }

        super.deliverResult(mapManager);
    }

    @Override
    protected void onStartLoading() {
        if (DEBUG) Log.i(TAG, "+++ onStartLoading() called! +++");

        if (mapManager != null) {
            if (DEBUG) Log.i(TAG, "+++ Delivering previously loaded data to the client...");
            deliverResult(mapManager);
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

        if (mapManager != null) {
            if (DEBUG) Log.w(TAG, "+++ mMap.resetPolygon() called! +++");
//            mapManager.getMap().resetPolygon(); // release map
            mapManager = null;
        }
    }

    @Override
    public void onCanceled(MapManager mapManager) {
        if (DEBUG) Log.i(TAG, "+++ onCanceled() called! +++");

        super.onCanceled(mapManager);

        if (mapManager != null) {
//            mapManager.getMap().resetPolygon();
        }
    }

    @Override
    public void forceLoad() {
        if (DEBUG) Log.i(TAG, "+++ forceLoad() called! +++");
        super.forceLoad();
    }
}
