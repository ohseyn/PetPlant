package com.example.petplant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class HomeMainActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private PageAdapter pageAdapter;
    private ViewPager2 viewPager;
    private ImageView character;
    private ConstraintLayout homeLayout;
    private TextView speechBubble;
    private TextView timeTextView;
    private TextView character_name;
    private TextView shop_coin;
    private Handler handler = new Handler();
    private Runnable timeUpdater;
    private SharedPreferences sharedPreferences;
    private long daysSinceSignUp; // 경과 일수 변수 추가
    private String name;
    private String plantName;
    private Long coin;
    private String profileUIri;
    private List<PageItem> pageItems;

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
        character.setImageResource(R.drawable.tomato_character_home);  // 기본 이미지 설정
        speechBubble = findViewById(R.id.speechbubble);
        viewPager = findViewById(R.id.viewPager);
        timeTextView = findViewById(R.id.timeTextView);
        shop_coin = findViewById(R.id.coin);
        homeLayout = findViewById(R.id.main); // 메인 레이아웃

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
            loadSelectedDesign(); // 선택된 디자인을 불러와서 적용
        } else {
            Log.e("HomeMainActivity", "User is not logged in");
        }

        // 퀘스트 상태 불러오기
        Intent intent = getIntent();
        for (int i = 0; i < 8; i++) {
            boolean isCompleted = intent.getBooleanExtra("completed" + i, false);
            if (isCompleted) {
                saveQuestCompletion(i);
            }
        }
    }

    private void setButtonListeners() {
        ImageButton inbox = findViewById(R.id.inbox);
        inbox.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), ActivityLogActivity.class)));

        ImageButton profile = findViewById(R.id.profile);
        profile.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), profile.class);
            intent.putExtra("name", name);
            intent.putExtra("plantName", plantName);
            intent.putExtra("profileImageUri", profileUIri);
            startActivity(intent);
        });

        ImageButton bell = findViewById(R.id.bell);
        bell.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), FriendRequestsActivity.class)));

        ImageButton store = findViewById(R.id.store);
        store.setOnClickListener(view -> openStore());

        ImageButton guide = findViewById(R.id.guide);
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

        Intent intent = getIntent();
        if (intent != null) {
            int questPosition = intent.getIntExtra("questPosition", -1);
            boolean isCompleted = intent.getBooleanExtra("isCompleted", false);

            if (isCompleted && questPosition != -1) {
                // 데이터 모델에서 버튼 상태 업데이트
                pageItems.get(questPosition).setButtonText("완료");
                pageItems.get(questPosition).setButtonEnabled(false);

                // 어댑터에 변경사항 반영
                pageAdapter.notifyItemChanged(questPosition);
            }
        }
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
                // 데이터 모델에서 버튼 상태 업데이트
                PageItem completedItem = pageItems.get(questPosition);
                completedItem.setButtonText("완료");
                completedItem.setButtonEnabled(false);

                // 뷰 홀더를 직접 찾아 상태 변경 (뷰가 화면에 있는 경우)
                RecyclerView recyclerView = findViewById(R.id.viewPager);
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(questPosition);

                if (viewHolder != null && viewHolder instanceof PageAdapter.PageViewHolder) {
                    PageAdapter.PageViewHolder pageViewHolder = (PageAdapter.PageViewHolder) viewHolder;
                    pageViewHolder.actionButton.setText("완료");
                    pageViewHolder.actionButton.setEnabled(false);
                } else {
                    // 전체 어댑터 항목 강제 갱신
                    runOnUiThread(() -> {
                        pageAdapter.notifyDataSetChanged();
                    });
                }

                // SharedPreferences에 완료 상태 저장
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("quest" + questPosition, true);
                editor.apply();
            }
        }
    }

    private void loadQuestStatus() {
        for (int i = 0; i < 8; i++) {
            if (sharedPreferences.getBoolean("quest" + i, false)) {
                // 완료된 퀘스트 버튼 상태 업데이트
                pageAdapter.updateButtonState(i, "완료", false);
            }
        }
    }

    private void saveQuestCompletion(int questPosition) {
        pageAdapter.updateButtonState(questPosition, "완료", false);
        pageAdapter.notifyItemChanged(questPosition); // 필요한 아이템만 갱신
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("quest" + questPosition, true);
        editor.apply();
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

    private void calculateDaysSinceSignUp(Long signUpDate) {
        if (signUpDate != null) {
            long currentDate = System.currentTimeMillis();

            // Calendar 인스턴스를 생성하여 시간 부분 제거
            Calendar signUpCalendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
            signUpCalendar.setTimeInMillis(signUpDate);
            signUpCalendar.set(Calendar.HOUR_OF_DAY, 0);
            signUpCalendar.set(Calendar.MINUTE, 0);
            signUpCalendar.set(Calendar.SECOND, 0);
            signUpCalendar.set(Calendar.MILLISECOND, 0);

            Calendar currentCalendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
            currentCalendar.setTimeInMillis(currentDate);
            currentCalendar.set(Calendar.HOUR_OF_DAY, 0);
            currentCalendar.set(Calendar.MINUTE, 0);
            currentCalendar.set(Calendar.SECOND, 0);
            currentCalendar.set(Calendar.MILLISECOND, 0);

            long daysSinceSignUp = (currentCalendar.getTimeInMillis() - signUpCalendar.getTimeInMillis()) / (1000 * 60 * 60 * 24);

            Log.d("HomeMainActivity", "daysSinceSignUp calculated: " + daysSinceSignUp);
            timeTextView.setText(" " + (daysSinceSignUp + 1)); // UI에 경과 일수 표시
            // 특정 일수에 다이얼로그 표시
            checkAndShowDialog(daysSinceSignUp + 1);
        } else {
            Log.e("HomeMainActivity", "가입일 정보가 없습니다.");
        }
    }

    private void startTimer() {
        // 현재 시간
        long currentTimeMillis = System.currentTimeMillis();

        // 자정까지 남은 시간 계산
        long millisInADay = 24 * 60 * 60 * 1000;
        long nextMidnightMillis = ((currentTimeMillis / millisInADay) + 1) * millisInADay;
        long delayUntilMidnight = nextMidnightMillis - currentTimeMillis;

        // 자정에 경과 일수 업데이트
        handler.postDelayed(() -> {
            runOnUiThread(() -> {
                GetData(); // 자정에 데이터를 다시 가져와 경과 일수 업데이트
                calculateDaysSinceSignUp(sharedPreferences.getLong("signUpDate", 0L)); // 경과 일수 갱신
            });
            startTimer(); // 다음 자정을 위해 타이머 다시 설정
        }, delayUntilMidnight);
        handler.post(timeUpdater);
        loadQuestStatus();
    }

    private void checkAndShowDialog(long daysSinceSignUp) {
        if (user == null) {
            Log.e("HomeMainActivity", "User not authenticated.");
            return;
        }

        // 계정별 SharedPreferences 키 생성
        String userId = user.getUid();
        String preferencesKey = "DialogPreferences_" + userId; // 계정별로 구분되는 SharedPreferences 이름
        SharedPreferences preferences = getSharedPreferences(preferencesKey, Context.MODE_PRIVATE);
        long lastShownDate = preferences.getLong("lastShownDate", -1);
        boolean notYetPressed = preferences.getBoolean("notYetPressed", false);

        if (notYetPressed || daysSinceSignUp == 10 || daysSinceSignUp == 27 || daysSinceSignUp == 37 || daysSinceSignUp == 47) {
            if (lastShownDate != daysSinceSignUp || notYetPressed) { // 같은 날 다이얼로그가 이미 표시되지 않았는지 확인
                showProgressDialog(daysSinceSignUp);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putLong("lastShownDate", daysSinceSignUp);
                //editor.putBoolean("notYetPressed", false);
                editor.apply();
            }
        }
    }

    private void showProgressDialog(long daysSinceSignUp) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_question, null);
        builder.setView(dialogView);

        TextView questionTitle = dialogView.findViewById(R.id.questionTitle);
        Button notYetButton = dialogView.findViewById(R.id.notYetButton);
        Button confirmedButton = dialogView.findViewById(R.id.confirmedButton);
        //String message = "";

        switch ((int) daysSinceSignUp) {
            case 10:
                questionTitle.setText("왕큰방울이의 꽃이 폈나요?");
                break;
            case 27:
                questionTitle.setText("왕큰방울이의 열매가 열렸나요?");
                break;
            case 37:
                questionTitle.setText("왕큰방울이의 열매가 주황색으로 익었나요?");
                break;
            case 47:
                questionTitle.setText("왕큰방울이의 열매가 빨갛게 익었나요?");
                break;
        }

        AlertDialog dialog = builder.create();
        dialog.show();

        notYetButton.setOnClickListener(v -> {
            dialog.dismiss();
            showNegativeDialog();
        });

        confirmedButton.setOnClickListener(v -> {
            dialog.dismiss();
            showPositiveDialog();
        });
    }

    private void showPositiveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_positive, null);
        builder.setView(dialogView);

        Button positiveConfirmButton = dialogView.findViewById(R.id.positiveConfirmButton);
        AlertDialog dialog = builder.create();
        dialog.show();

        positiveConfirmButton.setOnClickListener(v -> {
            updateCharacterImage();  // 캐릭터 이미지 업데이트 및 Firestore 저장
            dialog.dismiss();
        });
    }

    private void updateCharacterImage() {
        String userId = user.getUid();
        int newCharacterImage;

        // 경과 일수에 따라 캐릭터 이미지 변경
        switch ((int) daysSinceSignUp) {
            case 10:
                newCharacterImage = R.drawable.tomato_character_flower; // 꽃 상태 이미지
                break;
            case 27:
                newCharacterImage = R.drawable.tomato_character_home; // 열매(초기)
                break;
            case 37:
                newCharacterImage = R.drawable.tomato_character_home; // 열매(중기)
                break;
            case 47:
                newCharacterImage = R.drawable.tomato_character_home; // 열매(말기)
                break;
            default:
                return;
        }

        character.setImageResource(newCharacterImage);

        // Firestore에 변경된 캐릭터 이미지 저장
        db.collection("users").document(userId)
                .update("selectedCharacterImage", newCharacterImage)
                .addOnSuccessListener(aVoid -> Log.d("HomeMainActivity", "캐릭터 이미지가 업데이트되었습니다."))
                .addOnFailureListener(e -> Log.e("HomeMainActivity", "캐릭터 이미지 업데이트 실패", e));
    }

    private void showNegativeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_negative, null);
        builder.setView(dialogView);

        Button negativeConfirmButton = dialogView.findViewById(R.id.negativeConfirmButton);
        AlertDialog dialog = builder.create();
        dialog.show();

        negativeConfirmButton.setOnClickListener(v -> {
            dialog.dismiss();

            // 부정 응답을 기록하기 위해 SharedPreferences 업데이트
            String userId = user.getUid();
            String preferencesKey = "DialogPreferences_" + userId;
            SharedPreferences preferences = getSharedPreferences(preferencesKey, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            // 'notYetPressed' 상태를 true로 설정
            editor.putBoolean("notYetPressed", true);
            editor.apply();
        });
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("왕큰방울이의 성장을 기다리는 중입니다.")
                .setMessage("다음 날 다시 확인해보세요.")
                .setPositiveButton("확인", (dialog, which) -> {
                    // 다음날 다이얼로그가 다시 뜨도록 설정
                    Log.d("HomeMainActivity", "사용자가 확인을 누르고 다이얼로그 종료");
                })
                .show();
    }

    private void GetData() {
        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                name = documentSnapshot.getString("name");
                plantName = documentSnapshot.getString("plantName");
                character_name.setText(plantName + "와");

                coin = documentSnapshot.getLong("coin");
                shop_coin.setText(String.valueOf(coin));

                // 가입일 확인 및 저장
                Long signUpDate = documentSnapshot.getLong("signUpDate");
                Log.d("HomeMainActivity", "signUpDate from Firestore: " + signUpDate); // 로그 추가

                if (signUpDate == null || signUpDate == 0) {
                    // 가입일이 없으면 현재 시간을 저장
                    long currentTime = System.currentTimeMillis();
                    docRef.update("signUpDate", currentTime)
                            .addOnSuccessListener(aVoid -> Log.d("HomeMainActivity", "가입일이 저장되었습니다."))
                            .addOnFailureListener(e -> Log.e("HomeMainActivity", "가입일 저장 실패", e));
                    signUpDate = currentTime;
                } else {
                    // 이미 저장된 가입일을 SharedPreferences에 저장
                    sharedPreferences.edit().putLong("signUpDate", signUpDate).apply();
                }

                // 경과 일수 계산 및 표시
                calculateDaysSinceSignUp(signUpDate); // 수정된 호출 부분

                String path = documentSnapshot.getString("userImageUrl");
                if (path != null) {
                    FirebaseStorage.getInstance().getReference().child(path)
                            .getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                profileUIri = uri.toString();
                                setupViewPager(); // 데이터 로드 후 ViewPager 설정
                            })
                            .addOnFailureListener(exception ->
                                    Log.d("HomeMainActivity", exception.toString()));
                } else {
                    setupViewPager(); // 이미지가 없는 경우에도 ViewPager 설정
                }
            }
        }).addOnFailureListener(e ->
                Log.e("HomeMainActivity", "Firestore 에러: ", e));
    }

    private void loadSelectedDesign() {
        // Firestore에서 사용자 선택 배경과 아이템 불러오기
        String userId = user.getUid();
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot != null && snapshot.exists()) {
                        Long selectedBackground = snapshot.getLong("selectedBackgroundImage");
                        Long selectedItemImage = snapshot.getLong("selectedItemImage");

                        if (selectedBackground != null) {
                            homeLayout.setBackgroundResource(selectedBackground.intValue());
                        }
                        if (selectedItemImage != null) {
                            character.setImageResource(selectedItemImage.intValue());
                        }
                    }
                });
    }
}
