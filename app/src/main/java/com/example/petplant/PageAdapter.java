package com.example.petplant;

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
        // 아이템 레이아웃 설정
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_page, parent, false);
        view.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        return new PageViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
        PageItem item = pageItems.get(position);

        // 배경 이미지 설정
        holder.backgroundImage.setImageResource(item.getImageResource());

        // 버튼 텍스트와 상태 설정
        holder.actionButton.setText(item.getButtonText());

        // 버튼 색상 동적 설정
        holder.actionButton.setBackgroundTintList(
                ContextCompat.getColorStateList(holder.itemView.getContext(), item.getButtonColorResId()));

        // "완료" 버튼 비활성화 처리
        holder.actionButton.setEnabled(!item.getButtonText().equals("완료"));

        // 텍스트 설정 (카테고리, 포인트, 제목)
        holder.categoryTextView.setText(item.getCategory());
        holder.pointTextView.setText(item.getPoint());
        holder.titleTextView.setText(item.getTitle());

        int textColor = ContextCompat.getColor(holder.itemView.getContext(), item.getTextColorResId());
        holder.categoryTextView.setTextColor(textColor);
        holder.pointTextView.setTextColor(textColor);
    }

    @Override
    public int getItemCount() {
        return pageItems.size();
    }

    // 특정 위치의 텍스트 및 버튼을 업데이트하고 UI를 갱신하는 메서드
    public void updateButtonText(int position, String newText) {
        if (position < pageItems.size()) {
            PageItem item = pageItems.get(position);
            item.setButtonText(newText);
            notifyItemChanged(position); // 해당 아이템의 UI 갱신
        }
    }

    // ViewHolder 클래스 정의
    public static class PageViewHolder extends RecyclerView.ViewHolder {

        ImageView backgroundImage;
        Button actionButton;
        TextView categoryTextView, pointTextView, titleTextView;

        public PageViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            // 레이아웃에서 뷰 바인딩
            backgroundImage = itemView.findViewById(R.id.backgroundImage);
            actionButton = itemView.findViewById(R.id.do_quest);
            categoryTextView = itemView.findViewById(R.id.category);
            pointTextView = itemView.findViewById(R.id.points);
            titleTextView = itemView.findViewById(R.id.title);

            // 버튼 클릭 이벤트 처리
            actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null && actionButton.isEnabled()) {
                        listener.onButtonClick(getAdapterPosition());
                    }
                }
            });
        }
    }
}
