package com.example.campusmap.pathfinding;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.campusmap.pathfinding.graphic.Map;
import com.example.campusmap.pathfinding.graphic.Tile;

/**
 * Created by DB-31 on 2015-11-03.
 */
public class Drawing implements View.OnLongClickListener{
    private static final String TAG = "ADP_Drawing";
    private static final boolean DEBUG = true;

    Context context;
    ImageView imageView;
    private BitmapDrawable back;
    private Paint paint;
    private Canvas canvas;
    private Bitmap bitmap;

    private Map map;

    public Drawing(Context context, ImageView imageView) {
        this.context = context;

        this.imageView = imageView;
        imageView.setLongClickable(true);
        imageView.setOnLongClickListener(this);

        paint = new Paint();
        paint.setColor(0xaaffffff);
//        paint.setAntiAlias(true);

        back = (BitmapDrawable)imageView.getDrawable();
        if (back != null) {
            bitmap = back.getBitmap().copy(Bitmap.Config.ARGB_8888, true);
            canvas = new Canvas(bitmap);
            Rect canvasRect = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
            map = new Map(canvasRect.width(), canvasRect.height());
            Log.i("Drawing", "create canvas Size  w:" + canvasRect.width() + " h:" + canvasRect.height());
        }
    }

    public void drawPath() {
        //Size size = new Size(canvas.getWidth(), canvas.getHeight());
        if (bitmap != null) bitmap.recycle();
        bitmap = back.getBitmap().copy(Bitmap.Config.ARGB_8888, true);
        canvas = new Canvas(bitmap);

        map.initTile();
        map.pathFinding();
        reDraw();
    }

    public void reDraw() {
        if (DEBUG) Log.i(TAG, "+++ reDraw() called! +++");
        map.drawTiles(canvas, paint);

        if (DEBUG) Log.i(TAG, "+++ reDraw Tile count : "+getWallCount()+" +++");

        imageView.setImageBitmap(bitmap);
        imageView.invalidate();
    }

    @Override
    public boolean onLongClick(View v) {
        Toast.makeText(context, v.getX() + "," + v.getY(), Toast.LENGTH_LONG)
                .show();
        return true;
    }

    public Map getMap() {
        return this.map;
    }

    public int getWallCount() {
        int count = 0;
        Tile[][] tiles = map.getTiles();

        for(int h=0; h<map.ySIZE; h++)
        {
            for(int w=0; w<map.xSIZE; w++)
            {
                if (tiles[h][w].getState() == Tile.State.WALL) {
                    count++;
                }
            }
        }

        return count;
    }
}
