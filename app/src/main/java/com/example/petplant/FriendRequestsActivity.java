package com.example.petplant;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class FriendRequestsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private RecyclerView recyclerView;
    private FriendRequestAdapter friendRequestsAdapter;
    private StickerRequestAdapter stickerAdapter;
    private List<FriendRequest> friendRequestList;
    private List<StickerRequest> stickerRequestList;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests);

        // Firebase 초기화
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // TabLayout 초기화
        tabLayout = findViewById(R.id.tabLayout);
        recyclerView = findViewById(R.id.friendRequestsRecyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 두 가지 데이터 리스트 준비
        friendRequestList = new ArrayList<>();
        stickerRequestList = new ArrayList<>();

        // Adapter를 초기화
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

        stickerAdapter = new StickerRequestAdapter(stickerRequestList);

        // 탭 선택 리스너 설정
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 1) {  // '친구신청 알림' 탭
                    loadFriendRequests();
                    recyclerView.setAdapter(friendRequestsAdapter);
                } else {  // '스티커 알림' 탭
                    loadStickerRequests(auth.getUid());
                    recyclerView.setAdapter(stickerAdapter);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // 처음에는 '친구신청 알림' 탭을 선택
        TabLayout.Tab initialTab = tabLayout.getTabAt(0);
        if (initialTab != null) {
            initialTab.select();
        }
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

                        // timestamp가 null이 아닌지 확인
                        Timestamp timestamp = snapshot.getTimestamp("timestamp");  // Firestore에서 Timestamp 필드 읽기
                        if (timestamp != null) {
                            friendRequest.setTimeSinceRequest(getTimeSince(timestamp.toDate().getTime()));
                        } else {
                            friendRequest.setTimeSinceRequest("시간 정보 없음");
                        }

                        // 친구 요청 보낸 사람의 이름을 가져오기
                        db.collection("users").document(friendRequest.getFrom())
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        String userName = documentSnapshot.getString("name");
                                        String profileImageUrl = documentSnapshot.getString("profileImageUrl");

                                        friendRequest.setFrom(userName);  // 사용자 이름 설정
                                        friendRequest.setProfileImage(profileImageUrl);  // 프로필 이미지 설정

                                        friendRequestList.add(friendRequest);
                                        friendRequestsAdapter.notifyDataSetChanged();
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> Log.d("FriendRequests", "Error loading friend requests", e));
    }
    private void loadStickerRequests(String userUid) {
        DocumentReference userDocRef = db.collection("users").document(userUid);

        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<Map<String, Object>> activityRequest = (List<Map<String, Object>>) documentSnapshot.get("activityRequest");

                if (activityRequest != null && !activityRequest.isEmpty()) {
                    stickerRequestList.clear();
                    for (Map<String, Object> activity : activityRequest) {
                        String userName = (String) activity.get("userName");  // 사용자 이름
                        String stickerType = (String) activity.get("kind");  // 스티커 종류

                        // StickerRequest 객체 생성 (이름과 스티커 종류만 필요)
                        StickerRequest stickerRequest = new StickerRequest(userName, stickerType);
                        stickerRequestList.add(stickerRequest);
                    }
                    stickerAdapter.notifyDataSetChanged();
                } else {
                    Log.d("loadStickerRequests", "No sticker requests found.");
                }
            } else {
                Log.e("loadStickerRequests", "User document does not exist.");
            }
        }).addOnFailureListener(e -> {
            Log.e("loadStickerRequests", "Error getting user document: " + e.getMessage());
        });
    }



    private int getStickerDrawableResId(String kind) {
        // 스티커 종류에 따라 리소스 ID 반환
        switch (kind) {
            case "sticker1":
                return R.drawable.sticker1;
            case "sticker2":
                return R.drawable.sticker2;
            case "sticker3":
                return R.drawable.sticker3;
            case "sticker4":
                return R.drawable.sticker4;
            case "sticker5":
                return R.drawable.sticker5;
            case "sticker6":
                return R.drawable.sticker6;
            case "sticker7":
                return R.drawable.sticker7;
            case "sticker8":
                return R.drawable.sticker8;
            default:
                return R.drawable.sticker1;
        }
    }

    // 시간 차 계산 메서드 (n초 전, n분 전, n시간 전, n일 전)
    private String getTimeSince(long timestamp) {
        long currentTime = System.currentTimeMillis(); // 현재 시간
        long diff = currentTime - timestamp; // 시간차 계산

        long seconds = TimeUnit.MILLISECONDS.toSeconds(diff);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
        long hours = TimeUnit.MILLISECONDS.toHours(diff);
        long days = TimeUnit.MILLISECONDS.toDays(diff);

        if (seconds < 60) {
            return seconds + "초 전";
        } else if (minutes < 60) {
            return minutes + "분 전";
        } else if (hours < 24) {
            return hours + "시간 전";
        } else {
            return days + "일 전";
        }
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

        // 친구 목록에 추가
        usersRef.document(currentUserId).collection("friends").document(requestId)
                .set(new Friend(requestId))
                .addOnSuccessListener(aVoid -> {
                    usersRef.document(requestId).collection("friends").document(currentUserId)
                            .set(new Friend(currentUserId))
                            .addOnSuccessListener(aVoid1 -> {
                                Log.d("FriendRequestsActivity", "Friend request accepted successfully.");
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

        db.collection("users").document(currentUserId).collection("friendRequests")
                .document(requestId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("FriendRequestsActivity", "Friend request removed successfully");
                    loadFriendRequests(); // 새로고침
                })
                .addOnFailureListener(e -> Log.e("FriendRequestsActivity", "Error removing friend request", e));
    }
}
