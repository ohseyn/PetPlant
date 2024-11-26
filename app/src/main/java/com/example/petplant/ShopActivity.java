package com.example.petplant;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ShopActivity extends AppCompatActivity {

    private ImageView characterPreview;
    private Button purchaseButton;
    private Button backgroundButton;
    private Button itemButton;
    private GridView itemGridView;
    private GridView backgroundGridView;

    private int selectedItem = android.R.drawable.ic_menu_gallery;
    private String selectedItemName = "아이템 이름";
    private int selectedItemPrice = 0;
    private int selectedBackgroundPrice = 0;
    private String selectedBackgroundName = "";
    private int currentBackgroundItem = -1;
    private int currentItem = -1;

    private final int[] backgrounds = {
            android.R.color.holo_blue_bright, android.R.color.holo_green_light,
            android.R.color.holo_red_light, android.R.color.holo_orange_light,
            android.R.color.holo_purple, android.R.color.darker_gray,
            android.R.color.holo_orange_dark, android.R.color.black,
            android.R.color.holo_blue_dark
    };

    private final String[] backgroundNames = {
            "하늘색 배경", "초록색 배경", "빨간색 배경",
            "노란색 배경", "보라색 배경", "회색 배경",
            "주황색 배경", "검은색 배경", "남색 배경"
    };

    private final int[] backgroundPrices = {
            0, 60, 70, 80, 90, 100, 40, 20, 30
    };

    private Long userCoins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        Intent intent = getIntent();
        userCoins = intent.getLongExtra("coin", 0);

        characterPreview = findViewById(R.id.characterPreview);
        purchaseButton = findViewById(R.id.purchaseButton);
        backgroundButton = findViewById(R.id.backgroundButton);
        itemButton = findViewById(R.id.itemButton);
        itemGridView = findViewById(R.id.itemGridView);
        backgroundGridView = findViewById(R.id.backgroundGridView);

        characterPreview.setImageResource(android.R.drawable.ic_menu_gallery);

        setupGridViews();

        purchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canPurchase()) {
                    showPurchaseConfirmation();
                } else {
                    Toast.makeText(ShopActivity.this, "코인이 부족합니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        backgroundButton.setOnClickListener(v -> {
            backgroundGridView.setVisibility(View.VISIBLE);
            itemGridView.setVisibility(View.GONE);
            updatePurchaseButton();
        });

        itemButton.setOnClickListener(v -> {
            itemGridView.setVisibility(View.VISIBLE);
            backgroundGridView.setVisibility(View.GONE);
            updatePurchaseButton();
        });

        backgroundButton.performClick();
    }

    private void setupGridViews() {
        final int[] items = {
                android.R.drawable.ic_menu_camera, android.R.drawable.ic_menu_compass,
                android.R.drawable.ic_menu_gallery, android.R.drawable.ic_menu_manage,
                android.R.drawable.ic_menu_mapmode, android.R.drawable.ic_menu_myplaces,
                android.R.drawable.ic_menu_rotate, android.R.drawable.ic_menu_search,
                android.R.drawable.ic_menu_zoom
        };

        final String[] itemNames = {
                "카메라", "컴퍼스", "갤러리", "매니지", "맵모드", "마이플레이스",
                "로테이트", "서치", "줌"
        };

        final int[] itemPrices = {
                100, 120, 150, 130, 140, 160, 110, 180, 170
        };

        ItemAdapter backgroundAdapter = new ItemAdapter(this, backgrounds);
        backgroundGridView.setAdapter(backgroundAdapter);

        ItemAdapter itemAdapter = new ItemAdapter(this, items);
        itemGridView.setAdapter(itemAdapter);

        backgroundGridView.setOnItemClickListener((parent, view, position, id) ->
                handleBackgroundSelection(position));

        itemGridView.setOnItemClickListener((parent, view, position, id) ->
                handleItemSelection(position, items, itemNames, itemPrices));
    }

    private void handleBackgroundSelection(int position) {
        if (currentBackgroundItem == position) {
            characterPreview.setBackgroundResource(0);
            selectedBackgroundPrice = 0;
            selectedBackgroundName = "";
            currentBackgroundItem = -1;
        } else {
            selectedBackgroundPrice = backgroundPrices[position];
            selectedBackgroundName = backgroundNames[position];
            characterPreview.setBackgroundResource(backgrounds[position]);
            currentBackgroundItem = position;
        }
        updateTotalPrice();
        updatePurchaseButton();
    }

    private void handleItemSelection(int position, int[] items, String[] itemNames, int[] itemPrices) {
        if (currentItem == position) {
            characterPreview.setImageResource(android.R.drawable.ic_menu_gallery);
            selectedItemPrice = 0;
            selectedItemName = "";
            currentItem = -1;
        } else {
            selectedItem = items[position];
            selectedItemName = itemNames[position];
            selectedItemPrice = itemPrices[position];
            characterPreview.setImageResource(selectedItem);
            currentItem = position;
        }
        updateTotalPrice();
        updatePurchaseButton();
    }

    private void updateTotalPrice() {
        int totalPrice = selectedBackgroundPrice + selectedItemPrice;
        selectedItemPrice = totalPrice;
    }

    private void updatePurchaseButton() {
        boolean isItemSelected = (currentBackgroundItem != -1 || currentItem != -1);
        purchaseButton.setEnabled(isItemSelected);
    }

    private boolean canPurchase() {
        int totalPrice = selectedBackgroundPrice + selectedItemPrice;
        return userCoins >= totalPrice;
    }

    private void showPurchaseConfirmation() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_purchase_confirmation, null);

        ImageView itemImage = dialogLayout.findViewById(R.id.itemImage);
        //TextView itemName = dialogLayout.findViewById(R.id.itemName);
        TextView itemPrice = dialogLayout.findViewById(R.id.itemPrice);
        Button confirmButton = dialogLayout.findViewById(R.id.confirmButton);
        Button cancelButton = dialogLayout.findViewById(R.id.cancelButton);

        if (currentItem != -1) {
            itemImage.setImageResource(selectedItem);
            //itemName.setText(selectedItemName);
            itemPrice.setText(selectedItemPrice + " 코인");
        } else if (currentBackgroundItem != -1) {
            itemImage.setImageResource(backgrounds[currentBackgroundItem]);
            //itemName.setText(selectedBackgroundName);
            itemPrice.setText(selectedBackgroundPrice + " 코인");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(dialogLayout);
        AlertDialog dialog = builder.create();

// 다이얼로그 배경을 둥근 테두리로 설정
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.shop_rounded_dialog);

        confirmButton.setOnClickListener(v -> {
            userCoins -= selectedItemPrice;
            updateCoinsInFirestore();
            dialog.dismiss();
            showSuccessDialog();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }


    private void showSuccessDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_success, null);

        ImageView itemImage = dialogLayout.findViewById(R.id.itemImage);
        //TextView itemName = dialogLayout.findViewById(R.id.itemName);
        Button decorateButton = dialogLayout.findViewById(R.id.decorateButton);
        Button confirmButton2 = dialogLayout.findViewById(R.id.confirmButton2);

        if (currentItem != -1) {
            itemImage.setImageResource(selectedItem);
            //itemName.setText(selectedItemName);
        } else if (currentBackgroundItem != -1) {
            itemImage.setImageResource(backgrounds[currentBackgroundItem]);
            //itemName.setText(selectedBackgroundName);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(dialogLayout);
        AlertDialog dialog = builder.create();

// 다이얼로그 배경을 둥근 테두리로 설정
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.shop_rounded_dialog);

// 꾸미기 버튼 클릭 시 꾸미기 액티비티로 이동
        decorateButton.setOnClickListener(v -> {
            Intent intent = new Intent(ShopActivity.this, DecorateActivity.class);
            intent.putExtra("selectedItem", selectedItem);
            intent.putExtra("selectedItemName", selectedItemName);
            startActivity(intent);
            overridePendingTransition(0, 0);
            dialog.dismiss();
        });

// 취소 버튼 클릭 시 다이얼로그 닫기
        confirmButton2.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();

    }


    private void updateCoinsInFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        docRef.update("coin", userCoins)
                .addOnSuccessListener(aVoid -> {})
                .addOnFailureListener(e ->
                        Toast.makeText(this, "코인 업데이트 실패", Toast.LENGTH_SHORT).show()
                );
    }
}
