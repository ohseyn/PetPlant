package com.example.petplant;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class StoreActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StoreItemAdapter adapter;
    private List<StoreItem> backgroundList, itemList;
    private Button buyButton;
    private ImageView characterImage;
    private StoreItem selectedItem; // 선택한 아이템

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        // 2행 4열의 그리드 레이아웃 설정
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(gridLayoutManager);

        characterImage = findViewById(R.id.characterImage);
        buyButton = findViewById(R.id.buyButton);

        // 리스트 초기화 (배경 리스트)
        backgroundList = new ArrayList<>();
        backgroundList.add(new StoreItem("보라색 배경", R.drawable.guideimage1));
        backgroundList.add(new StoreItem("맑은 날", R.drawable.guideimage1));
        backgroundList.add(new StoreItem("봄날", R.drawable.guideimage1));
        backgroundList.add(new StoreItem("어두운 배경", R.drawable.guideimage1));
        backgroundList.add(new StoreItem("오아시스", R.drawable.guideimage1));
        backgroundList.add(new StoreItem("무대", R.drawable.guideimage1));
        backgroundList.add(new StoreItem("길거리", R.drawable.guideimage1));
        backgroundList.add(new StoreItem("눈 오는 날", R.drawable.guideimage1));

        // 아이템 리스트 초기화
        itemList = new ArrayList<>();
        itemList.add(new StoreItem("멋쟁이 안경", R.drawable.guideimage1));
        itemList.add(new StoreItem("굵은 수염", R.drawable.guideimage1));
        itemList.add(new StoreItem("귀여운 리본", R.drawable.guideimage1));
        itemList.add(new StoreItem("반려 인형", R.drawable.guideimage1));
        itemList.add(new StoreItem("운동화", R.drawable.guideimage1));
        itemList.add(new StoreItem("머리핀", R.drawable.guideimage1));
        itemList.add(new StoreItem("코주부 안경", R.drawable.guideimage1));
        itemList.add(new StoreItem("엔젤 링", R.drawable.guideimage1));

        // 기본 리스트 어댑터 설정
        adapter = new StoreItemAdapter(this, backgroundList, item -> {
            if (selectedItem == item) {
                // 선택한 아이템을 다시 누르면 해제
                selectedItem = null;
                buyButton.setVisibility(Button.GONE); // 구매 버튼 숨기기
            } else {
                // 다른 아이템을 선택하면 구매 버튼 보이기
                selectedItem = item;
                characterImage.setImageResource(item.getImageResource());
                buyButton.setVisibility(Button.VISIBLE);
            }
        });
        recyclerView.setAdapter(adapter);

        // 탭 레이아웃 설정
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) { // 배경 탭 선택 시
                    adapter.updateItemList(backgroundList);
                } else { // 아이템 탭 선택 시
                    adapter.updateItemList(itemList);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
        // 구매 버튼 클릭 시 구매 확인 다이얼로그 표시
        buyButton.setOnClickListener(v -> showBuyDialog(selectedItem));
    }

    // 구매 다이얼로그
    private void showBuyDialog(StoreItem item) {
        new AlertDialog.Builder(this)
                .setTitle("구매 확인")
                .setMessage(item.getName() + "를(을) 구매하시겠습니까?")
                .setPositiveButton("구매", (dialog, which) -> {
                    Toast.makeText(StoreActivity.this, item.getName() + "를(을) 구매했습니다!", Toast.LENGTH_SHORT).show();
                    buyButton.setVisibility(Button.GONE); // 구매 후 버튼 숨기기
                })
                .setNegativeButton("취소", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }
}
