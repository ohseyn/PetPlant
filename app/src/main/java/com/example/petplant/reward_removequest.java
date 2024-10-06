package com.example.petplant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class reward_removequest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_removequest);

        Button go_qiuz2 = findViewById(R.id.go_quiz2);
        go_qiuz2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), remove_oxquiz_start.class);
                startActivity(intent);
            }
        });

        Button complete2 = findViewById(R.id.complete2);
        complete2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 두 번째 퀘스트 완료 상태를 HomeMainActivity에 전달
                Intent intent = new Intent(getApplicationContext(), HomeMainActivity.class);
                intent.putExtra("completed2", true);  // 두 번째 퀘스트 완료 전달
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}
