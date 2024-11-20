package com.example.petplant;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.Arrays;
import java.util.List;

public class TutorialActivity extends AppCompatActivity {

    private ViewPager2 tutorialViewPager;
    private Button nextButton;
    private DotsIndicator dotsIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        // 뷰 초기화
        tutorialViewPager = findViewById(R.id.tutorialViewPager);
        nextButton = findViewById(R.id.nextButton);
        dotsIndicator = findViewById(R.id.dotsIndicator);

        // 튜토리얼 레이아웃 리스트
        List<Integer> tutorialLayouts = Arrays.asList(
                R.layout.tutorial_page1,
                R.layout.tutorial_page2,
                R.layout.tutorial_page3,
                R.layout.tutorial_page4,
                R.layout.tutorial_page5,
                R.layout.tutorial_page6,
                R.layout.tutorial_page7
        );

        // ViewPager2 어댑터 연결
        TutorialAdapter adapter = new TutorialAdapter(tutorialLayouts);
        tutorialViewPager.setAdapter(adapter);

        // DotsIndicator와 ViewPager2 연결
        dotsIndicator.setViewPager2(tutorialViewPager);

        // "다음" 버튼 동작 설정
        nextButton.setOnClickListener(v -> {
            int currentPage = tutorialViewPager.getCurrentItem();
            if (currentPage < tutorialLayouts.size() - 1) {
                // 다음 페이지로 이동
                tutorialViewPager.setCurrentItem(currentPage + 1);
            } else {
                // 완료: 메인 화면으로 이동
                startActivity(new Intent(TutorialActivity.this, HomeMainActivity.class));
                finish();
            }
        });
    }
}
