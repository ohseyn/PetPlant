package com.example.petplant;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class ShopMainActivity extends AppCompatActivity {

    private ImageView characterImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopmain);

        characterImage = findViewById(R.id.default_character);
        Button storeButton = findViewById(R.id.storeButton);

        // 상점 버튼 클릭 시 ShopActivity로 이동
        storeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShopMainActivity.this, ShopActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    // 상점에서 아이템을 구매한 후 돌아왔을 때 아이템을 장착
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            int selectedItem = data.getIntExtra("selectedItem", android.R.drawable.ic_menu_gallery);
            characterImage.setImageResource(selectedItem);
        }
    }
}
