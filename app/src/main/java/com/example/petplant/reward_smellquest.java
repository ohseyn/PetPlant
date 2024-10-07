package com.example.petplant;

import static com.example.petplant.R.layout.activity_reward_smellquest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class reward_smellquest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_reward_smellquest);


        Button complete_smell = (Button) findViewById(R.id.complete_smell);
        complete_smell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomeMainActivity.class);
                intent.putExtra("completed3", true);  // 두 번째 퀘스트 완료 상태 전달
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

}