package com.example.campusmap.database;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.util.Log;
import android.widget.Toast;

import com.example.campusmap.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by DBLAB on 2016-06-20.
 */
public class SQLiteHelperCampusInfo extends SQLiteOpenHelper {
    private static final String TAG = "SQLiteHelperCampusInfo";
    private static final boolean DEBUG = true;
    public static final String ns = null;
    private static final int DATABACE_VERSION = 14;
    private static final String DATABACE_NAME = "CampusInfo.db";
    private static SQLiteHelperCampusInfo instance;

    public static final String CREATE_TABLE_HEAD = "CREATE TABLE ";
    public static final String DELETE_TABLE_HEAD = "DROP TABLE ";
    public static final String IF_EXISTS = "IF EXISTS ";
    public static final String TYPE_INTEGER = " INTEGER";
    public static final String TYPE_CHARACTER = " CHAR(20)";
    public static final String TYPE_TEXT = " TEXT";
    public static final String PRIMARY_KEY = " PRIMARY KEY";
    public static final String FOREIGN_KEY = " FOREIGN KEY";
    public static final String REFERENCES = " REFERENCES ";
    public static final String COMMA_SEP = ",";
    public static final String COLUMN_START = " (";
    public static final String COLUMN_END = ")";

    public static abstract class BuildingEntry implements BaseColumns {
        public static final String TABLE_NAME = "building";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_NUMBER = "bnumber";
        public static final String COLUMN_NAME_DESCRIPTION = "bdesc";
        public static final String SQL_CREATE_TABLE =
                CREATE_TABLE_HEAD + TABLE_NAME + COLUMN_START +
                        _ID + TYPE_INTEGER + PRIMARY_KEY + COMMA_SEP +
                        COLUMN_NAME_NUMBER + TYPE_INTEGER + COMMA_SEP +
                        COLUMN_NAME_NAME + TYPE_CHARACTER + COMMA_SEP +
                        COLUMN_NAME_DESCRIPTION + TYPE_TEXT + COLUMN_END;
        public static final String SQL_DELETE_TABLE =
                DELETE_TABLE_HEAD + IF_EXISTS + TABLE_NAME;
    }

    public static abstract class FloorEntry implements BaseColumns {
        public static final String TABLE_NAME = "floor";
        public static final String COLUMN_NAME_NUMBER = "fnumber";
        public static final String COLUMN_NAME_BUILDING_ID = "bID";
        public static final String SQL_CREATE_TABLE =
                CREATE_TABLE_HEAD + TABLE_NAME + COLUMN_START +
                        _ID + TYPE_INTEGER + PRIMARY_KEY + COMMA_SEP +
                        COLUMN_NAME_BUILDING_ID + TYPE_INTEGER + COMMA_SEP +
                        COLUMN_NAME_NUMBER + TYPE_CHARACTER + COMMA_SEP +
                        FOREIGN_KEY + COLUMN_START + COLUMN_NAME_BUILDING_ID + COLUMN_END +
                            REFERENCES + BuildingEntry.TABLE_NAME + COLUMN_START + BuildingEntry._ID + COLUMN_END +
                        COLUMN_END;
        public static final String SQL_DELETE_TABLE =
                DELETE_TABLE_HEAD + IF_EXISTS + TABLE_NAME;
    }

    public static abstract class RoomEntry implements BaseColumns {
        public static final String TABLE_NAME = "room";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_DESCRIPTION = "rdesc";
        public static final String COLUMN_NAME_FLOOR_ID = "fID";
        public static final String SQL_CREATE_TABLE =
                CREATE_TABLE_HEAD + TABLE_NAME + COLUMN_START +
                        _ID + TYPE_INTEGER + PRIMARY_KEY + COMMA_SEP +
                        COLUMN_NAME_FLOOR_ID + TYPE_INTEGER + COMMA_SEP +
                        COLUMN_NAME_NAME + TYPE_CHARACTER + COMMA_SEP +
                        COLUMN_NAME_DESCRIPTION + TYPE_TEXT + COMMA_SEP +
                        FOREIGN_KEY + COLUMN_START + COLUMN_NAME_FLOOR_ID  +COLUMN_END +
                            REFERENCES + FloorEntry.TABLE_NAME + COLUMN_START + FloorEntry._ID + COLUMN_END +
                        COLUMN_END;
        public static final String SQL_DELETE_TABLE =
                DELETE_TABLE_HEAD + IF_EXISTS + TABLE_NAME;
    }

    public static SQLiteHelperCampusInfo getInstance(Context context) {
        if (instance == null) {
            instance = new SQLiteHelperCampusInfo(context);
        }
        return instance;
    }

    private Context mContext;
    private CampusInfoInsertAsyncTask mAsyncTask;

    private SQLiteHelperCampusInfo(Context context) {
        super(context, DATABACE_NAME, null, DATABACE_VERSION);
        mContext = context;
//        mHandler = handler;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (DEBUG) Log.i(TAG, "-=- onCreate: Database create version("+DATABACE_VERSION+")-=-");

        // Building
        if (DEBUG) {
            Log.i(TAG, "-=- BuildingEntry: execSQL(SQL_CREATE_TABLE) -=-");
            Log.i(TAG, "-=- BuildingEntry: {"+BuildingEntry.SQL_CREATE_TABLE+"} -=-");
        }
        db.execSQL(BuildingEntry.SQL_CREATE_TABLE);

        // Floor
        if (DEBUG) {
            Log.i(TAG, "-=- FloorEntry: execSQL(SQL_CREATE_TABLE) -=-");
            Log.i(TAG, "-=- FloorEntry: {"+FloorEntry.SQL_CREATE_TABLE+"} -=-");
        }
        db.execSQL(FloorEntry.SQL_CREATE_TABLE);

        // Room
        if (DEBUG) {
            Log.i(TAG, "-=- RoomEntry: execSQL(SQL_CREATE_TABLE) -=-");
            Log.i(TAG, "-=- RoomEntry: {"+RoomEntry.SQL_CREATE_TABLE+"} -=-");
        }
        db.execSQL(RoomEntry.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (DEBUG) Log.i(TAG, "-=- onUpgrade: Database delete -=-");

        // Building
        if (DEBUG) Log.i(TAG, "-=- BuildingEntry: execSQL(SQL_DELETE_TABLE) -=-");
        if (DEBUG) Log.i(TAG, "-=- BuildingEntry: {"+BuildingEntry.SQL_DELETE_TABLE+"} -=-");
        db.execSQL(BuildingEntry.SQL_DELETE_TABLE);

        // Floor
        if (DEBUG) Log.i(TAG, "-=- FloorEntry: execSQL(SQL_DELETE_TABLE) -=-");
        if (DEBUG) Log.i(TAG, "-=- FloorEntry: {"+FloorEntry.SQL_DELETE_TABLE+"} -=-");
        db.execSQL(FloorEntry.SQL_DELETE_TABLE);

        // Room
        if (DEBUG) Log.i(TAG, "-=- RoomEntry: execSQL(SQL_DELETE_TABLE) -=-");
        if (DEBUG) Log.i(TAG, "-=- RoomEntry: {"+RoomEntry.SQL_DELETE_TABLE+"} -=-");
        db.execSQL(RoomEntry.SQL_DELETE_TABLE);

        onCreate(db);
    }

    public void startInsertData() {
        if (mAsyncTask != null && !mAsyncTask.isCancelled() ) {
            Toast.makeText(mContext, "실행중인 AsyncTask가 있습니다.", Toast.LENGTH_SHORT).
                    show();
            return;
        }
        mAsyncTask = new CampusInfoInsertAsyncTask(mContext);
        mAsyncTask.execute(R.xml.building_info);
    }

    public Boolean isInsertFinished() throws ExecutionException, InterruptedException {
        if (mAsyncTask == null)
            return true;
        return mAsyncTask.get();
    }

    public void insertBuilding(SQLiteDatabase db, int ID, int number, String name, String description) {
        if ( !db.isReadOnly() ) {
            ContentValues values = new ContentValues();
            values.put(BuildingEntry._ID, ID);
            values.put(BuildingEntry.COLUMN_NAME_NUMBER, number);
            values.put(BuildingEntry.COLUMN_NAME_NAME, name);
            values.put(BuildingEntry.COLUMN_NAME_DESCRIPTION, description);
            db.insert(BuildingEntry.TABLE_NAME, null, values);
        } else {
            Log.e(TAG, "insertBuilding: SQLiteDatabase is not Writable..");
        }
    }

    public void insertFloor(SQLiteDatabase db, int ID, int floor, int buildingID) {
        if ( !db.isReadOnly() ) {
            ContentValues values = new ContentValues();
            values.put(FloorEntry._ID, ID);
            values.put(FloorEntry.COLUMN_NAME_NUMBER, floor);
            values.put(FloorEntry.COLUMN_NAME_BUILDING_ID, buildingID);
            db.insert(FloorEntry.TABLE_NAME, null, values);
        } else {
            Log.e(TAG, "insertBuilding: SQLiteDatabase is not Writable..");
        }
    }

    public void insertRoom(SQLiteDatabase db, int ID, String name, String desc, int floorID) {
        if ( !db.isReadOnly() ) {
            ContentValues values = new ContentValues();
            values.put(RoomEntry._ID, ID);
            values.put(RoomEntry.COLUMN_NAME_NAME, name);
            values.put(RoomEntry.COLUMN_NAME_DESCRIPTION, desc);
            values.put(RoomEntry.COLUMN_NAME_FLOOR_ID, floorID);
            db.insert(RoomEntry.TABLE_NAME, null, values);
        } else {
            Log.e(TAG, "insertBuilding: SQLiteDatabase is not Writable..");
        }
    }

    public int getTableSize(SQLiteDatabase db, String tableName) {
        int count = 0;
        Cursor cursor = db.query(tableName, null,
                null, null, null, null, null);
        while (cursor.moveToNext()) {
            count++;
        }
        return count;
    }

    public void deleteTable(SQLiteDatabase db, String tableName) {
        db.delete(tableName, null, null);
    }

    public class CampusInfoInsertAsyncTask extends AsyncTask<Integer, Integer, Boolean> {
        private Context mContext;
        private ProgressDialog mDlg;

        public CampusInfoInsertAsyncTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i(TAG, "-=##=- onPreExecute -=##=-");

            mDlg = new ProgressDialog(mContext);
            mDlg.setCancelable(false);
            mDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mDlg.show();
        }

        @Override
        protected Boolean doInBackground(Integer... IDs) {
            if (DEBUG) Log.i(TAG, "doInBackground: called");
            if (IDs == null || IDs.length < 1) {
                return false;
            }

            int count=0;
            int currentBuildingID=1, currentFloorID=1, currentRoomID=1;

            for (int ID : IDs) {
                count += getTotalCampusInfoTag(ID);
            }
            mDlg.setMax(count);

//            for (int i=0; i<=mDlg.getMax(); i++) {
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                publishProgress(i);
//            }

            count = 0;
            SQLiteDatabase db = getWritableDatabase();
            for (int ID : IDs) {
                XmlResourceParser parser = mContext.getResources().getXml(ID);
                int number;
                String name, text;

                db.beginTransaction();
                try {
                    while (parser.next() != XmlResourceParser.END_DOCUMENT) {
                        if (parser.getEventType() != XmlResourceParser.START_TAG) {
                            continue;
                        }

                        switch (parser.getName()) {
                            case "building":  // ## <building num="1" name="100주년 기념관"> ##
                                number = Integer.parseInt(parser.getAttributeValue(ns, "num"));
                                name = parser.getAttributeValue(ns, "name");
                                insertBuilding(db,
                                        currentBuildingID++,
                                        number,
                                        name,
                                        ""
                                );
                                break;
                            case "floor":     // ## <floor num="1"> ##
                                number = Integer.parseInt( parser.getAttributeValue(ns, "num") );
                                insertFloor(db,
                                        currentFloorID++,
                                        number,
                                        currentBuildingID
                                );
                                break;
                            case "room":      // ## <room name="방재센터"> ##
                                name = parser.getAttributeValue(ns, "name");
                                parser.require(XmlResourceParser.START_TAG, ns, "room");
                                parser.next();
                                text = parser.getText();
                                insertRoom(db,
                                        currentRoomID++,
                                        name,
                                        text,
                                        currentFloorID
                                );
                                break;
                        }

                        publishProgress(++count);
                    }
                    db.setTransactionSuccessful();
                } catch (XmlPullParserException | IOException e) {
                    e.printStackTrace();
                } finally {
                    db.endTransaction();
                }

                if (DEBUG) {
                    Log.i(TAG, "database insert building count : " + (currentBuildingID-1));
                    Log.i(TAG, "database insert floor count : " + (currentFloorID-1));
                    Log.i(TAG, "database insert room count : " + (currentRoomID-1));
                }

                parser.close();
            }
            db.close();

            mDlg.setMessage("데이터베이스의 정리가 완료되었습니다.");
            mDlg.dismiss();

            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (values == null || values.length < 1) {
                return;
            }
            mDlg.setMessage("데이터를 삽입하는 중입니다.\n" + values[0] + "번째 작업을 완료하였습니다.");
            mDlg.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            Log.i(TAG, "-=##=- onPostExecute -=##=-");
            if (mDlg != null)
                mDlg.dismiss();
            if (!isCancelled()) {
                cancel(true);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();

            if (mDlg != null)
                mDlg.dismiss();
        }

        private int getTotalCampusInfoTag(int xml_ID) {
            int max = 0;
            XmlResourceParser parser = mContext.getResources().getXml(xml_ID);
            try {
                while (parser.next() != XmlPullParser.END_DOCUMENT) {
                    if (parser.getEventType() == XmlPullParser.START_TAG) {
                        max ++;
                    }
                }
            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            }
            parser.close();
            return max;
        }
    }
}
