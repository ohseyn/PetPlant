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
    private int selectedPosition = RecyclerView.NO_POSITION; // 선택된 아이템 저장
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
        //View view = LayoutInflater.from(context).inflate(R.layout.item_shop, parent, false);
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

        if (currentItem.isPurchased()) {
            // 보유 중인 상태
            holder.itemImage.setAlpha(0.5f);
            holder.itemImage.setBackground(null); // 테두리 제거

            if (holder.itemPrice != null) {
                holder.itemPrice.setText("보유중"); // "보유 중" 텍스트 설정
                holder.itemPrice.setTextColor(context.getResources().getColor(android.R.color.darker_gray)); // 회색 글씨
                holder.itemPrice.setGravity(View.TEXT_ALIGNMENT_CENTER); // 텍스트 중앙 정렬
                holder.itemPrice.setBackgroundResource(R.drawable.text_background); // 중앙 정렬을 돕는 배경 추가
            }

            if (holder.coinImage != null) {
                holder.coinImage.setVisibility(View.GONE); // 코인 이미지는 숨김
            }
        } else {
            // 구매 가능한 상태
            holder.itemImage.setAlpha(1.0f); // 이미지 투명도 초기화

            // 선택된 아이템 가시화 (초록 테두리 및 연한 초록 필터)
            if (position == selectedItemPosition || position == selectedBackgroundPosition) {
                holder.itemImage.setBackgroundResource(R.drawable.green_border); // 초록 테두리 drawable 추가 필요
                holder.itemImage.setColorFilter(Color.parseColor("#1A02AE7A")); // 연한 초록색 필터 (opacity 10%)
            } else {
                holder.itemImage.setBackground(null);
                holder.itemImage.setColorFilter(null); // 필터 제거
            }

            if (holder.itemPrice != null) {
                holder.itemPrice.setText(String.valueOf(currentItem.getPrice())); // 가격 표시
                holder.itemPrice.setTextColor(context.getResources().getColor(android.R.color.holo_orange_light)); // 가격 글씨 색상 #FEC600
                //holder.itemPrice.setBackground(null); // 배경 제거
            }

            if (holder.coinImage != null) {
                holder.coinImage.setVisibility(View.VISIBLE); // 코인 이미지는 보임
            }
        }

        // 클릭 리스너
        holder.itemView.setOnClickListener(v -> {
            handleItemClick(currentItem, position);
        });

        // 아이템 클릭 시 처리
//        holder.itemView.setOnClickListener(v -> {
//            if (currentItem.isPurchased()) {
//                Toast.makeText(context, "이미 구매한 아이템입니다.", Toast.LENGTH_SHORT).show();
//            } else {
//                // 선택 상태 업데이트
//                int previousPosition = selectedPosition;
//                selectedPosition = position; // 현재 선택된 위치 저장
//
//                // 이전 위치와 현재 위치의 뷰를 갱신
//                notifyItemChanged(previousPosition);
//                notifyItemChanged(selectedPosition);
//
//                onItemClickListener.onItemClick(currentItem); // 클릭 이벤트 전달
////                selectedPosition = position; // 현재 선택된 위치 저장
////                onItemClickListener.onItemClick(currentItem); // 구매되지 않은 경우에만 선택 이벤트 처리
////                notifyDataSetChanged(); // 뷰 갱신
//            }
//        });
    }

    private void handleItemClick(StoreItem currentItem, int position) {
        if (currentItem == null) {
            Log.d("StoreItemAdapter", "handleItemClick: currentItem is null, position = " + position);
            onItemClickListener.onItemClick(null); // 선택 해제로 처리
            return;
        }

        if (currentItem.isPurchased()) {
            Toast.makeText(context, "이미 구매한 아이템입니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentItem.isBackground()) {
            // 배경 선택/해제 처리
            if (position == selectedBackgroundPosition) {
                // 선택 해제: 저장된 배경 상태로 복원
                int previousPosition = selectedBackgroundPosition;
                selectedBackgroundPosition = RecyclerView.NO_POSITION;
                notifyItemChanged(previousPosition); // 이전 선택 해제
                onItemClickListener.onItemClick(null); // 선택 해제 콜백 호출
            } else {
                // 새로운 배경 선택
                int previousPosition = selectedBackgroundPosition;
                selectedBackgroundPosition = position;
                notifyItemChanged(previousPosition); // 이전 선택 해제
                notifyItemChanged(position); // 현재 선택 적용
                onItemClickListener.onItemClick(currentItem); // 선택된 아이템 콜백 호출
            }
        } else if (currentItem.isItem()) {
            // 캐릭터 아이템 선택/해제 처리
            if (position == selectedItemPosition) {
                // 선택 해제: 저장된 캐릭터 상태로 복원
                int previousPosition = selectedItemPosition;
                selectedItemPosition = RecyclerView.NO_POSITION;
                notifyItemChanged(previousPosition); // 이전 선택 해제
                onItemClickListener.onItemClick(null); // 선택 해제 콜백 호출
            } else {
                // 새로운 아이템 선택
                int previousPosition = selectedItemPosition;
                selectedItemPosition = position;
                notifyItemChanged(previousPosition); // 이전 선택 해제
                notifyItemChanged(position); // 현재 선택 적용
                onItemClickListener.onItemClick(currentItem); // 선택된 아이템 콜백 호출
            }
        }
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

        public StoreItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.item_image);
            itemPrice = itemView.findViewById(R.id.item_price);
            coinImage = itemView.findViewById(R.id.coin_image);
        }
    }
}
