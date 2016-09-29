package com.example.campusmap.mapviewer;

import android.content.Context;

import com.example.campusmap.database.SQLiteHelperObstacle;
import com.example.campusmap.form.PointD;
import com.example.campusmap.form.Polygon;
import com.nhn.android.maps.overlay.NMapPathData;
import com.nhn.android.maps.overlay.NMapPathLineStyle;

import java.util.ArrayList;
import java.util.List;

public class PolygonDataManager {
    private Context mContext;
    private List<Polygon> polygons;

    public PolygonDataManager(Context context) {
        mContext = context;
        SQLiteHelperObstacle helper = SQLiteHelperObstacle.getInstance(context);
        polygons = helper.getObstacleList();
    }

    public List<NMapPathData> toNMapPathData() {
        ArrayList<NMapPathData> pathDates = new ArrayList<>();
        for (Polygon polygon : polygons) {
            List<PointD> pointDs = polygon.getPoints();

            NMapPathData pathData = new NMapPathData(pointDs.size());
            pathData.initPathData();
            for (PointD pointD : pointDs) {
                pathData.addPathPoint(pointD.x, pointD.y, NMapPathLineStyle.TYPE_SOLID);
            }
            pathData.endPathData();

            // set path line style
            NMapPathLineStyle pathLineStyle = new NMapPathLineStyle(mContext);
            pathLineStyle.setPataDataType(NMapPathLineStyle.DATA_TYPE_POLYGON);
            pathLineStyle.setLineColor(0xffffff, 0x00);
            pathLineStyle.setFillColor(0xA04DD2, 0x88);
            pathData.setPathLineStyle(pathLineStyle);

            pathDates.add(pathData);
        }
        return pathDates;
    }
}
