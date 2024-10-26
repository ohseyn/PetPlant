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

import java.util.ArrayList;
import java.util.List;

public class HomeMainActivity extends AppCompatActivity {
    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseUser user;
    String name;
    String plantName;
    Long coin;
    String profileUIri;

    private ViewPager2 viewPager;
    private PageAdapter pageAdapter;
    private ImageView character;
    private TextView speechBubble;
    private TextView timeTextView;
    private TextView character_name;
    private TextView shop_coin;
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
        timeTextView = findViewById(R.id.timeTextView);
        shop_coin = findViewById(R.id.coin);

        GetData(); // 데이터 가져오기

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("QuestPreferences", Context.MODE_PRIVATE);

        // 버튼 이벤트 핸들러들
        Button inbox = findViewById(R.id.inbox);
        inbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityLogActivity.class);
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
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                // Firestore에서 유저의 코인 값을 가져와 Intent에 전달
                db.collection("users").document(userId).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                Long coin = documentSnapshot.getLong("coin");
                                if (coin == null) {
                                    coin = 0L;  // 코인 값이 null일 경우 0으로 설정
                                }
                                Log.d("HomeMainActivity", "코인 값: " + coin);

                                // Intent로 코인 값 전달
                                Intent intent = new Intent(getApplicationContext(), StoreActivity.class);
                                intent.putExtra("coin", coin);
                                startActivity(intent);
                            } else {
                                Log.e("HomeMainActivity", "유저 데이터가 존재하지 않습니다.");
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e("HomeMainActivity", "Firestore 에러: ", e);
                        });
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
        pageItems.add(new PageItem("가꾸기 활동", "물 주기", "+15C", "하러 가기 >", R.drawable.grow));
        pageItems.add(new PageItem("가꾸기 활동", "곁순 제거하기", "+15C", "하러 가기 >", R.drawable.exceed));
        pageItems.add(new PageItem("더 보살피기 활동", "인공수정 하기", "+15C", "하러 가기 >", R.drawable.grow));
        pageItems.add(new PageItem("더 보살피기 활동", "비료 주기", "+15C", "하러 가기 >", R.drawable.exceed));
        pageItems.add(new PageItem("친해지기 활동", "향 맡아보기", "+15C", "하러 가기 >", R.drawable.friendly));
        pageItems.add(new PageItem("친해지기 활동", "바라보기", "+15C", "하러 가기 >", R.drawable.friendly));
        pageItems.add(new PageItem("친해지기 활동", "쓰다듬고 만지기", "+15C", "하러 가기 >", R.drawable.friendly));
        pageItems.add(new PageItem("친해지기 활동", "말 걸기", "+15C", "하러 가기 >", R.drawable.friendly));
        pageAdapter = new PageAdapter(this, pageItems, new PageAdapter.OnItemClickListener() {
            @Override
            public void onButtonClick(int position) {
                Intent intent = null;

                switch (position) {
                    case 0:
                        intent = new Intent(HomeMainActivity.this, waterquest_introduce.class);
                        break;
                    case 1:
                        intent = new Intent(HomeMainActivity.this, removequest_introduce.class);
                        break;
                    case 2:
                        intent = new Intent(HomeMainActivity.this, artificial_introduce.class);
                        break;
                    case 3:
                        intent = new Intent(HomeMainActivity.this, sand_introduce.class);
                        break;
                    case 4:
                        intent = new Intent(HomeMainActivity.this, smellquest_text.class);
                        break;
                    case 5:
                        intent = new Intent(HomeMainActivity.this, looking_text.class);
                        break;
                    case 6:
                        intent = new Intent(HomeMainActivity.this, touching_text.class);
                        break;
                    case 7:
                        intent = new Intent(HomeMainActivity.this, talking_text.class);
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
        if (sharedPreferences.getBoolean("quest3", false)) {
            pageAdapter.updateButtonText(3, "완료");
            Log.d("QuestStatus", "Quest 3 loaded as completed");
        }
        if (sharedPreferences.getBoolean("quest4", false)) {
            pageAdapter.updateButtonText(4, "완료");
            Log.d("QuestStatus", "Quest 4 loaded as completed");
        }
        if (sharedPreferences.getBoolean("quest5", false)) {
            pageAdapter.updateButtonText(5, "완료");
            Log.d("QuestStatus", "Quest 5 loaded as completed");
        }
        if (sharedPreferences.getBoolean("quest6", false)) {
            pageAdapter.updateButtonText(6, "완료");
            Log.d("QuestStatus", "Quest 6 loaded as completed");
        }
        if (sharedPreferences.getBoolean("quest7", false)) {
            pageAdapter.updateButtonText(7, "완료");
            Log.d("QuestStatus", "Quest 7 loaded as completed");
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
            Log.d("QuestStatus", "Quest 3 completed");// 두 번째 퀘스트 완료 상태 저장
        }
        if (getIntent().getBooleanExtra("completed4", false)) {
            pageAdapter.updateButtonText(3, "완료");
            saveQuestStatus(3);
            Log.d("QuestStatus", "Quest 4 completed");// 두 번째 퀘스트 완료 상태 저장
        }
        if (getIntent().getBooleanExtra("completed5", false)) {
            pageAdapter.updateButtonText(4, "완료");
            saveQuestStatus(4);
            Log.d("QuestStatus", "Quest 5 completed");// 세 번째 퀘스트 완료 상태 저장
        }
        if (getIntent().getBooleanExtra("completed6", false)) {
            pageAdapter.updateButtonText(5, "완료");
            saveQuestStatus(5);
            Log.d("QuestStatus", "Quest 6 completed");// 세 번째 퀘스트 완료 상태 저장
        }
        if (getIntent().getBooleanExtra("completed7", false)) {
            pageAdapter.updateButtonText(6, "완료");
            saveQuestStatus(6);
            Log.d("QuestStatus", "Quest 7 completed");// 세 번째 퀘스트 완료 상태 저장
        }
        if (getIntent().getBooleanExtra("completed8", false)) {
            pageAdapter.updateButtonText(7, "완료");
            saveQuestStatus(7);
            Log.d("QuestStatus", "Quest 8 completed");// 세 번째 퀘스트 완료 상태 저장
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
                    if (snapshot.contains("coin")) {
                        Long coinValue = snapshot.getLong("coin");
                        shop_coin.setText(String.valueOf(coinValue != null ? coinValue :coin));
                    } else {
                        Log.d("TAG", "코인 값이 Firestore에 없습니다.");
                        shop_coin.setText("0");
                    }

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
