package com.example.petplant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HomeMainActivity extends AppCompatActivity {
    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseUser user;
    String name;
    String plantName;
    String profileUIri;

    private ViewPager2 viewPager;
    private PageAdapter pageAdapter;
    private ImageView character;
    private TextView speechBubble;
    private TextView timeTextView;
    private TextView character_name;
    private Handler handler = new Handler();
    private Runnable timeUpdater;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homemain);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        character_name = findViewById(R.id.home_plantName);
        character = findViewById(R.id.tomato_home);
        speechBubble = findViewById(R.id.speechbubble);
        viewPager = findViewById(R.id.viewPager);
        timeTextView = findViewById(R.id.timeTextView); // 새로운 텍스트뷰 (일 단위로 업데이트되는 텍스트)

        GetData(); // 데이터 가져오기

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("QuestPreferences", Context.MODE_PRIVATE);

        // 버튼 이벤트 핸들러들
        Button inbox = findViewById(R.id.inbox);
        inbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), inbox_friend.class);
                startActivity(intent);
            }
        });

        Button profile = findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), profile.class);
                intent.putExtra("name", name);
                intent.putExtra("plantName", plantName);
                intent.putExtra("profileImageUri", profileUIri);
                startActivity(intent);
            }
        });

        Button bell = findViewById(R.id.bell);
        bell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FriendRequestsActivity.class);
                startActivity(intent);
            }
        });

        Button store = findViewById(R.id.store);
        store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ShopActivity.class);
                startActivity(intent);
            }
        });

        Button guide = findViewById(R.id.guide);
        guide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Guide.class);
                startActivity(intent);
            }
        });

        // ViewPager 설정
        List<PageItem> pageItems = new ArrayList<>();
        pageItems.add(new PageItem("가꾸기 활동", "물주기", "+15C", "하러 가기 >", R.drawable.grow));
        pageItems.add(new PageItem("더 보살피기 활동", "곁순 제거하기", "+15C", "하러 가기 >", R.drawable.exceed));
        pageItems.add(new PageItem("친해지기 활동", "향 맡아보기", "+15C", "하러 가기 >", R.drawable.friendly));
        pageAdapter = new PageAdapter(this, pageItems, new PageAdapter.OnItemClickListener() {
            @Override
            public void onButtonClick(int position) {
                Intent intent = null;

                switch (position) {
                    case 0:
                        intent = new Intent(HomeMainActivity.this, Home_waterquestintroduce.class);
                        break;
                    case 1:
                        intent = new Intent(HomeMainActivity.this, Home_removequest_introduce.class);
                        break;
                    case 2:
                        intent = new Intent(HomeMainActivity.this, Home_smellquest.class);
                        break;
                    default:
                        break;
                }

                if (intent != null) {
                    intent.putExtra("plantName", plantName);
                    startActivity(intent);
                }
            }
        });

        viewPager.setAdapter(pageAdapter);

        // 캐릭터 클릭 시 말풍선 표시/숨기기
        character.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (speechBubble.getVisibility() == View.GONE) {
                    speechBubble.setVisibility(View.VISIBLE);
                } else {
                    speechBubble.setVisibility(View.GONE);
                }
            }
        });

        long startTime = System.currentTimeMillis();

        // 날짜 갱신을 위한 Runnable
        timeUpdater = new Runnable() {
            @Override
            public void run() {
                long currentTimeMillis = System.currentTimeMillis();
                long elapsedTimeMillis = currentTimeMillis - startTime; // 시작 시간부터 지난 시간 계산

                long elapsedDays = elapsedTimeMillis / (1000 * 60 * 60 * 24); // 지난 시간의 일 수 계산

                long currentDay = elapsedDays + 1; // 시작하는 날이 1일이므로 +1

                timeTextView.setText(" " + currentDay);

                handler.postDelayed(this, 1000); // 1초 후에 다시 실행 (갱신)
            }
        };
        handler.post(timeUpdater);

        // SharedPreferences에서 상태 불러오기
        loadQuestStatus();
    }

    // 퀘스트 완료 상태를 SharedPreferences에 저장하는 메서드
    private void saveQuestStatus(int questIndex) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("quest" + questIndex, true);
        editor.apply();
        Log.d("QuestStatus", "Quest " + questIndex + " saved as completed");
    }

    // SharedPreferences에서 저장된 상태 불러오기
    private void loadQuestStatus() {
        if (sharedPreferences.getBoolean("quest0", false)) {
            pageAdapter.updateButtonText(0, "완료");
            Log.d("QuestStatus", "Quest 0 loaded as completed");
        }
        if (sharedPreferences.getBoolean("quest1", false)) {
            pageAdapter.updateButtonText(1, "완료");
            Log.d("QuestStatus", "Quest 1 loaded as completed");
        }
        if (sharedPreferences.getBoolean("quest2", false)) {
            pageAdapter.updateButtonText(2, "완료");
            Log.d("QuestStatus", "Quest 2 loaded as completed");
        }
    }

    // Intent를 통해 전달받은 완료 상태를 처리하고 SharedPreferences에 저장
    @Override
    protected void onResume() {
        super.onResume();

        if (getIntent().getBooleanExtra("completed", false)) {
            pageAdapter.updateButtonText(0, "완료");
            saveQuestStatus(0);
            Log.d("QuestStatus", "Quest 0 completed");// 첫 번째 퀘스트 완료 상태 저장
        }

        if (getIntent().getBooleanExtra("completed2", false)) {
            pageAdapter.updateButtonText(1, "완료");
            saveQuestStatus(1);
            Log.d("QuestStatus", "Quest 1 completed");// 두 번째 퀘스트 완료 상태 저장
        }
        if (getIntent().getBooleanExtra("completed3", false)) {
            pageAdapter.updateButtonText(2, "완료");
            saveQuestStatus(2);
            Log.d("QuestStatus", "Quest 2 completed");// 두 번째 퀘스트 완료 상태 저장
        }
    }

    public void GetData() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        super.onStart();
        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d("TAG", "Current data: " + snapshot.getData());

                    // 데이터 업데이트 처리
                    name = snapshot.getString("name");
                    plantName = snapshot.getString("plantName");
                    character_name.setText(plantName + "와");

                    String path = snapshot.getString("userImageUrl");
                    if (path != null && !path.isEmpty()) {
                        storageRef.child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // 이미지 다운로드 성공
                                profileUIri = uri.toString();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // 이미지 다운로드 실패 처리
                                Log.d("TAG", exception.toString());
                            }
                        });
                    }
                } else {
                    Log.d("TAG", "Current data: null");
                }
            }
        });
    }
}
