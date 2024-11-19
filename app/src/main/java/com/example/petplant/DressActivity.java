package com.example.petplant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DressActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ConstraintLayout dressLayout;
    private ImageView characterImage;
    private RecyclerView recyclerView;
    private List<StoreItem> backgroundList = new ArrayList<>();
    private List<StoreItem> itemList = new ArrayList<>();
    private StoreItemAdapter adapter;
    private StoreItem selectedBackground;
    private StoreItem selectedItem;
    private Button applyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dress);

        db = FirebaseFirestore.getInstance();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        registerReceiver(characterImageReceiver, new IntentFilter("UPDATE_CHARACTER_IMAGE"), Context.RECEIVER_NOT_EXPORTED);
        dressLayout = findViewById(R.id.dressLayout);
        characterImage = findViewById(R.id.characterImage);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));

        applyButton = findViewById(R.id.applyButton);
        applyButton.setEnabled(false);
        applyButton.setAlpha(0.5f); // 비활성화 상태일 때 투명도 조정

        loadCharacterImage();
        loadPurchasedItems();

        adapter = new StoreItemAdapter(this, new ArrayList<>(), item -> {
            if (backgroundList.contains(item)) {
                selectedBackground = item;
                dressLayout.setBackgroundResource(item.getImageResource());
            } else if (itemList.contains(item)){
                selectedItem = item;
                // onCreate 내에서 인텐트로 전달된 캐릭터 이미지 적용
                //int characterImageResource = getIntent().getIntExtra("characterImage", R.drawable.tomato_character_home);
                characterImage.setImageResource(item.getImageResource());
            }

            // 초록색 테두리 및 적용 상태 업데이트
            for (StoreItem storeItem : backgroundList) {
                storeItem.setCurrentlyApplied(storeItem == selectedBackground);
            }
            for (StoreItem storeItem : itemList) {
                storeItem.setCurrentlyApplied(storeItem == selectedItem);
            }

            adapter.updateItemList(backgroundList);
            adapter.updateItemList(itemList);

            // 적용하기 버튼 활성화 상태 설정
            if (selectedBackground != null || selectedItem != null) {
                applyButton.setEnabled(true);
                applyButton.setAlpha(1.0f); // 활성화 상태일 때 투명도 조정
            } else {
                applyButton.setEnabled(false);
                applyButton.setAlpha(0.5f); // 비활성화 상태일 때 투명도 조정
            }
        });
//        recyclerView.setAdapter(adapter);

        applyButton.setOnClickListener(v -> {
            if (selectedBackground != null || selectedItem != null) {
                saveDesignToFirestore(selectedBackground, selectedItem);
                Toast.makeText(this, "디자인이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                selectedBackground = null;
                selectedItem = null;
                // 적용 버튼 비활성화
                applyButton.setEnabled(false);
                applyButton.setAlpha(0.5f);
                finish();
            }
        });

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    adapter.updateItemList(backgroundList);
                } else {
                    adapter.updateItemList(itemList);
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadCharacterImage() {
        db.collection("users").document(user.getUid())
                .addSnapshotListener((snapshot, e) -> {
                    if (snapshot != null && snapshot.exists()) {
                        Long characterImageResource = snapshot.getLong("characterBaseImage");
                        if (characterImageResource != null) {
                            characterImage.setImageResource(characterImageResource.intValue());
                            characterImage.invalidate();
                            characterImage.requestLayout();
                            Log.d("DressActivity", "Character Image Loaded and Applied");
                        }
                    } else {
                        Log.e("DressActivity", "Character image load failed or user data not found.");
                    }
                });
    }

    // 성장 단계 이미지 업데이트 수신을 위한 BroadcastReceiver
    private final BroadcastReceiver characterImageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 전달된 이미지 리소스가 있는지 확인
            if (intent.hasExtra("characterImage")) {
                int imageResource = intent.getIntExtra("characterImage", -1);
                if (imageResource != -1) { // 유효한 값인지 확인
                    characterImage.setImageResource(imageResource);
                    characterImage.invalidate();
                    characterImage.requestLayout();
                    Log.d("DressActivity", "Character Image Updated via Broadcast: " + imageResource);
                } else {
                    Log.e("DressActivity", "Received invalid character image resource");
                }
            } else {
                Log.e("DressActivity", "No character image resource found in intent");
            }
        }
    };
//    private final BroadcastReceiver characterImageReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            int imageResource = intent.getIntExtra("characterImage", R.drawable.tomato_character_home);
//            characterImage.setImageResource(imageResource);
//            characterImage.invalidate();
//            characterImage.requestLayout();
//            Log.d("DressActivity", "Character Image Updated via Broadcast");
//        }
//    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(characterImageReceiver); // BroadcastReceiver 해제
    }

    private void loadPurchasedItems() {
        String userId = auth.getCurrentUser().getUid();
        db.collection("users").document(userId).collection("purchases")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    backgroundList.clear();
                    itemList.clear();

                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        // Firestore에서 각 필드 값을 가져옴
                        String itemName = document.getString("itemName");
                        Long iconImageLong = document.getLong("iconImage");
                        Long itemImageLong = document.getLong("itemImage");
                        int itemPrice = document.contains("itemPrice") ? document.getLong("itemPrice").intValue() : 0;
                        String itemType = document.getString("type");

                        StoreItem item = new StoreItem(
                                itemName,
                                iconImageLong != null ? iconImageLong.intValue() : R.drawable.icon_default,
                                itemImageLong != null ? itemImageLong.intValue() : R.drawable.tomato_character_default,
                                itemPrice,
                                itemType
                        );
                        item.setPurchased(true);

//                        // Null 체크 및 기본값 설정
//                        int iconImage = iconImageLong != null ? iconImageLong.intValue() : R.drawable.icon_default;
//                        int itemImage = itemImageLong != null ? itemImageLong.intValue() : R.drawable.tomato_character_default;
//
//                        // StoreItem 객체 생성
//                        StoreItem item = new StoreItem(itemName, iconImage, itemImage, itemPrice, itemType);
//                        item.setPurchased(true);

//                        // Firestore에서 현재 적용 중인 상태 불러오기
//                        db.collection("users").document(userId).get()
//                                .addOnSuccessListener(snapshot -> {
//                                    if (snapshot.contains("selectedBackgroundImage")) {
//                                        Long selectedBackgroundImage = snapshot.getLong("selectedBackgroundImage");
//                                        if (selectedBackgroundImage != null && selectedBackgroundImage.intValue() == item.getImageResource()) {
//                                            item.setCurrentlyApplied(true); // 배경: 현재 적용 중으로 설정
//                                        }
//                                    }
//                                    if (snapshot.contains("selectedItemImage")) {
//                                        Long selectedItemImage = snapshot.getLong("selectedItemImage");
//                                        if (selectedItemImage != null && selectedItemImage.intValue() == item.getImageResource()) {
//                                            item.setCurrentlyApplied(true); // 아이템: 현재 적용 중으로 설정
//                                        }
//                                    }
//                                });

                        if (item.isBackground()) {
                            backgroundList.add(item);
                        } else if (item.isItem()) {
                            itemList.add(item);
                        }
                    }

                    // 기본 배경 추가
                    StoreItem defaultBackground = new StoreItem(
                            "기본 배경",
                            R.drawable.icon_default,
                            R.drawable.background_default,
                            0,
                            "background"
                    );
                    defaultBackground.setCurrentlyApplied(true); // 기본 배경 적용 중으로 설정
                    backgroundList.add(defaultBackground);

                    adapter.updateItemList(backgroundList); // 기본적으로 배경 리스트를 보여줌
                    recyclerView.setAdapter(adapter); // 어댑터 연결

                    // Firestore에서 적용 중인 상태 설정
                    db.collection("users").document(userId)
                            .get()
                            .addOnSuccessListener(snapshot -> {
                                if (snapshot.exists()) {
                                    Long selectedBackground = snapshot.getLong("selectedBackgroundImage");
                                    Long selectedItem = snapshot.getLong("selectedItemImage");

                                    for (StoreItem item : backgroundList) {
                                        item.setCurrentlyApplied(item.getImageResource() == (selectedBackground != null ? selectedBackground.intValue() : -1));
                                    }
                                    for (StoreItem item : itemList) {
                                        item.setCurrentlyApplied(item.getImageResource() == (selectedItem != null ? selectedItem.intValue() : -1));
                                    }
                                    adapter.updateItemList(backgroundList); // 기본적으로 배경 리스트 먼저 보여줌
                                }
                            });
                })
                .addOnFailureListener(e -> Log.e("DressActivity", "구매한 아이템을 불러오는 중 오류 발생", e));
    }

    private void saveDesignToFirestore(StoreItem background, StoreItem item) {
        String userId = auth.getCurrentUser().getUid();

        if (background != null) {
            db.collection("users").document(userId)
                    .update("selectedBackground", background.getName(),
                            "selectedBackgroundIconImage", background.getIconImageResource(),
                            "selectedBackgroundImage", background.getImageResource())
                    .addOnSuccessListener(aVoid -> Log.d("DressActivity", "배경 디자인이 저장되었습니다."));
        }
//        else {
//            db.collection("users").document(userId)
//                    .update("selectedBackground", "기본 배경",
//                            "selectedBackgroundIconImage", R.drawable.icon_default,
//                            "selectedBackgroundImage", R.drawable.background_default)
//                    .addOnSuccessListener(aVoid -> Log.d("DressActivity", "기본 배경이 저장되었습니다."));
//        }

        if (item != null) {
            db.collection("users").document(userId)
                    .update("selectedItem", item.getName(),
                            "selectedItemIconImage", item.getIconImageResource(),
                            "selectedItemImage", item.getImageResource())
                    .addOnSuccessListener(aVoid -> Log.d("DressActivity", "아이템 디자인이 저장되었습니다."));
        }
//        else {
//            db.collection("users").document(userId)
//                    .update("selectedItem", null,
//                            "selectedItemIconImage", null,
//                            "selectedItemImage", null)
//                    .addOnSuccessListener(aVoid -> Log.d("DressActivity", "아이템 정보가 초기화되었습니다."));
//        }
    }
}
