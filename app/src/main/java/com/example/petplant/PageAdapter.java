package com.example.petplant;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PageAdapter extends RecyclerView.Adapter<PageAdapter.PageViewHolder> {

    private final List<PageItem> pageItems;
    private final OnItemClickListener listener;


    // 클릭 리스너 인터페이스 정의
    public interface OnItemClickListener {
        void onButtonClick(int position);
    }

    // 생성자
    public PageAdapter(List<PageItem> pageItems, OnItemClickListener listener) {
        this.pageItems = pageItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_page, parent, false);
        return new PageViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
        PageItem item = pageItems.get(position);

        // 배경 이미지 설정
        holder.backgroundImage.setImageResource(item.getImageResource());

        // 버튼 텍스트 및 상태 설정
        holder.actionButton.setText(item.getButtonText());
        holder.actionButton.setEnabled(item.isButtonEnabled());

        holder.actionButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onButtonClick(position);
            }
        });

        // 강제 갱신을 위해 로그 추가
        Log.d("PageAdapter", "Button text: " + item.getButtonText() + ", Position: " + position);


        // 버튼 색상 설정
        holder.actionButton.setBackgroundTintList(
                ContextCompat.getColorStateList(holder.itemView.getContext(), item.getButtonColorResId()));

        // 텍스트 설정
        holder.categoryTextView.setText(item.getCategory());
        holder.pointTextView.setText(item.getPoint());
        holder.titleTextView.setText(item.getTitle());

        // 텍스트 색상 설정
        int textColor = ContextCompat.getColor(holder.itemView.getContext(), item.getTextColorResId());
        holder.categoryTextView.setTextColor(textColor);
        holder.pointTextView.setTextColor(textColor);

        holder.itemView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        holder.itemView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;

    }

    @Override
    public int getItemCount() {
        return pageItems.size();
    }

    // **버튼 텍스트와 상태를 동시에 업데이트하는 메서드**
    public void updateButtonState(int position, String newText, boolean isEnabled) {
        if (position >= 0 && position < pageItems.size()) {
            PageItem item = pageItems.get(position);

            Log.d("PageAdapter", "Updating button state at position: " + position);

            item.setButtonText(newText); // 텍스트 변경
            item.setButtonEnabled(isEnabled); // 활성화 상태 변경
            notifyItemChanged(position); // UI 강제 갱신
        }
    }
    public void updateAllItems(List<PageItem> updatedPageItems) {
        this.pageItems.clear();
        this.pageItems.addAll(updatedPageItems);
        notifyDataSetChanged(); // 전체 데이터셋 변경을 알리기 위해 사용
    }

    // ViewHolder 정의
    public static class PageViewHolder extends RecyclerView.ViewHolder {

        ImageView backgroundImage;
        Button actionButton;
        TextView categoryTextView, pointTextView, titleTextView;

        public PageViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            // View 요소 초기화
            backgroundImage = itemView.findViewById(R.id.backgroundImage);
            actionButton = itemView.findViewById(R.id.do_quest);
            categoryTextView = itemView.findViewById(R.id.category);
            pointTextView = itemView.findViewById(R.id.points);
            titleTextView = itemView.findViewById(R.id.title);

            // 버튼 클릭 리스너 설정
            actionButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onButtonClick(position); // 클릭 리스너 호출
                }
            });
        }
    }
}
