package com.example.campusmap.database;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;

abstract class SQLiteOpenHelper extends android.database.sqlite.SQLiteOpenHelper {

    static final String CREATE_TABLE_HEAD = "CREATE TABLE ";
    static final String DELETE_TABLE_HEAD = "DROP TABLE ";
    static final String IF_EXISTS = "IF EXISTS ";
    static final String TYPE_INTEGER = " INTEGER";
    static final String TYPE_FLOAT = " REAL";
    static final String TYPE_CHARACTER = " CHAR(20)";
    static final String TYPE_CHARACTER2 = " CHAR(100)";
    static final String TYPE_TEXT = " TEXT";
    static final String UNIQUE = " UNIQUE";
    static final String PRIMARY_KEY = " PRIMARY KEY";
    static final String FOREIGN_KEY = " FOREIGN KEY";
    static final String AUTOINCREMENT = " AUTOINCREMENT";
    static final String REFERENCES = " REFERENCES ";
    static final String COMMA_SEP = ",";
    static final String COLUMN_START = " (";
    static final String COLUMN_END = ")";
    static final String ORDER_BY_ASCENDING = " ASC";

    SQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    SQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }
}
