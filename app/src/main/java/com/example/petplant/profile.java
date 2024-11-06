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
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class profile extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private CircleImageView profileImage;
    private TextView name, plantName, classname, friendCount;
    private Button logoutButton;
    private CardView friendCard;
    private ArrayList<String> friends = new ArrayList<>();

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
        classname = findViewById(R.id.classname);
        plantName = findViewById(R.id.plantName);
        friendCount = findViewById(R.id.friendCount); // 친구 수 표시

        //addFriendButton = findViewById(R.id.addFriendButton); // 친구 추가 버튼

        friendCard = findViewById(R.id.friendCardView1);

        // Firebase에서 프로필 이미지와 이름 가져오기
        getProfileImageFromFirebase();
        getFriendCount();
        getFriendIds();

//        // 친구 추가 버튼 클릭 시 다이얼로그 표시
//        addFriendButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showAddFriendDialog();
//            }
//        });

        // 프로필 수정 버튼 클릭 시 profile_edit으로 이동
        Button user_edit = findViewById(R.id.user_edit);
        user_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), profile_edit.class);
                startActivity(intent);
            }
        });

        // 친구 목록 카드뷰 클릭 시 친구 목록 화면으로 이동
        friendCard.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), FriendList.class);
            intent.putExtra("friends", friends); // 친구가 없더라도 빈 리스트 전달
            startActivity(intent);
        });
//        friendCard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (friends != null && !friends.isEmpty()) {
//                    Log.d("friends", friends.get(0)); // 친구 목록 출력
//                    Intent intent = new Intent(getApplicationContext(), FriendList.class);
//                    intent.putExtra("friends", friends);
//                    startActivity(intent);
//                } else {
//                    Log.d("friends", "No friends available"); // 친구가 없을 때 로그
//                }
//            }
//        });

        // 인박스 버튼 클릭 시 인박스 화면으로 이동
        Button user_inbox = findViewById(R.id.inbox);
        user_inbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityLogActivity.class);
                startActivity(intent);
            }
        });

        // 홈 버튼 클릭 시 홈 화면으로 이동
        Button user_home = findViewById(R.id.user_home);
        user_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomeMainActivity.class);
                startActivity(intent);
            }
        });

        // 로그아웃 버튼 설정
        logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }
        });
    }

    // Firebase에서 프로필 이미지 및 기타 정보 가져오기
    private void getProfileImageFromFirebase() {
        db.collection("users").document(auth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String profileImageUri = documentSnapshot.getString("profileImageUrl");

                            // 로그로 Firebase에서 가져온 프로필 이미지 URI 확인
                            Log.d("profile", "Firebase profileImageUri: " + profileImageUri);

                            if (profileImageUri != null && !profileImageUri.isEmpty()) {
                                Glide.with(profile.this).load(profileImageUri).into(profileImage);
                            } else {
                                profileImage.setImageResource(R.drawable.default_profile_image);  // 기본 이미지 설정
                            }

                            // 사용자 이름과 식물 이름 설정
                            String userName = documentSnapshot.getString("name");
                            String userPlantName = documentSnapshot.getString("plantName");
                            String userclassname = documentSnapshot.getString("classname");
                            name.setText(userName != null ? userName : "이름 없음");
                            classname.setText(userclassname != null ? userclassname : "이름 없음");
                            plantName.setText(userPlantName != null ? userPlantName : "식물 이름 없음");
                        } else {
                            Log.d("ProfileActivity", "No such document in Firestore");
                        }
                    }
                });
    }

//    // 친구 추가 다이얼로그 표시
//    private void showAddFriendDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("친구 추가");
//
//        final EditText input = new EditText(this);
//        input.setInputType(InputType.TYPE_CLASS_TEXT);
//        input.setHint("친구 이름을 입력하세요");
//        builder.setView(input);
//
//        builder.setPositiveButton("추가", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                String friendName = input.getText().toString();
//                if (!friendName.isEmpty()) {
//                    sendFriendRequest(friendName); // 친구 요청 전송
//                } else {
//                    Toast.makeText(profile.this, "친구 이름을 입력하세요.", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//
//        builder.show();
//    }
//
//    // 친구 추가 요청 보내기
//    private void sendFriendRequest(String friendName) {
//        String currentUserId = auth.getCurrentUser().getUid();
//
//        Map<String, Object> friendRequest = new HashMap<>();
//        friendRequest.put("from", currentUserId);
//        friendRequest.put("status", "pending");
//
//        db.collection("users")
//                .whereEqualTo("name", friendName)
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
//                        String recipientUserId = task.getResult().getDocuments().get(0).getId();
//                        db.collection("users").document(recipientUserId)
//                                .collection("friendRequests")
//                                .document(currentUserId)
//                                .set(friendRequest)
//                                .addOnSuccessListener(aVoid -> Toast.makeText(profile.this, "친구 요청을 보냈습니다!", Toast.LENGTH_SHORT).show())
//                                .addOnFailureListener(e -> Toast.makeText(profile.this, "친구 요청을 보내는 데 실패했습니다.", Toast.LENGTH_SHORT).show());
//                    } else {
//                        Toast.makeText(profile.this, "친구를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }

    // Firebase에서 친구 ID 가져오기
    private void getFriendIds() {
        db.collection("users")
                .document(auth.getCurrentUser().getUid())
                .collection("friends")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            friends.add(document.getId());  // 친구들의 ID를 리스트에 추가
                        }
                        getFriendCount();
                    } else {
                        Log.d("ProfileActivity", "Error getting friend IDs: ", task.getException());
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 친구 수를 다시 불러오기
        getFriendCount();
    }

    // Firebase에서 친구 수 가져오기
    private void getFriendCount() {
        db.collection("users").document(auth.getCurrentUser().getUid())
                .collection("friends")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int count = task.getResult().size();
                        friendCount.setText("친구\n"+ count + "명");
                    } else {
                        Log.d("ProfileActivity", "Error getting friends: ", task.getException());
                    }
                });
    }
}
