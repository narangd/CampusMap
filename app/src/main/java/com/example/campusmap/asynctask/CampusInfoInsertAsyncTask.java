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

import com.example.campusmap.Internet;
import com.example.campusmap.R;
import com.example.campusmap.database.SQLiteHelperCampusInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;

public class CampusInfoInsertAsyncTask extends AsyncTask<String, Integer, Integer> implements DialogInterface.OnCancelListener {
    private static final String TAG = "CampusInfoInsertAsync";
    private static final boolean DEBUG = true;
    private static final String ns = null;
    private static final int TIMEOUT = 1000 * 5;

    private SharedPreferences preferences;
    private int version;
    private Context mContext;
    private ProgressDialog mDlg;
    private SQLiteHelperCampusInfo helper;
    private String message = "";

    final AlertDialog alertDialog;

    public CampusInfoInsertAsyncTask(Context context) {
        mContext = context;
        helper = SQLiteHelperCampusInfo.getInstance(context);
        alertDialog = new AlertDialog.Builder(mContext).create();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.i(TAG, "-=##=- onPreExecute -=##=-");

        version = preferences.getInt( mContext.getString(R.string.pref_key_db_version), 0 );
        Log.i(TAG, "onPreExecute: preferences version : " + version);

        mDlg = new ProgressDialog(mContext);
        mDlg.setCancelable(false);
        mDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDlg.setTitle("데이터베이스를 구성중입니다");
        mDlg.setMessage("읽어오는 중입니다");
        mDlg.setIndeterminate(true);
        mDlg.setOnCancelListener(this);
        mDlg.show();
    }

    @Override
    protected Integer doInBackground(String... URLs) {
        if (DEBUG) Log.i(TAG, "doInBackground: called");
        if (URLs == null || URLs.length < 1) {
            Log.e(TAG, "doInBackground: parameter expected");
            return 0;
        }

        String BUILDING = SQLiteHelperCampusInfo.BuildingEntry.TABLE_NAME;
        String FLOOR = SQLiteHelperCampusInfo.FloorEntry.TABLE_NAME;
        String ROOM = SQLiteHelperCampusInfo.RoomEntry.TABLE_NAME;
        SQLiteDatabase database = helper.getWritableDatabase();
//        String result = bringJSON(URLs[0]);
        String result;
        try {
            HashMap<String,String> parameters = new HashMap<>();
            parameters.put("version", String.valueOf(version));
            result = Internet.connectHttpPage(
                    URLs[0],
                    Internet.CONNECTION_METHOD_GET,
                    parameters
            );
//            Log.i(TAG, "doInBackground: json length " + json.length() + " contents " + json);
        } catch (SocketTimeoutException e) {
            Log.e(TAG, "doInBackground: timeout ");
            result = null;
        }
        int version = 0;

        if (isCancelled()) {
            return null;
        }

        message = result;
        publishProgress(-2);
        if (result != null)
            Log.i(TAG, "doInBackground: result length : " + result.length());

        if (result != null && result.length() > 0 && !result.equals("newest")) {
            database.beginTransaction();

            message = "입력하는중입니다";
            publishProgress(-1);

            helper.deleteTable(database, BUILDING);
            helper.deleteTable(database, FLOOR);
            helper.deleteTable(database, ROOM);

            version = parsingJSON(result, helper, database);
            Log.i(TAG, "doInBackground: JSON version : " + version);

            database.setTransactionSuccessful();
            database.endTransaction();
        }
        database.close();

        return version;
    }

    private String bringJSON(String urlString) {
        StringBuilder builder = new StringBuilder();

        HttpURLConnection connection = null;
        try {
            long startMilli = System.currentTimeMillis();

            message ="서버에 접속하는 중입니다";

            urlString += "?version=" + version;
            if (DEBUG) Log.i(TAG, "bringJSON: URL : " + urlString);

            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Accept-Encoding", "identity");
            connection.setConnectTimeout( TIMEOUT );
            connection.setReadTimeout( TIMEOUT );
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setDoInput(true);

            OutputStream os = connection.getOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(os);
            writer.write("version=" + version);
            writer.flush();
            writer.close();

            if (DEBUG) {

                Log.d(TAG, "bringJSON: take Time : " + (System.currentTimeMillis() - startMilli) + "ms");
                Log.d(TAG, "bringJSON: Content Encoding : " + connection.getContentEncoding());
                Log.d(TAG, "bringJSON: Content Length : " + connection.getContentLength());

                Log.d(TAG, "bringJSON: Response Message : " + connection.getResponseMessage());
                Log.d(TAG, "bringJSON: Response Code : " + connection.getResponseCode());

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                cancel(true);
                Log.e(TAG, "bringJSON: i return null : " + connection.getResponseCode());
                return null;
            }

            String line;
            InputStream is = connection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader reader  = new BufferedReader(isr);

            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            reader.close();

        } catch (SocketTimeoutException e) {
            Log.e(TAG, "bringJSON: Time Out : " + e.getMessage());
            cancel(true);
            return null;
        } catch (IOException e) {
            Log.e(TAG, "bringJSON: local.. " + e.getLocalizedMessage());
//            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return builder.toString();
    }

    private void parsingXML(XmlResourceParser parser, SQLiteHelperCampusInfo helper, SQLiteDatabase database)
            throws IOException, XmlPullParserException {

        int count=0;
        int currentBuildingID=0, currentFloorID=0, currentRoomID=0;

        int number;
        String buildingName="", path="", roomName;
        String text, main;

        while ( parser.next() != XmlResourceParser.END_DOCUMENT ) {
            if ( parser.getEventType() != XmlResourceParser.START_TAG ) {
                continue;
            }

            switch ( parser.getName() ) {
                case "building":  // ## <building num="1" name="100주년 기념관"> ##
                    number = Integer.parseInt(parser.getAttributeValue(ns, "num"));
                    if (number == 0)
                        number = Integer.parseInt(parser.getAttributeValue(ns, "number"));
                    buildingName = parser.getAttributeValue(ns, "name");
                    helper.insertBuilding(database,
                            ++currentBuildingID,             // # id #
                            number,                          // # number #
                            buildingName,                    // # name #
                            ""                               // # description #
                    );
                    break;
                case "floor":     // ## <floor num="1"> ##
                    number = Integer.parseInt( parser.getAttributeValue(ns, "num") );
                    if (number == 0)
                        number = Integer.parseInt(parser.getAttributeValue(ns, "number"));
                    path = buildingName + " / " + String.valueOf(number)+"층";
                    helper.insertFloor(database,
                            ++currentFloorID,                // # id #
                            number,                          // # number #
                            currentBuildingID                // # building id #
                    );
                    break;
                case "room":      // ## <room name="방재센터"> ##
                    roomName = parser.getAttributeValue(ns, "name");
                    main = parser.getAttributeValue(ns, "main");
                    parser.require(XmlResourceParser.START_TAG, ns, "room");
                    parser.next();
                    text = parser.getText();
                    helper.insertRoom(database,
                            ++currentRoomID,                  // # id #
                            roomName,                         // # name
                            text,                             // # description #
                            path,                             // # path string #
                            currentFloorID,                   // # floor id #
                            currentBuildingID,                // # building id #
                            main != null                      // # is main room? #
                    );
                    break;
            }

            publishProgress(++count);
        }

        if (DEBUG) {
            Log.d( TAG, "database insert building count : " + currentBuildingID
                    + ", floor count : " + currentFloorID
                    + ", insert room count : " + currentRoomID );
        }
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
                publishProgress(++count);
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
                    publishProgress(++count);
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
                        publishProgress(++count);
//                        if (DEBUG) {
//                            Log.d(TAG, "parsingJSON(room)["+rowID+"] id : " + currentRoomID +
//                                    ", roomName : " + roomName +
//                                    ", description : " + description +
//                                    ", main : " + main +
//                                    ", roomName : " + roomName);
//                        }
                    }

//                    if (DEBUG) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
//                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            message = json;
            publishProgress(-2);
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
    protected void onProgressUpdate(Integer... values) {
        if (values == null || values.length < 1) {
            return;
        }

        if (values[0] >= 0) {
            mDlg.setIndeterminate(false);
            mDlg.setProgress(values[0]);
        } else {
            switch (-values[0]) {
                case 1:
                    mDlg.setMessage(message);
                    break;
                case 2:
//                    alertDialog.setTitle("JSON 결과");
//                    alertDialog.setMessage(message);
//                    alertDialog.setCanceledOnTouchOutside(true);
//                    alertDialog.show();
                    break;
            }
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