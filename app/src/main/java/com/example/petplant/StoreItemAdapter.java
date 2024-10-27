package com.example.petplant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    public void onBindViewHolder(@NonNull StoreItemViewHolder holder, @SuppressLint("RecyclerView") int position) {
        StoreItem currentItem = itemList.get(position);
        holder.itemName.setText(currentItem.getName());
        holder.itemImage.setImageResource(currentItem.getImageResource());

        // 구매한 아이템인지 여부에 따라 표시 다르게 처리
        if (currentItem.isPurchased()) {
            holder.itemView.setAlpha(0.5f);  // 구매한 아이템은 반투명 처리
            holder.itemName.setText("구매됨");
        } else {
            holder.itemView.setAlpha(1.0f);
            holder.itemName.setText(currentItem.getName()); // 원래 이름 표시
        }

        // 아이템 클릭 시 처리
        holder.itemView.setOnClickListener(v -> {
            if (currentItem.isPurchased()) {
                Toast.makeText(context, "이미 구매한 아이템입니다.", Toast.LENGTH_SHORT).show();
            } else {
                onItemClickListener.onItemClick(currentItem); // 구매되지 않은 경우에만 선택 이벤트 처리
            }
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
