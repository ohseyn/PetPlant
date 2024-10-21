package com.example.petplant;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class StoreActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StoreItemAdapter adapter;
    private List<StoreItem> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        // 2행 4열의 그리드 레이아웃 설정
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(gridLayoutManager);

        // 아이템 리스트 초기화
        itemList = new ArrayList<>();
        itemList.add(new StoreItem("보라색 배경", R.drawable.guideimage1));
        itemList.add(new StoreItem("맑은 날", R.drawable.guideimage1));
        // 아이템 더 추가

        // 어댑터 설정
        adapter = new StoreItemAdapter(this, itemList, item -> {
            // 아이템을 클릭했을 때의 동작 정의 (예: 구매 다이얼로그 띄우기)
            showBuyDialog(item);
        });

        recyclerView.setAdapter(adapter);
    }

    private void showBuyDialog(StoreItem item) {
        // 구매 다이얼로그 로직
    }
}
