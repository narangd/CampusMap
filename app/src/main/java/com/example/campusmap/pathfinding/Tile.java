package com.example.campusmap.pathfinding;

import android.graphics.Color;
import android.graphics.Point;
import android.support.annotation.NonNull;

import com.example.campusmap.form.PointD;

public class Tile implements Comparable<Tile> {
    private static final boolean DEBUG = false;
    static final int SIZE = 10;

    public enum State {
        NONE, WALL, START, GOAL, WAY,
        OPEN, CLOSE
    }

    private Point index;
    private PointD point;
    public State state;
    public int G; // G:시작점에서 새로운 지점까지 이동비용.
    public int H; // 얻어진 사각형으로 부터 최종목적지점 까지 예상 이동 비용.
    public int F; // F:총비용

    public Tile(double x, double y, int xi, int yi) {
        index = new Point(xi, yi);
        point = new PointD(x, y);
        state = State.NONE;
        init();
    }

    public void init() {
        G = 0;
        H = 0;
        F = 0;
        state = (state==State.WALL? State.WALL : State.NONE);
    }

    public int getColor() {
        int color;
        switch (state) {
            case WALL: color = Color.YELLOW; break;
            case START: color = Color.GREEN; break;
            case GOAL: color = Color.RED; break;
            case WAY: color = Color.BLUE; break;
            case OPEN: color = Color.LTGRAY; break;
            case CLOSE: color = Color.DKGRAY; break;
            case NONE: // __
            default:
                color = Color.argb(0, 0,0,0);
                break;
        }
        return color;
    }
    public int getX() {
        return index.x;
    }
    public int getY() {
        return index.y;
    }
    public PointD getPoint() { return point; }
    public Point getIndex() {
        return index;
    }
    public int getFScore() { return F; }

    public int getDistance(Tile neighbor) {
        int xDistance = Math.abs(getX() - neighbor.getX());
        int yDistance = Math.abs(getY() - neighbor.getY());
        if (xDistance==0 || yDistance==0)
            return 10;
        else
            return 14;
    }

//    public void draw(Canvas canvas, Paint paint) {
//        paint.setStyle(Paint.Style.FILL);
//        paint.setColor(getColor());
//        canvas.drawRect(rect, paint);
//
//
//        if (DEBUG) {
//            // parent 방향
//            if (parent != null) {
//                paint.setColor(Color.MAGENTA);
//                int dx = parent.rect.centerX() - rect.centerX();
//                dx /= 2;
//                int dy = parent.rect.centerY() - rect.centerY();
//                dy /= 2;
//                canvas.drawLine(rect.centerX(), rect.centerY(), rect.centerX()+dx, rect.centerY()+dy, paint);
//            }
//
//            paint.setColor(Color.LTGRAY);
//            paint.setStyle(Paint.Style.STROKE);
//            canvas.drawRect(rect, paint);
//
////            paint.setColor(Color.BLACK);
////            paint.setTextSize(10);
////            Point centerPoint = getPoint();
////            canvas.drawText("F"+Integer.toString(F), getX(), centerPoint.y, paint);
////            canvas.drawText("G"+Integer.toString(G), getX(), getY()+Tile.height, paint);
////            canvas.drawText("H"+Integer.toString(H), centerPoint.x, getY()+Tile.height, paint);
//        }
//    }

//    @Override
//    public int compareTo(@NonNull Tile another)
//    { return another.F; } // 문제 없음 >:1, =:0, <:-1...
//    @Override
//    public int compare(Tile one, Tile another) {
//        return one.F-another.F;
//    }

    @Override
    public int compareTo(@NonNull Tile another) {
        return getFScore() - another.getFScore();
//        if (F == another.F)
//            return 0;
//        else if (F > another.F)
//            return 1;
//        else
//            return -1;
    }

    //    @Override
//    public int compare(Tile one, Tile another) {
//        if (one.F > another.F)
//            return 1;
//        else
//            return -1;
//    }


    @Override
    public String toString() {
        return "F:" + Integer.toString(getFScore()) + ",index:" + index + ",point:" + point;
    }

    public State getState() {
        return state;
    }


}
