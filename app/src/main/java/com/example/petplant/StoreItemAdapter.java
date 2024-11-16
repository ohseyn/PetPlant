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
        // context를 통해 현재 화면이 StoreActivity인지 DressActivity인지 확인
        boolean isStoreActivity = context instanceof StoreActivity;

        // 레이아웃 파일을 화면에 따라 선택
        int layoutResource = isStoreActivity ? R.layout.item_shop : R.layout.item_shop_no_coin;

        // 선택된 레이아웃 파일을 inflate
        View view = LayoutInflater.from(context).inflate(layoutResource, parent, false);
        //View view = LayoutInflater.from(context).inflate(R.layout.item_shop, parent, false);
        return new StoreItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreItemViewHolder holder, @SuppressLint("RecyclerView") int position) {
        StoreItem currentItem = itemList.get(position);
        //holder.itemPrice.setText(currentItem.getName());
        holder.itemImage.setImageResource(currentItem.getImageResource());

        // 레이아웃에 따라 itemPrice가 있을 경우에만 설정
        if (holder.itemPrice != null) {
            // 구매한 아이템인지 여부에 따라 표시 다르게 처리
            if (currentItem.isPurchased()) {
                holder.itemView.setAlpha(0.5f);  // 구매한 아이템은 반투명 처리
                holder.itemPrice.setText("보유 중");
            } else {
                holder.itemView.setAlpha(1.0f);
                holder.itemPrice.setText(currentItem.getPrice() + ""); // 가격 표시
            }
        }

//        // 구매한 아이템인지 여부에 따라 표시 다르게 처리
//        if (currentItem.isPurchased()) {
//            holder.itemView.setAlpha(0.5f);  // 구매한 아이템은 반투명 처리
//            holder.itemPrice.setText("보유 중");
//        } else {
//            holder.itemView.setAlpha(1.0f);
//            holder.itemPrice.setText(currentItem.getPrice() + "");
//        }

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
        public TextView itemPrice;

        public StoreItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.item_image);
            itemPrice = itemView.findViewById(R.id.item_price);
        }
    }
}
