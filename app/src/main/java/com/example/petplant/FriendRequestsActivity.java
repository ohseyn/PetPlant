package com.example.petplant;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FriendRequestsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private RecyclerView friendRequestsRecyclerView;
    private FriendRequestAdapter friendRequestsAdapter;
    private List<FriendRequest> friendRequestList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests);

        // Firebase 초기화
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // RecyclerView 초기화
        friendRequestsRecyclerView = findViewById(R.id.friendRequestsRecyclerView);
        friendRequestsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        friendRequestList = new ArrayList<>();
        friendRequestsAdapter = new FriendRequestAdapter(friendRequestList, new FriendRequestAdapter.OnFriendRequestActionListener() {
            @Override
            public void onAccept(String requestUserId) {
                acceptFriendRequest(requestUserId);
            }

            @Override
            public void onDecline(String requestUserId) {
                declineFriendRequest(requestUserId);
            }
        });
        friendRequestsRecyclerView.setAdapter(friendRequestsAdapter);

        // 친구 요청 로드
        loadFriendRequests();
    }

    // 친구 요청 목록 로드
    private void loadFriendRequests() {
        String currentUserId = auth.getCurrentUser().getUid();
        db.collection("users").document(currentUserId).collection("friendRequests")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    friendRequestList.clear();
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        FriendRequest friendRequest = snapshot.toObject(FriendRequest.class);
                        // Firestore 문서 ID를 requestId로 설정
                        friendRequest.setRequestId(snapshot.getId());

                        // 친구 요청 보낸 사람의 이름을 가져오기
                        db.collection("users").document(friendRequest.getFrom())
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        String userName = documentSnapshot.getString("name");
                                        friendRequest.setFrom(userName);  // 사용자 이름 설정
                                        friendRequestList.add(friendRequest);
                                        friendRequestsAdapter.notifyDataSetChanged();
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> Log.d("FriendRequests", "Error loading friend requests", e));
    }

    // 친구 요청 수락
    public void acceptFriendRequest(String requestId) {
        Log.d("FriendRequestsActivity", "acceptFriendRequest: requestId = " + requestId);
        if (requestId == null) {
            Log.e("FriendRequestsActivity", "Error: requestId is null");
            return;
        }

        String currentUserId = auth.getCurrentUser().getUid();
        CollectionReference usersRef = db.collection("users");

        // 친구 목록에 추가할 데이터를 정의
        // 친구의 id와 username을 저장하는 예시
        usersRef.document(currentUserId).collection("friends").document(requestId)
                .set(new Friend(requestId))  // Friend는 별도의 클래스 (아래 코드 참고)
                .addOnSuccessListener(aVoid -> {
                    // 친구 요청 보낸 유저의 친구 목록에도 현재 유저 추가
                    usersRef.document(requestId).collection("friends").document(currentUserId)
                            .set(new Friend(currentUserId))
                            .addOnSuccessListener(aVoid1 -> {
                                Log.d("FriendRequestsActivity", "Friend request accepted successfully.");
                                // 친구 요청 삭제
                                removeFriendRequest(requestId);
                            });
                })
                .addOnFailureListener(e -> Log.e("FriendRequestsActivity", "Error accepting friend request", e));
    }

    // 친구 요청 거절
    private void declineFriendRequest(String requestUserId) {
        removeFriendRequest(requestUserId);
        Toast.makeText(FriendRequestsActivity.this, "친구 요청을 거절했습니다.", Toast.LENGTH_SHORT).show();
    }

    // 친구 요청 삭제
    private void removeFriendRequest(String requestId) {
        String currentUserId = auth.getCurrentUser().getUid();

        FirebaseFirestore.getInstance()
                .collection("users").document(currentUserId).collection("friendRequests")
                .document(requestId)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d("FriendRequestsActivity", "Friend request removed successfully"))
                .addOnFailureListener(e -> Log.e("FriendRequestsActivity", "Error removing friend request", e));
    }
}
