package com.example.campusmap.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import com.example.campusmap.R;
import com.example.campusmap.mapviewer.NMapViewerResourceProvider;
import com.example.campusmap.mapviewer.PolygonDataManager;
import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.overlay.NMapCircleData;
import com.nhn.android.maps.overlay.NMapCircleStyle;
import com.nhn.android.maps.overlay.NMapPathData;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPathDataOverlay;
import com.nhn.android.mapviewer.overlay.NMapResourceProvider;

import java.util.List;

public class NMTestActivity extends NMapActivity {
    private static final String CLIENT_ID = "4vY170qiGQL_GaGL1BL4";
    private static final String TAG = "NMTestActivity";

    private final NGeoPoint startPoint = new NGeoPoint(128.09389828957734 , 35.18079876226641 /*126.978371, 37.5666091*/ /*127.108099, 37.366034*/);


    private NMapView mMapView;
    private NMapController mMapController;

    private NMapResourceProvider mMapResourceProvider;
    private NMapOverlayManager mOverlayManager;
    private PolygonDataManager mPolygonDataManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nmtest);

        mMapView = (NMapView) findViewById(R.id.map_view);

        // set a registered Client Id for Open MapViewer Library
        mMapView.setClientId(CLIENT_ID);

        // initialize map view
        mMapView.setClickable(true);
        mMapView.setEnabled(true);
        mMapView.setFocusable(true);
        mMapView.setFocusableInTouchMode(true);
        mMapView.requestFocus();

        // register listener for map state changes
        mMapView.setOnMapStateChangeListener(onMapViewStateChangeListener);
        mMapView.setOnMapViewTouchEventListener(onMapViewTouchEventListener);

        mMapController = mMapView.getMapController();

        // use built in zoom controls
        NMapView.LayoutParams lp = new NMapView.LayoutParams(NMapView.LayoutParams.WRAP_CONTENT,
                NMapView.LayoutParams.WRAP_CONTENT, NMapView.LayoutParams.BOTTOM_RIGHT);
        mMapView.setBuiltInZoomControls(true, lp);

        mMapResourceProvider = new NMapViewerResourceProvider(this);

        mOverlayManager = new NMapOverlayManager(this, mMapView, mMapResourceProvider);

//        new NMapOverlayManager(this, mMapView, mMap)
        mPolygonDataManager = new PolygonDataManager(this);

        displayPolygon();
        displayBaseRectangles();


    }
    private NMapView.OnMapStateChangeListener onMapViewStateChangeListener = new NMapView.OnMapStateChangeListener() {
        @Override
        public void onMapInitHandler(NMapView nMapView, NMapError nMapError) {
            if (nMapError == null) {
                mMapController.setMapCenter(startPoint, 11); // ,
            } else {
                Log.e(TAG, "onMapInitHandler: error=" + nMapError.toString());
            }
        }

        @Override
        public void onMapCenterChange(NMapView nMapView, NGeoPoint nGeoPoint) {

        }

        @Override
        public void onMapCenterChangeFine(NMapView nMapView) {

        }

        @Override
        public void onZoomLevelChange(NMapView nMapView, int i) {

        }

        @Override
        public void onAnimationStateChange(NMapView nMapView, int i, int i1) {

        }
    };

    private NMapView.OnMapViewTouchEventListener onMapViewTouchEventListener = new NMapView.OnMapViewTouchEventListener() {
        @Override
        public void onLongPress(NMapView nMapView, MotionEvent motionEvent) {

        }

        @Override
        public void onLongPressCanceled(NMapView nMapView) {

        }

        @Override
        public void onTouchDown(NMapView nMapView, MotionEvent motionEvent) {

        }

        @Override
        public void onTouchUp(NMapView nMapView, MotionEvent motionEvent) {

        }

        @Override
        public void onScroll(NMapView nMapView, MotionEvent motionEvent, MotionEvent motionEvent1) {

        }

        @Override
        public void onSingleTapUp(NMapView nMapView, MotionEvent motionEvent) {
            displayPoint();
        }
    };

    private void displayPolygon() {
        List<NMapPathData> pathDates = mPolygonDataManager.toNMapPathData();
        mOverlayManager.createPathDataOverlay(pathDates);
    }

    private void displayBaseRectangles() {
        List<NMapPathData> pathDates = mPolygonDataManager.getBaseRectangles();

        mOverlayManager.createPathDataOverlay(pathDates);
    }

    private void displayPoint() {
        displayBaseRectangles();

        NMapCircleData circleData = new NMapCircleData(1);
        circleData.addCirclePoint(mPolygonDataManager.min.x, mPolygonDataManager.min.y, 10f);

        NMapCircleStyle style = new NMapCircleStyle(this);
        style.setStrokeColor(0xffffff, 0x00);
        style.setFillColor(0xA04DD2, 0x88);
        circleData.setCircleStyle(style);

        mOverlayManager.clearOverlays();
        NMapPathDataOverlay overlay = mOverlayManager.createPathDataOverlay();
        overlay.addCircleData(circleData);
        mOverlayManager.insertOverlay(overlay);

    }
}
