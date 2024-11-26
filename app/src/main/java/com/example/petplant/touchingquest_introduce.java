package com.example.petplant;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class touchingquest_introduce extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String userPlantName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touchingquestintroduce);

        // Firebase 초기화
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        // 버튼 초기화 및 클릭 리스너 추가
        Button do_quest_touching = findViewById(R.id.do_quest_touching); // `activity_touchingquestintroduce.xml`에 있는 버튼 ID 사용

        if (user != null) {
            db.collection("users").document(user.getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                userPlantName = documentSnapshot.getString("plantName");
                                Log.d("touchingquest_introduce", "Plant name retrieved: " + userPlantName);
                            } else {
                                Log.d("touchingquest_introduce", "No such document");
                                userPlantName = "저"; // 기본값 설정
                            }
                        }
                    });
        } else {
            Log.e("touchingquest_introduce", "User not authenticated.");
        }

        do_quest_touching.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 다음 화면으로 이동하는 Intent 생성
                Intent intent = new Intent(touchingquest_introduce.this, touching_text.class);
                intent.putExtra("plantName", userPlantName != null ? userPlantName : "저");
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
    }
}
