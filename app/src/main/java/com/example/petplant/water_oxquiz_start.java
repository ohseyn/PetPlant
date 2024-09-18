package com.example.petplant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class water_oxquiz_start extends AppCompatActivity {

    private Button btnCheckAnswer;
    private Button selectedButton = null;

    // 예시로 정답을 "노란색"으로 설정
    private static final String CORRECT_ANSWER = "노란색";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_oxquiz_start);

        btnCheckAnswer = findViewById(R.id.btnCheckAnswer);

        Button btnYellow = findViewById(R.id.btnYellow);
        Button btnPink = findViewById(R.id.btnPink);
        Button btnRed = findViewById(R.id.btnRed);
        Button btnBlue = findViewById(R.id.btnBlue);

        View.OnClickListener optionClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedButton != null) {
                    selectedButton.setSelected(false); // 이전에 선택된 버튼의 선택 해제
                }
                selectedButton = (Button) v;
                selectedButton.setSelected(true); // 현재 선택된 버튼 선택
                btnCheckAnswer.setEnabled(true); // 정답 확인하기 버튼 활성화
            }
        };

        btnYellow.setOnClickListener(optionClickListener);
        btnPink.setOnClickListener(optionClickListener);
        btnRed.setOnClickListener(optionClickListener);
        btnBlue.setOnClickListener(optionClickListener);

        // 정답 확인하기 버튼 클릭 시 선택된 답에 따라 화면 전환
        btnCheckAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedButton != null) {
                    String selectedAnswer = selectedButton.getText().toString();
                    Intent intent;
                    if (selectedAnswer.equals(CORRECT_ANSWER)) {
                        intent = new Intent(water_oxquiz_start.this, CorrectActivity.class);
                    } else {
                        intent = new Intent(water_oxquiz_start.this, IncorrectActivity.class);
                    }
                    startActivity(intent);
                }
            }
        });
    }
}
