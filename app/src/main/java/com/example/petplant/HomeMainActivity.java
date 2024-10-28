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
import androidx.recyclerview.widget.LinearLayoutManager;
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
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private PageAdapter pageAdapter;
    private ViewPager2 viewPager;
    private ImageView character;
    private TextView speechBubble;
    private TextView timeTextView;
    private TextView character_name;
    private TextView shop_coin;
    private Handler handler = new Handler();
    private Runnable timeUpdater;
    private SharedPreferences sharedPreferences;

    private String name;
    private String plantName;
    private Long coin;
    private String profileUIri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homemain);

        // Firebase 초기화
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        // View 바인딩
        character_name = findViewById(R.id.home_plantName);
        character = findViewById(R.id.tomato_home);
        speechBubble = findViewById(R.id.speechbubble);
        viewPager = findViewById(R.id.viewPager);
        timeTextView = findViewById(R.id.timeTextView);
        shop_coin = findViewById(R.id.coin);

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("QuestPreferences", Context.MODE_PRIVATE);

        // 버튼 이벤트 설정
        setButtonListeners();

        // ViewPager 설정 및 어댑터 연결
        setupViewPager();

        // 캐릭터 클릭 이벤트 설정
        character.setOnClickListener(v -> toggleSpeechBubble());

        // 시간 갱신
        startTimer();

        // Firebase에서 데이터 가져오기
        if (user != null) {
            GetData();
        } else {
            Log.e("HomeMainActivity", "User is not logged in");
        }

        // 퀘스트 상태 불러오기
        loadQuestStatus();
    }

    private void setButtonListeners() {
        Button inbox = findViewById(R.id.inbox);
        inbox.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), ActivityLogActivity.class)));

        Button profile = findViewById(R.id.profile);
        profile.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), profile.class);
            intent.putExtra("name", name);
            intent.putExtra("plantName", plantName);
            intent.putExtra("profileImageUri", profileUIri);
            startActivity(intent);
        });

        Button bell = findViewById(R.id.bell);
        bell.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), FriendRequestsActivity.class)));

        Button store = findViewById(R.id.store);
        store.setOnClickListener(view -> openStore());

        Button guide = findViewById(R.id.guide);
        guide.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), Guide.class)));
    }

    private void setupViewPager() {
        List<PageItem> pageItems = new ArrayList<>();
        String displayPlantName = (plantName != null) ? plantName : "식물";
        pageItems.add(new PageItem("가꾸기 활동", displayPlantName + "에게 물 주기", "+15C", "하러 가기 >", R.drawable.blue, R.color.blue, R.color.blue));
        pageItems.add(new PageItem("가꾸기 활동", displayPlantName + "의 곁순 제거해주기", "+15C", "하러 가기 >", R.drawable.blue, R.color.blue, R.color.blue));
        pageItems.add(new PageItem("더 보살피기 활동", displayPlantName + "에게 인공수정 해주기", "+15C", "하러 가기 >", R.drawable.green, R.color.green, R.color.green));
        pageItems.add(new PageItem("더 보살피기 활동", displayPlantName + "에게 비료 주기", "+15C", "하러 가기 >", R.drawable.green, R.color.green, R.color.green));
        pageItems.add(new PageItem("친해지기 활동", displayPlantName + "의 향 맡아보기", "+15C", "하러 가기 >", R.drawable.pink, R.color.pink, R.color.pink));
        pageItems.add(new PageItem("친해지기 활동", displayPlantName + " 바라보기", "+15C", "하러 가기 >", R.drawable.pink, R.color.pink, R.color.pink));
        pageItems.add(new PageItem("친해지기 활동", displayPlantName + " 쓰다듬고 만지기", "+15C", "하러 가기 >", R.drawable.pink, R.color.pink, R.color.pink));
        pageItems.add(new PageItem("친해지기 활동", displayPlantName + "에게 말 걸기", "+15C", "하러 가기 >", R.drawable.pink, R.color.pink, R.color.pink));

        pageAdapter = new PageAdapter(pageItems, this::handlePageItemClick);
        viewPager.setAdapter(pageAdapter);
        viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
    }

    private void handlePageItemClick(int position) {
        Intent intent = null;
        switch (position) {
            case 0:
                intent = new Intent(this, waterquest_introduce.class);
                break;
            case 1:
                intent = new Intent(this, removequest_introduce.class);
                break;
            case 2:
                intent = new Intent(this, artificial_introduce.class);
                break;
            case 3:
                intent = new Intent(this, sand_introduce.class);
                break;
            case 4:
                intent = new Intent(this, smellquest_text.class);
                break;
            case 5:
                intent = new Intent(this, looking_text.class);
                break;
            case 6:
                intent = new Intent(this, touching_text.class);
                break;
            case 7:
                intent = new Intent(this, talking_text.class);
                break;
        }
        if (intent != null) {
            intent.putExtra("plantName", plantName);
            intent.putExtra("questPosition", position); // 퀘스트 위치 전달
            startActivityForResult(intent, 100);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            int questPosition = data.getIntExtra("questPosition", -1);
            boolean isCompleted = data.getBooleanExtra("isCompleted", false);
            if (isCompleted && questPosition != -1) {
                pageAdapter.updateButtonText(questPosition, "완료");
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("quest" + questPosition, true);
                editor.apply();
            }
        }
    }

    private void openStore() {
        String userId = user.getUid();
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Long coin = documentSnapshot.getLong("coin");
                    Intent intent = new Intent(getApplicationContext(), StoreActivity.class);
                    intent.putExtra("coin", coin != null ? coin : 0L);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> Log.e("HomeMainActivity", "Firestore 에러: ", e));
    }

    private void toggleSpeechBubble() {
        if (speechBubble.getVisibility() == View.GONE) {
            speechBubble.setVisibility(View.VISIBLE);
        } else {
            speechBubble.setVisibility(View.GONE);
        }
    }

    private void startTimer() {
        long startTime = System.currentTimeMillis();
        timeUpdater = () -> {
            long elapsedTimeMillis = System.currentTimeMillis() - startTime;
            long elapsedDays = elapsedTimeMillis / (1000 * 60 * 60 * 24);
            timeTextView.setText(" " + (elapsedDays + 1));
            handler.postDelayed(timeUpdater, 1000);
        };
        handler.post(timeUpdater);
        loadQuestStatus();
    }

    private void GetData() {
        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.addSnapshotListener((snapshot, e) -> {
            if (snapshot != null && snapshot.exists()) {
                name = snapshot.getString("name");
                plantName = snapshot.getString("plantName");
                character_name.setText(plantName + "와");
                shop_coin.setText(String.valueOf(snapshot.getLong("coin")));

                String path = snapshot.getString("userImageUrl");
                if (path != null) {
                    FirebaseStorage.getInstance().getReference().child(path)
                            .getDownloadUrl()
                            .addOnSuccessListener(uri -> profileUIri = uri.toString())
                            .addOnFailureListener(exception -> Log.d("TAG", exception.toString()));
                }

                setupViewPager();
            }
        });
    }

    private void loadQuestStatus() {
        for (int i = 0; i < 8; i++) {
            if (sharedPreferences.getBoolean("quest" + i, false)) {
                pageAdapter.updateButtonText(i, "완료");
            }
        }
    }
}
