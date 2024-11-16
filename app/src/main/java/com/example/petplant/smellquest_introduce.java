package com.example.petplant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class smellquest_introduce extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smellquestintroduce);

        // 버튼 초기화 및 클릭 리스너 추가
        Button do_quest_smell = findViewById(R.id. do_quest_smell); // `activity_smellquestintroduce.xml`에 있는 버튼 ID 사용
        do_quest_smell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 다음 화면으로 이동하는 Intent 생성
                Intent intent = new Intent(smellquest_introduce.this, smellquest_text.class); // `NextActivity`는 이동할 대상 액티비티
                startActivity(intent);
            }
        });
    }
}
