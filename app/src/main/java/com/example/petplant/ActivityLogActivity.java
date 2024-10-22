package com.example.petplant;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActivityLogActivity extends AppCompatActivity {
    private RecyclerView recyclerViewActivities;
    private ActivityLogAdapter adapter;
    private FirebaseFirestore db;
    private List<UserActivity> activityList;
    private String currentTab = "friends";  // 기본값은 친구들의 활동
    private String currentDate;
    private TextView textDate;
    private ImageButton buttonPrevious, buttonNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        Button user_home = findViewById(R.id.user_home);
        user_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomeMainActivity.class);
                startActivity(intent);
            }
        });

        Button profile = findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), profile.class);
                startActivity(intent);
            }
        });

        // View 초기화
        recyclerViewActivities = findViewById(R.id.recyclerView_activities);
        recyclerViewActivities.setLayoutManager(new LinearLayoutManager(this));
        activityList = new ArrayList<>();
        adapter = new ActivityLogAdapter(activityList, activity -> {
            // 좋아요 혹은 스티커와 같은 반응을 처리하는 리스너
            handleReaction(activity);
        });
        recyclerViewActivities.setAdapter(adapter);

        textDate = findViewById(R.id.textDate);
        buttonPrevious = findViewById(R.id.buttonPrevious);
        buttonNext = findViewById(R.id.buttonNext);

        // Firebase 초기화
        db = FirebaseFirestore.getInstance();
        currentDate = getCurrentDate();  // 초기 날짜 설정

        updateDateDisplay();  // 날짜 디스플레이 업데이트

        // 탭 선택 리스너 설정 (친구 활동 / 내 활동)
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    currentTab = "friends";
                } else {
                    currentTab = "my";
                }
                loadActivities();  // 탭 변경 시 활동 기록 로드
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // 날짜 변경 버튼 리스너 설정
        buttonPrevious.setOnClickListener(v -> changeDate(-1));
        buttonNext.setOnClickListener(v -> changeDate(1));

        // 처음 화면 로드시 활동 기록 로드
        loadActivities();
    }

    private void loadActivities() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // 선택된 날짜의 시작 시간과 끝 시간을 구함
        Timestamp startTimestamp = getStartOfDayTimestamp(currentDate);  // 자정 시간
        Timestamp endTimestamp = getEndOfDayTimestamp(currentDate);  // 23:59:59 시간

        // Firestore 쿼리 작성
        Query query;

        if (currentTab.equals("friends")) {
            // 친구들의 활동을 조회하는 쿼리
            query = db.collection("activities")
                    .whereNotEqualTo("userId", userId)  // 현재 사용자가 아닌 활동 조회
                    .whereGreaterThanOrEqualTo("timestamp", startTimestamp)  // 선택한 날짜의 시작 시간
                    .whereLessThanOrEqualTo("timestamp", endTimestamp)  // 선택한 날짜의 끝 시간
                    .orderBy("timestamp", Query.Direction.ASCENDING);
        } else {
            // 내 활동을 조회하는 쿼리
            query = db.collection("activities")
                    .whereEqualTo("userId", userId)  // 현재 사용자 활동 조회
                    .whereGreaterThanOrEqualTo("timestamp", startTimestamp)  // 선택한 날짜의 시작 시간
                    .whereLessThanOrEqualTo("timestamp", endTimestamp)  // 선택한 날짜의 끝 시간
                    .orderBy("timestamp", Query.Direction.ASCENDING);
        }

        // 쿼리 실행 및 데이터 로드
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            activityList.clear();  // 기존 리스트 초기화
            if (!queryDocumentSnapshots.isEmpty()) {
                for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                    UserActivity activity = snapshot.toObject(UserActivity.class);
                    if (activity != null) {
                        activity.setDocumentId(snapshot.getId());
                        activityList.add(activity);  // 리스트에 추가
                    }
                }
                adapter.notifyDataSetChanged();
                Log.d(TAG, "활동 기록을 성공적으로 로드했습니다.");
            } else {
                adapter.notifyDataSetChanged();  // 데이터가 없을 때도 리프레시
                Toast.makeText(this, "해당 날짜에 활동 기록이 없습니다.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "활동 기록이 없습니다.");  // 데이터가 없을 경우
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Firestore 데이터 로드 실패", e);
        });
    }

    // 선택한 날짜의 자정 (00:00:00) Timestamp를 반환
    private Timestamp getStartOfDayTimestamp(String dateString) {
        Calendar calendar = Calendar.getInstance();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            Date date = sdf.parse(dateString);
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            return new Timestamp(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 선택한 날짜의 끝 시간 (23:59:59) Timestamp를 반환
    private Timestamp getEndOfDayTimestamp(String dateString) {
        Calendar calendar = Calendar.getInstance();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            Date date = sdf.parse(dateString);
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            return new Timestamp(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void changeDate(int days) {
        String nextDate = getNextDate(currentDate, days);  // 변경된 날짜
        String today = getCurrentDateWithoutTime();  // 오늘 날짜 (시간 제거)

        // 미래 날짜로 이동할 수 없도록 설정
        if (nextDate.compareTo(today) > 0) {
            Toast.makeText(this, "미래의 활동 기록은 확인할 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;  // 버튼을 막아 더 이상 진행하지 않음
        }

        currentDate = nextDate;  // 날짜 변경
        updateDateDisplay();  // 변경된 날짜 UI 업데이트
        loadActivities();  // 날짜 변경 후 활동 기록 로드
    }

    // 시간을 제외한 오늘 날짜를 구하는 함수
    private String getCurrentDateWithoutTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return sdf.format(new Date());
    }

    private String getCurrentDate() {
        return new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
    }

    private String getNextDate(String currentDate, int days) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        try {
            Date date = sdf.parse(currentDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_YEAR, days);
            return sdf.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return currentDate;
        }
    }

    // 현재 날짜를 TextView에 표시하는 메서드
    private void updateDateDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd(E)", Locale.getDefault());
        try {
            Date date = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).parse(currentDate);
            String formattedDate = sdf.format(date);
            textDate.setText(formattedDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 활동 기록에 좋아요 반응을 추가하는 메서드
    private void handleReaction(UserActivity activity) {
        db.collection("activities").document(activity.getDocumentId())
                .update("likes", activity.getLikes() + 1)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "좋아요 추가됨", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Log.e("ActivityLog", "좋아요 업데이트 오류", e));
    }
}
