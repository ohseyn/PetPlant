package com.example.petplant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class waterquiz_correct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waterquiz_correct);
    }

    public void onButtonClick(View view)
    {
        Intent intent = new Intent(this, home_waterrequest_complete.class);
        startActivity(intent);
    }


}