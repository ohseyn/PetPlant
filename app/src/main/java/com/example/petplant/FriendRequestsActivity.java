package com.example.petplant;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class FriendRequestsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private TextView requestText;
    private Button acceptButton, declineButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests);

        // Firebase 초기화
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // UI 요소 연결
        requestText = findViewById(R.id.requestText);
        acceptButton = findViewById(R.id.acceptButton);
        declineButton = findViewById(R.id.declineButton);

        // 요청한 친구의 정보를 가져옴
        String requestUserId = getIntent().getStringExtra("requestUserId");
        requestText.setText("친구 요청을 수락하시겠습니까?");

        // 친구 요청 수락 버튼
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptFriendRequest(requestUserId);
            }
        });

        // 친구 요청 거절 버튼
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                declineFriendRequest(requestUserId);
            }
        });
    }

    // 친구 요청 수락
    private void acceptFriendRequest(String requestUserId) {
        String currentUserId = auth.getCurrentUser().getUid();

        // 친구 요청 수락 후 친구 목록에 추가
        db.collection("users").document(currentUserId).collection("friends")
                .document(requestUserId)
                .set(new Object())
                .addOnSuccessListener(aVoid -> {
                    db.collection("users").document(requestUserId).collection("friends")
                            .document(currentUserId)
                            .set(new Object())
                            .addOnSuccessListener(aVoid1 -> {
                                Toast.makeText(FriendRequestsActivity.this, "친구 추가 완료!", Toast.LENGTH_SHORT).show();
                                finish(); // 액티비티 종료
                            });
                });
    }

    // 친구 요청 거절
    private void declineFriendRequest(String requestUserId) {
        String currentUserId = auth.getCurrentUser().getUid();

        // 친구 요청 삭제
        db.collection("users").document(currentUserId).collection("friendRequests")
                .document(requestUserId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(FriendRequestsActivity.this, "친구 요청을 거절했습니다.", Toast.LENGTH_SHORT).show();
                    finish(); // 액티비티 종료
                });
    }

}
