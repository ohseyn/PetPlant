package com.example.petplant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private LinearLayout pageIndicator;
    private Button chooseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        viewPager = findViewById(R.id.viewPager);
        pageIndicator = findViewById(R.id.page_indicator);
        chooseButton = findViewById(R.id.choose_button);

        // 페이지 목록 설정
        List<OnboardingPage> pages = new ArrayList<>();
        pages.add(new OnboardingPage("식물의 특징", "방울토마토는 비타민이 풍부한\n영양 만점 열매채소입니다.\n생으로 먹기도 하고, 파스타나 샐러드 등\n다양한 요리 재료로 쓰기도 합니다.\n가꾸기 쉽고 건강하게 잘 자라기 때문에\n다양한 장소에서 가꿀 수 있습니다."));

        OnboardingPagerAdapter adapter = new OnboardingPagerAdapter(pages);
        viewPager.setAdapter(adapter);

        // 페이지 변화 리스너
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                // 인디케이터는 고정되며 슬라이드 시 변경되지 않음
                if (position == pages.size() - 1) {
                    chooseButton.setText("선택하기");
                } else {
                    chooseButton.setText("선택하기");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        // 버튼 클릭 리스너
        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() < pages.size() - 1) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                } else {
                    startSecondOnboarding();
                }
            }
        });

        // 초기 페이지 인디케이터 설정 (슬라이드 시 변경되지 않음)
        updatePageIndicator(0);
    }

    // 페이지 인디케이터 업데이트
    private void updatePageIndicator(int position) {
        // 인디케이터는 고정되어 있으며, 슬라이드할 때 변경되지 않음
        for (int i = 0; i < pageIndicator.getChildCount(); i++) {
            View indicator = pageIndicator.getChildAt(i);
            if (i == position) {
                indicator.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
            } else {
                indicator.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            }
        }
    }

    // 두 번째 온보딩 화면으로 이동
    private void startSecondOnboarding() {
        Intent intent = new Intent(OnboardingActivity.this, SecondOnboardingActivity.class);
        startActivity(intent);
        finish();
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

            OnboardingPage page = pages.get(position);
            content.setText(page.content);

            ((ViewPager) container).addView(view);
            return view;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
        }
    }
}