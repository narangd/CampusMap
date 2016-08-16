package com.example.campusmap.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.XmlResourceParser;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.example.campusmap.database.SQLiteHelperCampusInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

public class CampusInfoInsertAsyncTask extends AsyncTask<Integer, Integer, Boolean> {
    private static final String TAG = "CampusInfoInsertAsync";
    private static final boolean DEBUG = true;
    private static final String ns = null;
    private Context mContext;
    private ProgressDialog mDlg;
    private SQLiteHelperCampusInfo helper;
    private String message = "";

    public CampusInfoInsertAsyncTask(Context context) {
        mContext = context;
        helper = SQLiteHelperCampusInfo.getInstance(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.i(TAG, "-=##=- onPreExecute -=##=-");

        ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();

        if (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting()) {
            Log.i("연결됨" , "연결이 되었습니다.");

        } else {
            Log.i("연결 안 됨" , "연결이 다시 한번 확인해주세요");
            cancel(true);
        }

        mDlg = new ProgressDialog(mContext);
        mDlg.setCancelable(false);
        mDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDlg.setTitle("데이터베이스를 구성중입니다");
        mDlg.setMessage("읽어오는 중입니다");
        mDlg.setIndeterminate(true);
        mDlg.show();
    }

    @Override
    protected Boolean doInBackground(Integer... IDs) {
        if (DEBUG) Log.i(TAG, "doInBackground: called");
        if (IDs == null || IDs.length < 1) {
            return false;
        }

        String result = bringJSON("http://203.232.193.178/download/android/campusmap.php");
//        if (DEBUG) {
//            if (result != null) {
//                result = result.substring(0, result.length() > 200 ? 200 : result.length());
//                if (!result.equals("newest")) {
//                    parsingJSON();
//                }
//            }
//            Log.i(TAG, "doInBackground: result : " + result);
//        }
        if (isCancelled()) {
            return null;
        }

        if (DEBUG) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        publishProgress(0);

        SQLiteDatabase db = helper.getWritableDatabase();
        for (int ID : IDs) {
            mDlg.setMax( getTotalCampusInfoTag(ID) );
            XmlResourceParser parser = mContext.getResources().getXml(ID);

            db.beginTransaction();
            try {
                if (result != null && !result.equals("newest")) {
                    parsingJSON(result, helper, db);
                }

                parsingXML(parser, helper, db);

                db.setTransactionSuccessful();
            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
            }

            parser.close();
        }
        db.close();

        return true;
    }

    private String bringJSON(String urlString) {
        StringBuilder builder = new StringBuilder();

        HttpURLConnection connection = null;
        try {
            long startMilli = System.currentTimeMillis();

            if (DEBUG) Log.i(TAG, "bringJSON: URL : " + urlString);
            message ="서버에 접속하는 중입니다";
            publishProgress(-1);

            SharedPreferences preferences = mContext.getSharedPreferences("buildinginfo", 0);
            int version = preferences.getInt("version", 0);

            URL url = new URL(urlString + "?version=" + version);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(1000 * 15 /* 15초 */);
            connection.setReadTimeout(1000 * 10 /* 10초 */);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            int contentLength = connection.getContentLength();

            mDlg.setMax(contentLength);

            if (DEBUG) {

                Log.i(TAG, "bringJSON: take Time : " + (System.currentTimeMillis() - startMilli) + "ms");
                Log.i(TAG, "bringJSON: Content Encoding : " + connection.getContentEncoding());
                Log.i(TAG, "bringJSON: Content Length : " + contentLength);

                Log.i(TAG, "bringJSON: Response Message : " + connection.getResponseMessage());
                Log.i(TAG, "bringJSON: Response Code : " + connection.getResponseCode());

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            message ="변환중입니다";
            publishProgress(-1);

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                cancel(true);
                return null;
            }

            char[] buffer = new char[1024];
            InputStream in = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(in, "UTF-8");
//            BufferedReader bufferedReader = new BufferedReader(reader);

            long currentMilli = System.currentTimeMillis();
            while (reader.read(buffer) > 0) {
                builder.append(buffer);
                publishProgress(builder.length());

                if (DEBUG) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (System.currentTimeMillis() > currentMilli + 1000) {
                    currentMilli = System.currentTimeMillis();
                    if (DEBUG) Log.i(TAG, "bringJSON: current Length : " + builder.length());
                }
            }
            if (DEBUG) Log.i(TAG, "bringJSON: builder Length : " + builder.length());

            reader.close();

        } catch (SocketTimeoutException e) {
            cancel(true);
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return builder.toString();
    }

    private void parsingXML(XmlResourceParser parser, SQLiteHelperCampusInfo helper, SQLiteDatabase db)
            throws IOException, XmlPullParserException {

        int count=0;
        int currentBuildingID=0, currentFloorID=0, currentRoomID=0;

        int number;
        String buildingName="", path="", roomName;
        String text, main;

        while (parser.next() != XmlResourceParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlResourceParser.START_TAG) {
                continue;
            }

            switch (parser.getName()) {
                case "building":  // ## <building num="1" name="100주년 기념관"> ##
                    number = Integer.parseInt(parser.getAttributeValue(ns, "num"));
                    if (number == 0)
                        number = Integer.parseInt(parser.getAttributeValue(ns, "number"));
                    buildingName = parser.getAttributeValue(ns, "name");
                    helper.insertBuilding(db,
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
                    helper.insertFloor(db,
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
                    helper.insertRoom(db,
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

            if (DEBUG) {
                try {
                    Thread.sleep(0, 500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }

            publishProgress(++count);
        }

        if (DEBUG) {
            Log.i( TAG, "database insert building count : " + (currentBuildingID-1)
                    + ", floor count : " + (currentFloorID-1)
                    + ", insert room count : " + (currentRoomID-1) );
        }
    }

    private void parsingJSON(String json, SQLiteHelperCampusInfo helper, SQLiteDatabase db) {
        int count=0;
        int currentBuildingID=0, currentFloorID=0, currentRoomID=0;

        int number;
        String name="", path="";
        String description, main;

        try {
            JSONObject object = new JSONObject(json);
            int version = object.getInt("version");

            SharedPreferences preferences = mContext.getSharedPreferences("buildinginfo", 0);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("version", version);
            editor.apply();

            JSONArray buildings = object.getJSONArray("building");
            for (int buildingIndex=0; buildingIndex<buildings.length(); buildingIndex++) {

                JSONObject building = buildings.getJSONObject(buildingIndex);
                number = Integer.parseInt( building.getString("number") );
                name = building.getString("name");
                description = building.getString("description");

                if (DEBUG) {
                    Log.i(TAG, "parsingJSON: id : " + ++currentBuildingID +
                            ", number : " + number +
                            ", name : " + name +
                            ", description : " + description);
                }

                JSONArray floors = building.getJSONArray("floor");
                for (int floorIndex=0; floorIndex<floors.length(); floorIndex++) {

                    JSONObject floor = floors.getJSONObject(floorIndex);
                    number = Integer.parseInt( floor.getString("number") );
                }
//                helper.insertBuilding(db,
//                        ++currentBuildingID,    // # id #
//                        number,                 // # number #
//                        name,                   // # name #
//                        description             // # description #
//                );
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (values == null || values.length < 1) {
            return;
        }

        if (values[0] < 0) {
            mDlg.setMessage(message);
        } else {
            mDlg.setIndeterminate(false);
            mDlg.setProgress(values[0]);
        }

    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        Log.i(TAG, "-=##=- onPostExecute -=##=-");
        mDlg.dismiss();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (DEBUG) Log.i(TAG, "onCancelled: called!!");
        mDlg.dismiss();
    }

    @Override
    protected void onCancelled(Boolean aBoolean) {
        super.onCancelled(aBoolean);
        if (DEBUG) Log.i(TAG, "onCancelled: called!! ("+aBoolean+")");
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