package com.example.campusmap.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.XmlResourceParser;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.example.campusmap.R;
import com.example.campusmap.data.server.BuildingJson;
import com.example.campusmap.data.server.FloorJson;
import com.example.campusmap.data.server.RoomJson;
import com.example.campusmap.data.server.RootJson;
import com.example.campusmap.database.SQLiteHelperCampusInfo;
import com.example.campusmap.server.ServerClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class VersionUpdateAsyncTask extends AsyncTask<Void, String, Integer> implements DialogInterface.OnCancelListener {
    private static final String TAG = "CampusInfoInsertAsync";
    private static final boolean DEBUG = true;

    private SharedPreferences preferences;
    private int version;
    private Context context;
    private ProgressDialog mDlg;
    private SQLiteHelperCampusInfo helper;

    private static final String Progress_Progress = "progress";
    private static final String Progress_Dialog = "dialog";

    private AlertDialog alertDialog;

    public VersionUpdateAsyncTask(Context context) {
        this.context = context;
        helper = SQLiteHelperCampusInfo.getInstance(context);
        alertDialog = new AlertDialog.Builder(this.context).create();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        version = preferences.getInt( context.getString(R.string.pref_key_db_version), 0 );
        Log.i(TAG, "onPreExecute: DB version : " + version);

        mDlg = new ProgressDialog(context);
        mDlg.setCancelable(false);
        mDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDlg.setTitle("데이터베이스를 구성중입니다");
        mDlg.setMessage("");
        mDlg.setIndeterminate(true);
        mDlg.setOnCancelListener(this);
        mDlg.show();
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        if (DEBUG) Log.i(TAG, "doInBackground: called");

        publishProgress(Progress_Dialog, "읽어오는 중입니다");

        RootJson rootJson = ServerClient.datas(version);

        publishProgress(Progress_Dialog, "입력하는중입니다");

        SQLiteDatabase database = helper.getWritableDatabase();
        try {
            database.beginTransaction();

            helper.delete(database);

            version = rootJson.getVersion();
            Log.i(TAG, "doInBackground: Server Version : " + version);

            int count = 0;

            for (BuildingJson buildingJson : rootJson.getBuilding()) {
                helper.insert(database, buildingJson);
                int buildingId = buildingJson.getId();

                for (FloorJson floorJson : buildingJson.getFloor()) {
                    helper.insert(database, floorJson);

                    for (RoomJson roomJson : floorJson.getRoom()) {
                        helper.insert(database, roomJson, buildingId,
                                buildingJson.getName() + "/" + floorJson.getNumber() + "층");
//                        path = buildingName + " / " + String.valueOf(number)+"층";

                        publishProgress(Progress_Progress, ++count + "");
                    }

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            database.setTransactionSuccessful();
        } catch (Exception ignored) {
            Log.e(TAG, "doInBackground: ", ignored);
        } finally {
            database.endTransaction();
            database.close();
        }
        return version;
    }

    private int parsingJSON(String json, SQLiteHelperCampusInfo helper, SQLiteDatabase database) {
        int count=0;
        int currentBuildingID=0, currentFloorID=0, currentRoomID=0;

        int version = 0;
        int id, number;
        String buildingName, path, roomName;
        String description, main;

        long rowID;
        mDlg.setMax(getTotalCampusInfoJSONObject(json));
        Log.i(TAG, "parsingJSON: json object count : " + mDlg.getMax());

        try {
            JSONObject buildingInfo = new JSONObject(json);
            version = buildingInfo.getInt("version");

            JSONArray buildings = buildingInfo.getJSONArray("building");
            for (int buildingIndex=0; buildingIndex<buildings.length(); buildingIndex++) {

                JSONObject building = buildings.getJSONObject(buildingIndex);
                currentBuildingID = Integer.parseInt( building.getString("id") );
                number = Integer.parseInt( building.getString("number") );
                buildingName = building.getString("name");
                description = building.getString("description");
                description = description == null ? "" : description;

                helper.insertBuilding(database,
                        currentBuildingID,    // # id #
                        number,                 // # number #
                        buildingName,                   // # name #
                        description             // # description #
                );
                publishProgress("progress", ++count + "");
//                if (DEBUG) {
//                    Log.d(TAG, "parsingJSON(building)["+rowID+"] id : " + currentBuildingID +
//                            ", number : " + number +
//                            ", name : " + buildingName +
//                            ", description : " + description);
//                }

                JSONArray floors = building.getJSONArray("floor");
                for (int floorIndex=0; floorIndex<floors.length(); floorIndex++) {

                    JSONObject floor = floors.getJSONObject(floorIndex);
                    currentFloorID = Integer.parseInt( floor.getString("id") );
                    number = Integer.parseInt( floor.getString("number") );
//                    currentBuildingID = Integer.parseInt( floor.getString("building_id") );

                    path = buildingName + " / " + String.valueOf(number)+"층";
                    helper.insertFloor(database,
                            currentFloorID,                // # id #
                            number,                          // # number #
                            currentBuildingID                // # building id #
                    );
                    publishProgress("progress", ++count + "");
//                    if (DEBUG) {
//                        Log.d(TAG, "parsingJSON(floor)["+rowID+"] id : " + currentFloorID +
//                                ", number : " + number);
//                    }

                    JSONArray rooms = floor.getJSONArray("room");
                    for (int roomIndex=0; roomIndex<rooms.length(); roomIndex++) {

                        JSONObject room = rooms.getJSONObject(roomIndex);
                        currentRoomID = Integer.parseInt( room.getString("id") );
                        roomName = room.getString("name");
                        description = room.getString("description");
                        description = description == null ? "" : description;
                        main = room.getString("main");
//                        currentFloorID = Integer.parseInt( building.getstring)

                        helper.insertRoom(database,
                                currentRoomID,                  // # id #
                                roomName,                         // # name
                                description,                             // # description #
                                path,                             // # path string #
                                currentFloorID,                   // # floor id #
                                currentBuildingID,                // # building id #
                                main.equals("1")                      // # is main room? #
                        );
                        publishProgress("progress", ++count + "");
//                        if (DEBUG) {
//                            Log.d(TAG, "parsingJSON(room)["+rowID+"] id : " + currentRoomID +
//                                    ", roomName : " + roomName +
//                                    ", description : " + description +
//                                    ", main : " + main +
//                                    ", roomName : " + roomName);
//                        }
                    }

//                    if (DEBUG) {
//                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            publishProgress(Progress_Dialog, json);
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
            Log.e(TAG, "parsingJSON: 실패");
            return -1;
        }

        if (DEBUG) {
            Log.d( TAG, "database insert building count : " + currentBuildingID
                    + ", floor count : " + currentFloorID
                    + ", insert room count : " + currentRoomID );
        }

        return version;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        if (values == null || values.length < 1) {
            return;
        }

        switch (values[0]) {
            case Progress_Progress:
                mDlg.setIndeterminate(false);
                mDlg.setProgress(Integer.parseInt(values[1]));
                break;
            case Progress_Dialog:
                mDlg.setMessage(values[1]);
                break;
//                    alertDialog.setTitle("JSON 결과");
//                    alertDialog.setMessage(message);
//                    alertDialog.setCanceledOnTouchOutside(true);
//                    alertDialog.show();
        }
    }

    @Override
    protected void onPostExecute(Integer version) {
        super.onPostExecute(version);
        Log.i(TAG, "-=##=- onPostExecute -=##=-");
        mDlg.dismiss();
        alertDialog.dismiss();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (DEBUG) Log.i(TAG, "onCancelled: called!!");
//        if (mConnection != null) mConnection.disconnect();
        mDlg.dismiss();
        alertDialog.dismiss();
    }

    @Override
    protected void onCancelled(Integer version) {
        super.onCancelled(version);
        if (DEBUG) Log.i(TAG, "onCancelled: called!! ("+version+")");
//        if (mConnection != null) mConnection.disconnect();
        mDlg.dismiss();
        alertDialog.dismiss();
    }

    private int getTotalCampusInfoTag(int xml_ID) {
        int max = 0;
        XmlResourceParser parser = context.getResources().getXml(xml_ID);
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

    private int getTotalCampusInfoJSONObject(final String json) {
        int count = 0;
        for (int i=0; i<json.length(); i++) {
            if (json.charAt(i) == '{') { // count json start object '{'
                count++;
            }
        }
        return count;
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        this.cancel(false);
    }
}