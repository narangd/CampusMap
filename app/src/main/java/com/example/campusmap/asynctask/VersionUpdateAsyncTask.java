package com.example.campusmap.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.example.campusmap.R;
import com.example.campusmap.activity.MainActivity;
import com.example.campusmap.data.server.BuildingJson;
import com.example.campusmap.data.server.FloorJson;
import com.example.campusmap.data.server.RoomJson;
import com.example.campusmap.data.server.RootJson;
import com.example.campusmap.database.SQLiteHelperCampusInfo;
import com.example.campusmap.server.ServerClient;
import com.example.campusmap.util.Json;
import com.example.campusmap.util.ResourceUtl;

public class VersionUpdateAsyncTask extends AsyncTask<Void, String, RootJson> implements DialogInterface.OnCancelListener {
    private static final String TAG = "VersionUpdateAsyncTask";
    private static final boolean DEBUG = true;

    private SharedPreferences preferences;
    private int version;
    private Context context;
    private ProgressDialog mDlg;
    private SQLiteHelperCampusInfo helper;

    private static final String Progress_Progress = "progress";
    private static final String Progress_Dialog = "dialog";
    private static final String Progress_JobDone = "job_done";

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
        Log.i(TAG, "onPreExecute: mobile version : " + version);

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
    protected RootJson doInBackground(Void... voids) {
        if (DEBUG) Log.i(TAG, "doInBackground: called");

        publishProgress(Progress_Dialog, "읽어오는 중입니다");

        RootJson rootJson = ServerClient.versions(version);

        if (rootJson == null) {
            return new RootJson();
        } else if (version >= rootJson.getVersion()) {
            publishProgress(Progress_JobDone, "최신버전입니다");

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return rootJson;
        } else {
            String default_json = ResourceUtl.getRaw(context, R.raw.default_info);
            rootJson = Json.to(default_json, RootJson.class);
            if (rootJson == null) {
                rootJson = new RootJson();
            }
        }

        publishProgress(Progress_Dialog, "입력하는중입니다");

        mDlg.setMax(getTotalCampusInfoJSONObject(rootJson));

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
        return rootJson;
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
            case Progress_JobDone:
                mDlg.setIndeterminate(false);
            case Progress_Dialog:
                mDlg.setIndeterminate(true);
                mDlg.setMessage(values[1]);
                break;
//                    alertDialog.setTitle("JSON 결과");
//                    alertDialog.setMessage(message);
//                    alertDialog.setCanceledOnTouchOutside(true);
//                    alertDialog.show();
        }

        mDlg.show();
    }

    @Override
    protected void onPostExecute(RootJson rootJson) {
        super.onPostExecute(rootJson);
        Log.i(TAG, "-=##=- onPostExecute -=##=-");

        if (rootJson != null && version >= rootJson.getVersion()) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(context.getString(R.string.pref_key_campus_name), rootJson.getName());
            editor.apply();
        }

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
    protected void onCancelled(RootJson rootJson) {
        super.onCancelled(rootJson);
        if (DEBUG) Log.i(TAG, "onCancelled: called!! ("+version+")");
//        if (mConnection != null) mConnection.disconnect();
        mDlg.dismiss();
        alertDialog.dismiss();
    }

    private int getTotalCampusInfoJSONObject(RootJson rootJson) {
        int count = 0;

        for (BuildingJson buildingJson : rootJson.getBuilding()) {
            for (FloorJson floorJson : buildingJson.getFloor()) {
                for (RoomJson roomJson : floorJson.getRoom()) {
                    ++count;
                }
            }
        }
        return count;
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        this.cancel(false);
    }
}