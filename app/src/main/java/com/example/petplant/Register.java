package com.example.petplant;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword;
    Button buttonReg;
    FirebaseAuth mAuth;
    ProgressBar progressBar;

    ImageButton back_profile;


//    @Override
//    public void onStart() {
//        super.onStart();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser != null){
//            Intent intent = new Intent(getApplicationContext(), OnboardingActivity.class);
//            startActivity(intent);
//            finish();
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_sign_up);

        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.button_sound);

        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonReg = findViewById(R.id.signUpButton);
        progressBar = findViewById(R.id.progressBar);
        back_profile = findViewById(R.id.back_profile);
        back_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });


        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.start();
                progressBar.setVisibility(View.VISIBLE);
                String email, password;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(Register.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    Toast.makeText(Register.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    // Firestore에 사용자 정보 저장
                                    Map<String, Object> userInfo = new HashMap<>();
                                    userInfo.put("name", "사용자이름"); // 이름은 나중에 설정할 수 있게 하거나 프로필 설정에서 처리
                                    userInfo.put("email", user.getEmail());
                                    userInfo.put("friends", new ArrayList<String>()); // 친구 목록 초기화
                                    userInfo.put("friendRequestsSent", new ArrayList<String>()); // 보낸 친구 요청 초기화
                                    userInfo.put("friendRequestsReceived", new ArrayList<String>()); // 받은 친구 요청 초기화

                                    // Firestore에 데이터 저장
                                    db.collection("users").document(user.getUid()).set(userInfo)
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(Register.this, "Account created and data saved.", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getApplicationContext(), account_success.class);
                                                startActivity(intent);
                                                overridePendingTransition(0, 0);
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(Register.this, "Error saving user data.", Toast.LENGTH_SHORT).show();
                                            });

//                                    Toast.makeText(Register.this, "Account created.", Toast.LENGTH_SHORT).show();
//                                    Intent intent = new Intent(getApplicationContext(), Login.class);
//                                    startActivity(intent);
//                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(Register.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}