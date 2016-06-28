package com.example.campusmap.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.example.campusmap.tree.branch.Room;

import java.util.ArrayList;

/**
 * Created by DBLAB on 2016-06-20.
 */
public class SQLiteHelperCampusInfo extends SQLiteOpenHelper {
    private static final String TAG = "SQLiteHelperCampusInfo";
    private static final boolean DEBUG = true;
    private static final String ns = null;
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

    private SQLiteHelperCampusInfo(Context context) {
        super(context, DATABACE_NAME, null, DATABACE_VERSION);
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

    public ArrayList<SearchResultItem> searchResultItems(SQLiteDatabase db, String query) {
        ArrayList<SearchResultItem> resultList = new ArrayList<>();
        query = "%" + query + "%";

        // ## Search Building ##
        Cursor buildingCursor = db.query(
                BuildingEntry.TABLE_NAME,
                new String[]{BuildingEntry.COLUMN_NAME_NAME},
                BuildingEntry.COLUMN_NAME_NAME + " LIKE ?",
                new String[]{query},
                null, null,
                BuildingEntry.COLUMN_NAME_NAME+" ASC");
        while (buildingCursor.moveToNext()) {
            resultList.add(new SearchResultItem(
                    buildingCursor.getString(buildingCursor.getColumnIndex(BuildingEntry.COLUMN_NAME_NAME)),
                    SearchResultItem.Tag.BUILDING,
                    -1
            ));
        }
        buildingCursor.close();

        // ## Search Room ##
        Cursor roomCursor = db.query(
                RoomEntry.TABLE_NAME,
                new String[]{RoomEntry.COLUMN_NAME_NAME, RoomEntry.COLUMN_NAME_FLOOR_ID},
                BuildingEntry.COLUMN_NAME_NAME + " LIKE ?",
                new String[]{query},
                null, null,
                RoomEntry.COLUMN_NAME_NAME+" ASC");
        while (roomCursor.moveToNext()) {
            resultList.add(new SearchResultItem(
                    roomCursor.getString(roomCursor.getColumnIndex(RoomEntry.COLUMN_NAME_NAME)),
                    SearchResultItem.Tag.ROOM,
                    roomCursor.getInt(roomCursor.getColumnIndex(RoomEntry.COLUMN_NAME_FLOOR_ID))
            ));
        }
        roomCursor.close();

        return resultList;
    }

    public String searchRoomHierarchy(SQLiteDatabase db, int floorId) {
        String hierarchy = "";

        // ## Search floor ID from Database ##
        Cursor floorCursor = db.query(
                FloorEntry.TABLE_NAME,
                new String[]{FloorEntry.COLUMN_NAME_NUMBER, FloorEntry.COLUMN_NAME_BUILDING_ID},
                FloorEntry._ID + "=?",
                new String[]{String.valueOf(floorId)},
                null, null, null
        );

        // ## Found floor ID ##
        if (floorCursor.moveToNext()) {
            int floorNumber = floorCursor.getInt(floorCursor.getColumnIndex(FloorEntry.COLUMN_NAME_NUMBER));
            int buildingId = floorCursor.getInt(floorCursor.getColumnIndex(FloorEntry.COLUMN_NAME_BUILDING_ID));
            hierarchy = floorNumber +"층";

            // ## Search building ID from Database ##
            Cursor buildingCursor = db.query(
                    BuildingEntry.TABLE_NAME,
                    new String[]{BuildingEntry.COLUMN_NAME_NAME},
                    BuildingEntry._ID + "=?",
                    new String[]{String.valueOf(buildingId)},
                    null, null, null
            );

            // ## Found building ID ##
            if (buildingCursor.moveToNext()) {
                hierarchy = buildingCursor.getString(buildingCursor.getColumnIndex(BuildingEntry.COLUMN_NAME_NAME))+" / " + hierarchy;
            }  else {
                Log.e(TAG, "searchRoomHierarchy: not found building ID:" + buildingId);
            }
        } else {
            Log.e(TAG, "searchRoomHierarchy: not found floor ID:" + floorId);
        }
        return hierarchy;
    }


}
