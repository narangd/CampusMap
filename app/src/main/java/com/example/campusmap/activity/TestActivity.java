package com.example.campusmap.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.campusmap.R;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        int b_num = getIntent().getExtras().getInt("building");
        TextView textView = (TextView) findViewById(R.id.testTextView11);
        textView.setText(Html.fromHtml("test<br><a href='http://www.naver.com/'>NAVER</a>endtest"));
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        // Html.fromHtml("test<br><a href='http://www.daum.com/'>DAUM</a>endtest")

        ImageView imageView =(ImageView)findViewById(R.id.image);

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
