package com.example.petplant;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class water_oxquiz_start extends AppCompatActivity {

    private Button btnCheckAnswer;
    private Button selectedButton = null;
    private Button correctButton = null; // 정답 버튼 추적
    private TextView questionTextView;   // 질문 텍스트뷰

    // 정답 및 텍스트 설정
    private static final String CORRECT_ANSWER = "흠뻑 줘야 한다";
    private static final String REWARD_TEXT = "보상받기";

    // 색상 값
    private static final String SELECTED_COLOR = "#F4FCF4"; // 정답 버튼 배경색
    private static final String BORDER_COLOR = "#46C140";   // 정답 버튼 테두리 색상
    private static final String WRONG_BACKGROUND_COLOR = "#FEECEA"; // 오답 버튼 배경색
    private static final String WRONG_BORDER_COLOR = "#FF453C";      // 오답 버튼 테두리 색상

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_oxquiz_start);

        // TextView와 버튼 초기화
        questionTextView = findViewById(R.id.questionTextView);
        btnCheckAnswer = findViewById(R.id.btnCheckAnswer);
        btnCheckAnswer.setEnabled(false);  // 초기 비활성화

        Button btn1 = findViewById(R.id.btn1);
        Button btn2 = findViewById(R.id.btn2);

        // 정답 버튼 설정
        if (btn1.getText().toString().equals(CORRECT_ANSWER)) {
            correctButton = btn1;
        } else if (btn2.getText().toString().equals(CORRECT_ANSWER)) {
            correctButton = btn2;
        }

        // 버튼 클릭 리스너 설정
        View.OnClickListener optionClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedButton != null) {
                    resetButtonStyle(selectedButton);  // 이전 선택 초기화
                }
                selectedButton = (Button) v;
                setButtonStyle(selectedButton);  // 선택된 버튼 스타일 적용
                btnCheckAnswer.setEnabled(true);  // 정답 확인 버튼 활성화
            }
        };

        // 각 버튼에 리스너 연결
        btn1.setOnClickListener(optionClickListener);
        btn2.setOnClickListener(optionClickListener);

        // 정답 확인 버튼 클릭 시 동작
        btnCheckAnswer.setOnClickListener(new View.OnClickListener() {
            private boolean isFirstClick = true;

            @Override
            public void onClick(View v) {
                if (isFirstClick) {
                    if (!selectedButton.getText().toString().equals(CORRECT_ANSWER)) {
                        setWrongButtonStyle(selectedButton);
                        addCheckMark(selectedButton, false); // 오답일 경우 빨간 체크 아이콘 추가
                        setCorrectButtonStyle(correctButton);
                        addCheckMark(correctButton, true); // 정답일 경우 녹색 체크 아이콘 추가
                    } else {
                        addCheckMark(selectedButton, true); // 정답 선택 시 녹색 체크 아이콘 추가
                    }

                    // 질문 텍스트 변경
                    questionTextView.setText("방울토마토에게는 물을, \n \"흠뻑\" 주어야 해요.");
                    btnCheckAnswer.setText(REWARD_TEXT);  // 버튼 텍스트 변경
                    isFirstClick = false;
                } else {
                    // 다음 화면으로 이동
                    Intent intent;
                    if (selectedButton.getText().toString().equals(CORRECT_ANSWER)) {
                        intent = new Intent(water_oxquiz_start.this, water_quiz_CorrectActivity.class);
                    } else {
                        intent = new Intent(water_oxquiz_start.this, water_quiz_IncorrectActivity.class);
                    }
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        });
    }

    // 버튼 스타일 설정 (둥근 모서리 유지)
    private void setButtonStyle(Button button) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.parseColor(SELECTED_COLOR));
        drawable.setStroke(5, Color.parseColor(BORDER_COLOR));
        drawable.setCornerRadius(60f);
        button.setBackground(drawable);
    }

    // 정답 버튼 스타일 설정
    private void setCorrectButtonStyle(Button button) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.parseColor(SELECTED_COLOR));
        drawable.setStroke(5, Color.parseColor(BORDER_COLOR));
        drawable.setCornerRadius(60f);
        button.setBackground(drawable);
    }

    // 오답 버튼 스타일 설정
    private void setWrongButtonStyle(Button button) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.parseColor(WRONG_BACKGROUND_COLOR));
        drawable.setStroke(5, Color.parseColor(WRONG_BORDER_COLOR));
        drawable.setCornerRadius(60f);
        button.setBackground(drawable);
    }

    // 버튼 스타일 초기화
    private void resetButtonStyle(Button button) {
        button.setBackgroundResource(R.drawable.button_quiz);
        button.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    }

    // 체크 아이콘 추가
    private void addCheckMark(Button button, boolean isCorrect) {
        Drawable checkMark;
        if (isCorrect) {
            checkMark = ContextCompat.getDrawable(this, R.mipmap.ic_check_green);
        } else {
            checkMark = ContextCompat.getDrawable(this, R.mipmap.ic_check_red);
        }
        InsetDrawable insetDrawable = new InsetDrawable(checkMark, 30, 0, 0, 0);
        button.setCompoundDrawablesWithIntrinsicBounds(insetDrawable, null, null, null);
    }
}
