package com.example.campusmap;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.example.campusmap.database.SQLiteHelperCampusInfo;

import java.util.concurrent.ExecutionException;

public class IntroActivity extends Activity {
    private static final String TAG = "IntroActivity";
    private static final boolean DEBUG = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (DEBUG) Log.i(TAG, "onCreate: =============================================");

        // ## Database ##
        final SQLiteHelperCampusInfo sqLiteHelper = SQLiteHelperCampusInfo.getInstance(IntroActivity.this);
        sqLiteHelper.getReadableDatabase();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                try {
                    while (!sqLiteHelper.isInsertFinished()) {
                        Thread.sleep(500);
                    }
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "run: done wait..");

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finish();

//                handler.sendEmptyMessage(0);
            }
        });

        // ## Laytout ##
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
    }

    @Override
    public void onBackPressed() { /* do noting.. */ }

}
