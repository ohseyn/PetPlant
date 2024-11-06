package com.example.petplant;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendList extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ArrayList<Map<String, String>> friendsInfo = new ArrayList<>();
    private FriendListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendlist);

        // Firebase 초기화
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        Button back_profile = findViewById(R.id.back_profile);
        back_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), profile.class);
                startActivity(intent);
            }
        });

        Button friendPlus = findViewById(R.id.friend_plus);
        friendPlus.setOnClickListener(view -> {
            Intent intent = new Intent(FriendList.this, FriendAddActivity.class);
            startActivity(intent);
        });

        // RecyclerView 설정
        RecyclerView recyclerView = findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FriendListAdapter(friendsInfo, this);
        recyclerView.setAdapter(adapter);

        // 검색 기능
        EditText searchBar = findViewById(R.id.search_bar);
        searchBar.setOnEditorActionListener((v, actionId, event) -> {
            String query = searchBar.getText().toString();
            if (!query.isEmpty()) {
                searchFriend(query);
            } else {
                // 검색어가 비어있을 경우 전체 목록을 다시 보여줌
                adapter.updateList(friendsInfo);
            }
            return true;
        });

        // Intent로 전달된 친구 목록 가져오기
        ArrayList<String> friends = getIntent().getStringArrayListExtra("friends");
        if (friends != null && !friends.isEmpty()) {
            getFriendsFromFirestore(friends);
        } else {
            Log.d("FriendList", "No friends available.");
            // 친구 추가 화면으로 바로 이동하는 대신 빈 목록을 유지하여 처리
        }

//        // Intent에서 friends 배열 받기
//        ArrayList<String> friends = getIntent().getStringArrayListExtra("friends");
//        ArrayList<Map<String, String>> friendsInfo = new ArrayList<>();
//
//        if (friends != null) {
//            // 친구 정보를 비동기적으로 가져옴
//            for (String friend : friends) {
//                db.collection("users").document(friend)
//                        .get()
//                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                            @Override
//                            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                if (documentSnapshot.exists()) {
//                                    String userName = documentSnapshot.getString("name");
//                                    String userPlantName = documentSnapshot.getString("plantName");
//                                    String profileImageUri = documentSnapshot.getString("profileImageUrl");
//
//                                    Map<String, String> ww = new HashMap<>(); // 변경: emptyMap() 대신 HashMap 사용
//                                    ww.put("name", userName);
//                                    ww.put("plantName", userPlantName);
//                                    ww.put("profileImageUri", profileImageUri);
//                                    friendsInfo.add(ww);
//
//                                    // 모든 친구 정보를 가져온 후 RecyclerView 업데이트
//                                    if (friendsInfo.size() == friends.size()) { // 모든 친구 정보를 다 가져왔을 때
//                                        updateRecyclerView(friendsInfo);
//                                    }
//                                } else {
//                                    Log.d("ProfileActivity", "No such document in Firestore");
//                                }
//                            }
//                        });
//            }
//        }
    }

    // Firebase에서 친구 정보 가져오기
    private void getFriendsFromFirestore(ArrayList<String> friends) {
        friendsInfo.clear(); // 기존 친구 목록 초기화
        for (String friendId : friends) {
            db.collection("users").document(friendId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String userName = documentSnapshot.getString("name");
                            String userPlantName = documentSnapshot.getString("plantName");
                            String profileImageUri = documentSnapshot.getString("profileImageUrl");

                            Map<String, String> friendData = new HashMap<>();
                            friendData.put("id", documentSnapshot.getId());
                            friendData.put("name", userName);
                            friendData.put("plantName", userPlantName);
                            friendData.put("profileImageUri", profileImageUri);
                            friendData.put("isFriend", "true"); // 친구 여부

                            friendsInfo.add(friendData);
                            // RecyclerView 갱신
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d("FriendList", "친구 정보를 찾을 수 없습니다.");
                        }
                    })
                    .addOnFailureListener(e -> Log.d("FriendList", "Error: " + e.getMessage()));
        }
    }

    // 친구 이름으로 검색하는 기능
    private void searchFriend(String friendName) {
        if (friendName.isEmpty()) {
            // 검색어가 비어 있으면 전체 친구 목록을 다시 보여줌
            adapter.updateList(friendsInfo);
            return;
        }

        ArrayList<Map<String, String>> filteredList = new ArrayList<>();
        for (Map<String, String> friend : friendsInfo) {
            if (friend.get("name").toLowerCase().contains(friendName.toLowerCase())) {
                filteredList.add(friend);
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
        } else {
            adapter.updateList(filteredList);  // 검색 결과 업데이트
        }
    }

//    // RecyclerView 업데이트 메서드
//    private void updateRecyclerView(ArrayList<Map<String, String>> friendsInfo) {
//        RecyclerView recyclerView = findViewById(R.id.rv);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(linearLayoutManager);
//
//        FriendListAdapter customAdapter = new FriendListAdapter(friendsInfo);
//        recyclerView.setAdapter(customAdapter);
//
//        if (!friendsInfo.isEmpty()) {
//            Log.d("FriendList", friendsInfo.get(0).get("name"));
//        }
//    }

}
