package com.example.petplant;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class loadingactivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_profile);

        // 4초 후에 HomeMainActivity로 이동
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(loadingactivity.this, HomeMainActivity.class);
                intent.putExtras(getIntent().getExtras()); // 기존 인텐트의 데이터를 전달
                startActivity(intent);
                finish();
            }
        }, 3000);
    }
}
