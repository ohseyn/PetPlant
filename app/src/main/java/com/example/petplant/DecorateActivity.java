package com.example.petplant;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DecorateActivity extends AppCompatActivity {

    private ImageView decoratedItemImage;
    private TextView decoratedItemName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decorate);

        decoratedItemImage = findViewById(R.id.decoratedItemImage);
        decoratedItemName = findViewById(R.id.decoratedItemName);

        // Intent로 전달된 데이터 가져오기
        int selectedItem = getIntent().getIntExtra("selectedItem", -1);
        String selectedItemName = getIntent().getStringExtra("selectedItemName");

        // 가져온 데이터로 UI 업데이트
        if (selectedItem != -1) {
            decoratedItemImage.setImageResource(selectedItem);
        }
        if (selectedItemName != null) {
            decoratedItemName.setText(selectedItemName);
        }
    }
}
