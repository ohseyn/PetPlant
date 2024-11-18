package com.example.petplant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> {

    private ArrayList<Map<String, String>> localDataSet;
    private Context context;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView plantTextView;
        private CircleImageView profileImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textName);
            plantTextView = itemView.findViewById(R.id.textPlantName);
            profileImageView = itemView.findViewById(R.id.friendProfileImageView);
        }

        public List<View> getViews() {
            return Arrays.asList(nameTextView, plantTextView, profileImageView);
        }
    }

    public FriendListAdapter(ArrayList<Map<String, String>> dataSet, Context context) {
        localDataSet = dataSet;
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public FriendListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_friend_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendListAdapter.ViewHolder holder, int position) {
        Map<String, String> friendData = localDataSet.get(position);
        String name = friendData.get("name");
        String plantName = friendData.get("plantName");
        String profileImageUri = friendData.get("profileImageUri");
        String friendId = friendData.get("id");
        String isFriend = friendData.get("isFriend");

        // 프로필 이미지 설정 (기본 이미지 포함)
        if (profileImageUri != null && !profileImageUri.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(profileImageUri)
                    .placeholder(R.drawable.profile_frame) // 로딩 중 기본 이미지
                    .error(R.drawable.profile_frame) // 오류 시 기본 이미지
                    .into(holder.profileImageView);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(profileImageUri)
                    .placeholder(R.drawable.profile_frame) // 로딩 중 기본 이미지
                    .error(R.drawable.profile_frame) // 오류 시 기본 이미지
                    .into(holder.profileImageView);
        }

        holder.nameTextView.setText(name);
        holder.plantTextView.setText(plantName);

        holder.itemView.setOnClickListener(v -> {
            if (friendId != null && !friendId.isEmpty()) {
                showProfileDialog(holder.itemView.getContext(), name, plantName, profileImageUri, friendData);
            } else {
                Toast.makeText(holder.itemView.getContext(), "친구 ID를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showProfileDialog(Context context, String name, String plantName, String profileImageUri, Map<String, String> friendData) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_friend_profile, null);

        CircleImageView profileImageView = dialogView.findViewById(R.id.profileImageView);
        TextView nameTextView = dialogView.findViewById(R.id.textName);
        TextView plantNameTextView = dialogView.findViewById(R.id.textPlantName);
        TextView introTextView = dialogView.findViewById(R.id.textIntro);
        Button actionButton = dialogView.findViewById(R.id.actionButton);

        // 프로필 이미지 설정 (기본 이미지 포함)
        if (profileImageUri != null && !profileImageUri.isEmpty()) {
            Glide.with(context).load(profileImageUri)
                    .placeholder(R.drawable.profile_frame) // 로딩 중 기본 이미지
                    .error(R.drawable.profile_frame) // 오류 시 기본 이미지
                    .into(profileImageView);
        } else {
            Glide.with(context).load(R.drawable.profile_frame).into(profileImageView);
        }


        nameTextView.setText(name);
        plantNameTextView.setText(plantName);

        String friendId = friendData.get("id");
        db.collection("users").document(friendId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String intro = documentSnapshot.getString("intro");
                    introTextView.setText(intro != null && !intro.isEmpty() ? intro : "소개글이 없습니다.");
                });

        if ("true".equals(friendData.get("isFriend"))) {
            actionButton.setText("친구 끊기");
            actionButton.setOnClickListener(v -> removeFriend(friendId, friendData));
        } else {
            actionButton.setText("친구 신청");
            actionButton.setOnClickListener(v -> sendFriendRequest(friendId, friendData));
        }

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void removeFriend(String friendId, Map<String, String> friendData) {
        String currentUserId = auth.getCurrentUser().getUid();

        db.collection("users").document(currentUserId)
                .collection("friends").document(friendId).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "친구를 삭제했습니다.", Toast.LENGTH_SHORT).show();
                    db.collection("users").document(friendId)
                            .collection("friends").document(currentUserId).delete()
                            .addOnSuccessListener(aVoid2 -> {
                                localDataSet.remove(friendData);
                                notifyDataSetChanged();
                                updateFriendCount();
                            });
                });
    }

    private void sendFriendRequest(String friendId, Map<String, String> friendData) {
        String currentUserId = auth.getCurrentUser().getUid();

        Map<String, Object> friendRequest = new HashMap<>();
        friendRequest.put("from", currentUserId);
        friendRequest.put("status", "pending");
        friendRequest.put("timestamp", FieldValue.serverTimestamp());

        db.collection("users").document(friendId)
                .collection("friendRequests").document(currentUserId).set(friendRequest)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "친구 요청을 보냈습니다.", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateFriendCount() {
        String currentUserId = auth.getCurrentUser().getUid();
        db.collection("users").document(currentUserId)
                .collection("friends")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int count = task.getResult().size();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public void updateList(ArrayList<Map<String, String>> newList) {
        localDataSet = newList;
        notifyDataSetChanged();
    }
}
