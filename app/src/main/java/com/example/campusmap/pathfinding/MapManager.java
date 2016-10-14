package com.example.campusmap.pathfinding;

import android.graphics.Paint;

import com.example.campusmap.form.PointD;

public class MapManager {
    private static final String TAG = "MapManager";
    private static final boolean DEBUG = false;

    private Paint paint;

    private Map map;

    public MapManager(PointD min, PointD max) {

        paint = new Paint();
        paint.setColor(0xaaffffff);

        map = new Map(min, max);
    }

    public void resetPath() {
        map.initRandomToStartGoalTile();
        map.initTiles();
        map.pathFinding();
    }

    public void redraw() {
    }
}
