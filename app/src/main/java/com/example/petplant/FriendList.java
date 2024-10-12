package com.example.petplant;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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

        // Intent에서 friends 배열 받기
        ArrayList<String> friends = getIntent().getStringArrayListExtra("friends");
        ArrayList<Map<String, String>> friendsInfo = new ArrayList<>();

        if (friends != null) {
            // 친구 정보를 비동기적으로 가져옴
            for (String friend : friends) {
                db.collection("users").document(friend)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    String userName = documentSnapshot.getString("name");
                                    String userPlantName = documentSnapshot.getString("plantName");
                                    String profileImageUri = documentSnapshot.getString("profileImageUrl");

                                    Map<String, String> ww = new HashMap<>(); // 변경: emptyMap() 대신 HashMap 사용
                                    ww.put("name", userName);
                                    ww.put("plantName", userPlantName);
                                    ww.put("profileImageUri", profileImageUri);
                                    friendsInfo.add(ww);

                                    // 모든 친구 정보를 가져온 후 RecyclerView 업데이트
                                    if (friendsInfo.size() == friends.size()) { // 모든 친구 정보를 다 가져왔을 때
                                        updateRecyclerView(friendsInfo);
                                    }
                                } else {
                                    Log.d("ProfileActivity", "No such document in Firestore");
                                }
                            }
                        });
            }
        }
    }

    // RecyclerView 업데이트 메서드
    private void updateRecyclerView(ArrayList<Map<String, String>> friendsInfo) {
        RecyclerView recyclerView = findViewById(R.id.rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        FriendListAdapter customAdapter = new FriendListAdapter(friendsInfo);
        recyclerView.setAdapter(customAdapter);

        if (!friendsInfo.isEmpty()) {
            Log.d("FriendList", friendsInfo.get(0).get("name"));
        }
    }

}
