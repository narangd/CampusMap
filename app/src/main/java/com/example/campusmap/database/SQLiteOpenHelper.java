package com.example.campusmap.database;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

abstract class SQLiteOpenHelper extends android.database.sqlite.SQLiteOpenHelper {
    private static final String TAG = "SQLiteOpenHelper";
    private static final boolean DEBUG = true;
    private static final String ns = null;

    protected static final String CREATE_TABLE_HEAD = "CREATE TABLE ";
    protected static final String DELETE_TABLE_HEAD = "DROP TABLE ";
    protected static final String IF_EXISTS = "IF EXISTS ";
    protected static final String TYPE_INTEGER = " INTEGER";
    protected static final String TYPE_FLOAT = " REAL";
    protected static final String TYPE_CHARACTER = " CHAR(20)";
    protected static final String TYPE_CHARACTER2 = " CHAR(100)";
    protected static final String TYPE_TEXT = " TEXT";
    protected static final String UNIQUE = " UNIQUE";
    protected static final String PRIMARY_KEY = " PRIMARY KEY";
    protected static final String FOREIGN_KEY = " FOREIGN KEY";
    protected static final String AUTOINCREMENT = " AUTOINCREMENT";
    protected static final String REFERENCES = " REFERENCES ";
    protected static final String COMMA_SEP = ",";
    protected static final String COLUMN_START = " (";
    protected static final String COLUMN_END = ")";
    protected static final String ORDER_BY_ASCENDING = " ASC";

    SQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    protected static class BuildingEntry implements BaseColumns {
        protected static final String TABLE_NAME = "building";
        protected static final String COLUMN_NAME_NAME = "name";
        protected static final String COLUMN_NAME_NUMBER = "bnumber";
        protected static final String COLUMN_NAME_DESCRIPTION = "bdesc";
        protected static final String SQL_CREATE_TABLE =
                CREATE_TABLE_HEAD + TABLE_NAME + COLUMN_START +
                        _ID + TYPE_INTEGER + PRIMARY_KEY + COMMA_SEP +
                        COLUMN_NAME_NUMBER + TYPE_INTEGER + UNIQUE + COMMA_SEP +
                        COLUMN_NAME_NAME + TYPE_CHARACTER + COMMA_SEP +
                        COLUMN_NAME_DESCRIPTION + TYPE_TEXT + COLUMN_END;
        protected static final String SQL_DELETE_TABLE =
                DELETE_TABLE_HEAD + IF_EXISTS + TABLE_NAME;
    }

    protected static class FloorEntry implements BaseColumns {
        protected static final String TABLE_NAME = "floor";
        protected static final String COLUMN_NAME_NUMBER = "fnumber";
        protected static final String COLUMN_NAME_BUILDING_ID = "bID";
        protected static final String SQL_CREATE_TABLE =
                CREATE_TABLE_HEAD + TABLE_NAME + COLUMN_START +
                        _ID + TYPE_INTEGER + PRIMARY_KEY + COMMA_SEP +
                        COLUMN_NAME_BUILDING_ID + TYPE_INTEGER + COMMA_SEP +
                        COLUMN_NAME_NUMBER + TYPE_INTEGER + COMMA_SEP +
                        // # reference building #
                        FOREIGN_KEY + COLUMN_START + COLUMN_NAME_BUILDING_ID + COLUMN_END +
                        REFERENCES + BuildingEntry.TABLE_NAME + COLUMN_START + BuildingEntry._ID + COLUMN_END +
                        COLUMN_END;
        protected static final String SQL_DELETE_TABLE =
                DELETE_TABLE_HEAD + IF_EXISTS + TABLE_NAME;
    }

    protected static class RoomEntry implements BaseColumns {
        protected static final String TABLE_NAME = "room";
        protected static final String COLUMN_NAME_NAME = "name";
        protected static final String COLUMN_NAME_DESCRIPTION = "rdesc";
        protected static final String COLUMN_NAME_PATH = "path";
        protected static final String COLUMN_NAME_MAIN = "main";
        protected static final String COLUMN_NAME_FLOOR_ID = "fID";
        protected static final String COLUMN_NAME_BUILDING_ID = "bID";
        protected static final String SQL_CREATE_TABLE =
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
        protected static final String SQL_DELETE_TABLE =
                DELETE_TABLE_HEAD + IF_EXISTS + TABLE_NAME;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (DEBUG) Log.i(TAG, "-=- onCreate: Database create version("+db.getVersion()+")-=-");

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
}
