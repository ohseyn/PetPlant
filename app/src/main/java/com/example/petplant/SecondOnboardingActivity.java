package com.example.petplant;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SecondOnboardingActivity extends AppCompatActivity {

    private LinearLayout pageIndicator;
    private Button nextButton;
    private EditText nameInput;

    // Firestore 인스턴스 선언
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_onboarding);

        // Firestore 초기화
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

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
                    // Firestore에 이름 저장
                    saveNameToFirestore(inputText);

                    // 다음 화면으로 이동
                    startThirdOnboardingActivity(inputText);
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

    // Firestore에 이름 저장
    private void saveNameToFirestore(String inputName) {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Firestore에 저장할 데이터 준비
            Map<String, Object> userData = new HashMap<>();
            userData.put("name", inputName);

            // Firestore에 데이터 저장
            db.collection("users").document(userId).set(userData)
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "사용자 이름이 성공적으로 저장되었습니다."))
                    .addOnFailureListener(e -> Log.w("Firestore", "사용자 이름 저장 실패", e));
        } else {
            Log.w("Firestore", "로그인된 사용자가 없습니다.");
        }
    }

    // 다음 화면으로 이동
    private void startThirdOnboardingActivity(String inputName) {
        Intent intent = new Intent(SecondOnboardingActivity.this, ThirdOnboardingActivity.class);
        intent.putExtra("name", inputName);
        startActivity(intent);
    }
}
