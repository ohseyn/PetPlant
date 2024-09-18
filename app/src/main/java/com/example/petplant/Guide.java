package com.example.petplant;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

public class Guide extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TextView pageIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        viewPager = findViewById(R.id.view_pager);
        pageIndicator = findViewById(R.id.page_indicator);

        GuideSlidePagerAdapter pagerAdapter = new GuideSlidePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // 페이지 변경 리스너
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                pageIndicator.setText((position + 1) + " / 9");
            }
        });
    }
}
