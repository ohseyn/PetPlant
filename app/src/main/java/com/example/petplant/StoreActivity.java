package com.example.petplant;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
    private Long coin;

    private RecyclerView recyclerView;
    private StoreItemAdapter adapter;
    private List<StoreItem> backgroundList, itemList;
    private Button buyButton;
    private ImageView characterImage;
    private StoreItem selectedItem;
    private TextView shopCoinTextView;
    private ConstraintLayout storeLayout; // 배경을 변경할 레이아웃

    private int selectedItemPrice = 0;
    private String selectedItemName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        storeLayout = findViewById(R.id.storeLayout); // 레이아웃을 변수에 저장
        Button back_profile = findViewById(R.id.back_profile);
        Button dressButton = findViewById(R.id.dress);

        dressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DressActivity.class);
                startActivity(intent);
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

        // Firestore와 Auth 초기화
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // View 초기화
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));

        characterImage = findViewById(R.id.characterImage);
        buyButton = findViewById(R.id.buyButton);
        buyButton.setVisibility(View.GONE);
        shopCoinTextView = findViewById(R.id.coin);

        // Intent로 전달된 코인 값 받아오기
        coin = getIntent().getLongExtra("coin", 0L);
        // 코인 값 UI에 표시
        updateCoinTextView();

        // 아이템 리스트 초기화
        backgroundList = getBackgroundItems();
        itemList = getCharacterItems();

        // 선택된 아이템이 구매된 상태인지 확인 후 토스트 메시지 출력
        adapter = new StoreItemAdapter(this, backgroundList, item -> {
            if (item.isPurchased()) {
                Toast.makeText(this, "이미 구매한 아이템입니다.", Toast.LENGTH_SHORT).show();
                selectedItem = null; // 선택된 아이템 초기화
                buyButton.setVisibility(View.GONE); // 구매 버튼 숨기기
            } else {
                selectedItem = item;
                selectedItemPrice = item.getPrice();
                selectedItemName = item.getName();
                if (backgroundList.contains(item)) {
                    storeLayout.setBackgroundResource(item.getImageResource());
                } else {
                    characterImage.setImageResource(item.getImageResource());
                }
                buyButton.setVisibility(View.VISIBLE);
            }
            adapter.notifyDataSetChanged();
//                // 새로 선택된 아이템이거나 구매되지 않은 경우
//                if (selectedItem != null && selectedItem.equals(item)) {
//                    selectedItem = null;
//                    buyButton.setVisibility(View.GONE);
//                } else {
//                    selectedItem = item;
//                    selectedItemPrice = item.getPrice();
//                    selectedItemName = item.getName();
//
//                    // 선택한 아이템이 배경인지 아이템인지에 따라 다르게 표시
//                    if (backgroundList.contains(item)) {
//                        storeLayout.setBackgroundResource(item.getImageResource());
//                    } else {
//                        characterImage.setImageResource(item.getImageResource());
//                    }
//                    buyButton.setVisibility(View.VISIBLE);
//                }
//            }
//            adapter.notifyDataSetChanged();
        });

//        adapter = new StoreItemAdapter(this, backgroundList, item -> {
//            if (selectedItem != null && selectedItem.equals(item)) {
//                // 이미 선택된 아이템을 다시 선택한 경우 해제
//                selectedItem = null;
//                selectedItemPrice = 0;
//                selectedItemName = "";
//                buyButton.setVisibility(View.GONE);
//            } else {
//                // 새로운 아이템을 선택한 경우
//                selectedItem = item;
//                selectedItemPrice = item.getPrice();
//                selectedItemName = item.getName();
//                characterImage.setImageResource(item.getImageResource());
//
//                // 이미 구매한 아이템인지 확인
//                if (item.isPurchased()) {
//                    buyButton.setVisibility(View.GONE);
//                    Toast.makeText(this, "이미 구매한 아이템입니다.", Toast.LENGTH_SHORT).show();
//                } else {
//                    buyButton.setVisibility(View.VISIBLE);
//                }
//            }
//            // 선택된 상태 업데이트
//            adapter.notifyDataSetChanged();
//        });
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

        // 구매 버튼 클릭 리스너 설정
        buyButton.setOnClickListener(v -> {
            if (selectedItem != null) {
                if (coin >= selectedItemPrice) {
                    showBuyDialog();
                } else {
                    Toast.makeText(StoreActivity.this, "코인이 부족합니다.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(StoreActivity.this, "아이템을 선택해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
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

    // 배경 아이템 리스트 생성
    private List<StoreItem> getBackgroundItems() {
        List<StoreItem> list = new ArrayList<>();
        list.add(new StoreItem("보라색 배경", R.drawable.background_night, 60));
        list.add(new StoreItem("맑은 날", R.drawable.guideimage1, 70));
        list.add(new StoreItem("봄날", R.drawable.background_spring, 80));
        list.add(new StoreItem("어두운 배경", R.drawable.guideimage1, 90));
        list.add(new StoreItem("오아시스", R.drawable.guideimage1, 100));
        list.add(new StoreItem("무대", R.drawable.guideimage1, 40));
        list.add(new StoreItem("길거리", R.drawable.guideimage1, 50));
        list.add(new StoreItem("눈 오는 날", R.drawable.guideimage1, 30));
        return list;
    }

    // 캐릭터 아이템 리스트 생성
    private List<StoreItem> getCharacterItems() {
        List<StoreItem> list = new ArrayList<>();
        list.add(new StoreItem("멋쟁이 안경", R.drawable.tomato_glass, 100));
        list.add(new StoreItem("굵은 수염", R.drawable.tomato_mustache, 120));
        list.add(new StoreItem("멋쟁이 신사", R.drawable.tomato_gentle, 150));
        list.add(new StoreItem("귀도리 모자", R.drawable.tomato_hat, 130));
        list.add(new StoreItem("책가방", R.drawable.tomato_bag, 140));
        list.add(new StoreItem("리본", R.drawable.tomato_bow, 160));
        list.add(new StoreItem("잎사귀 우산", R.drawable.tomato_leaf, 110));
        list.add(new StoreItem("선글라스", R.drawable.tomato_sunglasses, 180));
        return list;
    }

    // 구매 다이얼로그 표시
    private void showBuyDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_purchase_confirmation, null);

        ImageView itemImage = dialogLayout.findViewById(R.id.itemImage);
        TextView itemName = dialogLayout.findViewById(R.id.itemName);
        TextView itemPrice = dialogLayout.findViewById(R.id.itemPrice);
        Button confirmButton = dialogLayout.findViewById(R.id.confirmButton);
        Button cancelButton = dialogLayout.findViewById(R.id.cancelButton);

        itemImage.setImageResource(selectedItem.getImageResource());
        itemName.setText(selectedItemName);
        itemPrice.setText(selectedItemPrice + " 코인");

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogLayout)
                .create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.shop_rounded_dialog);

        confirmButton.setOnClickListener(v -> {
            coin -= selectedItemPrice;
            updateCoinTextView(); // 코인 값 즉시 업데이트
            updateCoinsInFirestore();
            savePurchasedItemToFirestore(); // 아이템 저장
            dialog.dismiss();
            showSuccessDialog();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    // 구매 성공 다이얼로그 표시
    private void showSuccessDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_success, null);

        ImageView itemImage = dialogLayout.findViewById(R.id.itemImage);
        TextView itemName = dialogLayout.findViewById(R.id.itemName);
        Button decorateButton = dialogLayout.findViewById(R.id.decorateButton);
        Button confirmButton2 = dialogLayout.findViewById(R.id.confirmButton2);

        itemImage.setImageResource(selectedItem.getImageResource());
        itemName.setText(selectedItemName);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogLayout)
                .create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.shop_rounded_dialog);

        decorateButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, DressActivity.class);
            intent.putExtra("selectedItem", selectedItem);
            startActivity(intent);
            dialog.dismiss();
        });

        confirmButton2.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void savePurchasedItemToFirestore() {
        String userId = auth.getCurrentUser().getUid();

        Map<String, Object> purchasedItem = new HashMap<>();
        purchasedItem.put("itemName", selectedItem.getName());
        purchasedItem.put("itemImage", selectedItem.getImageResource());
        purchasedItem.put("itemPrice", selectedItem.getPrice());

        db.collection("users").document(userId).collection("purchases")
                .add(purchasedItem)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(StoreActivity.this, "아이템이 저장되었습니다!", Toast.LENGTH_SHORT).show();
                    selectedItem.setPurchased(true);  // 구매 상태 업데이트
                    // 인벤토리 상태 업데이트
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
                                item.setPurchased(true);  // 구매한 아이템을 표시하도록 상태 설정
                            }
                        }
                    }

                    // RecyclerView 갱신
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("StoreActivity", "구매한 아이템 정보를 가져오는 중 오류 발생", e);
                });
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
}
