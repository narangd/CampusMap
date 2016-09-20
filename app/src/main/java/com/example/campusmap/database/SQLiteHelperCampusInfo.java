package com.example.campusmap.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;
import android.util.Pair;

import com.example.campusmap.tree.branch.Building;
import com.example.campusmap.tree.branch.Floor;
import com.example.campusmap.tree.branch.Room;

import java.util.ArrayList;

public class SQLiteHelperCampusInfo extends SQLiteOpenHelper {
    private static final String TAG = "SQLiteHelperCampusInfo";
    private static final boolean DEBUG = true;
    private static final String ns = null;
    private static final int DATABACE_VERSION = 16;
    private static final String DATABACE_NAME = "CampusInfo.db";
    private static SQLiteHelperCampusInfo instance;

    public static final String CREATE_TABLE_HEAD = "CREATE TABLE ";
    public static final String DELETE_TABLE_HEAD = "DROP TABLE ";
    public static final String IF_EXISTS = "IF EXISTS ";
    public static final String TYPE_INTEGER = " INTEGER";
    public static final String TYPE_CHARACTER = " CHAR(20)";
    public static final String TYPE_CHARACTER2 = " CHAR(100)";
    public static final String TYPE_TEXT = " TEXT";
    public static final String UNIQUE = " UNIQUE";
    public static final String PRIMARY_KEY = " PRIMARY KEY";
    public static final String FOREIGN_KEY = " FOREIGN KEY";
    public static final String REFERENCES = " REFERENCES ";
    public static final String COMMA_SEP = ",";
    public static final String COLUMN_START = " (";
    public static final String COLUMN_END = ")";
    public static final String ORDER_BY_ASCENDING = " ASC";

    public static final int TRUE = 1;
    public static final int FALSE = 0;

    // FOREIGN KEY(---)
    // REFERENCES ---(---)

    public static abstract class BuildingEntry implements BaseColumns {
        public static final String TABLE_NAME = "building";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_NUMBER = "bnumber";
        public static final String COLUMN_NAME_DESCRIPTION = "bdesc";
        public static final String SQL_CREATE_TABLE =
                CREATE_TABLE_HEAD + TABLE_NAME + COLUMN_START +
                        _ID + TYPE_INTEGER + PRIMARY_KEY + COMMA_SEP +
                        COLUMN_NAME_NUMBER + TYPE_INTEGER + UNIQUE + COMMA_SEP +
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
                        // # reference building #
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
        public static final String COLUMN_NAME_PATH = "path";
        public static final String COLUMN_NAME_MAIN = "main";
        public static final String COLUMN_NAME_FLOOR_ID = "fID";
        public static final String COLUMN_NAME_BUILDING_ID = "bID";
        public static final String SQL_CREATE_TABLE =
                CREATE_TABLE_HEAD + TABLE_NAME + COLUMN_START +
                        _ID + TYPE_INTEGER + PRIMARY_KEY + COMMA_SEP +
                        COLUMN_NAME_FLOOR_ID + TYPE_INTEGER + COMMA_SEP +
                        COLUMN_NAME_BUILDING_ID + TYPE_INTEGER + COMMA_SEP +
                        COLUMN_NAME_NAME + TYPE_CHARACTER + COMMA_SEP +
                        COLUMN_NAME_MAIN + TYPE_INTEGER + COMMA_SEP +
                        COLUMN_NAME_DESCRIPTION + TYPE_TEXT + COMMA_SEP +
                        COLUMN_NAME_PATH + TYPE_CHARACTER2 + COMMA_SEP +
                        // # reference floor #
                        FOREIGN_KEY + COLUMN_START + COLUMN_NAME_FLOOR_ID  +COLUMN_END +
                            REFERENCES + FloorEntry.TABLE_NAME + COLUMN_START + FloorEntry._ID + COLUMN_END + COMMA_SEP +
                        // # reference building #
                        FOREIGN_KEY + COLUMN_START + COLUMN_NAME_BUILDING_ID  +COLUMN_END +
                            REFERENCES + BuildingEntry.TABLE_NAME + COLUMN_START + BuildingEntry._ID + COLUMN_END +
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



    public long insertBuilding(SQLiteDatabase db, int ID, int number, String name, String description) {
        if ( !db.isReadOnly() ) {
            ContentValues values = new ContentValues();
            values.put(BuildingEntry._ID, ID);
            values.put(BuildingEntry.COLUMN_NAME_NUMBER, number);
            values.put(BuildingEntry.COLUMN_NAME_NAME, name);
            values.put(BuildingEntry.COLUMN_NAME_DESCRIPTION, description);
            return db.insert(BuildingEntry.TABLE_NAME, null, values);
        } else {
            Log.e(TAG, "insertBuilding: SQLiteDatabase is not Writable..");
        }
        return -1;
    }

    public long insertFloor(SQLiteDatabase db, int ID, int floor, int buildingID) {
        if ( !db.isReadOnly() ) {
            ContentValues values = new ContentValues();
            values.put(FloorEntry._ID, ID);
            values.put(FloorEntry.COLUMN_NAME_NUMBER, floor);
            values.put(FloorEntry.COLUMN_NAME_BUILDING_ID, buildingID);
            return db.insert(FloorEntry.TABLE_NAME, null, values);
        } else {
            Log.e(TAG, "insertBuilding: SQLiteDatabase is not Writable..");
        }
        return -1;
    }

    public long insertRoom(
            SQLiteDatabase db,
            int ID, String name,
            String desc,
            String path,
            int floorID,
            int buildingID,
            boolean isMain) {
        if ( !db.isReadOnly() ) {
            ContentValues values = new ContentValues();
            values.put(RoomEntry._ID, ID);
            values.put(RoomEntry.COLUMN_NAME_NAME, name);
            values.put(RoomEntry.COLUMN_NAME_DESCRIPTION, desc);
            values.put(RoomEntry.COLUMN_NAME_PATH, path);
            values.put(RoomEntry.COLUMN_NAME_FLOOR_ID, floorID);
            values.put(RoomEntry.COLUMN_NAME_BUILDING_ID, buildingID);
            values.put(RoomEntry.COLUMN_NAME_MAIN, isMain? 1 : 0);
            return db.insert(RoomEntry.TABLE_NAME, null, values);
        } else {
            Log.e(TAG, "insertBuilding: SQLiteDatabase is not Writable..");
        }
        return -1;
    }

    public int getTableSize(SQLiteDatabase db, String tableName) {
        int count = 0;
        Cursor cursor = db.query(tableName, null,
                null, null, null, null, null);
        while (cursor.moveToNext()) {
            count++;
        }
        cursor.close();
        return count;
    }

    public void deleteTable(SQLiteDatabase db, String tableName) {
        db.delete(tableName, null, null);
    }

    public ArrayList<InfoLocation> searchResultItems(String query) {
        SQLiteDatabase database = getReadableDatabase();
        ArrayList<InfoLocation> resultList = new ArrayList<>();
        query = "%" + query + "%";

        // ## Search Building ##
        Cursor buildingCursor = database.query(
                BuildingEntry.TABLE_NAME,
                new String[]{
                        BuildingEntry.COLUMN_NAME_NAME,
                        BuildingEntry._ID
                },
                BuildingEntry.COLUMN_NAME_NAME + " LIKE ?",
                new String[]{query},
                null, null,
                BuildingEntry.COLUMN_NAME_NAME + ORDER_BY_ASCENDING
        );
        while (buildingCursor.moveToNext()) {
            resultList.add(new InfoLocation(
                    buildingCursor.getString(buildingCursor.getColumnIndex(BuildingEntry.COLUMN_NAME_NAME)),
                    InfoLocation.TAG_BUILDING,
                    buildingCursor.getInt(buildingCursor.getColumnIndex(BuildingEntry._ID)),
                    InfoLocation.NONE,
                    InfoLocation.NONE
            ));
        }
        buildingCursor.close();

        // ## Search Room ##
        Cursor roomCursor = database.query(
                RoomEntry.TABLE_NAME,
                new String[]{
                        RoomEntry.COLUMN_NAME_NAME,
                        RoomEntry._ID,
                        RoomEntry.COLUMN_NAME_FLOOR_ID,
                        RoomEntry.COLUMN_NAME_BUILDING_ID
                },
                RoomEntry.COLUMN_NAME_NAME + " LIKE ?",
                new String[]{query},
                null, null,
                RoomEntry.COLUMN_NAME_NAME + ORDER_BY_ASCENDING
        );
        while (roomCursor.moveToNext()) {
            resultList.add(new InfoLocation(
                    roomCursor.getString(roomCursor.getColumnIndex(RoomEntry.COLUMN_NAME_NAME)),
                    InfoLocation.TAG_ROOM,
                    roomCursor.getInt(roomCursor.getColumnIndex(RoomEntry.COLUMN_NAME_BUILDING_ID)),
                    roomCursor.getInt(roomCursor.getColumnIndex(RoomEntry.COLUMN_NAME_FLOOR_ID)),
                    roomCursor.getInt(roomCursor.getColumnIndex(RoomEntry._ID))
            ));
        }
        roomCursor.close();
        database.close();

        return resultList;
    }

    public String getRoomPath(int roomID) {
        SQLiteDatabase database = getReadableDatabase();
        String hierarchy = "";

        // ## Search floor ID from Database ##
        Cursor cursor = database.query(
                RoomEntry.TABLE_NAME,
                new String[]{RoomEntry.COLUMN_NAME_PATH},
                RoomEntry._ID + "=?",
                new String[]{String.valueOf(roomID)},
                null, null, null
        );

        // ## Found floor ID ##
        if (cursor.moveToNext()) {
            hierarchy = cursor.getString(cursor.getColumnIndex(RoomEntry.COLUMN_NAME_PATH));
        }
        cursor.close();
        database.close();
        return hierarchy;
    }

    public ArrayList<Building> getBuildingList() {
        SQLiteDatabase database = getReadableDatabase();
        ArrayList<Building> buildingList = new ArrayList<>();
        Cursor cursor = database.query(
                BuildingEntry.TABLE_NAME,
                new String[]{
                        BuildingEntry._ID,
                        BuildingEntry.COLUMN_NAME_NUMBER,
                        BuildingEntry.COLUMN_NAME_NAME,
                        BuildingEntry.COLUMN_NAME_DESCRIPTION
                },
                null, null,
                null, null,
                BuildingEntry._ID + ORDER_BY_ASCENDING
        );
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(BuildingEntry._ID));
            int number = cursor.getInt(cursor.getColumnIndex(BuildingEntry.COLUMN_NAME_NUMBER));
            String name = cursor.getString(cursor.getColumnIndex(BuildingEntry.COLUMN_NAME_NAME));
            String description = cursor.getString(cursor.getColumnIndex(BuildingEntry.COLUMN_NAME_DESCRIPTION));
            buildingList.add( new Building(id, number, name, description) );
        }
        cursor.close();
        database.close();
        return buildingList;
    }

    public ArrayList<Floor> getFloorList(int buildingID) {
        SQLiteDatabase database = getReadableDatabase();
        ArrayList<Floor> floorList = new ArrayList<>();
        Cursor cursor = database.query(
                FloorEntry.TABLE_NAME,
                new String[]{
                        FloorEntry._ID,
                        FloorEntry.COLUMN_NAME_NUMBER
                },
                FloorEntry.COLUMN_NAME_BUILDING_ID + " =?",
                new String[]{String.valueOf(buildingID)},
                null, null,
                FloorEntry._ID + ORDER_BY_ASCENDING
        );
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(FloorEntry._ID));
            int number = cursor.getInt(cursor.getColumnIndex(FloorEntry.COLUMN_NAME_NUMBER));
            floorList.add( new Floor(id, number, buildingID) );
        }
        cursor.close();
        database.close();
        return floorList;
    }

    public ArrayList<Room> getRoomList(int buildingID, int floorID) {
        SQLiteDatabase database = getReadableDatabase();
        ArrayList<Room> roomList = new ArrayList<>();
        Cursor cursor = database.query(
                RoomEntry.TABLE_NAME,
                new String[]{
                        RoomEntry._ID,
                        RoomEntry.COLUMN_NAME_NAME,
                        RoomEntry.COLUMN_NAME_DESCRIPTION
                },
                RoomEntry.COLUMN_NAME_BUILDING_ID + "=? AND " + RoomEntry.COLUMN_NAME_FLOOR_ID + "=?",
                new String[]{String.valueOf(buildingID), String.valueOf(floorID)},
                null, null,
                RoomEntry._ID + ORDER_BY_ASCENDING
        );
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(RoomEntry._ID));
            String name = cursor.getString(cursor.getColumnIndex(RoomEntry.COLUMN_NAME_NAME));
            String text = cursor.getString(cursor.getColumnIndex(RoomEntry.COLUMN_NAME_DESCRIPTION));
            roomList.add( new Room(id, name, text, buildingID, floorID) );
        }
        cursor.close();
        database.close();
        return roomList;
    }

    public Building getBuildingDetail(int buildingID) {
        SQLiteDatabase database = getReadableDatabase();
        Building building = null;
        Cursor cursor = database.query(
                BuildingEntry.TABLE_NAME,
                new String[]{
                        BuildingEntry._ID,
                        BuildingEntry.COLUMN_NAME_NUMBER,
                        BuildingEntry.COLUMN_NAME_NAME,
                        BuildingEntry.COLUMN_NAME_DESCRIPTION
                },
                BuildingEntry._ID + "=?",
                new String[]{String.valueOf(buildingID)},
                null, null, null
        );
        if (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(BuildingEntry._ID));
            int number = cursor.getInt(cursor.getColumnIndex(BuildingEntry.COLUMN_NAME_NUMBER));
            String name = cursor.getString(cursor.getColumnIndex(BuildingEntry.COLUMN_NAME_NAME));
            String description = cursor.getString(cursor.getColumnIndex(BuildingEntry.COLUMN_NAME_DESCRIPTION));
            building = new Building(id, number, name, description);
        }
        cursor.close();
        database.close();
        return building;
    }

    public Floor getFloorDetail(int floorID) {
        SQLiteDatabase database = getReadableDatabase();
        Floor floor = null;
        Cursor cursor = database.query(
                FloorEntry.TABLE_NAME,
                new String[]{
                        FloorEntry._ID,
                        FloorEntry.COLUMN_NAME_NUMBER,
                        FloorEntry.COLUMN_NAME_BUILDING_ID
                },
                FloorEntry._ID + "=?",
                new String[]{String.valueOf(floorID)},
                null, null, null
        );
        if (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(FloorEntry._ID));
            int number = cursor.getInt(cursor.getColumnIndex(FloorEntry.COLUMN_NAME_NUMBER));
            int building_id = cursor.getInt(cursor.getColumnIndex(FloorEntry.COLUMN_NAME_BUILDING_ID));
            floor = new Floor(id, number, building_id);
        }
        cursor.close();
        database.close();
        return floor;
    }

    public Room getRoomDetail(int roomID) {
        SQLiteDatabase database = getReadableDatabase();
        Room room = null;
        Cursor cursor = database.query(
                RoomEntry.TABLE_NAME,
                new String[]{
                        RoomEntry._ID,
                        RoomEntry.COLUMN_NAME_NAME,
                        RoomEntry.COLUMN_NAME_DESCRIPTION,
                        RoomEntry.COLUMN_NAME_BUILDING_ID,
                        RoomEntry.COLUMN_NAME_FLOOR_ID
                },
                RoomEntry._ID + "=?",
                new String[]{String.valueOf(roomID)},
                null, null, null
        );
        if (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(RoomEntry._ID));
            String name = cursor.getString(cursor.getColumnIndex(RoomEntry.COLUMN_NAME_NAME));
            String description = cursor.getString(cursor.getColumnIndex(RoomEntry.COLUMN_NAME_DESCRIPTION));
            int building_id = cursor.getInt(cursor.getColumnIndex(RoomEntry.COLUMN_NAME_BUILDING_ID));
            int floor_id = cursor.getInt(cursor.getColumnIndex(RoomEntry.COLUMN_NAME_FLOOR_ID));
            room = new Room(id, name, description, building_id, floor_id);
        }
        cursor.close();
        database.close();
        return room;
    }

    public ArrayList<Pair<String,Integer[]>> getMainRooms(int buildingID) {
        SQLiteDatabase database = getReadableDatabase();
        ArrayList<Pair<String,Integer[]>> mainRooms = new ArrayList<>();
        Cursor cursor = database.query(
                RoomEntry.TABLE_NAME,
                new String[]{
                        RoomEntry.COLUMN_NAME_NAME,
                        RoomEntry._ID,
                        RoomEntry.COLUMN_NAME_FLOOR_ID,
                        RoomEntry.COLUMN_NAME_BUILDING_ID
                },
                RoomEntry.COLUMN_NAME_BUILDING_ID + "=? AND " + RoomEntry.COLUMN_NAME_MAIN + "=?",
                new String[]{String.valueOf(buildingID), String.valueOf(TRUE)},
                null, null,
                RoomEntry.COLUMN_NAME_NAME + ORDER_BY_ASCENDING
        );
        while (cursor.moveToNext()) {
            mainRooms.add(new Pair<>(
                    cursor.getString(cursor.getColumnIndex(RoomEntry.COLUMN_NAME_NAME)),
                    new Integer[]{
                            cursor.getInt(cursor.getColumnIndex(RoomEntry.COLUMN_NAME_BUILDING_ID)),
                            cursor.getInt(cursor.getColumnIndex(RoomEntry.COLUMN_NAME_FLOOR_ID)),
                            cursor.getInt(cursor.getColumnIndex(RoomEntry._ID))
                    }
            ));
        }
        cursor.close();
        database.close();
        return mainRooms;
    }


}
