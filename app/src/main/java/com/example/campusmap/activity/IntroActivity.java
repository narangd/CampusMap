package com.example.campusmap.activity;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.campusmap.R;
import com.example.campusmap.asynctask.CampusInfoInsertAsyncTask;
import com.example.campusmap.database.SQLiteHelperCampusInfo;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class IntroActivity extends Activity {
    private static final String TAG = "IntroActivity";
    private static final boolean DEBUG = true;
    private TextView mTextStatus;
    private ProgressBar mProgressLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (DEBUG) Log.i(TAG, "onCreate: =============================================");

        // ## Layout ##
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        mTextStatus = (TextView) findViewById(R.id.text_status);
        mProgressLoading = (ProgressBar) findViewById(R.id.progress_loading);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // ## Database ##
        final SQLiteHelperCampusInfo sqLiteHelper = SQLiteHelperCampusInfo.getInstance(IntroActivity.this);
        final SQLiteDatabase db = sqLiteHelper.getReadableDatabase();

        final String BUILDING = SQLiteHelperCampusInfo.BuildingEntry.TABLE_NAME;
        final String FLOOR = SQLiteHelperCampusInfo.FloorEntry.TABLE_NAME;
        final String ROOM = SQLiteHelperCampusInfo.RoomEntry.TABLE_NAME;

       // ## Check Database ##
        mTextStatus.setText("데이터베이스 확인중...");
        if (sqLiteHelper.getTableSize(db, BUILDING) == getTagSize(BUILDING) &&
                sqLiteHelper.getTableSize(db, FLOOR) == getTagSize(FLOOR) &&
                sqLiteHelper.getTableSize(db, ROOM) == getTagSize(ROOM)) {
            db.close();

            waitAsyncTask(null);
        } else {
            sqLiteHelper.deleteTable(db, BUILDING);
            sqLiteHelper.deleteTable(db, FLOOR);
            sqLiteHelper.deleteTable(db, ROOM);
            db.close();

            waitAsyncTask(
                    new CampusInfoInsertAsyncTask(this).execute(R.xml.building_info)
            );
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
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }

                publishProgress();

                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                IntroActivity.this.finish();
                return null;
            }

            @Override
            protected void onProgressUpdate(Void... values) {
                super.onProgressUpdate(values);

                mTextStatus.setText("");
                mProgressLoading.setVisibility(View.INVISIBLE);
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Log.i(TAG, "onPostExecute: data insert done, wait done.");
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

    @Override
    public void onBackPressed() { /* do noting.. */ }

}
