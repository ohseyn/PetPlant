package com.example.petplant;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.FriendRequestViewHolder> {
    private List<FriendRequest> friendRequestList;
    private OnFriendRequestActionListener listener;

    public interface OnFriendRequestActionListener {
        void onAccept(String requestUserId);
        void onDecline(String requestUserId);
    }

    public FriendRequestAdapter(List<FriendRequest> friendRequestList, OnFriendRequestActionListener listener) {
        this.friendRequestList = friendRequestList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FriendRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_request_item, parent, false);
        return new FriendRequestViewHolder(view);
    }

    public void onBindViewHolder(@NonNull FriendRequestViewHolder holder, int position) {
        FriendRequest request = friendRequestList.get(position);
        String requestId = request.getRequestId();
        String requestUserName = request.getFrom();  // 요청한 사용자의 이름 가져오기
        String profileImageUrl = request.getProfileImage();
        String timeSinceRequest = request.getTimeSinceRequest();

        holder.requestTextView.setText(requestUserName);
        holder.requestTimeTextView.setText(timeSinceRequest);

        // 프로필 이미지 로드
        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(profileImageUrl)
                    .into(holder.profileImageView);
        } else {
            holder.profileImageView.setImageResource(R.drawable.default_profile_image);  // 기본 이미지
        }

        holder.acceptButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAccept(requestId);
                friendRequestList.remove(position);  // 수락 후 알림 제거
                notifyDataSetChanged();  // 화면 새로고침
            }
        });

        holder.declineButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDecline(requestId);
                friendRequestList.remove(position);  // 거절 후 알림 제거
                notifyDataSetChanged();  // 화면 새로고침
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendRequestList.size();
    }

    public class FriendRequestViewHolder extends RecyclerView.ViewHolder {

        TextView requestTextView;
        TextView requestTimeTextView;
        ImageView profileImageView;
        Button acceptButton, declineButton;

        public FriendRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            requestTextView = itemView.findViewById(R.id.requestText);
            requestTimeTextView = itemView.findViewById(R.id.requestTime);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            declineButton = itemView.findViewById(R.id.declineButton);
        }
    }
}
