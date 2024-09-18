package com.example.petplant;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HomeMainActivity extends AppCompatActivity{

    private ViewPager2 viewPager;
    private PageAdapter pageAdapter;
    private ImageView character;
    private TextView speechBubble;
    private TextView timeTextView;
    private Handler handler = new Handler();
    private Runnable timeUpdater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homemain);

        character = findViewById(R.id.tomato_home);
        speechBubble = findViewById(R.id.speechbubble);
        viewPager = findViewById(R.id.viewPager);
        timeTextView = findViewById(R.id.timeTextView); // 새로운 텍스트뷰 (초 단위로 업데이트되는 텍스트)

        // 버튼 이벤트 핸들러들 (유지)
        Button inbox = findViewById(R.id.inbox);
        inbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), inbox_friend.class);
                startActivity(intent);
            }
        });

        Button profile = findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), profile.class);
                startActivity(intent);
            }
        });

        Button store = findViewById(R.id.store);
        store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ShopActivity.class);
                startActivity(intent);
            }
        });

        Button bell = findViewById(R.id.bell);
        bell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), alarm.class);
                startActivity(intent);
            }
        });

        Button guide = findViewById(R.id.guide);
        guide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Guide.class);
                startActivity(intent);
            }
        });

        // ViewPager 설정
        List<PageItem> pageItems = new ArrayList<>();
        pageItems.add(new PageItem("가꾸기 활동", "왕큰방울이에게 물주기", "+15C", "하러 가기 >", R.drawable.grow));
        pageItems.add(new PageItem("친해지기 활동", "왕큰방울이의 향 맡아보기", "+15C", "하러 가기 >", R.drawable.friendly));
        pageItems.add(new PageItem("더 보살피기 활동", "왕큰방울이에게 비료 주기", "+15C", "하러 가기 >", R.drawable.exceed));

        PageAdapter adapter = new PageAdapter(this, pageItems, new PageAdapter.OnItemClickListener() {
            @Override
            public void onButtonClick(int position) {
                Intent intent = null;

                switch (position) {
                    case 0:
                        // 첫 번째 페이지 버튼 클릭 시 이동할 액티비티
                        intent = new Intent(HomeMainActivity.this, Home_waterquestintroduce.class);
                        break;
                    case 1:
                        // 두 번째 페이지 버튼 클릭 시 이동할 액티비티
                        intent = new Intent(HomeMainActivity.this, Home_removequest_introduce.class);
                        break;
                    case 2:
                        // 세 번째 페이지 버튼 클릭 시 이동할 액티비티
                        intent = new Intent(HomeMainActivity.this, Home_smellquest.class);
                        break;
                    default:
                        break;
                }

                if (intent != null) {
                    startActivity(intent);

                }
            }
        });

        viewPager.setAdapter(adapter);

        // 캐릭터 클릭 시 말풍선 표시/숨기기
        character.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (speechBubble.getVisibility() == View.GONE) {
                    speechBubble.setVisibility(View.VISIBLE);
                } else {
                    speechBubble.setVisibility(View.GONE);
                }
            }
        });

        // 초 단위로 업데이트되는 텍스트 설정
        timeUpdater = new Runnable() {
            @Override
            public void run() {
                // 현재 시간 가져오기
                String currentTime = getCurrentTimeString();
                // 텍스트뷰에 시간 설정
                timeTextView.setText("" + currentTime);
                // 1초 후에 다시 실행
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(timeUpdater); // 시간 갱신 시작
    }

    // 현재 시간을 "HH:mm:ss" 형식으로 반환하는 메서드
    private String getCurrentTimeString() {
        SimpleDateFormat sdf = new SimpleDateFormat("ss", Locale.getDefault());
        return sdf.format(Calendar.getInstance().getTime());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(timeUpdater); // 액티비티가 파괴될 때 시간 갱신 중지
    }
}