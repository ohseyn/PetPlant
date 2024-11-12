package com.example.petplant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FriendAddActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private ArrayList<Map<String, String>> allUsers = new ArrayList<>();
    private FriendListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_add);

        ImageButton back_profile = findViewById(R.id.back_profile);
        back_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FriendList.class);
                startActivity(intent);
            }
        });

        db = FirebaseFirestore.getInstance();

        EditText searchBar = findViewById(R.id.search_bar);
        RecyclerView recyclerView = findViewById(R.id.rv_add_friend);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new FriendListAdapter(allUsers, this);
        recyclerView.setAdapter(adapter);

        searchBar.setOnEditorActionListener((v, actionId, event) -> {
            String query = searchBar.getText().toString();
            if (!query.isEmpty()) {
                searchUsers(query);
            } else {
                Toast.makeText(FriendAddActivity.this, "이름을 입력하세요.", Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }

    private void searchUsers(String name) {
        db.collection("users")
                .whereEqualTo("name", name)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        allUsers.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String userName = document.getString("name");
                            String userPlantName = document.getString("plantName");
                            String profileImageUri = document.getString("profileImageUrl");

                            Map<String, String> userData = new HashMap<>();
                            userData.put("id", document.getId());
                            userData.put("name", userName);
                            userData.put("plantName", userPlantName);
                            userData.put("profileImageUri", profileImageUri);
                            userData.put("isFriend", "false"); // 친구 여부 false로 설정
                            allUsers.add(userData);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(FriendAddActivity.this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
