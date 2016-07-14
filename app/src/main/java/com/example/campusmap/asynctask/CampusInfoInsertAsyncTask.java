package com.example.campusmap.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.example.campusmap.database.SQLiteHelperCampusInfo;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by DBLAB on 2016-06-27.
 */
public class CampusInfoInsertAsyncTask extends AsyncTask<Integer, Integer, Boolean> {
    private static final String TAG = "CampusInfoInsertAsync";
    private static final boolean DEBUG = true;
    private static final String ns = null;
    private Context mContext;
    private ProgressDialog mDlg;
    private SQLiteHelperCampusInfo helper;

    public CampusInfoInsertAsyncTask(Context context) {
        mContext = context;
        helper = SQLiteHelperCampusInfo.getInstance(context);
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
        int currentBuildingID=0, currentFloorID=0, currentRoomID=0;

        // # Calculate Max #
        for (int ID : IDs) {
            count += getTotalCampusInfoTag(ID);
        }
        mDlg.setMax(count);

        count = 0;
        SQLiteDatabase db = helper.getWritableDatabase();
        for (int ID : IDs) {
            XmlResourceParser parser = mContext.getResources().getXml(ID);
            int number;
            String buildingName="", floorName="", roomName;
            String text, main;

            db.beginTransaction();
            try {
                while (parser.next() != XmlResourceParser.END_DOCUMENT) {
                    if (parser.getEventType() != XmlResourceParser.START_TAG) {
                        continue;
                    }

                    switch (parser.getName()) {
                        case "building":  // ## <building num="1" name="100주년 기념관"> ##
                            number = Integer.parseInt(parser.getAttributeValue(ns, "num"));
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
                            floorName = String.valueOf(number)+"층";
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
                                    buildingName + " / " + floorName, // # path string #
                                    currentFloorID,                   // # floor id #
                                    currentBuildingID,                // # building id #
                                    main != null                      // # is main room? #
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

    public boolean isCompleted() {
        Boolean isCompleted = null;

        if (isCancelled()) {
            return true;
        }

        try { // 여기서 cancellationexception이 일어난다.
            isCompleted = get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        if (isCompleted == null) {
            return true;
        } else {
            return isCompleted;
        }
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