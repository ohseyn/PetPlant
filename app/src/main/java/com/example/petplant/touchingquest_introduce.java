package com.example.petplant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class touchingquest_introduce extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touchingquestintroduce);

        // 버튼 초기화 및 클릭 리스너 추가
        Button do_quest_touching = findViewById(R.id. do_quest_touching); // `activity_smellquestintroduce.xml`에 있는 버튼 ID 사용
        do_quest_touching.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 다음 화면으로 이동하는 Intent 생성
                Intent intent = new Intent(touchingquest_introduce.this, touching_text.class); // `NextActivity`는 이동할 대상 액티비티
                startActivity(intent);
            }
        });
    }
}
