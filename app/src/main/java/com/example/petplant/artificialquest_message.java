package com.example.petplant;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.GradientDrawable;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.IOException;

public class artificialquest_message extends AppCompatActivity {
    private String photoPath;
    private Button artificial_answer1, artificial_answer2, nextButton_artificial;
    private TextView responseText_artificial; // "좋아요!"를 표시할 TextView
    private ImageView talkBalloon_artificial; // 이미지 표시를 위한 ImageView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artificialquest_message);

        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.button_sound);

        // UI 요소 초기화
        artificial_answer1 = findViewById(R.id.artificial_answer1);
        artificial_answer2 = findViewById(R.id.artificial_answer2);
        nextButton_artificial = findViewById(R.id.next_button_artificial);
        responseText_artificial = findViewById(R.id.responseText); // TextView 연결// ImageView 연결

        // "대화마치기" 버튼 비활성화 (처음엔 연한 색으로 설정)
        nextButton_artificial.setEnabled(false);

        // 사진 경로 가져오기
        photoPath = getIntent().getStringExtra("photoPath");
        Log.d("sandquest_message", "Photo path: " + photoPath);  // 로그로 경로 확인
        if (photoPath != null) {
            ImageView imageView = findViewById(R.id.imageView);
            Bitmap bitmap = BitmapFactory.decodeFile(photoPath);

            // 이미지가 null인지 체크
            if (bitmap == null) {
                Log.e("sandquest_message", "Bitmap is null, check the photo path or storage permission.");
            } else {
                Bitmap rotatedBitmap = rotateImageIfRequired(bitmap, photoPath);
                imageView.setImageBitmap(rotatedBitmap);
            }
        }

        // answer1 클릭 리스너 설정
        artificial_answer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectButton(artificial_answer1);
                showResponseTextAndImage(); // "좋아요!" 메시지와 이미지를 표시
            }
        });

        // answer2 클릭 리스너 설정
        artificial_answer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectButton(artificial_answer2);
                showResponseTextAndImage(); // "좋아요!" 메시지와 이미지를 표시
            }
        });

        // next_button 클릭 리스너 설정
        nextButton_artificial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                onButtonClick(v);  // 버튼 클릭 시 reward_quiz로 이동
            }
        });
    }

    // 버튼 선택 시 호출되는 메서드
    private void selectButton(Button selectedButton) {
        // answer1과 answer2의 배경을 초기화
        resetButtonBorder(artificial_answer1);
        resetButtonBorder(artificial_answer2);

        // 선택된 버튼에 테두리 색상 적용
        setButtonBorder(selectedButton, R.color.water_color);

        // "대화마치기" 버튼 활성화
        nextButton_artificial.setEnabled(true);  // 활성화되면 색상이 진해짐
    }

    // 버튼의 테두리 색상 변경
    private void setButtonBorder(Button button, int colorResId) {
        GradientDrawable drawable = (GradientDrawable) button.getBackground().mutate(); // GradientDrawable을 변형 가능 상태로 가져옴
        drawable.setStroke(4, ContextCompat.getColor(this, colorResId)); // 테두리 두께와 색상 설정
    }

    // 버튼 테두리 초기화
    private void resetButtonBorder(Button button) {
        GradientDrawable drawable = (GradientDrawable) button.getBackground().mutate(); // GradientDrawable을 변형 가능 상태로 가져옴
        drawable.setStroke(0, ContextCompat.getColor(this, android.R.color.transparent)); // 투명 테두리 설정
    }

    // "좋아요!" 메시지와 이미지를 표시하는 메서드
    private void showResponseTextAndImage() {
        responseText_artificial.setVisibility(View.VISIBLE); // 텍스트 보이기
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

        // next_button이 활성화된 후에만 reward_quiz로 이동
        if (nextButton_artificial.isEnabled()) {
            Intent intent = new Intent(this, reward_artificialquest.class); // reward_quiz 액티비티로 이동
            intent.putExtra("photoPath", photoPath); // 필요시 데이터 전달
            startActivity(intent); // reward_quiz 액티비티 시작
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }
}
