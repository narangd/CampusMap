package com.example.campusmap.pathfinding.asynctask;

import android.os.AsyncTask;

/**
 * Created by 연구생 on 2015-11-25.
 */
public class PolygonAsynctask extends AsyncTask<Void, Void, Boolean> {


    PolygonAsynctask() {
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            // Simulate network access.
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            return false;
        }

        return true;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
    }
}
