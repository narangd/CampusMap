package com.example.campusmap.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.example.campusmap.BuildConfig;
import com.example.campusmap.Internet;
import com.example.campusmap.R;
import com.example.campusmap.asynctask.VersionUpdateAsyncTask;
import com.example.campusmap.data.server.RootJson;
import com.example.campusmap.fragment.MenuPlannerFragment;
import com.example.campusmap.util.Json;

import java.io.InputStream;
import java.util.UUID;

import io.fabric.sdk.android.Fabric;

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

        Log.i(TAG, "onCreate: " + BuildConfig.BUILD_TYPE);
        Log.i(TAG, "onCreate: start connect server");

        if (Internet.isConnect(this)) {
            Log.i(TAG, "onCreate: 인터넷에 연결되어 있습니다");

//            runAsyncTask();
//            runAsyncTaskTest();

            mAsyncTask = new VersionUpdateAsyncTask(this) {
                @Override
                protected void onPostExecute(RootJson rootJson) {
                    super.onPostExecute(rootJson);
                    postExecute(rootJson);
                }
            }.execute();
        } else {
            Log.i(TAG, "onCreate: 인터넷에 연결되어 있지 않습니다");

            mHandler = new Handler();
            mHandler.postDelayed(startMainActivity, 1000);
        }

        // test
        
    }

    private void postExecute(RootJson rootJson) {
        try {

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(IntroActivity.this);
            SharedPreferences.Editor editor = preferences.edit();

            String key = IntroActivity.this.getString(R.string.pref_key_db_version);
            if (rootJson.getVersion() > preferences.getInt(key, 0)) {
                editor.putInt(getString(R.string.pref_key_db_version), rootJson.getVersion());
            }

            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            editor.putString(getString(R.string.pref_key_app_version), packageInfo.versionName);
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
