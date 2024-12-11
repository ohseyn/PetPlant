package com.example.petplant;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class account_success extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_success);

        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.button_sound);

        Button startOnboarding = findViewById(R.id.startOnboarding);
        startOnboarding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                Intent intent = new Intent(account_success.this, OnboardingActivity.class);
                startActivity(intent);

                // 전환 애니메이션 적용
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }
}
