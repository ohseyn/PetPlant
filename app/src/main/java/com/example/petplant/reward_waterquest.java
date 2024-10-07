package com.example.petplant;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class reward_waterquest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_waterquest);

        Button go_quiz_waterquest = findViewById(R.id.go_quiz_waterquest);
        go_quiz_waterquest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), water_oxquiz_start.class);
                startActivity(intent);
            }
        });

        Button complete_waterquest = findViewById(R.id.complete_waterquest);
        complete_waterquest.setOnClickListener(new View.OnClickListener() {
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
