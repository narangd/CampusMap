package com.example.campusmap.form;

import com.nhn.android.maps.overlay.NMapPathData;
import com.nhn.android.maps.overlay.NMapPathLineStyle;

public class NMapRectData extends NMapPathData {
    private double width;
    private double height;
    private PointD point = new PointD();

    public NMapRectData() {
        super(4);
    }

    private void reset() {
        initPathData();
        addPathPoint(point.x, point.y, NMapPathLineStyle.TYPE_SOLID);
        addPathPoint(point.x, point.y + height, NMapPathLineStyle.TYPE_SOLID);
        addPathPoint(point.x + width, point.y + height, NMapPathLineStyle.TYPE_SOLID);
        addPathPoint(point.x + width, point.y, NMapPathLineStyle.TYPE_SOLID);
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
        reset();
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
        reset();
    }

    public PointD getPoint() {
        return point;
    }

    public void setPoint(PointD point) {
        this.point = point;
        reset();
    }
}
