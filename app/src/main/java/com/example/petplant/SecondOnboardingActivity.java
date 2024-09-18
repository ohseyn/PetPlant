package com.example.petplant;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class SecondOnboardingActivity extends AppCompatActivity {

    private LinearLayout pageIndicator;
    private Button nextButton;
    private EditText nameInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_onboarding);

        pageIndicator = findViewById(R.id.page_indicator);
        nextButton = findViewById(R.id.next_button);
        nameInput = findViewById(R.id.name_input);

        // 페이지 인디케이터 업데이트
        updatePageIndicator(1);  // 두 번째 페이지로 설정

        // 버튼 클릭 리스너
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 텍스트 입력 검증 (예: 빈 값이 아닌 경우)
                String inputText = nameInput.getText().toString();
                if (!inputText.trim().isEmpty()) {
                    startThirdOnboardingActivity();
                } else {
                    nameInput.setError("이름을 입력해 주세요.");
                }
            }
        });
    }

    // 페이지 인디케이터 업데이트
    private void updatePageIndicator(int position) {
        for (int i = 0; i < pageIndicator.getChildCount(); i++) {
            View indicator = pageIndicator.getChildAt(i);
            if (i == position) {
                indicator.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
            } else {
                indicator.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            }
        }
    }

    // 메인 화면으로 이동
    private void startThirdOnboardingActivity() {
        Intent intent = new Intent(SecondOnboardingActivity.this, ThirdOnboardingActivity.class);
        startActivity(intent);
    }
}