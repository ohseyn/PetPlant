package com.example.petplant;

import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

public class profile extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private CircleImageView profileImage;
    private TextView name, plantName, friendCount;
    private Button addFriendButton, logoutButton;
    private CardView friendCard;
    private CollectionReference postsCollectionRef;
    ArrayList<String> friends = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Firebase 초기화
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // UI 요소 연결
        profileImage = findViewById(R.id.friendProfileImageView);
        name = findViewById(R.id.name);
        plantName = findViewById(R.id.plantName);
        friendCount = findViewById(R.id.friendCount); // 친구 수 표시
        addFriendButton = findViewById(R.id.addFriendButton); // 친구 추가 버튼
        friendCard = findViewById(R.id.friendCardView1);

        // Firebase에서 프로필 정보를 가져와 설정
        getProfileImageFromFirebase();
        getFriends();
        getFriendCount();

        // 친구 추가 버튼 클릭 시 친구 추가 다이얼로그 표시
        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddFriendDialog();
            }
        });

        // Intent로 전달된 데이터도 설정
        Intent intent = getIntent();
        name.setText(intent.getStringExtra("name"));
        plantName.setText(intent.getStringExtra("plantName"));
        Glide.with(profile.this).load(intent.getStringExtra("profileImageUri")).into(profileImage);

        // 버튼 동작 설정
        Button user_home = findViewById(R.id.user_home);
        user_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomeMainActivity.class);
                startActivity(intent);
            }
        });

        CardView friendCard = findViewById(R.id.friendCardView1); // friendCard 초기화
        friendCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (friends != null && !friends.isEmpty()) { // 리스트가 비어있지 않은지 확인
                    Log.d("friends", friends.get(0)); // 친구 목록 출력
                } else {
                    Log.d("friends", "No friends available"); // 친구가 없을 때 로그
                }

                Intent intent = new Intent(getApplicationContext(), FriendList.class);
                intent.putExtra("friends", friends);
                startActivity(intent);
            }
        });


        Button user_inbox = findViewById(R.id.inbox);
        user_inbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), inbox_friend.class);
                startActivity(intent);
            }
        });

        Button edit_profile = findViewById(R.id.user_edit);
        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SecondOnboardingActivity.class);
                startActivity(intent);
            }
        });

        logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }
        });
    }

    // 친구 추가 다이얼로그 표시
    private void showAddFriendDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("친구 추가");

        // 입력 필드 생성
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("친구 이름을 입력하세요");
        builder.setView(input);

        // "추가" 버튼 설정
        builder.setPositiveButton("추가", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String friendName = input.getText().toString();
                if (!friendName.isEmpty()) {
                    sendFriendRequest(friendName); // 입력받은 친구 이름으로 친구 요청 전송
                } else {
                    Toast.makeText(profile.this, "친구 이름을 입력하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // "취소" 버튼 설정
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    // Firebase에서 프로필 이미지 URL 및 기타 정보 가져오기
    private void getProfileImageFromFirebase() {
        db.collection("users").document(auth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // 사용자 이름과 식물 이름 설정
                            String userName = documentSnapshot.getString("name");
                            String userPlantName = documentSnapshot.getString("plantName");
                            String profileImageUri = documentSnapshot.getString("profileImageUrl");

                            // Firestore에서 가져온 값 설정
                            name.setText(userName != null ? userName : "이름 없음");
                            plantName.setText(userPlantName != null ? userPlantName : "식물 이름 없음");

                            // 프로필 이미지 설정
                            if (profileImageUri != null && !profileImageUri.isEmpty()) {
                                Glide.with(profile.this).load(profileImageUri).into(profileImage);
                            } else {
                                Log.d("ProfileActivity", "Profile Image URI is null or empty");
                                profileImage.setImageResource(R.drawable.default_profile_image);  // 기본 이미지
                            }
                        } else {
                            Log.d("ProfileActivity", "No such document in Firestore");
                        }
                    }
                });





        

    }
    private void getFriends() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users")
                .document(userId)
                .collection("friends")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                friends.add(document.getString("userId"));
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
//        // Get a reference to the 'friends' subcollection within the user's document
//        CollectionReference friendsCollectionRef = db.collection("users")
//                .document(userUid)
//                .collection("friends");
//        friendsCollectionRef.get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        if (!queryDocumentSnapshots.isEmpty()) {
//                            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
//                                // Get the 'userId' field from each friend document
//                                String friendId = document.getString("userId");
//                                friends.add(friendId);
//                            }
//                            // Access the friends list here
//                            if (!friends.isEmpty()) {
//                                Log.d("friends", friends.get(0));
//                            } else {
//                                Log.d("Friends", "No friends found.");
//                            }
//                        } else {
//                            Log.d("Friends", "No friends found.");
//                        }
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.e("Firestore Error", "Error getting friends", e);
//                    }
//                });
    }


    // 친구 수 가져오기
    private void getFriendCount() {
        db.collection("users").document(auth.getCurrentUser().getUid())
                .collection("friends")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int count = task.getResult().size();
                        friendCount.setText("친구 수: " + count + "명");
                    } else {
                        Log.d("ProfileActivity", "Error getting friends: ", task.getException());
                    }
                });
    }

    // 친구 추가 요청 보내기
    private void sendFriendRequest(String friendName) {
        // 현재 사용자 ID
        String currentUserId = auth.getCurrentUser().getUid();

        // 친구 요청 정보 생성
        Map<String, Object> friendRequest = new HashMap<>();
        friendRequest.put("from", currentUserId);
        friendRequest.put("status", "pending");

        // 상대방의 이름으로 검색하여 userId를 가져온 후 친구 요청 전송
        db.collection("users")
                .whereEqualTo("name", friendName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        String recipientUserId = task.getResult().getDocuments().get(0).getId();
                        db.collection("users").document(recipientUserId)
                                .collection("friendRequests")
                                .document(currentUserId)
                                .set(friendRequest)
                                .addOnSuccessListener(aVoid -> Toast.makeText(profile.this, "친구 요청을 보냈습니다!", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(profile.this, "친구 요청을 보내는 데 실패했습니다.", Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(profile.this, "친구를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
