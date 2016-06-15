package com.example.campusmap.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.campusmap.R;

public class BuildingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building);

        int b_num = getIntent().getExtras().getInt("index");
        TextView textView=(TextView)findViewById(R.id.testTextView11);
        textView.setText(b_num + " : data sended");

        ImageView imageView =(ImageView)findViewById(R.id.imageView);

        Context context = imageView.getContext();
        int id = context.getResources().getIdentifier("building_" + (b_num + 1), "drawable", context.getPackageName());

        Log.d("id", id + "");
        if(id == 0) {
            imageView.setImageResource(R.drawable.image_not_found);
            textView.setText("이미지를 찾을 수 없습니다.");
        } else {
            imageView.setImageResource(id);
        }


    }

}
