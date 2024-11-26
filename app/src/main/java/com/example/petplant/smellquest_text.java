package com.example.petplant;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class smellquest_text extends AppCompatActivity {
    Intent intent = getIntent();
    private String currentPhotoPath;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private TextView plantName;
    private EditText inputEditText;
    private TextView charCountTextView;
    private Button nextButton3;
    private Button back_home;
    private static final int MAX_CHAR_COUNT = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smellquest); // Replace with your layout file name

        // Firebase 초기화
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        // View 초기화
        plantName = findViewById(R.id.plantName);
        inputEditText = findViewById(R.id.inputEditText);
        charCountTextView = findViewById(R.id.charCountTextView);
        nextButton3 = findViewById(R.id.nextButton3);

        // Firebase에서 식물 이름 가져오기 메서드 호출
        if (user != null) {
            getProfileImageFromFirebase();
        } else {
            Log.e("smellquest_text", "User not authenticated.");
        }

        // Set initial character count
        charCountTextView.setText("0/" + MAX_CHAR_COUNT);

        // Add TextWatcher to monitor text changes in the EditText
        inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Update the character count as text changes
                int currentCharCount = s.length();
                charCountTextView.setText(currentCharCount + "/" + MAX_CHAR_COUNT);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Additional validation can be added here if needed
            }
        });

        // Handle next button click
        nextButton3.setOnClickListener(view -> {
            String userInput = inputEditText.getText().toString().trim();
            if (!userInput.isEmpty()) {
                uploadImageToStorage(null);
                Intent intent = new Intent(getApplicationContext(), smellquest_message.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
    }

    // Firebase에서 프로필 이미지 및 식물 이름 가져오기
    private void getProfileImageFromFirebase() {
        db.collection("users").document(auth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String profileImageUri = documentSnapshot.getString("profileImageUrl");
                            Log.d("profile", "Firebase profileImageUri: " + profileImageUri);

                            // 사용자 식물 이름 설정
                            String userPlantName = documentSnapshot.getString("plantName");
                            if (plantName != null) {
                                plantName.setText(userPlantName != null ? userPlantName : "저");
                            } else {
                                Log.e("smellquest_text", "plantName TextView is null");
                            }
                        } else {
                            Log.d("smellquest_text", "No such document in Firestore");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("smellquest_text", "Error fetching plant name from Firebase", e);
                    }
                });
    }

    // Firebase Storage에 이미지 업로드 및 데이터 생성
    private void uploadImageToStorage(Uri imageUri) {
        // Firebase 초기화
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        Log.d("user",user.getUid());
        String textActivity = inputEditText.getText().toString();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        Intent thisIntent = getIntent();
        Map<String, Object> activity = new HashMap<>();
        String description = "smellquest" +timeStamp+"_"+user.getUid();
        activity.put("activityDescription","smellquest"); //waterquest, removequest, smellquest
        activity.put("imageUrI", "");
        activity.put("plantName", thisIntent.getStringExtra("plantName")); // Reference to the plant document
        activity.put("textActivity", textActivity);
        activity.put("timestamp", Timestamp.now());
        activity.put("userId", user.getUid().toString());

        db.collection("activities").document(description)  // description을 문서 ID로 설정
                .set(activity)  // 데이터를 저장
                .addOnSuccessListener(new OnSuccessListener<Void>() {  // OnSuccessListener의 반환 타입은 Void
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");

                        // 문서가 성공적으로 작성된 후, 해당 문서 참조를 다시 가져옴
                        db.collection("activities").document(description)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                // 문서가 성공적으로 가져와졌을 때, Intent를 실행
                                                Intent intent = new Intent(getApplicationContext(), smellquest_message.class);
                                                startActivity(intent);
                                                overridePendingTransition(0, 0);
                                            } else {
                                                Log.w(TAG, "No such document");
                                                // 문서가 존재하지 않을 때 처리 (예: 사용자에게 메시지 표시)
                                            }
                                        } else {
                                            Log.w(TAG, "Error getting document", task.getException());
                                            // 문서를 가져오는 중 에러 발생 처리 (예: 사용자에게 메시지 표시)
                                        }
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        // Handle error gracefully (e.g., display a message)
                    }
                });
    }


}
