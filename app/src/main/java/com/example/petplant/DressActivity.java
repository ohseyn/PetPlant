package com.example.petplant;

import android.content.Intent;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DressActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ConstraintLayout dressLayout;
    private ImageView characterImage;
    private RecyclerView recyclerView;
    private List<StoreItem> backgroundList = new ArrayList<>();
    private List<StoreItem> itemList = new ArrayList<>();
    private StoreItemAdapter adapter;
    private StoreItem selectedBackground;
    private StoreItem selectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dress);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        dressLayout = findViewById(R.id.dressLayout);
        characterImage = findViewById(R.id.characterImage);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));

        loadPurchasedItems();

        adapter = new StoreItemAdapter(this, new ArrayList<>(), item -> {
            if (backgroundList.contains(item)) {
                selectedBackground = item;
                dressLayout.setBackgroundResource(item.getImageResource());
            } else {
                selectedItem = item;
                characterImage.setImageResource(item.getImageResource());
            }
            findViewById(R.id.applyButton).setVisibility(View.VISIBLE);
        });
        recyclerView.setAdapter(adapter);

        findViewById(R.id.applyButton).setOnClickListener(v -> {
            if (selectedBackground != null || selectedItem != null) {
                saveDesignToFirestore(selectedBackground, selectedItem);
                Toast.makeText(this, "디자인이 저장되었습니다.", Toast.LENGTH_SHORT).show();
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

    private void loadPurchasedItems() {
        String userId = auth.getCurrentUser().getUid();
        db.collection("users").document(userId).collection("purchases")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String itemName = document.getString("itemName");
                        int itemImage = document.getLong("itemImage").intValue();
                        int itemPrice = document.getLong("itemPrice").intValue();
                        String itemType = document.getString("type"); // Firestore에 저장된 type 필드 확인

                        StoreItem item = new StoreItem(itemName, itemImage, itemPrice, itemType);
                        if (item.isBackground()) {
                            backgroundList.add(item); // 배경일 경우 backgroundList에 추가
                        } else if (item.isItem()) {
                            itemList.add(item); // 아이템일 경우 itemList에 추가
                        }
                    }
                    adapter.updateItemList(backgroundList);
                });
    }

    private void saveDesignToFirestore(StoreItem background, StoreItem item) {
        String userId = auth.getCurrentUser().getUid();

        if (background != null) {
            db.collection("users").document(userId)
                    .update("selectedBackground", background.getName(), "selectedBackgroundImage", background.getImageResource())
                    .addOnSuccessListener(aVoid -> Log.d("DressActivity", "배경 디자인이 저장되었습니다."));
        }
        if (item != null) {
            db.collection("users").document(userId)
                    .update("selectedItem", item.getName(), "selectedItemImage", item.getImageResource())
                    .addOnSuccessListener(aVoid -> Log.d("DressActivity", "아이템 디자인이 저장되었습니다."));
        }
//        String userId = auth.getCurrentUser().getUid();
//        db.collection("users").document(userId)
//                .update("selectedItem", item.getName(), "selectedImage", item.getImageResource())
//                .addOnSuccessListener(aVoid -> Log.d("DressActivity", "디자인이 저장되었습니다."));
    }
}
