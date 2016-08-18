package com.example.campusmap.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.example.campusmap.R;
import com.example.campusmap.asynctask.CampusInfoInsertAsyncTask;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
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

        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();

        if (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting()) {
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
                int sleep = 1000;

                try {
                    if (task != null) {
                        sleep = 1500;
                        task.get();
                    }
                } catch (CancellationException e) {
                    Log.e(TAG, "doInBackground: task Cancel!");
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Log.i(TAG, "onPostExecute: data insert done, wait done.");

                startActivity(
                        new Intent(IntroActivity.this, MainActivity.class)
                );
                finish();
            }
        }.execute();
    }

    public int getTagSize(final String tagName) {
        int count=0;
        XmlPullParser parser = getApplicationContext().getResources().getXml(R.xml.building_info);
        try {
            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }

                if (parser.getName().equals(tagName)) {
                    count++;
                }
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
        return count;
    }

//    @Override
//    public void onBackPressed() { /* do noting.. */ }

}
