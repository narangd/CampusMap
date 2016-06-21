package com.example.campusmap;

import android.app.Activity;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.campusmap.database.SQLiteHelperCampusInfo;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class IntroActivity extends Activity {
    public static final String ns = null;
    private static final String TAG = "IntroActivity";
    private static final boolean DEBUG = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        if (DEBUG) Log.i(TAG, "onCreate: =============================================");

        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (DEBUG) Log.i(TAG, "run: start..");
                initDB();
                finish();
            }
        });
    }

    private void clearDB() {
        if (DEBUG) Log.i(TAG, "-=- clearDB: clear database... -=-");
        SQLiteHelperCampusInfo sqliteHelper = SQLiteHelperCampusInfo.getInstance(this);
        if (sqliteHelper.isCreatedBuilding()) sqliteHelper.deleteBuilding();
        if (sqliteHelper.isCreatedFloor()) sqliteHelper.deleteFloor();
        if (sqliteHelper.isCreatedRoom()) sqliteHelper.deleteRoom();
    }

    @Override
    public void onBackPressed() { finish(); }

    private void initDB() {
        clearDB();
        if (DEBUG) Log.i(TAG, "-=- initDB: initialize database... -=-");
        SQLiteHelperCampusInfo sqliteHelper = SQLiteHelperCampusInfo.getInstance(this);
        int currentBuildingID=1, currentFloorID=1, currentRoomID=1;
//        database.insert()
//        values.put(SQLiteHelperCampusInfo.BuildingEntry.);

        XmlResourceParser parser =  this.getResources().getXml(R.xml.building_info);
        try {
            while (parser.next() != XmlResourceParser.END_DOCUMENT) {
                switch (parser.getEventType()) {
                case XmlResourceParser.START_TAG:
                    switch (parser.getName()) {
                        case "building":  // ## <building num="1" name="100주년 기념관"> ##
                            int building_num = Integer.parseInt(parser.getAttributeValue(ns, "num"));
                            String building_name = parser.getAttributeValue(ns, "name");
                            sqliteHelper.insertBuilding(
                                    currentBuildingID++,
                                    building_num,
                                    building_name,
                                    ""
                            );
//                            if (DEBUG) Log.i(TAG, "-=- building: "+currentBuildingID+", "+building_num+", "+building_name+" -=-");
                            break;
                        case "floor":     // ## <floor num="1"> ##
                            int floor_num = Integer.parseInt( parser.getAttributeValue(ns, "num") );
                            sqliteHelper.insertFloor(
                                    currentFloorID++,
                                    floor_num,
                                    currentBuildingID
                            );
//                            if (DEBUG) Log.i(TAG, "-=- floor: "+currentFloorID+", "+floor_num+", "+currentBuildingID+" -=-");
                            break;
                        case "room":      // ## <room name="방재센터"> ##
                            String room_name = parser.getAttributeValue(ns, "name");
                            parser.require(XmlResourceParser.START_TAG, ns, "room");
                            parser.next();
                            String room_description = parser.getText();
                            sqliteHelper.insertRoom(
                                    currentRoomID++,
                                    room_name,
                                    room_description,
                                    currentFloorID
                            );
//                            if (DEBUG) Log.i(TAG, "-=- room: "+currentRoomID+", "+room_name+", "+room_description+", "+currentFloorID+" -=-");
                            break;
                    }
                    break;
                }
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        parser.close();

        if (DEBUG) {
            Log.i(TAG, "database insert building count : " + (currentBuildingID-1));
            Log.i(TAG, "database insert floor count : " + (currentFloorID-1));
            Log.i(TAG, "database insert room count : " + (currentRoomID-1));
        }
    }
}
