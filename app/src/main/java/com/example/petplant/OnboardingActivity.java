package com.example.petplant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class OnboardingActivity extends AppCompatActivity {

    private Button chooseButton;
    private ImageButton back;
    private ImageView onboardingBack;
    private boolean isSelected = false; // 초기 상태는 선택되지 않음

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        chooseButton = findViewById(R.id.choose_button);
        chooseButton.setEnabled(false); // 초기 상태는 비활성화

        back = findViewById(R.id.back);
        onboardingBack = findViewById(R.id.onboarding_back);

        // '뒤로가기' 버튼 이벤트 설정
        back.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), Login.class)));

        // onboarding_back 클릭 시 choose_button 활성화
        onboardingBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSelected) {
                    // 선택 취소: 기본 배경으로 복원하고 버튼 비활성화
                    onboardingBack.setBackgroundResource(R.drawable.onboardingrectangle);
                    chooseButton.setEnabled(false);
                    chooseButton.setAlpha(0.5f); // 투명도 변경
                } else {
                    // 선택: 초록색 테두리를 추가하고 버튼 활성화
                    onboardingBack.setBackgroundResource(R.drawable.onboarding_back_selected);
                    chooseButton.setEnabled(true);
                    chooseButton.setAlpha(1.0f); // 투명도 변경
                }
                // 선택 상태를 토글
                isSelected = !isSelected;
//                // 선택된 상태를 나타내기 위해 Drawable 변경
//                onboardingBack.setBackgroundResource(R.drawable.onboarding_back_selected);
//                chooseButton.setEnabled(true);
//                chooseButton.setBackgroundColor(getResources().getColor(R.color.selected_tab_text_color)); // 필요 시 색상 변경
            }
        });

        // choose_button 클릭 시 다음 페이지로 이동
        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chooseButton.isEnabled()) {
                    // 다음 액티비티로 이동
                    Intent intent = new Intent(OnboardingActivity.this, SecondOnboardingActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            }
        });
    }
}
