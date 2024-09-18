package com.example.petplant;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;

public class ThirdOnboardingActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private ImageView profileImage;
    private EditText nameInput;
    private Button editButton, startButton;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_onboarding);

        profileImage = findViewById(R.id.profile_image);
        nameInput = findViewById(R.id.name_input);
        editButton = findViewById(R.id.edit_button);
        startButton = findViewById(R.id.start_button);

        // 페이지 인디케이터 업데이트
        updatePageIndicator(2);  // 세 번째 페이지로 설정

        // '편집' 버튼 클릭 리스너
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        // '시작하기' 버튼 클릭 리스너
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMainActivity(); // 원하는 액티비티로 이동
            }
        });
    }

    // 페이지 인디케이터 업데이트
    private void updatePageIndicator(int position) {
        LinearLayout pageIndicator = findViewById(R.id.page_indicator);
        for (int i = 0; i < pageIndicator.getChildCount(); i++) {
            View indicator = pageIndicator.getChildAt(i);
            if (i == position) {
                indicator.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
            } else {
                indicator.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            }
        }
    }

    // 갤러리 열기
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                profileImage.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 메인 화면으로 이동
    private void startMainActivity() {
        Intent intent = new Intent(ThirdOnboardingActivity.this, HomeMainActivity.class);
        startActivity(intent);
        finish();
    }
}