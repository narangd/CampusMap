package com.example.campusmap.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.example.campusmap.Internet;
import com.example.campusmap.R;
import com.example.campusmap.asynctask.CampusInfoInsertAsyncTask;
import com.example.campusmap.fragment.MenuPlannerFragment;

import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import io.fabric.sdk.android.Fabric;

public class IntroActivity extends Activity {
    private static final String TAG = "IntroActivity";
    private static final boolean DEBUG = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (DEBUG) Log.i(TAG, "onCreate: =============================================");

        // ## Layout ##
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_intro);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (DEBUG) Log.i(TAG, "onStart: DataBase Check");

        if (Internet.isInternetConnect(this)) {
            Log.i(TAG, "onStart: 인터넷에 연결되어 있습니다");

            waitAsyncTask(
                    new CampusInfoInsertAsyncTask(IntroActivity.this).execute(
                            "http://203.232.193.178/download/android/campusmap.php"
                    )
            );
        } else {
            Log.e(TAG, "onStart: 인터넷에 연결되어 있지 않습니다");
            waitAsyncTask( null );
        }
    }

    private void waitAsyncTask(final AsyncTask task) {
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(IntroActivity.this);
                    SharedPreferences.Editor editor = preferences.edit();

                    if (task != null) {
                        int version = (int) task.get();
                        String key = IntroActivity.this.getString(R.string.pref_key_db_version);
                        Log.i(TAG, "doInBackground: version : " + version);
                        if (version > preferences.getInt(key, 0)) {
                            editor.putInt(getString(R.string.pref_key_db_version), version);
                        }
                    }

                    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    editor.putString(getString(R.string.pref_key_app_version), pInfo.versionName);
                    editor.putBoolean(getString(R.string.pref_key_today_menu_planner),
                            !preferences.getString(getString(R.string.pref_key_last_skip_date), "-").equals(MenuPlannerFragment.TODAY_DATE)
                    );
                    if (preferences.getString(getString(R.string.pref_key_app_id), "").equals("")) {
                        editor.putString(
                                getString(R.string.pref_key_app_id),
                                UUID.randomUUID().toString()
                        );
                    }
                    editor.apply();

                } catch (CancellationException e) { // time out
                    Log.e(TAG, "doInBackground: task Cancel!");
                } catch (InterruptedException | ExecutionException | PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Log.i(TAG, "onPostExecute: data insert done, wait done.");

                Intent main = new Intent(IntroActivity.this, MainActivity.class);
                main.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(main);
                finish();
            }
        }.execute();
    }

    @Override
    public void onBackPressed() { /* do noting.. */ }

}
