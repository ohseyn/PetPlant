package com.example.petplant;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

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

        holder.requestTextView.setText(requestUserName + "님이 친구 신청을 보냈습니다.");

        holder.acceptButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAccept(requestId);
            }
        });

        holder.declineButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDecline(requestId);
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendRequestList.size();
    }

    public class FriendRequestViewHolder extends RecyclerView.ViewHolder {

        TextView requestTextView;
        Button acceptButton, declineButton;

        public FriendRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            requestTextView = itemView.findViewById(R.id.requestText);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            declineButton = itemView.findViewById(R.id.declineButton);
        }
    }
}
