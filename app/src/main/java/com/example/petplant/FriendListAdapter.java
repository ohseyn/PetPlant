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
import com.example.petplant.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder>{

    private ArrayList<Map<String,String>> localDataSet;
    private Context context; // Context를 클래스의 멤버 변수로 선언
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    // 뷰홀더 클래스
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
        public List<View> getViews() { // 메서드 이름 변경
            return Arrays.asList(nameTextView, plantTextView, profileImageView);
        }
    }

    // 생성자를 통해서 데이터를 전달받도록 함
    public FriendListAdapter (ArrayList<Map<String,String>> dataSet, Context context) {
        localDataSet = dataSet;
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override   // ViewHolder 객체를 생성하여 리턴한다.
    public FriendListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_friend_item, parent, false);
        FriendListAdapter.ViewHolder viewHolder = new FriendListAdapter.ViewHolder(view);

        return new ViewHolder(view);
    }

    @Override   // ViewHolder안의 내용을 position에 해당되는 데이터로 교체한다.
    public void onBindViewHolder(@NonNull FriendListAdapter.ViewHolder holder, int position) {
        Map<String, String> friendData = localDataSet.get(position); // Map 형태의 데이터 가져오기
        String name = friendData.get("name"); // 이름 가져오기
        String plantName = friendData.get("plantName"); // 식물 이름 가져오기
        String profileImageUri = friendData.get("profileImageUri"); // 프로필 이미지 URI 가져오기
        String friendId = friendData.get("id");
        String isFriend = friendData.get("isFriend"); // 친구 여부 가져오기

        // Glide를 사용하여 이미지 로드
        Glide.with(holder.itemView.getContext())
                .load(profileImageUri)
                .into(holder.profileImageView); // CircleImageView에 이미지 설정

        // TextView에 데이터 바인딩
        holder.nameTextView.setText(name); // 이름 설정
        holder.plantTextView.setText(plantName); // 식물 이름 설정

        // 친구 이름 또는 이미지 클릭 시 다이얼로그를 띄움
        holder.itemView.setOnClickListener(v -> {
            if (friendId != null && !friendId.isEmpty()) { // id가 null이거나 빈 값이 아닌지 체크
                showProfileDialog(holder.itemView.getContext(), name, plantName, profileImageUri, friendData);
            } else {
                Toast.makeText(holder.itemView.getContext(), "친구 ID를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 다이얼로그를 보여주는 함수
    private void showProfileDialog(Context context, String name, String plantName, String profileImageUri, Map<String, String> friendData) {
        // 다이얼로그 띄우기
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_friend_profile, null); // 다이얼로그 레이아웃 연결

        CircleImageView profileImageView = dialogView.findViewById(R.id.profileImageView);
        TextView nameTextView = dialogView.findViewById(R.id.textName);
        TextView plantNameTextView = dialogView.findViewById(R.id.textPlantName);
        TextView introTextView = dialogView.findViewById(R.id.textIntro);
        Button actionButton = dialogView.findViewById(R.id.actionButton);

        // 프로필 이미지 설정
        Glide.with(context).load(profileImageUri).into(profileImageView);

        // 이름 및 식물 이름 설정
        nameTextView.setText(name);
        plantNameTextView.setText(plantName);

        // Firestore에서 소개글 불러오기
        String friendId = friendData.get("id"); // 친구의 UID로 가져옴
        db.collection("users").document(friendId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String intro = documentSnapshot.getString("intro");
                    introTextView.setText(intro != null && !intro.isEmpty() ? intro : "소개글이 없습니다.");
                });

        // 이미 친구라면 삭제 버튼, 아니라면 친구 신청 버튼 설정
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

    // 친구 삭제 함수
    private void removeFriend(String friendId, Map<String, String> friendData) {
        String currentUserId = auth.getCurrentUser().getUid();

        db.collection("users").document(currentUserId)
                .collection("friends").document(friendId).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "친구를 삭제했습니다.", Toast.LENGTH_SHORT).show();
                    // 상대방 친구 목록에서 현재 유저 삭제
                    db.collection("users").document(friendId)
                            .collection("friends").document(currentUserId).delete()
                            .addOnSuccessListener(aVoid2 -> {
                                // 친구 삭제 후 즉시 목록과 친구 수 갱신
                                localDataSet.remove(friendData);
                                notifyDataSetChanged();
                                updateFriendCount();
                            });
                });
    }

    // 친구 신청 함수
    private void sendFriendRequest(String friendId, Map<String, String> friendData) {
        String currentUserId = auth.getCurrentUser().getUid();

        Map<String, Object> friendRequest = new HashMap<>();
        friendRequest.put("from", currentUserId);
        friendRequest.put("status", "pending");

        FirebaseFirestore.getInstance().collection("users").document(friendId)
                .collection("friendRequests").document(currentUserId).set(friendRequest)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "친구 요청을 보냈습니다.", Toast.LENGTH_SHORT).show();
                });
    }

    // 친구 수 업데이트 함수 (현재 유저의 친구 수를 갱신)
    private void updateFriendCount() {
        String currentUserId = auth.getCurrentUser().getUid();
        db.collection("users").document(currentUserId)
                .collection("friends")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int count = task.getResult().size();
                        // 친구 수가 갱신된 것을 반영 (필요한 곳에 반영할 수 있도록 처리)
                        // 예: profile 화면에서 친구 수 업데이트
                    }
                });
    }

    @Override   // 전체 데이터의 갯수를 리턴한다.
    public int getItemCount() {
        return localDataSet.size();
    }

    // 검색 결과 업데이트
    public void updateList(ArrayList<Map<String, String>> newList) {
        localDataSet = newList;
        notifyDataSetChanged();
    }
}