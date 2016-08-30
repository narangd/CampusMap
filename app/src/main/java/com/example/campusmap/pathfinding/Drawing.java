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

public class Drawing implements View.OnLongClickListener{
    private static final String TAG = "ADP_Drawing";
    private static final boolean DEBUG = false;

    private Context mContext;
    private Paint paint;
    private ImageView imageView;

    private Map map;
    private BitmapDrawable back;

    private Progress progress;

    public Drawing(Context context, ImageView imageView) {
        mContext = context;

        this.imageView = imageView;
        imageView.setLongClickable(true);
        imageView.setOnLongClickListener(this);

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

            imageView.setImageBitmap(copyBitmap);
            imageView.invalidate();
        }
    }

    public void setOnProgressUpdate(Progress progress) {
        this.progress = progress;
    }

    @Override
    public boolean onLongClick(View v) {
        Toast.makeText(mContext, v.getX() + "," + v.getY(), Toast.LENGTH_LONG)
                .show();
        return true;
    }

    public Map getMap() {
        return this.map;
    }

    public int getWallCount() {
        int count = 0;
        Tile[][] tiles = map.getTiles();

        for(int h = 0; h<map.yTileSIZE; h++)
        {
            for(int w = 0; w<map.xTileSIZE; w++)
            {
                if (tiles[h][w].getState() == Tile.State.WALL) {
                    count++;
                }
            }
        }

        return count;
    }

    public interface Progress {
        void onProgressUpdate();

    }
}
