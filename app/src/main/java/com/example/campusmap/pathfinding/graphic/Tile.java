package com.example.campusmap.pathfinding.graphic;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * Created by DB-31 on 2015-11-03.
 */
public class Tile implements Comparable<Tile>{
    public enum State {
        NONE, WALL, START, GOAL, WAY
    }
    private static final int SIZE = 10;
    private static final boolean debug = false;

    public static int width = SIZE;
    public static int height = SIZE;
    Rect rect;
    public State state;
    public int G, H, F;
    public Tile parent;

    public Tile(int x, int y) {
        rect = new Rect(x, y, x+width, y+height);
        state = State.NONE;
        init();
    }

    public void init() {
        G = H = F = 0;
        parent = null;
    }

    public int getColor() {
        int color = 0;
        switch (state) {
            case NONE: color = Color.argb(0, 0,0,0); break;
            case WALL: color = Color.YELLOW; break;
            case START: color = Color.GREEN; break;
            case GOAL: color = Color.RED; break;
            case WAY: color = Color.BLUE; break;
        }
        return color;
    }
    public int getX() {
        return rect.left;
    }
    public int getY() {
        return rect.top;
    }
    public Rect getRect() {
        return rect;
    }
    public Point getPoint() {
        return new Point(rect.centerX(), rect.centerY());
    }

    public int getDistance(Tile neighbor) {
        int diectX = (int)Math.abs( this.getX() - neighbor.getX() );
        int diectY = (int)Math.abs( this.getY() - neighbor.getY() );

        if (diectX == 0)
            return width;
        else if(diectY == 0)
            return height;
        return (int)((width+height)/2*1.4);
    }

    public void draw(Canvas canvas, Paint paint) {
        // parent 방향
        if (parent != null) {
            paint.setColor(Color.MAGENTA);
            int dx = parent.rect.centerX() - rect.centerX();
            dx /= 2;
            int dy = parent.rect.centerY() - rect.centerY();
            dy /= 2;
            canvas.drawLine(rect.centerX(), rect.centerY(), rect.centerX()+dx, rect.centerY()+dy, paint);
        }
        paint.setColor(getColor());
        canvas.drawRect(rect, paint);
    }

    @Override
    public int compareTo(Tile another)
    { return F - another.F; }

    public State getState() {
        return state;
    }

}
