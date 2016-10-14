package com.example.campusmap.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.example.campusmap.R;
import com.example.campusmap.form.PointD;
import com.example.campusmap.mapviewer.NMapPOIflagType;
import com.example.campusmap.mapviewer.NMapViewerResourceProvider;
import com.example.campusmap.mapviewer.PolygonDataManager;
import com.example.campusmap.pathfinding.Map;
import com.example.campusmap.pathfinding.Tile;
import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.overlay.NMapCircleData;
import com.nhn.android.maps.overlay.NMapCircleStyle;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.maps.overlay.NMapPathData;
import com.nhn.android.maps.overlay.NMapPathLineStyle;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPathDataOverlay;
import com.nhn.android.mapviewer.overlay.NMapResourceProvider;

import java.util.List;
import java.util.Random;

public class NMTestActivity extends NMapActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static final String CLIENT_ID = "4vY170qiGQL_GaGL1BL4";
    private static final String TAG = "NMTestActivity";
    private static final int REQUEST_CODE_GPS = 200;

    private final NGeoPoint startPoint = new NGeoPoint(128.09389828957734, 35.18079876226641 /*126.978371, 37.5666091*/ /*127.108099, 37.366034*/);


    private NMapView mMapView;
    private NMapController mMapController;

    private NMapResourceProvider mMapResourceProvider;
    private NMapOverlayManager mOverlayManager;
    private PolygonDataManager mPolygonDataManager;
    private Map map;

    private LocationManager locationManager;
    private NMapPOIitem currentPOI;
    private PointD currentLocation = new PointD();
    private Toast mToast;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nmtest);

        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

        mMapView = (NMapView) findViewById(R.id.map_view);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

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

//        MapManager mMapManager = new MapManager(mPolygonDataManager.min, mPolygonDataManager.max);
//        mMapManager.
        map = new Map(mPolygonDataManager.min, mPolygonDataManager.max);


        // Here, thisActivity is the current activity
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_GPS);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_LOW);
            criteria.setPowerRequirement(Criteria.POWER_LOW);
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);

            locationManager.getBestProvider(criteria, true);
            Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location location = locationNet;
            if (location == null) {
                location = locationGPS;
            }
            if (location != null) {
                currentLocation.x = location.getLongitude();
                currentLocation.y = location.getLatitude();

                displayLocation(location.getLongitude(), location.getLatitude());
            } else {
                displayLocation(startPoint.longitude, startPoint.latitude);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_GPS && grantResults.length >= 2) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                boolean gps_enabled;
                boolean network_enabled;

                gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                Location net_loc = null, gps_loc = null, finalLoc;

                if (gps_enabled)
                    gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (network_enabled)
                    net_loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if (gps_loc != null && net_loc != null) {

                    if (gps_loc.getAccuracy() >= net_loc.getAccuracy())
                        finalLoc = gps_loc;
                    else
                        finalLoc = net_loc;

                    currentLocation.x = finalLoc.getLongitude();
                    currentLocation.y = finalLoc.getLatitude();
                    displayLocation(finalLoc.getLongitude(), finalLoc.getLatitude());
                }
            }
        }
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

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            currentLocation.x = location.getLongitude();
            currentLocation.y = location.getLatitude();
//            currentPOI.setPoint(new NGeoPoint(location.getLongitude(), location.getLatitude()));

            mOverlayManager.clearOverlays();
            displayBaseRectangles();
            displayLocation(location.getLongitude(), location.getLatitude());
            Log.i(TAG, "onLocationChanged()");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i(TAG, "onStatusChanged()");
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.i(TAG, "onProviderEnabled()");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.i(TAG, "onProviderDisabled()");
        }
    };

    private void displayLocation(double longitude, double latitude) {
        // set POI data
        NMapPOIdata poiData = new NMapPOIdata(1, mMapResourceProvider);

        poiData.beginPOIdata(1);
        currentPOI = poiData.addPOIitem(new NGeoPoint(longitude, latitude), "내 위치!", NMapPOIflagType.PIN, 0);
        poiData.endPOIdata();

        mOverlayManager.createPOIdataOverlay(poiData, null);

        Tile tile = map.getTile(longitude, latitude);
        if (tile == null) {
            mToast.setText("경남과학기술대학교 주위를 벗어난듯 합니다.");
            mToast.show();
            return;
        }
        PointD pointD = tile.getPoint();

        mToast.setText("타일 인덱스 : " + tile.getX() + "," + tile.getY());
        mToast.show();

        NMapPathData pathData = new NMapPathData(4);
        pathData.addPathPoint(pointD.x, pointD.y, NMapPathLineStyle.TYPE_SOLID);
        pathData.addPathPoint(pointD.x, pointD.y+map.rect_size, NMapPathLineStyle.TYPE_SOLID);
        pathData.addPathPoint(pointD.x+map.rect_size, pointD.y+map.rect_size, NMapPathLineStyle.TYPE_SOLID);
        pathData.addPathPoint(pointD.x+map.rect_size, pointD.y, NMapPathLineStyle.TYPE_SOLID);

        NMapPathLineStyle style = new NMapPathLineStyle(this);
        style.setLineColor(0xffffff, 0x00);
        style.setFillColor(0x00ff00, 0xaa);
        style.setPataDataType(NMapPathLineStyle.DATA_TYPE_POLYGON);
        pathData.setPathLineStyle(style);
        mOverlayManager.createPathDataOverlay(pathData);
    }

    private void displayPolygon() {
        List<NMapPathData> pathDates = mPolygonDataManager.toNMapPathData();
        mOverlayManager.createPathDataOverlay(pathDates);
    }

    List<NMapPathData> pathDates = null;

    private void displayBaseRectangles() {
        if (pathDates == null) {
            pathDates = mPolygonDataManager.getBaseRectangles();
        }
        mOverlayManager.createPathDataOverlay(pathDates);
    }

    private void displayPoint() {
        mOverlayManager.clearOverlays();
        displayBaseRectangles();
        displayLocation(currentLocation.x, currentLocation.y);
        Random random = new Random();

        Tile tile = map.getTile(random.nextInt(map.getXTileCount()), random.nextInt(map.getYTileCount()));
        NMapCircleData circleData = new NMapCircleData(1);
        circleData.addCirclePoint(tile.getPoint().x, tile.getPoint().y, 10f);

        NMapCircleStyle style = new NMapCircleStyle(this);
        style.setStrokeColor(0xffffff, 0x00);
        style.setFillColor(0xA04DD2, 0x88);
        circleData.setCircleStyle(style);

        NMapPathDataOverlay overlay = mOverlayManager.createPathDataOverlay();
        overlay.addCircleData(circleData);
        mOverlayManager.insertOverlay(overlay);

    }
}
