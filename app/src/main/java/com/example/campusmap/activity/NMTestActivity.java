package com.example.campusmap.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.example.campusmap.R;
import com.example.campusmap.data.branch.Building;
import com.example.campusmap.database.SQLiteHelperCampusInfo;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NMTestActivity extends NMapActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static final String CLIENT_ID = "4vY170qiGQL_GaGL1BL4";
    private static final String TAG = "NMTestActivity";
    private static final int REQUEST_CODE_GPS = 200;
    private static final boolean DEBUG = false;

    private final NGeoPoint startPoint = new NGeoPoint(128.09389828957734, 35.18079876226641 /*126.978371, 37.5666091*/ /*127.108099, 37.366034*/);


    private NMapView mMapView;
    private NMapController mMapController;

    private NMapResourceProvider mMapResourceProvider;
    private NMapOverlayManager mOverlayManager;
    private PolygonDataManager mPolygonDataManager;
    private LocationManager locationManager;
    private Map map;

    private NMapPOIitem currentPOI;
    private PointD currentLocation = new PointD();
    private Toast mToast;
    private ArrayAdapter<Building> mBuildingAdapter;
    private int destination_building_number;
    private AlertDialog pathfinding_dialog;
    private AlertDialog select_dialog;
    private ArrayList<NMapPathData> basePolygonDatas;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nmtest);

        createAlertDialog();
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

//        MapManager mMapManager = new MapManager(mPolygonDataManager.min, mPolygonDataManager.max);
//        mMapManager.
        if (mPolygonDataManager.min == null || mPolygonDataManager.max == null) {
            mPolygonDataManager.min = new PointD(startPoint.longitude, startPoint.latitude);
            mPolygonDataManager.max = new PointD(startPoint.longitude, startPoint.latitude);
        }
        map = new Map(mPolygonDataManager.min, mPolygonDataManager.max);
        map.register(mPolygonDataManager.getPolygons());

        // Here, thisActivity is the current activity
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_GPS);
        } else {
            setBestLocation();
        }

        displayPolygon();
        displayBaseRectangles();
    }

    @SuppressWarnings("MissingPermission")
    private void setBestLocation() {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_LOW);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);

        String best_provider = locationManager.getBestProvider(criteria, true);
        Log.i(TAG, "onCreate: Best Provider : " + best_provider);

        Location location = locationManager.getLastKnownLocation(best_provider);
        if (location != null) {
            currentLocation.x = location.getLongitude();
            currentLocation.y = location.getLatitude();

            displayLocation(location.getLongitude(), location.getLatitude());
        } else {
            currentLocation.x = startPoint.longitude;
            currentLocation.y = startPoint.latitude;

            displayLocation(startPoint.longitude, startPoint.latitude);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(locationListener);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_GPS && grantResults.length >= 2) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                setBestLocation();
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
//            displayPoint();
            pathfinding_dialog.show();
        }
    };

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            currentLocation.x = location.getLongitude();
            currentLocation.y = location.getLatitude();
            map.start = map.getTile(location.getLongitude(), location.getLatitude());

            mOverlayManager.clearOverlays();
            displayBaseRectangles();
            displayPolygon();
            displayPath(map.pathFinding());
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

    private void createAlertDialog() {
        View dialog_layout = getLayoutInflater().inflate(R.layout.dialog_select_destination, null);
        final Button button = (Button) dialog_layout.findViewById(R.id.destination);
        // Button Click Listener
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select_dialog.show();
            }
        });

        pathfinding_dialog = new AlertDialog.Builder(NMTestActivity.this)
                .setView(dialog_layout)
                .setTitle("도착지를 선택해주세요")
                .setNegativeButton("취소", null)
                .setPositiveButton("경로찾기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Random random = new Random();

                        Tile start = map.getTile(currentLocation.x, currentLocation.y);
                        Log.e(TAG, "onClick: " + currentLocation);
                        if (start == null) {
                            return;
                        }
                        Log.e(TAG, "onClick: " + start.getPoint());

                        map.initRandomToStartGoalTile();
                        map.start = start;

                        NMapPathLineStyle style = new NMapPathLineStyle(NMTestActivity.this);
                        style.setLineColor(0xffffff, 0x00);
                        style.setFillColor(0xff0000, 0xaa);
                        style.setPataDataType(NMapPathLineStyle.DATA_TYPE_POLYGON);
                        NMapPathData pathData = mPolygonDataManager.getMapTile(map.goal, style);

                        mOverlayManager.clearOverlays();
                        mOverlayManager.createPathDataOverlay(pathData);
                        displayBaseRectangles();
                        displayPolygon();
                        displayPath(map.pathFinding());
                        displayLocation(currentLocation.x, currentLocation.y);
                    }
                })
                .create();

        SQLiteHelperCampusInfo helper = SQLiteHelperCampusInfo.getInstance(this);
        mBuildingAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                helper.getBuildingList()
        );
        
        select_dialog = new AlertDialog.Builder(NMTestActivity.this)
                .setTitle("리스트에서 도착지를 선택해주세요")
                .setAdapter(mBuildingAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Building building = mBuildingAdapter.getItem(which);
                        if (building != null) {
                            button.setText(building.getName());
                            destination_building_number = building.getNumber();
                        }
                        dialog.cancel();
                    }
                })
                .create();
    }

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
            if (longitude < mPolygonDataManager.min.x) {
                currentLocation.x = mPolygonDataManager.min.x;
            }
            if (longitude > mPolygonDataManager.max.x) {
                currentLocation.x = mPolygonDataManager.max.x;
            }
            if (latitude < mPolygonDataManager.min.y) {
                currentLocation.y = mPolygonDataManager.min.y;
            }
            if (latitude > mPolygonDataManager.max.y) {
                currentLocation.y = mPolygonDataManager.max.y;
            }
            tile = map.getTile(currentLocation.x, currentLocation.y);
            if (tile == null) {
                return;
            }
        }
        map.start = tile;

        if (DEBUG) {
            mToast.setText("타일 인덱스 : " + tile.getX() + "," + tile.getY());
            mToast.show();
        }

        NMapPathLineStyle style = new NMapPathLineStyle(NMTestActivity.this);
        style.setLineColor(0xffffff, 0x00);
        style.setFillColor(0x00ff00, 0xaa);
        style.setPataDataType(NMapPathLineStyle.DATA_TYPE_POLYGON);
        NMapPathData pathData = mPolygonDataManager.getMapTile(tile, style);
        mOverlayManager.createPathDataOverlay(pathData);
    }

    private void displayPath(List<PointD> path) {
        NMapPathData pathData = new NMapPathData(path.size());
        for (PointD pointD : path) {
            pathData.addPathPoint(pointD.x, pointD.y, NMapPathLineStyle.TYPE_SOLID);
        }
        pathData.endPathData();
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

        if (basePolygonDatas == null) {
            basePolygonDatas = new ArrayList<>();

            NMapPathLineStyle style = new NMapPathLineStyle(this);
            style.setLineColor(0xffffff, 0x00);
            style.setFillColor(Color.YELLOW, 0xaa);
            style.setPataDataType(NMapPathLineStyle.DATA_TYPE_POLYGON);

            for (int h=0; h<map.getYTileCount(); h++) {
                for (int w=0; w<map.getXTileCount(); w++) {
                    Tile tile = map.getTile(w, h);
                    if (tile != null && tile.state == Tile.State.WALL) {
                        NMapPathData pathData = mPolygonDataManager.getMapTile(tile, style);
                        basePolygonDatas.add(pathData);
                    }
                }
            }
        }
//        if (DEBUG) {
            mOverlayManager.createPathDataOverlay(basePolygonDatas);
//        }
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
