package com.example.petplant;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private Button chooseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        viewPager = findViewById(R.id.viewPager);
        chooseButton = findViewById(R.id.choose_button);

        // 페이지 목록 설정
        List<OnboardingPage> pages = new ArrayList<>();
        pages.add(new OnboardingPage("식물의 특징", "방울토마토는 비타민이 풍부한\n영양 만점 열매채소입니다.\n생으로 먹기도 하고, 파스타나 샐러드 등\n다양한 요리 재료로 쓰기도 합니다.\n가꾸기 쉽고 건강하게 잘 자라기 때문에\n다양한 장소에서 가꿀 수 있습니다."));

        OnboardingPagerAdapter adapter = new OnboardingPagerAdapter(pages);
        viewPager.setAdapter(adapter);

        // 버튼 클릭 리스너: 마지막 페이지일 때만 두 번째 온보딩으로 이동
        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() < pages.size() - 1) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                } else {
                    startSecondOnboarding();  // 두 번째 온보딩으로 이동하는 메서드 호출
                }
            }
        });
    }

    // 두 번째 온보딩 화면으로 이동
    private void startSecondOnboarding() {
        Intent intent = new Intent(OnboardingActivity.this, SecondOnboardingActivity.class);
        startActivity(intent);
        finish();  // 현재 액티비티를 종료
    }

    // 페이지 정보 클래스
    private static class OnboardingPage {
        String title;
        String content;

        OnboardingPage(String title, String content) {
            this.title = title;
            this.content = content;
        }
    }

    // ViewPager 어댑터 클래스
    private class OnboardingPagerAdapter extends PagerAdapter {

        private List<OnboardingPage> pages;

        OnboardingPagerAdapter(List<OnboardingPage> pages) {
            this.pages = pages;
        }

        @Override
        public int getCount() {
            return pages.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(View container, int position) {
            View view = getLayoutInflater().inflate(R.layout.onboarding_page, null);

            TextView content = view.findViewById(R.id.changeablecontent);
            ImageView onboardingBack = view.findViewById(R.id.onboarding_back); // 여기서 onboarding_back을 참조

            OnboardingPage page = pages.get(position);
            content.setText(page.content);

            // "onboarding_back" 클릭 시 "선택하기" 버튼 활성화 및 색상 변경
            onboardingBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chooseButton.setEnabled(true);
                    chooseButton.setBackgroundColor(Color.parseColor("#02AE7A"));
                }
            });

            ((ViewPager) container).addView(view);
            return view;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
        }
    }
}
