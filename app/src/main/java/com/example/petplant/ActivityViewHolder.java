package com.example.petplant;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ActivityViewHolder extends RecyclerView.ViewHolder {
    TextView textUserName, textActivity, textTime;
    ImageView imageActivity;
    ImageButton buttonLike;

    // 스티커가 표시될 View들
    View viewSticker1, viewSticker2;
    FrameLayout frameSticker;
    ImageView imageSticker;
    TextView textSticker;
    private int stickerCount = 0; // ViewHolder별 스티커 선택 수 추적

    public ActivityViewHolder(@NonNull View itemView) {
        super(itemView);
        textUserName = itemView.findViewById(R.id.textUserName);
        textActivity = itemView.findViewById(R.id.textActivity);
        textTime = itemView.findViewById(R.id.textTime);
        imageActivity = itemView.findViewById(R.id.imageActivity);
        buttonLike = itemView.findViewById(R.id.buttonLike);

        // 스티커가 표시될 View 초기화
        viewSticker1 = itemView.findViewById(R.id.viewSticker1);
        viewSticker2 = itemView.findViewById(R.id.viewSticker2);
        frameSticker = itemView.findViewById(R.id.frameSticker);
        imageSticker = itemView.findViewById(R.id.imageSticker);
        textSticker = itemView.findViewById(R.id.textSticker);
    }

    // 스티커를 업데이트하는 메서드
    public void updateStickerView(int stickerResId) {
        stickerCount++; // 스티커 선택 횟수 증가

        if (stickerCount == 1) {
            viewSticker1.setVisibility(View.VISIBLE);
            viewSticker1.setBackgroundResource(stickerResId);
        } else if (stickerCount == 2) {
            viewSticker2.setVisibility(View.VISIBLE);
            viewSticker2.setBackgroundResource(stickerResId);
        } else if (stickerCount == 3) {
            frameSticker.setVisibility(View.VISIBLE);
            imageSticker.setImageResource(stickerResId);
            textSticker.setText(""); // 기본 텍스트 제거
        } else {
            // 네 개 이상의 스티커 선택 시, 세 번째 스티커에 회색 배경과 숫자만 업데이트
            textSticker.setText("+" + (stickerCount - 2)); // 세 번째 스티커에 선택된 개수 표시
        }
    }
}
