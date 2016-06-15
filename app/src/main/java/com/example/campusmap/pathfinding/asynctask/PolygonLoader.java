package com.example.campusmap.pathfinding.asynctask;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.campusmap.pathfinding.Drawing;
import com.example.campusmap.pathfinding.db.MySQLHelper;
import com.example.campusmap.pathfinding.graphic.Map;
import com.example.campusmap.pathfinding.graphic.Polygon;

import java.util.ArrayList;

/**
 * Created by 연구생 on 2015-11-16.
 */
public class PolygonLoader extends AsyncTaskLoader<ArrayList<Polygon>> {
    Map map;

    public PolygonLoader(Context context, Map map) {
        super(context);
        this.map = map;
    }

    @Override
    public ArrayList<Polygon> loadInBackground() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ArrayList<Polygon> polygons = new ArrayList<>();
        Cursor cursor = MySQLHelper.getInstance(getContext()).select();
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

}
