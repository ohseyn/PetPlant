package com.example.petplant;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class reward_quiz extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_quiz);
        // 노치 처리 코드
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = getWindow().getDecorView();
            // 레이아웃이 상태바와 겹치도록 설정 (상태바 투명)
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            // 상태바 색상 설정 (투명)
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        Button go_quiz = findViewById(R.id.go_quiz);
        go_quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), water_oxquiz_start.class);
                startActivity(intent);
            }
        });

        Button complete = findViewById(R.id.complete);
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 완료 버튼을 누르면 HomeMainActivity로 돌아가면서 완료 상태를 전달
                Intent intent = new Intent(getApplicationContext(), HomeMainActivity.class);
                intent.putExtra("completed", true); // 완료 상태 전달
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}
