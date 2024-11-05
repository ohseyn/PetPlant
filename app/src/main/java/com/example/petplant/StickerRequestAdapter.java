package com.example.petplant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class StickerRequestAdapter extends RecyclerView.Adapter<StickerRequestAdapter.StickerRequestViewHolder> {
    private List<StickerRequest> stickerRequestList;

    public StickerRequestAdapter(List<StickerRequest> stickerRequestList) {
        this.stickerRequestList = stickerRequestList;
    }

    @Override
    public StickerRequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sticker_request, parent, false);
        return new StickerRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StickerRequestViewHolder holder, int position) {
        StickerRequest stickerRequest = stickerRequestList.get(position);
        holder.userNameTextView.setText(stickerRequest.getUserName()+ " 님이 내 활동 기록에 스티커를 남겼어요!");

        // stickerType에 따라 이미지 로드
        String stickerType = stickerRequest.getStickerType();
        int stickerResId = getStickerResId(stickerType);

        // Glide를 사용하여 스티커 이미지를 로드
        Glide.with(holder.itemView.getContext())
                .load(stickerResId)
                .into(holder.photoImageView);
    }

    @Override
    public int getItemCount() {
        return stickerRequestList.size();
    }

    // 스티커 종류에 맞는 리소스 ID를 반환하는 메서드
    private int getStickerResId(String stickerType) {
        switch (stickerType) {
            case "sticker1":
                return R.drawable.sticker1;  // sticker1 리소스
            case "sticker2":
                return R.drawable.sticker2;  // sticker2 리소스
            case "sticker3":
                return R.drawable.sticker3;  // sticker3 리소스
            case "sticker4":
                return R.drawable.sticker4;  // sticker3 리소스
            case "sticker5":
                return R.drawable.sticker5;  // sticker3 리소스
            case "sticker6":
                return R.drawable.sticker6;  // sticker3 리소스
            case "sticker7":
                return R.drawable.sticker7;  // sticker3 리소스case "sticker3":
            case "sticker8":
                return R.drawable.sticker8;  // sticker3 리소스
            // 추가적인 스티커가 있으면 계속 추가
            default:
                return R.drawable.sticker1;  // 기본 스티커
        }
    }

    public static class StickerRequestViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView;
        ImageView photoImageView;

        public StickerRequestViewHolder(View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            photoImageView = itemView.findViewById(R.id.photoImageView);
        }
    }
}



