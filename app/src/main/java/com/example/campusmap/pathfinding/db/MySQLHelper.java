package com.example.campusmap.pathfinding.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by 연구생 on 2015-11-09.
 */
public class MySQLHelper extends SQLiteOpenHelper {
    private static MySQLHelper instance;

    private static final int VERSION = 7; // 7 : 현재
    private static final String TABLE_NAME = "CompusOstacle";
    private SQLiteDatabase db;

    public static MySQLHelper getInstance(Context context) {
        if (instance == null)
            instance = new MySQLHelper(context);
        return instance;
    }

    private MySQLHelper(Context context) {
        super(context, "campus.db", null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        Log.i("DataBase Create", "데이터베이스가 생성됩니다.");
        db.execSQL("CREATE TABLE "+TABLE_NAME+" ( id INTEGER PRIMARY KEY AUTOINCREMENT, polygon TEXT NOT NULL);");
        // testing.. data..
        insert(db, "13.121 24.132,15.435 24.132,15.435 30.932,13.129 30.932,13.121 24.132");
        insert(db, "16.935 24.159,16.935 29.750,17.411 29.750,17.411 30.033,17.001 30.614,17.001 31.273,17.616 32.145,18.540 32.145,19.030 31.450,19.188 30.611,18.550 29.705,19.215 29.705,19.215 24.159,16.935 24.159");
        insert(db, "22.769 24.449,22.769 25.382,21.689 25.382,21.689 29.580,22.112 29.580,22.112 29.314,26.635 29.314,26.635 29.647,27.747 29.647,27.747 24.560,22.769 24.449");
        insert(db, "13.424 34.622,13.424 35.466,13.167 35.564,13.167 36.710,13.408 36.710,13.480 37.090,15.584 37.090,15.716 37.415,17.103 37.415,17.103 36.554,17.870 36.554,18.190 36.099,18.190 35.488,17.923 35.386,17.923 34.577,17.071 34.577,17.100 34.728,13.424 34.622");
        insert(db, "20.578 31.002,26.354 31.002,26.354 36.865,20.562 36.865,20.578 31.002");
        insert(db, "30.658 28.292,32.004 28.292,32.142 27.562,32.787 27.562,33.018 28.130,34.165 28.130,41.803 28.130,41.803 25.804,34.916 25.804,34.916 24.249,30.627 24.249,30.658 28.292");
        insert(db, "13.628 42.151,18.010 42.151,18.010 40.064,13.753 40.064,13.628 42.151");
        insert(db, "14.723 42.373,14.610 42.976,14.024 42.753,14.024 44.461,16.289 44.461,16.289 42.329,14.723 42.373");
        insert(db, "13.659 44.550,13.659 45.927,13.377 45.927,13.377 46.904,16.664 46.904,16.664 44.550,13.659 44.550");
        insert(db, "41.678 51.280,41.678 55.544,42.461 55.544,42.461 56.255,43.678 56.718,48.018 56.718,48.018 57.899,48.808 59.020,49.438 58.781,49.852 57.764,49.852 56.211,49.537 55.438,49.537 52.479,46.484 52.479,46.484 51.302,41.678 51.280");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("DataBase Upgrade", oldVersion + "버전에서 " + newVersion + "버전으로 업그레이드를 위해 데이터베이스를 삭제합니다.");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insert(SQLiteDatabase db, String geomText) {
//        ContentValues values = new ContentValues();
//        values.put("polygon", geomText);
        String sql = "INSERT INTO "+TABLE_NAME+" (id, polygon) VALUES (null, '"+geomText+"')";
        Log.i("SQL line", sql);
        db.execSQL(sql);//insert(TABLE_NAME, null, values);
    }

    public Cursor select() {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_NAME, null);//rawQuery("SELECT polygon FROM " + TABLE_NAME, null);
        return cursor;
    }
}
