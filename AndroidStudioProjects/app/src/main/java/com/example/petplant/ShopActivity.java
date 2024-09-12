package com.example.petplant;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ShopActivity extends AppCompatActivity {

    private ImageView characterPreview;
    private Button purchaseButton;
    private Button backgroundButton;
    private Button itemButton;
    private GridView itemGridView;
    private GridView backgroundGridView;

    private int selectedItem = android.R.drawable.ic_menu_gallery;
    private String selectedItemName = "아이템 이름";
    private int selectedItemPrice = 0; // 총 가격
    private int selectedBackgroundPrice = 0; // 선택한 배경 아이템의 가격
    private String selectedBackgroundName = ""; // 선택한 배경 아이템 이름
    private int currentBackgroundItem = -1; // 현재 선택된 배경 아이템 (없음: -1)
    private int currentItem = -1; // 현재 선택된 일반 아이템 (없음: -1)

    // 배경 아이템 관련 배열을 클래스 변수로 이동
    private final int[] backgrounds = {
            android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_red_light,
            android.R.color.holo_orange_light, android.R.color.holo_purple, android.R.color.darker_gray,
            android.R.color.holo_orange_dark, android.R.color.black, android.R.color.holo_blue_dark
    };

    private final String[] backgroundNames = {
            "하늘색 배경", "초록색 배경", "빨간색 배경",
            "노란색 배경", "보라색 배경", "회색 배경",
            "주황색 배경", "검은색 배경", "남색 배경"
    };

    private final int[] backgroundPrices = {
            50, 60, 70,
            80, 90, 100,
            40, 20, 30
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        characterPreview = findViewById(R.id.characterPreview);
        purchaseButton = findViewById(R.id.purchaseButton);
        backgroundButton = findViewById(R.id.backgroundButton);
        itemButton = findViewById(R.id.itemButton);
        itemGridView = findViewById(R.id.itemGridView);
        backgroundGridView = findViewById(R.id.backgroundGridView);

        characterPreview.setImageResource(android.R.drawable.ic_menu_gallery);

        // 상점의 아이템 목록을 설정
        final int[] items = {
                android.R.drawable.ic_menu_camera, android.R.drawable.ic_menu_compass, android.R.drawable.ic_menu_gallery,
                android.R.drawable.ic_menu_manage, android.R.drawable.ic_menu_mapmode, android.R.drawable.ic_menu_myplaces,
                android.R.drawable.ic_menu_rotate, android.R.drawable.ic_menu_search, android.R.drawable.ic_menu_zoom
        };

        final String[] itemNames = {
                "카메라", "컴퍼스", "갤러리",
                "매니지", "맵모드", "마이플레이스",
                "로테이트", "서치", "줌"
        };

        final int[] itemPrices = {
                100, 120, 150,
                130, 140, 160,
                110, 180, 170
        };

        // 배경 GridView에 어댑터 설정
        ItemAdapter backgroundAdapter = new ItemAdapter(this, backgrounds);
        backgroundGridView.setAdapter(backgroundAdapter);

        // 일반 아이템 GridView에 어댑터 설정
        ItemAdapter itemAdapter = new ItemAdapter(this, items);
        itemGridView.setAdapter(itemAdapter);

        // 배경 아이템 클릭 시 미리보기 캐릭터에 적용
        backgroundGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentBackgroundItem == position) {
                    // 현재 선택된 배경 아이템을 다시 클릭한 경우
                    characterPreview.setBackgroundResource(0); // 미리보기 해제
                    selectedBackgroundPrice = 0; // 가격 초기화
                    selectedBackgroundName = ""; // 배경 아이템 이름 초기화
                    currentBackgroundItem = -1; // 선택된 배경 아이템 초기화
                } else {
                    // 다른 배경 아이템 클릭 시 미리보기 업데이트
                    selectedBackgroundPrice = backgroundPrices[position]; // 선택한 배경 아이템 가격 저장
                    selectedBackgroundName = backgroundNames[position]; // 선택한 배경 아이템 이름 저장
                    characterPreview.setBackgroundResource(backgrounds[position]); // 배경 설정
                    currentBackgroundItem = position; // 선택된 배경 아이템 업데이트
                }
                updateTotalPrice(); // 가격 업데이트
                updatePurchaseButton(); // 구매 버튼 업데이트
            }
        });

        // 일반 아이템 클릭 시 미리보기 캐릭터에 적용
        itemGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentItem == position) {
                    // 현재 선택된 일반 아이템을 다시 클릭한 경우
                    characterPreview.setImageResource(android.R.drawable.ic_menu_gallery); // 기본 이미지 설정
                    selectedItemPrice = 0; // 가격 초기화
                    selectedItemName = ""; // 일반 아이템 이름 초기화
                    currentItem = -1; // 선택된 일반 아이템 초기화
                } else {
                    // 다른 일반 아이템 클릭 시 미리보기 업데이트
                    selectedItem = items[position];
                    selectedItemName = itemNames[position];
                    selectedItemPrice = itemPrices[position]; // 선택한 일반 아이템 가격 저장
                    characterPreview.setImageResource(selectedItem); // 캐릭터 아이템 변경
                    currentItem = position; // 선택된 일반 아이템 업데이트
                }
                updateTotalPrice(); // 가격 업데이트
                updatePurchaseButton(); // 구매 버튼 업데이트
            }
        });

        // 구매하기 버튼 클릭 시
        purchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPurchaseConfirmation();
            }
        });

        // 초기 구매 버튼 상태 비활성화
        purchaseButton.setEnabled(false);

        // 배경 버튼 클릭 시 배경 아이템 표시
        backgroundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backgroundGridView.setVisibility(View.VISIBLE);
                itemGridView.setVisibility(View.GONE);
                updatePurchaseButton(); // 구매 버튼 업데이트
            }
        });

        // 아이템 버튼 클릭 시 상점 아이템 표시
        itemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemGridView.setVisibility(View.VISIBLE);
                backgroundGridView.setVisibility(View.GONE);
                updatePurchaseButton(); // 구매 버튼 업데이트
            }
        });

        // 초기 상태 설정: 배경 아이템 버튼 클릭 시 자동으로 배경 아이템 보이기
        backgroundButton.performClick();
    }

    // 총 가격을 업데이트하는 메서드
    private void updateTotalPrice() {
        // 총 가격은 선택한 배경 아이템의 가격과 선택한 일반 아이템의 가격의 합
        int totalPrice = selectedBackgroundPrice + selectedItemPrice;
        selectedItemPrice = totalPrice; // 총 가격 업데이트
    }

    // 구매 버튼의 활성화 상태를 업데이트하는 메서드
    private void updatePurchaseButton() {
        // 배경 아이템이나 일반 아이템이 선택되었는지 확인
        boolean isItemSelected = (currentBackgroundItem != -1 || currentItem != -1);
        purchaseButton.setEnabled(isItemSelected); // 버튼 활성화
    }

    // 구매 확인 팝업을 보여주는 메서드
    private void showPurchaseConfirmation() {
        // 팝업 레이아웃을 inflate
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_purchase_confirmation, null);

        ImageView itemImage = dialogLayout.findViewById(R.id.itemImage);
        TextView itemName = dialogLayout.findViewById(R.id.itemName);
        TextView itemPrice = dialogLayout.findViewById(R.id.itemPrice);
        Button cancelButton = dialogLayout.findViewById(R.id.cancelButton);
        Button confirmButton = dialogLayout.findViewById(R.id.confirmButton);

        // 선택한 아이템의 정보 설정
        if (currentItem != -1) {
            // 일반 아이템이 선택된 경우
            itemImage.setImageResource(selectedItem);
            itemName.setText(selectedItemName);
            itemPrice.setText(selectedItemPrice + " 코인"); // 합산된 가격 표시
        } else if (currentBackgroundItem != -1) {
            // 배경 아이템이 선택된 경우
            itemImage.setImageResource(backgrounds[currentBackgroundItem]); // 배경 아이템 이미지 설정
            itemName.setText(selectedBackgroundName); // 배경 아이템 이름 표시
            itemPrice.setText(selectedBackgroundPrice + " 코인"); // 배경 아이템 가격 표시
        }

        // 팝업 제목 설정
        TextView title = new TextView(this);
        title.setText("아이템을 구매하시겠습니까?");
        title.setPadding(10, 30, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextSize(20);

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setCustomTitle(title)  // 중앙 정렬된 제목 설정
                .setView(dialogLayout);

        AlertDialog dialog = builder.create();

        // '취소하기' 버튼 클릭 시
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // '구매하기' 버튼 클릭 시
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 구매 성공 시 메인으로 돌아감
                Intent resultIntent = new Intent();
                resultIntent.putExtra("selectedItem", selectedItem);
                setResult(RESULT_OK, resultIntent);
                Toast.makeText(ShopActivity.this, "구매 성공", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                finish();
            }
        });

        dialog.show();
    }
}
