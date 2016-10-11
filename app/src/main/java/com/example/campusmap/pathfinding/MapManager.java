package com.example.campusmap.pathfinding;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.ImageView;

public class MapManager {
    private static final String TAG = "MapManager";
    private static final boolean DEBUG = false;

    private Context mContext;
    private Paint paint;

    private Map map;
    private BitmapDrawable back;

    public MapManager(Context context, ImageView imageView) {
        mContext = context;

        imageView.setLongClickable(true);

        paint = new Paint();
        paint.setColor(0xaaffffff);
//        paint.setAntiAlias(true);

        back = (BitmapDrawable)imageView.getDrawable();
        if (back != null) {
            Bitmap bitmap = back.getBitmap();
            Rect canvasRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            map = new Map(canvasRect.width(), canvasRect.height());
        }
    }

    public void resetPath() {
        map.initRandomToStartGoalTile();
        map.initTiles();
        map.pathFinding();
    }

    public void drawOnImageView(ImageView imageView) {
        if (DEBUG) Log.i(TAG, "+++ drawOnImageView() called! +++");

        if (back != null) {
            Bitmap copyBitmap = back.getBitmap().copy(Bitmap.Config.ARGB_8888, true);
            Canvas copyCanvas = new Canvas(copyBitmap);

            if (DEBUG) Log.i(TAG, "+++ reDraw Tile count : "+getWallCount()+" +++");
            map.drawTiles(copyCanvas, paint); // draw map to copied Canvas.
            map.drawPath(copyCanvas, paint);

            imageView.setImageBitmap(copyBitmap);
            imageView.invalidate();
        }
    }

    public Map getMap() {
        return map;
    }

    public int getWallCount() {
        int count = 0;
        Tile[][] tiles = map.getTiles();

        for(int h = 0; h<map.yTileSIZE; h++) {
            for(int w = 0; w<map.xTileSIZE; w++) {
                if (tiles[h][w].getState() == Tile.State.WALL) {
                    count++;
                }
            }
        }

        return count;
    }
}
