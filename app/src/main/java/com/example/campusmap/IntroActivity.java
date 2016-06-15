package com.example.campusmap;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

public class IntroActivity extends Activity implements Runnable {
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_intro);

        handler = new Handler();
        handler.postDelayed(this, 1000);
    }

    @Override
    public void run() {
        finish();
    }

    @Override
    public void onBackPressed() { /* none */ }
}
