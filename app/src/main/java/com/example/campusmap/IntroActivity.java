package com.example.campusmap;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.example.campusmap.database.SQLiteHelperCampusInfo;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class IntroActivity extends Activity {
    private static final String TAG = "IntroActivity";
    private static final boolean DEBUG = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (DEBUG) Log.i(TAG, "onCreate: =============================================");

        // ## Laytout ##
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // ## Database ##
        final SQLiteHelperCampusInfo sqLiteHelper = SQLiteHelperCampusInfo.getInstance(IntroActivity.this);
        final SQLiteDatabase db = sqLiteHelper.getReadableDatabase();

        final String BUILDING = SQLiteHelperCampusInfo.BuildingEntry.TABLE_NAME;
        final String FLOOR = SQLiteHelperCampusInfo.FloorEntry.TABLE_NAME;
        final String ROOM = SQLiteHelperCampusInfo.RoomEntry.TABLE_NAME;
        if (sqLiteHelper.getTableSize(db, BUILDING) == getTagSize(BUILDING) &&
                sqLiteHelper.getTableSize(db, FLOOR) == getTagSize(FLOOR) &&
                sqLiteHelper.getTableSize(db, ROOM) == getTagSize(ROOM)) {
            Log.i(TAG, "onCreate: data is already inserted...");
            db.close();

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    finish();
                }
            });
        } else {
            sqLiteHelper.deleteTable(db, BUILDING);
            sqLiteHelper.deleteTable(db, FLOOR);
            sqLiteHelper.deleteTable(db, ROOM);

            sqLiteHelper.startInsertData();

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (!sqLiteHelper.isInsertFinished()) {
                            Thread.sleep(500);
                        }
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, "run: done wait..");

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    db.close();
                    finish();

//                handler.sendEmptyMessage(0);
                }
            });

        }
    }

    public int getTagSize(final String tagName) {
        int count=0;
        XmlPullParser parser = getApplicationContext().getResources().getXml(R.xml.building_info);
        try {
            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }

                if (parser.getName().equals(tagName)) {
                    count++;
                }
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
        return count;
    }

    @Override
    public void onBackPressed() { /* do noting.. */ }

}
