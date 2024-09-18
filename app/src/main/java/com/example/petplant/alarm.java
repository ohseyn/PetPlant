package com.example.petplant;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class alarm extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarmmain);

        // 첫 번째 알림에 대한 버튼과 레이아웃 참조
        Button confirmButton1 = findViewById(R.id.confirm_button_1);
        Button rejectButton1 = findViewById(R.id.reject_button_1);
        final LinearLayout notificationItem1 = findViewById(R.id.notification_item_1);

        // 확인 버튼 클릭 시 알림 숨김
        confirmButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationItem1.setVisibility(View.GONE);
            }
        });

        // 거절 버튼 클릭 시 알림 숨김
        rejectButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationItem1.setVisibility(View.GONE);
            }
        });

        // 두 번째 알림도 동일하게 설정 가능
        Button confirmButton2 = findViewById(R.id.confirm_button_2);
        Button rejectButton2 = findViewById(R.id.reject_button_2);
        final LinearLayout notificationItem2 = findViewById(R.id.notification_item_2);

        confirmButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationItem2.setVisibility(View.GONE);
            }
        });

        rejectButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationItem2.setVisibility(View.GONE);
            }
        });
    }
}
