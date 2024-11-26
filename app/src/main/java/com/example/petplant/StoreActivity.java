package com.example.petplant;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoreActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private Long coin;
    private RecyclerView recyclerView;
    private StoreItemAdapter adapter;
    private List<StoreItem> backgroundList, itemList;
    private Button buyButton;
    private ImageView characterImage;
    private StoreItem selectedBackground, selectedItem;
    private TextView shopCoinTextView;
    private ConstraintLayout storeLayout; // 배경을 변경할 레이아웃
    private int selectedItemPrice = 0;
    private String selectedItemName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        // Firestore와 Auth 초기화
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        storeLayout = findViewById(R.id.storeLayout); // 레이아웃을 변수에 저장
        ImageButton back_profile = findViewById(R.id.back_profile);
        ImageButton dressButton = findViewById(R.id.dress);

        dressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DressActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        back_profile.setOnClickListener(new View.OnClickListener() {
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
                                Intent intent = new Intent(getApplicationContext(), HomeMainActivity.class);
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

        registerReceiver(characterImageReceiver, new IntentFilter("UPDATE_CHARACTER_IMAGE"), Context.RECEIVER_NOT_EXPORTED);

        // View 초기화
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));

        characterImage = findViewById(R.id.characterImage);
        buyButton = findViewById(R.id.buyButton);
        buyButton.setEnabled(false);
        buyButton.setAlpha(0.5f); // 초기 투명도
        shopCoinTextView = findViewById(R.id.coin);

        loadCharacterImage();

        // Intent로 전달된 코인 값 받아오기
        coin = getIntent().getLongExtra("coin", 0L);
        // 코인 값 UI에 표시
        updateCoinTextView();

        // 아이템 리스트 초기화
        backgroundList = getBackgroundItems();
        itemList = getCharacterItems();

        // 선택된 아이템이 구매된 상태인지 확인 후 토스트 메시지 출력
        adapter = new StoreItemAdapter(this, backgroundList, item -> {
            if (item == null) {
                // 선택 해제 로직
                restoreSavedBackground();
                restoreSavedCharacter();
                selectedBackground = null;
                selectedItem = null;
                buyButton.setEnabled(false);
                buyButton.setAlpha(0.5f);
                return;
            }

            if (item.isPurchased()) {
                // 이미 구매한 아이템 클릭 시 처리
                Toast.makeText(this, "이미 구매한 아이템입니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 아이템 선택 시 가격 설정
            selectedItemPrice = item.getPrice();
            selectedItemName = item.getName();

            if (backgroundList.contains(item)) {
                // 배경 아이템 선택 또는 해제
                if (selectedBackground != null && selectedBackground.equals(item)) {
                    // 배경 선택 해제
                    selectedBackground = null;
                    // 사용자가 저장한 배경 상태로 복원
                    restoreSavedBackground();
                } else {
                    // 배경 선택
                    selectedBackground = item;
                    storeLayout.setBackgroundResource(item.getImageResource());
                }
            } else if (itemList.contains(item)) {
                // 캐릭터 아이템 선택 또는 해제
                if (selectedItem != null && selectedItem.equals(item)) {
                    // 캐릭터 아이템 선택 해제
                    selectedItem = null;
                    // 사용자가 저장한 캐릭터 상태로 복원
                    restoreSavedCharacter();
                } else {
                    // 캐릭터 아이템 선택
                    selectedItem = item;
                    characterImage.setImageResource(item.getImageResource());
                }
            }

            // 버튼 활성화/비활성화 상태 갱신
            if (selectedBackground != null || selectedItem != null) {
                buyButton.setEnabled(true);
                buyButton.setAlpha(1.0f);
            } else {
                buyButton.setEnabled(false);
                buyButton.setAlpha(0.5f);
            }

            // RecyclerView 갱신 (UI 업데이트)
            adapter.notifyDataSetChanged();
        });
        recyclerView.setAdapter(adapter);

        // TabLayout 설정
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

        // Firestore에서 구매한 아이템 정보 불러오기
        loadPurchasedItems();
        loadSelectedDesign();

        // 구매 버튼 클릭 리스너 설정
        buyButton.setOnClickListener(v -> {
            if ((selectedBackground != null || selectedItem != null)){
                if (coin >= selectedItemPrice) {
                    showBuyDialog();
                } else {
                    showInsufficientCoinsDialog();
                }
            } else {
                Toast.makeText(StoreActivity.this, "아이템을 선택해주세요.", Toast.LENGTH_SHORT).show();
            }
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
                            Log.d("StoreActivity", "Character Image Loaded and Applied");
                        }
                    } else {
                        Log.e("StoreActivity", "Character image load failed or user data not found.");
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
                    Log.d("StoreActivity", "Character Image Updated via Broadcast: " + imageResource);
                } else {
                    Log.e("v", "Received invalid character image resource");
                }
            } else {
                Log.e("StoreActivity", "No character image resource found in intent");
            }
        }
    };

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
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String purchasedItemName = documentSnapshot.getString("itemName");
                        for (StoreItem item : itemList) {
                            if (item.getName().equals(purchasedItemName)) {
                                item.setPurchased(true);  // 구매한 아이템 표시
                            }
                        }
                        for (StoreItem item : backgroundList) {
                            if (item.getName().equals(purchasedItemName)) {
                                item.setPurchased(true);  // 구매한 배경 표시
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("StoreActivity", "구매한 아이템 정보를 가져오는 중 오류 발생", e);
                });
    }

    private void loadSelectedDesign() {
        String userId = auth.getCurrentUser().getUid();
        db.collection("users").document(userId)
                .addSnapshotListener((snapshot, e) -> {
                    if (snapshot != null && snapshot.exists()) {
                        Long selectedBackground = snapshot.getLong("selectedBackgroundImage");
                        Long selectedItemImage = snapshot.getLong("selectedItemImage");

                        if (selectedBackground != null) {
                            storeLayout.setBackgroundResource(selectedBackground.intValue());
                        }
                        if (selectedItemImage != null) {
                            characterImage.setImageResource(selectedItemImage.intValue());
                        }
                    }
                });
    }

    // 배경 아이템 리스트 생성
    private List<StoreItem> getBackgroundItems() {
        List<StoreItem> list = new ArrayList<>();
        list.add(new StoreItem("무지개", R.mipmap.icon_rainbow, R.drawable.background_rainbow, 200, "background"));
        list.add(new StoreItem("해질녘", R.mipmap.icon_evening, R.drawable.background_evening, 200, "background"));
        list.add(new StoreItem("밤하늘", R.mipmap.icon_night, R.drawable.background_night, 250, "background"));
        list.add(new StoreItem("오로라", R.mipmap.icon_aurora, R.drawable.background_aurora, 230, "background"));
        list.add(new StoreItem("봄날", R.mipmap.icon_spring, R.drawable.background_spring, 350, "background"));
        list.add(new StoreItem("여름", R.mipmap.icon_summer, R.drawable.background_summer, 380, "background"));
        list.add(new StoreItem("가을", R.mipmap.icon_fall, R.drawable.background_fall, 380, "background"));
        list.add(new StoreItem("겨울", R.mipmap.icon_winter, R.drawable.background_winter, 400, "background"));
        return list;
    }

    // 캐릭터 아이템 리스트 생성
    private List<StoreItem> getCharacterItems() {
        List<StoreItem> list = new ArrayList<>();
        list.add(new StoreItem("멋쟁이 안경", R.mipmap.icon_glass, R.drawable.tomato_seed_with_glass, 15, "item"));
        list.add(new StoreItem("굵은 수염", R.mipmap.icon_mustache, R.drawable.tomato_seed_with_mustache, 150, "item"));
        list.add(new StoreItem("리본", R.mipmap.icon_bow, R.drawable.tomato_seed_with_bow, 150, "item"));
        list.add(new StoreItem("멋쟁이 신사", R.mipmap.icon_gentle, R.drawable.tomato_seed_with_gentle, 180, "item"));
        list.add(new StoreItem("책가방", R.mipmap.icon_bag, R.drawable.tomato_seed_with_bag, 180, "item"));
        list.add(new StoreItem("선글라스", R.mipmap.icon_sunglasses, R.drawable.tomato_seed_with_sunglasses, 200, "item"));
        list.add(new StoreItem("귀도리 모자", R.mipmap.icon_hat, R.drawable.tomato_seed_with_hat, 200, "item"));
        list.add(new StoreItem("잎사귀 우산", R.mipmap.icon_leaf, R.drawable.tomato_seed_with_leaf, 230, "item"));
        return list;
    }

    // 구매 다이얼로그 표시
    private void showBuyDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_purchase_confirmation, null);

        ImageView itemImage = dialogLayout.findViewById(R.id.itemImage);
        //TextView itemName = dialogLayout.findViewById(R.id.itemName);
        TextView itemPrice = dialogLayout.findViewById(R.id.itemPrice);
        Button confirmButton = dialogLayout.findViewById(R.id.confirmButton);
        Button cancelButton = dialogLayout.findViewById(R.id.cancelButton);

        // 선택된 아이템이나 배경에 따라 이미지와 이름 설정
        if (selectedItem != null) {
            itemImage.setImageResource(selectedItem.getIconImageResource());
            itemPrice.setText(String.valueOf(selectedItem.getPrice()));
        } else if (selectedBackground != null) {
            itemImage.setImageResource(selectedBackground.getIconImageResource());
            itemPrice.setText(String.valueOf(selectedBackground.getPrice()));
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogLayout)
                .create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.shop_rounded_dialog);

        confirmButton.setOnClickListener(v -> {
            coin -= selectedItemPrice;
            updateCoinTextView();
            updateCoinsInFirestore();
            savePurchasedItemToFirestore(); // 아이템 저장
            dialog.dismiss();
            showSuccessDialog();
            selectedItem = null; // 구매 후 선택 초기화
            selectedBackground = null; // 구매 후 선택 초기화
            buyButton.setEnabled(false);
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    // 구매 성공 다이얼로그 표시
    private void showSuccessDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_success, null);

        ImageView itemImage = dialogLayout.findViewById(R.id.itemImage);
        //TextView itemName = dialogLayout.findViewById(R.id.itemName);
        Button decorateButton = dialogLayout.findViewById(R.id.decorateButton);
        Button confirmButton2 = dialogLayout.findViewById(R.id.confirmButton2);

        // 선택된 아이템이나 배경이 null이 아닌 경우에만 이미지를 설정
        if (selectedItem != null) {
            itemImage.setImageResource(selectedItem.getIconImageResource());
        } else if (selectedBackground != null) {
            itemImage.setImageResource(selectedBackground.getIconImageResource());
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogLayout)
                .create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.shop_rounded_dialog);

        decorateButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, DressActivity.class);
            if (selectedItem != null) {
                intent.putExtra("selectedItem", selectedItem);
            } else if (selectedBackground != null) {
                intent.putExtra("selectedBackground", selectedBackground);
            }
            startActivity(intent);
            dialog.dismiss();
        });

        confirmButton2.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showInsufficientCoinsDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_fail, null); // dialog_fail.xml 사용

        Button failButton = dialogLayout.findViewById(R.id.failbutton);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogLayout)
                .create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.shop_rounded_dialog); // 다이얼로그 배경 설정

        failButton.setOnClickListener(v -> dialog.dismiss()); // 확인 버튼 클릭 시 다이얼로그 닫기

        dialog.show();
    }

    private void savePurchasedItemToFirestore() {
        String userId = auth.getCurrentUser().getUid();
        Map<String, Object> purchasedItem = new HashMap<>();

        if (selectedItem != null) {
            purchasedItem.put("itemName", selectedItem.getName());
            purchasedItem.put("iconImage", selectedItem.getIconImageResource());
            purchasedItem.put("itemImage", selectedItem.getImageResource());
            purchasedItem.put("itemPrice", selectedItem.getPrice());
            purchasedItem.put("type", "item"); // item으로 설정
        } else if (selectedBackground != null) {
            purchasedItem.put("itemName", selectedBackground.getName());
            purchasedItem.put("iconImage", selectedBackground.getIconImageResource());
            purchasedItem.put("itemImage", selectedBackground.getImageResource());
            purchasedItem.put("itemPrice", selectedBackground.getPrice());
            purchasedItem.put("type", "background"); // background로 설정
        } else {
            return;
        }

        db.collection("users").document(userId).collection("purchases")
                .add(purchasedItem)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(StoreActivity.this, "아이템이 저장되었습니다!", Toast.LENGTH_SHORT).show();
                    if (selectedItem != null) {
                        selectedItem.setPurchased(true);
                    } else if (selectedBackground != null) {
                        selectedBackground.setPurchased(true);
                    }
                    updateInventoryUI();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(StoreActivity.this, "저장 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateInventoryUI() {
        String userId = auth.getCurrentUser().getUid();

        db.collection("users").document(userId).collection("purchases")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String purchasedItemName = documentSnapshot.getString("itemName");
                        for (StoreItem item : itemList) {
                            if (item.getName().equals(purchasedItemName)) {
                                item.setPurchased(true);
                            }
                        }
                        for (StoreItem item : backgroundList) {
                            if (item.getName().equals(purchasedItemName)) {
                                item.setPurchased(true);
                            }
                        }
                    }

                    runOnUiThread(() -> adapter.notifyDataSetChanged());
                    //adapter.notifyDataSetChanged(); // 업데이트 후 갱신
                })
                .addOnFailureListener(e -> Log.e("StoreActivity", "구매한 아이템 정보를 가져오는 중 오류 발생", e));
    }

    // Firestore에 코인 업데이트
    private void updateCoinsInFirestore() {
        DocumentReference docRef = db.collection("users")
                .document(auth.getCurrentUser().getUid());

        docRef.update("coin", coin)
                .addOnSuccessListener(aVoid -> Log.d("StoreActivity", "코인 업데이트 성공"))
                .addOnFailureListener(e ->
                        Toast.makeText(this, "코인 업데이트 실패", Toast.LENGTH_SHORT).show()
                );
    }

    // 코인 값 업데이트 함수
    private void updateCoinTextView() {
        shopCoinTextView.setText(String.valueOf(coin));
    }

    private void restoreSavedBackground() {
        db.collection("users").document(user.getUid())
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        Long savedBackgroundResource = snapshot.getLong("selectedBackgroundImage");
                        if (savedBackgroundResource != null) {
                            storeLayout.setBackgroundResource(savedBackgroundResource.intValue());
                        } else {
                            storeLayout.setBackgroundResource(R.drawable.background_default); // 기본 배경
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("StoreActivity", "Failed to restore background", e));
    }

    private void restoreSavedCharacter() {
        db.collection("users").document(user.getUid())
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        Long savedCharacterResource = snapshot.getLong("selectedItemImage");
                        if (savedCharacterResource != null) {
                            characterImage.setImageResource(savedCharacterResource.intValue());
                        } else {
                            characterImage.setImageResource(R.drawable.tomato_character_default); // 기본 캐릭터
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("StoreActivity", "Failed to restore character", e));
    }
}
