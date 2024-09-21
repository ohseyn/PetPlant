package com.example.petplant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;

public class profile extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this.getIntent());

        setContentView(R.layout.activity_profile);
        TextView name = findViewById(R.id.name);
        TextView plantName = findViewById(R.id.plantName);
        CircleImageView profileImage = findViewById(R.id.profileImageView);

        name.setText(intent.getStringExtra("name"));
        plantName.setText(intent.getStringExtra("plantName"));
        Glide.with(profile.this).load(intent.getStringExtra("profileImageUri")).into(profileImage);



        Button user_home = findViewById(R.id.user_home);
        user_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomeMainActivity.class);
                startActivity(intent);
            }
        });

        Button user_inbox = findViewById(R.id.user_inbox);
        user_inbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), inbox_friend.class);
                startActivity(intent);
            }
        });

        Button edit_profile = findViewById(R.id.user_edit);
        edit_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view){
                    Intent intent = new Intent(getApplicationContext(), SecondOnboardingActivity.class);
                    startActivity(intent);
        }
        });

    }

}

