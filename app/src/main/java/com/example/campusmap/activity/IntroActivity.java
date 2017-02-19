package com.example.campusmap.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.example.campusmap.Internet;
import com.example.campusmap.R;
import com.example.campusmap.asynctask.CampusInfoInsertAsyncTask;
import com.example.campusmap.data.server.RootJson;
import com.example.campusmap.database.SQLiteHelperObstacle;
import com.example.campusmap.fragment.MenuPlannerFragment;
import com.example.campusmap.util.Json;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.util.UUID;
import java.util.concurrent.CancellationException;

import io.fabric.sdk.android.Fabric;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IntroActivity extends Activity {
    private static final String TAG = "IntroActivity";
    private static final boolean DEBUG = false;

    private AsyncTask mAsyncTask;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // ## Layout ##
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        Fabric.with(this, new Crashlytics());

        log.info("start connect server");

        if (Internet.isInternetConnect(this)) {
            log.info("인터넷에 연결되어 있습니다");

//            runAsyncTask();
            runAsyncTaskTest();
        } else {
            log.info("인터넷에 연결되어 있지 않습니다");

            mHandler = new Handler();
            mHandler.postDelayed(startMainActivity, 1000);
        }

        // test
        
    }

    private void runAsyncTaskTest() {

        InputStream inputStream = getResources().openRawResource(R.raw.default_info);
        RootJson rootJson = Json.toClass(inputStream, RootJson.class);
        if (rootJson != null) {
            postExecute(rootJson.getVersion());
        }
        mHandler = new Handler();
        mHandler.postDelayed(startMainActivity, 500);

    }

    private void runAsyncTask() {
        try {
            mAsyncTask = new CampusInfoInsertAsyncTask(IntroActivity.this) {
                @Override
                protected Integer doInBackground(String... URLs) {
                    String json;
                    try {
                        json = Internet.connectHttpPage(
                                "http://203.232.193.178/android/obstacle/make.php",
                                Internet.CONNECTION_METHOD_GET,
                                null
                        );
                    } catch (SocketTimeoutException e) {
                        // TODO: 2017-02-09 서버가 응답이 없다면, 파일에서 읽지만, 강제로 하기에 버전을 비교해야 한다.
                        // 아니면 데이터베이스가 비엇을시에 시도.
                        InputStream inputStream = getResources().openRawResource(R.raw.default_obstacle);
                        json = Json.toClass(inputStream, String.class);
                        json = json == null ? "" : json;
//                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//                        char[] buffer = new char[1024];
//                        StringBuilder stringBuilder = new StringBuilder();
//                        try {
//                            while (inputStreamReader.read(buffer) > 0) {
//                                stringBuilder.append(buffer);
//                            }
//
//                            json = stringBuilder.toString();
//                        } catch (IOException e1) {
//                            json = "";
//                        }
                    }
                    Log.i(TAG, "json length : " + json.length());

                    SQLiteHelperObstacle helper = SQLiteHelperObstacle.getInstance(IntroActivity.this);
                    SQLiteDatabase database = helper.getWritableDatabase();
                    database.beginTransaction();
                    int obstacle_index = 0, entrance_index = 0;

                    helper.removeObstacle(database);
                    helper.removeEntrance(database);
                    try {
                        JSONObject jsonObject = new JSONObject(json);

                        JSONArray buildingArray = jsonObject.getJSONArray("building");
                        for (int bi=0; bi<buildingArray.length(); bi++) {
                            JSONObject building = buildingArray.getJSONObject(bi);

                            JSONArray obstacleArray = building.getJSONArray("obstacle");
                            for (int oi=0; oi<obstacleArray.length(); oi++) {
                                JSONObject obstacle = obstacleArray.getJSONObject(oi);
                                int number = obstacle.getInt("building_id");
                                double longitude = obstacle.getDouble("longitude");
                                double latitude = obstacle.getDouble("latitude");
                                helper.insertObstacle(
                                        database,
                                        ++obstacle_index,
                                        number,
                                        longitude,
                                        latitude
                                );

                            }

                            JSONArray entranceArray = building.getJSONArray("entrance");
                            for (int ei=0; ei<entranceArray.length(); ei++) {
                                JSONObject entrance = entranceArray.getJSONObject(ei);
                                int number = entrance.getInt("building_id");
                                double longitude = entrance.getDouble("longitude");
                                double latitude = entrance.getDouble("latitude");
                                helper.insertEntrance(
                                        database,
                                        ++entrance_index,
                                        number,
                                        longitude,
                                        latitude
                                );

                            }
                        }
                        database.setTransactionSuccessful();
                    } catch (JSONException e) {
                        Log.e(TAG, "doInBackground: JSONException(" + e.getMessage() + "), \"" + json + "\"");
                    } finally {
                        database.endTransaction();
                    }
                    Log.i(TAG, "doInBackground: obstacle insert " + obstacle_index);
                    Log.i(TAG, "doInBackground: obstacle insert " + entrance_index);

                    return super.doInBackground(URLs);
                }

                @Override
                protected void onPostExecute(Integer version) {
                    super.onPostExecute(version);

                    mAsyncTask = null;
                    postExecute(version);
                }

                @Override
                protected void onCancelled() {
                    super.onCancelled();

                    mHandler = new Handler();
                    mHandler.postDelayed(startMainActivity, 500);
                }

                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    super.onCancel(dialogInterface);
                }
            }.execute(
                    "http://203.232.193.178/download/android/campusmap.php"
            );
        } catch (CancellationException e) { // time out
            Log.e(TAG, "doInBackground: task Cancel!");
        }
    }

    private void postExecute(Integer version) {
        try {

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(IntroActivity.this);
            SharedPreferences.Editor editor = preferences.edit();

            String key = IntroActivity.this.getString(R.string.pref_key_db_version);
            Log.i(TAG, "doInBackground: version : " + version);
            if (version > preferences.getInt(key, 0)) {
                editor.putInt(getString(R.string.pref_key_db_version), version);
            }

            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            editor.putString(getString(R.string.pref_key_app_version), pInfo.versionName);
            editor.putBoolean(getString(R.string.pref_key_download_recent_app),
                    !preferences.getString(getString(R.string.pref_key_last_skip_date), "-").equals(MenuPlannerFragment.TODAY_DATE)
            );
            if (preferences.getString(getString(R.string.pref_key_app_id), "").equals("")) {
                editor.putString(
                        getString(R.string.pref_key_app_id),
                        UUID.randomUUID().toString()
                );
            }
            editor.apply();

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        mHandler = new Handler();
        mHandler.postDelayed(startMainActivity, 500);
    }

    private Runnable startMainActivity = new Runnable() {
        @Override
        public void run() {
            Intent main = new Intent(IntroActivity.this, MainActivity.class);
            main.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(main);
            finish();
        }
    };

    @Override
    public void onBackPressed() {
        if (mAsyncTask != null) {
            mAsyncTask.cancel(true);
        }
        if (mHandler != null) {
            mHandler.removeCallbacks(startMainActivity);
        }

        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (mAsyncTask != null) {
            mAsyncTask.cancel(true);
        }
        if (mHandler != null) {
            mHandler.removeCallbacks(startMainActivity);
        }
        super.onDestroy();
    }
}
