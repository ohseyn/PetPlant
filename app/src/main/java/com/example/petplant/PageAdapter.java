package com.example.petplant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PageAdapter extends RecyclerView.Adapter<PageAdapter.PageViewHolder> {

    private List<PageItem> pageItems;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onButtonClick(int position);
    }

    public PageAdapter(HomeMainActivity mainActivity, List<PageItem> pageItems, OnItemClickListener listener) {
        this.pageItems = pageItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_page, parent, false);
        return new PageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
        PageItem item = pageItems.get(position);
        holder.backgroundImage.setImageResource(item.getImageResource());
        holder.actionButton.setText(item.getButtonText());

        // 버튼이 "완료"인 경우 비활성화
        if (item.getButtonText().equals("완료")) {
            holder.actionButton.setEnabled(false);  // 버튼 비활성화
        } else {
            holder.actionButton.setEnabled(true);   // 버튼 활성화
        }

        holder.actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null && holder.actionButton.isEnabled()) {
                    listener.onButtonClick(holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return pageItems.size();
    }

    // 페이지 아이템의 버튼 텍스트를 업데이트하고 버튼을 비활성화하는 메서드 추가
    public void updateButtonText(int position, String newText) {
        if (position < pageItems.size()) {
            PageItem item = pageItems.get(position);
            item.setButtonText(newText);
            notifyItemChanged(position); // 해당 위치의 아이템 업데이트
        }
    }

    public static class PageViewHolder extends RecyclerView.ViewHolder {

        ImageView backgroundImage;
        Button actionButton;

        public PageViewHolder(@NonNull View itemView) {
            super(itemView);
            backgroundImage = itemView.findViewById(R.id.backgroundImage);
            actionButton = itemView.findViewById(R.id.do_quest);
        }
    }
}
