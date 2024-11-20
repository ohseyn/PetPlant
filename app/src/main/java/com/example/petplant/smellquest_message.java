package com.example.petplant;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.GradientDrawable;
import android.media.ExifInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.IOException;

public class smellquest_message extends AppCompatActivity {
    private String photoPath;
    private Button smell_answer1, smell_answer2, next_button_smell;
    private TextView responseText_smell;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smellquest_message);

        // UI 요소 초기화
        smell_answer1 = findViewById(R.id.smell_answer1);
        smell_answer2 = findViewById(R.id.smell_answer2);
        next_button_smell = findViewById(R.id.next_button_smell);
        responseText_smell = findViewById(R.id.responseText_smell);

        // 버튼 배경 강제 설정
        smell_answer1.setBackground(ContextCompat.getDrawable(this, R.drawable.button_background_default));
        smell_answer2.setBackground(ContextCompat.getDrawable(this, R.drawable.button_background_default));

        // "대화마치기" 버튼 비활성화
        next_button_smell.setEnabled(false);

        // 사진 경로 가져오기
        photoPath = getIntent().getStringExtra("photoPath");
        if (photoPath != null) {
            ImageView imageView = findViewById(R.id.imageView);
            Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
            Bitmap rotatedBitmap = rotateImageIfRequired(bitmap, photoPath);
            imageView.setImageBitmap(rotatedBitmap);
        }

        // 디버깅: 버튼 배경 출력
        Log.d("smellquest_message", "smell_answer1 Background: " + smell_answer1.getBackground());
        Log.d("smellquest_message", "smell_answer2 Background: " + smell_answer2.getBackground());

        // 버튼 클릭 리스너 설정
        smell_answer1.setOnClickListener(v -> {
            selectButton(smell_answer1);
            showResponseText();
        });

        smell_answer2.setOnClickListener(v -> {
            selectButton(smell_answer2);
            showResponseText();
        });

        next_button_smell.setOnClickListener(this::onButtonClick);
    }

    // 버튼 선택 시 호출되는 메서드
    private void selectButton(Button selectedButton) {
        // 다른 버튼 초기화
        resetButtonBorder(smell_answer1);
        resetButtonBorder(smell_answer2);

        // 선택된 버튼에 테두리 색상 적용
        setButtonBorder(selectedButton, R.color.smell_color);

        // "대화마치기" 버튼 활성화
        next_button_smell.setEnabled(true);
    }

    // 버튼의 테두리 색상 변경
    private void setButtonBorder(Button button, int colorResId) {
        if (button.getBackground() instanceof GradientDrawable) {
            GradientDrawable drawable = (GradientDrawable) button.getBackground().mutate();
            drawable.setStroke(4, ContextCompat.getColor(this, colorResId));
        } else {
            // GradientDrawable이 아닌 경우 강제로 배경 설정
            Log.e("smellquest_message", "Button background is not a GradientDrawable, applying default.");
            button.setBackground(ContextCompat.getDrawable(this, R.drawable.button_background_selected));
        }
    }

    // 버튼 테두리 초기화
    private void resetButtonBorder(Button button) {
        if (button.getBackground() instanceof GradientDrawable) {
            GradientDrawable drawable = (GradientDrawable) button.getBackground().mutate();
            drawable.setStroke(0, ContextCompat.getColor(this, android.R.color.transparent));
        } else {
            // GradientDrawable이 아닌 경우 기본 배경 설정
            button.setBackground(ContextCompat.getDrawable(this, R.drawable.button_background_default));
        }
    }

    // "좋아요!" 메시지를 표시하는 메서드
    private void showResponseText() {
        responseText_smell.setVisibility(View.VISIBLE);
    }

    // 이미지 회전 메서드
    private Bitmap rotateImageIfRequired(Bitmap img, String photoPath) {
        ExifInterface ei;
        try {
            ei = new ExifInterface(photoPath);
        } catch (IOException e) {
            e.printStackTrace();
            return img;
        }

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    // 이미지 회전 적용 메서드
    private Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    // "대화마치기" 버튼 클릭 시 호출되는 메서드
    public void onButtonClick(View view) {
        if (next_button_smell.isEnabled()) {
            Intent intent = new Intent(this, reward_smellquest.class);
            intent.putExtra("photoPath", photoPath);
            startActivity(intent);
        }
    }
}
