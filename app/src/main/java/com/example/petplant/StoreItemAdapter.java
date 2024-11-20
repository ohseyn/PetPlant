package com.example.petplant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
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
    private int selectedItemPosition = RecyclerView.NO_POSITION; // 선택된 아이템 위치
    private int selectedBackgroundPosition = RecyclerView.NO_POSITION; // 선택된 배경 위치

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
        return new StoreItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreItemViewHolder holder, @SuppressLint("RecyclerView") int position) {
        StoreItem currentItem = itemList.get(position);

        if (currentItem == null) {
            Log.e("StoreItemAdapter", "currentItem is null at position: " + position);
            return; // 아이템이 null이면 처리 중단
        }

        holder.itemImage.setImageResource(currentItem.getIconImageResource());
        boolean isDressActivity = context instanceof DressActivity;

        if (isDressActivity) {
            // 현재 적용 중인 아이템/배경 처리
            if (currentItem.isCurrentlyApplied()) {
                holder.appliedText.setVisibility(View.VISIBLE); // "적용 중" 표시
                holder.itemImage.setBackgroundResource(R.drawable.green_border); // 초록색 테두리
                holder.itemImage.setColorFilter(Color.parseColor("#1A02AE7A")); // 연한 초록색 필터
            } else {
                holder.appliedText.setVisibility(View.GONE); // 숨김
                holder.itemImage.setBackground(null); // 테두리 제거
                holder.itemImage.setColorFilter(null); // 필터 제거
            }
        } else {
            // StoreActivity 처리
            if (currentItem.isPurchased()) {
                holder.itemImage.setAlpha(0.5f); // 반투명 처리
                holder.appliedText.setVisibility(View.VISIBLE); // "보유 중" 표시
                holder.itemPrice.setText("보유중");
                holder.itemPrice.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
                holder.itemImage.setColorFilter(Color.parseColor("#26000000"));
                holder.itemPrice.setVisibility(View.GONE); // 가격 숨김
                holder.coinImage.setVisibility(View.GONE); // 코인 숨김
            } else {
                holder.itemImage.setAlpha(1.0f); // 투명도 초기화
                holder.itemPrice.setText(String.valueOf(currentItem.getPrice())); // 가격 표시
                holder.appliedText.setVisibility(View.GONE); // 숨김
                holder.itemPrice.setTextColor(context.getResources().getColor(android.R.color.holo_orange_light));
                holder.coinImage.setVisibility(View.VISIBLE); // 코인 표시
                if (position == selectedItemPosition || position == selectedBackgroundPosition) {
                    holder.itemImage.setBackgroundResource(R.drawable.green_border); // 초록색 테두리
                } else {
                    holder.itemImage.setBackground(null); // 테두리 제거
                }
            }

            // 초록 테두리와 필터
            if (position == selectedItemPosition || position == selectedBackgroundPosition) {
                holder.itemImage.setBackgroundResource(R.drawable.green_border);
                holder.itemImage.setColorFilter(Color.parseColor("#1A02AE7A"));
            } else {
                holder.itemImage.setBackground(null);
                holder.itemImage.setColorFilter(null);
            }
        }

        holder.itemView.setOnClickListener(v -> {
            onItemClickListener.onItemClick(currentItem);
            // 초록 테두리 및 적용 상태 갱신
            notifyItemChanged(position);
        });
    }

    private void handleItemClick(StoreItem currentItem, int position, boolean isDressActivity) {
        if (currentItem == null) {
            Log.d("StoreItemAdapter", "handleItemClick: currentItem is null, position = " + position);
            onItemClickListener.onItemClick(null); // 선택 해제로 처리
            return;
        }

        if (!isDressActivity) {
            // 상점 화면에서는 구매 여부와 관계없이 선택 가능
            onItemClickListener.onItemClick(currentItem);
            notifyItemChanged(position);
            return;
        }

        // 꾸미기 화면에서는 "적용 중" 상태 설정
        if (currentItem.isCurrentlyApplied()) {
            Toast.makeText(context, "이미 적용 중입니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        onItemClickListener.onItemClick(currentItem);
        notifyItemChanged(position);
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
        public ImageView coinImage;
        public TextView appliedText; // "적용 중" 텍스트뷰 추가

        public StoreItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.item_image);
            itemPrice = itemView.findViewById(R.id.item_price);
            coinImage = itemView.findViewById(R.id.coin_image);
            appliedText = itemView.findViewById(R.id.applied_text); // 바인딩

            // Null 체크 및 로그 추가
            if (appliedText == null) {
                Log.e("StoreItemViewHolder", "applied_text is not found in layout: " + itemView.getContext());
            }
            if (itemPrice == null) {
                Log.e("StoreItemViewHolder", "item_price is not found in layout: " + itemView.getContext());
            }
            if (coinImage == null) {
                Log.e("StoreItemViewHolder", "coin_image is not found in layout: " + itemView.getContext());
            }
        }
    }
}
