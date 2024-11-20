package com.example.petplant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TutorialAdapter extends RecyclerView.Adapter<TutorialAdapter.TutorialViewHolder> {
    private final List<Integer> tutorialLayouts; // 페이지 레이아웃 목록

    // 생성자: 튜토리얼 레이아웃 리스트를 초기화
    public TutorialAdapter(List<Integer> tutorialLayouts) {
        this.tutorialLayouts = tutorialLayouts;
    }

    @NonNull
    @Override
    public TutorialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // XML 레이아웃 파일을 View로 변환 (inflate)
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new TutorialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TutorialViewHolder holder, int position) {
        // 각 페이지 데이터 바인딩 (필요하면 추가 구현 가능)
    }

    @Override
    public int getItemCount() {
        // 전체 페이지 수 반환
        return tutorialLayouts.size();
    }

    @Override
    public int getItemViewType(int position) {
        // 현재 위치에 해당하는 레이아웃 리소스 ID 반환
        return tutorialLayouts.get(position);
    }

    public static class TutorialViewHolder extends RecyclerView.ViewHolder {
        // ViewHolder: 각 페이지의 뷰를 담는 클래스
        public TutorialViewHolder(@NonNull View itemView) {
            super(itemView);
            // itemView로 페이지의 뷰에 접근 가능
        }
    }
}
