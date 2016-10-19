package com.example.campusmap.mapviewer;

import android.content.Context;

import com.example.campusmap.database.SQLiteHelperObstacle;
import com.example.campusmap.form.NMapRectData;
import com.example.campusmap.form.PointD;
import com.example.campusmap.form.Polygon;
import com.nhn.android.maps.overlay.NMapPathData;
import com.nhn.android.maps.overlay.NMapPathLineStyle;

import java.util.ArrayList;
import java.util.List;

public class PolygonDataManager {
    private static final String TAG = "PolygonDataManager";
    private static final double EXTERNAL_GAP = 0.0005;


    private Context mContext;
    private List<Polygon> polygons;
    public PointD min = new PointD();
    public PointD max = new PointD();
    public final double rect_size = 0.0004;

    public PolygonDataManager(Context context) {
        mContext = context;
        SQLiteHelperObstacle helper = SQLiteHelperObstacle.getInstance(context);
        polygons = helper.getObstacleList();

        initMinMax();
    }

    private void initMinMax() {
        boolean first = true;

        // get left top location and right bottom location!
        for (Polygon polygon : polygons) {
            for (PointD point : polygon.getPoints()) {
                if (first) {
                    min.x =  point.x;
                    min.y =  point.y;
                    max.x =  point.x;
                    max.y =  point.y;
                    first = false;
                } else {
                    if (min.x > point.x) {
                        min.x = point.x;
                    }
                    if (min.y > point.y) {
                        min.y = point.y;
                    }
                    if (max.x < point.x) {
                        max.x = point.x;
                    }
                    if (max.y < point.y) {
                        max.y = point.y;
                    }
                }
            }
        }
        min.x -= EXTERNAL_GAP;
        min.y -= EXTERNAL_GAP;
        max.x += EXTERNAL_GAP;
        max.y += EXTERNAL_GAP;
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

    public ArrayList<NMapPathData> getBaseRectangles() {
        ArrayList<NMapPathData> pathDates = new ArrayList<>();

        // Log.i(TAG, "getBaseRectangles: min : " + min + ", max : " + max); // good!
        NMapRectData rectData = new NMapRectData();
        rectData.addPathPoint(min.x, min.y, NMapPathLineStyle.TYPE_SOLID);
        rectData.addPathPoint(min.x, max.y, NMapPathLineStyle.TYPE_SOLID);
        rectData.addPathPoint(max.x, max.y, NMapPathLineStyle.TYPE_SOLID);
        rectData.addPathPoint(max.x, min.y, NMapPathLineStyle.TYPE_SOLID);

        // set path line style
        NMapPathLineStyle pathLineStyle = new NMapPathLineStyle(mContext);
        pathLineStyle.setPataDataType(NMapPathLineStyle.DATA_TYPE_POLYGON);
        pathLineStyle.setLineColor(0xffffff, 0x00);
        pathLineStyle.setFillColor(0xd24da0, 0x88);
        rectData.setPathLineStyle(pathLineStyle);

        pathDates.add(
                rectData
        );
//        Random random = new Random();
//        int count = 0;
//
//        for (double x=min.x; x<=max.x; x+=rect_size) {
//            for (double y=min.y; y<=max.y; y+=rect_size) {
//                NMapRectData cell = new NMapRectData();
//                cell.addPathPoint(x, y, NMapPathLineStyle.TYPE_SOLID);
//                cell.addPathPoint(x, y+rect_size, NMapPathLineStyle.TYPE_SOLID);
//                cell.addPathPoint(x+rect_size, y+rect_size, NMapPathLineStyle.TYPE_SOLID);
//                cell.addPathPoint(x+rect_size, y, NMapPathLineStyle.TYPE_SOLID);
//
//                NMapPathLineStyle cellLineStyle = new NMapPathLineStyle(mContext);
//                cellLineStyle.setPataDataType(NMapPathLineStyle.DATA_TYPE_POLYGON);
//                cellLineStyle.setLineColor(0xffffff, 0x00);
//                cellLineStyle.setFillColor(random.nextInt(0xffffff), 0x20);
//                cell.setPathLineStyle(cellLineStyle);
//
//                pathDates.add(cell);
//
//                count ++;
//            }
//        }
//        Log.i(TAG, "getBaseRectangles: Count : + " + count);

        return pathDates;
    }
}
