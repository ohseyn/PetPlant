package com.example.petplant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.ViewHolder> {
    private List<FriendRequest> friendRequestList;
    private FirebaseFirestore db;
    private String currentUserUid;

    public FriendRequestAdapter(List<FriendRequest> friendRequestList, FirebaseFirestore db, String currentUserUid) {
        this.friendRequestList = friendRequestList;
        this.db = db;
        this.currentUserUid = currentUserUid;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_request_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FriendRequest friendRequest = friendRequestList.get(position);

        holder.requesterEmail.setText(friendRequest.getFromEmail());

        holder.acceptButton.setOnClickListener(v -> {
            String requestId = friendRequest.getId();
            String friendUid = friendRequest.getFrom();

            db.collection("friend_requests").document(requestId)
                    .update("status", "accepted")
                    .addOnSuccessListener(aVoid -> {
                        db.collection("users").document(currentUserUid)
                                .update("friends", FieldValue.arrayUnion(friendUid));
                        db.collection("users").document(friendUid)
                                .update("friends", FieldValue.arrayUnion(currentUserUid));

                        Toast.makeText(holder.itemView.getContext(), "Friend request accepted", Toast.LENGTH_SHORT).show();
                    });
        });

        holder.rejectButton.setOnClickListener(v -> {
            String requestId = friendRequest.getId();

            db.collection("friend_requests").document(requestId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(holder.itemView.getContext(), "Friend request rejected", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    @Override
    public int getItemCount() {
        return friendRequestList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView requesterEmail;
        Button acceptButton, rejectButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            requesterEmail = itemView.findViewById(R.id.requester_email);
            acceptButton = itemView.findViewById(R.id.btn_accept_request);
            rejectButton = itemView.findViewById(R.id.btn_reject_request);
        }
    }

}
