package com.example.petplant;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class inbox_friend extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox_friend);

        // Profile 화면으로 이동하는 버튼
        Button go_profile = findViewById(R.id.go_profile);
        go_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), profile.class);
                startActivity(intent);
            }
        });

        // Home 화면으로 이동하는 버튼
        Button go_home = findViewById(R.id.go_home);
        go_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomeMainActivity.class);
                startActivity(intent);
            }
        });

        // Dialog를 열기 위한 버튼
        View openDialog = findViewById(R.id.open_dialog);
        openDialog.setOnClickListener(v -> showDialog());
    }

    // Dialog를 표시하는 메서드
    private void showDialog() {
        Dialog dialog = new Dialog(inbox_friend.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀바 제거
        dialog.setContentView(R.layout.dialog_layout);

        // Dialog 배경을 투명하게 설정
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // 닫기 버튼 설정
        ImageButton closeButton = dialog.findViewById(R.id.close_button);
        closeButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show(); // Dialog 띄우기
    }
}
