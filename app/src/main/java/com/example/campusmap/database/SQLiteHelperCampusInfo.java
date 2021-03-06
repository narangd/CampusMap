package com.example.campusmap.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.campusmap.data.branch.Building;
import com.example.campusmap.data.branch.Floor;
import com.example.campusmap.data.branch.Room;
import com.example.campusmap.data.server.BuildingJson;
import com.example.campusmap.data.server.FloorJson;
import com.example.campusmap.data.server.RoomJson;
import com.example.campusmap.form.InfoLocation;

import java.util.ArrayList;

public class SQLiteHelperCampusInfo extends com.example.campusmap.database.SQLiteOpenHelper {
    private static final String TAG = "SQLiteHelperCampusInfo";
    private static final boolean DEBUG = true;

    private static final int DATABASE_VERSION = 17;
    private static final String DATABASE_NAME = "CampusInfo.db";
    private static SQLiteHelperCampusInfo instance;

    public static final int TRUE = 1;
    public static final int FALSE = 0;

    // FOREIGN KEY(---)
    // REFERENCES ---(---)



    public static SQLiteHelperCampusInfo getInstance(Context context) {
        if (instance == null) {
            instance = new SQLiteHelperCampusInfo(context);
        }
        return instance;
    }

    private SQLiteHelperCampusInfo(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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

    public long insert(SQLiteDatabase db, BuildingJson building) {
        if ( !db.isReadOnly() ) {
            ContentValues values = new ContentValues();
            values.put(BuildingEntry._ID, building.getId());
            values.put(BuildingEntry.COLUMN_NAME_NUMBER, building.getNumber());
            values.put(BuildingEntry.COLUMN_NAME_NAME, building.getName());
            values.put(BuildingEntry.COLUMN_NAME_DESCRIPTION, building.getDescription());
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

    public long insert(SQLiteDatabase db, FloorJson floor) {
        if ( !db.isReadOnly() ) {
            ContentValues values = new ContentValues();
            values.put(FloorEntry._ID, floor.getId());
            values.put(FloorEntry.COLUMN_NAME_NUMBER, floor.getNumber());
            values.put(FloorEntry.COLUMN_NAME_BUILDING_ID, floor.getBuildingId());
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

    public long insert(SQLiteDatabase db, RoomJson room, int buildingId, String path) {
        if ( !db.isReadOnly() ) {
            ContentValues values = new ContentValues();
            values.put(RoomEntry._ID, room.getId());
            values.put(RoomEntry.COLUMN_NAME_NAME, room.getName());
            values.put(RoomEntry.COLUMN_NAME_DESCRIPTION, room.getDescription());
            values.put(RoomEntry.COLUMN_NAME_PATH, path);
            values.put(RoomEntry.COLUMN_NAME_FLOOR_ID, room.getFloorId());
            values.put(RoomEntry.COLUMN_NAME_BUILDING_ID, buildingId);
            values.put(RoomEntry.COLUMN_NAME_MAIN, room.getMain());
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

    public int delete(SQLiteDatabase db) {
        int count = 0;
        count += db.delete(BuildingEntry.TABLE_NAME, null, null);
        count += db.delete(FloorEntry.TABLE_NAME, null, null);
        count += db.delete(RoomEntry.TABLE_NAME, null, null);
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
                FloorEntry.COLUMN_NAME_NUMBER + ORDER_BY_ASCENDING
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
                RoomEntry.COLUMN_NAME_NAME + ORDER_BY_ASCENDING
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

    public ArrayList<InfoLocation> getMainRooms(int buildingID) {
        SQLiteDatabase database = getReadableDatabase();
        ArrayList<InfoLocation> mainRooms = new ArrayList<>();
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
            String name = cursor.getString(cursor.getColumnIndex(RoomEntry.COLUMN_NAME_NAME));
            int bid = cursor.getInt(cursor.getColumnIndex(RoomEntry.COLUMN_NAME_BUILDING_ID));
            int fid = cursor.getInt(cursor.getColumnIndex(RoomEntry.COLUMN_NAME_FLOOR_ID));
            int rid = cursor.getInt(cursor.getColumnIndex(RoomEntry._ID));
            mainRooms.add(new InfoLocation(
                    name,
                    InfoLocation.TAG_ROOM,
                    bid, fid, rid
            ));
        }
        cursor.close();
        database.close();
        return mainRooms;
    }

    public Cursor searchBuildingInfoName(String newText) {
        SQLiteDatabase database = getReadableDatabase();
        newText = "%" + newText + "%";

        /* (SELECT id, name, 0 floor_id, 0 room_id
            FROM `building`
            WHERE name LIKE "%1%"
            ORDER BY name ASC)
            UNION
            (SELECT room.id, room.name, floor_id, room.id room_id
            FROM room
            WHERE name LIKE "%1%"
            ORDER BY room.name ASC)
        */
        return database.rawQuery("SELECT " +
                BuildingEntry._ID + COMMA_SEP + BuildingEntry.COLUMN_NAME_NAME +
                " FROM " + BuildingEntry.TABLE_NAME +
                " WHERE " + BuildingEntry.COLUMN_NAME_NAME + " LIKE ?" +
                " UNION ALL SELECT " +
                RoomEntry._ID + COMMA_SEP + RoomEntry.COLUMN_NAME_NAME +
                " FROM " + RoomEntry.TABLE_NAME +
                " WHERE " + RoomEntry.COLUMN_NAME_NAME + " LIKE ?" +
                " ORDER BY " + RoomEntry.COLUMN_NAME_NAME + ORDER_BY_ASCENDING + "",
                new String[]{newText, newText}
        );
    }
}
