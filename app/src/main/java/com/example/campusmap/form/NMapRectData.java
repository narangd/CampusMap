package com.example.campusmap.form;

import com.nhn.android.maps.overlay.NMapPathData;
import com.nhn.android.maps.overlay.NMapPathLineStyle;

public class NMapRectData extends NMapPathData {
    private double width;
    private double height;
    private PointD center = new PointD();

    public NMapRectData() {
        super(4);
    }

    private void reset() {
        initPathData();
        addPathPoint(center.x - width/2, center.y - height/2, NMapPathLineStyle.TYPE_SOLID);
        addPathPoint(center.x + width/2, center.y - height/2, NMapPathLineStyle.TYPE_SOLID);
        addPathPoint(center.x + width/2, center.y + height/2, NMapPathLineStyle.TYPE_SOLID);
        addPathPoint(center.x - width/2, center.y + height/2, NMapPathLineStyle.TYPE_SOLID);
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

    public PointD getCenter() {
        return center;
    }

    public void setCenter(PointD center) {
        this.center = center;
        reset();
    }
}
