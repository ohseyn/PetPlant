package com.example.petplant;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class profile extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private CircleImageView profileImage;
    private TextView name, plantName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Firebase 초기화
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // UI 요소 연결
        profileImage = findViewById(R.id.profileImageView);
        name = findViewById(R.id.name);
        plantName = findViewById(R.id.plantName);

        // Firebase에서 프로필 정보를 가져와 설정
        getProfileImageFromFirebase();

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

        Button user_inbox = findViewById(R.id.user_inbox);
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
}
