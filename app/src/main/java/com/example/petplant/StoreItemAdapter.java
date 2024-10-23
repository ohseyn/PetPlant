package com.example.petplant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StoreItemAdapter extends RecyclerView.Adapter<StoreItemAdapter.StoreItemViewHolder> {

    private Context context;
    private List<StoreItem> itemList;
    private OnItemClickListener onItemClickListener;
    private int selectedPosition = RecyclerView.NO_POSITION; // 선택된 아이템 저장

    // 인터페이스 정의 (아이템 클릭 시 호출)
    public interface OnItemClickListener {
        void onItemClick(StoreItem item);
    }

    public StoreItemAdapter(Context context, List<StoreItem> itemList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.itemList = itemList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public StoreItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // XML 파일을 뷰로 변환하여 뷰홀더에 전달
        View view = LayoutInflater.from(context).inflate(R.layout.item_shop, parent, false);
        return new StoreItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreItemViewHolder holder, int position) {
        StoreItem currentItem = itemList.get(position);
        holder.itemName.setText(currentItem.getName());
        holder.itemImage.setImageResource(currentItem.getImageResource());

        holder.itemView.setSelected(selectedPosition == position); // 선택된 아이템 강조

        // 아이템 클릭 처리
        holder.itemView.setOnClickListener(v -> {
            if (selectedPosition == position) {
                selectedPosition = RecyclerView.NO_POSITION; // 같은 아이템을 다시 클릭하면 선택 해제
                onItemClickListener.onItemClick(null); // 선택 해제 신호 전달
            } else {
                selectedPosition = position; // 새로운 아이템 선택
                onItemClickListener.onItemClick(currentItem);
            }
            notifyDataSetChanged(); // UI 업데이트
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size(); // 아이템 개수 리턴
    }

    public void updateItemList(List<StoreItem> newList) {
        this.itemList = newList;
        notifyDataSetChanged();
    }

    // 뷰홀더 클래스
    public static class StoreItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView itemImage;
        public TextView itemName;

        public StoreItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.item_image);
            itemName = itemView.findViewById(R.id.item_name);
        }
    }
}
