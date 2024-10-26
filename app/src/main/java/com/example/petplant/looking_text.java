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

public class looking_text extends AppCompatActivity {
    private String currentPhotoPath;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private EditText inputEditText;
    private TextView charCountTextView;
    private Button nextButton3;
    private Button back_home;
    Intent intent = getIntent();
    private static final int MAX_CHAR_COUNT = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lookingquest); // Replace with your layout file name

        // Initialize views
        inputEditText = findViewById(R.id.inputEditText);
        charCountTextView = findViewById(R.id.charCountTextView);
        nextButton3 = findViewById(R.id.nextButton3);

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

        // Handle next button click (if you need to perform an action on click)
        nextButton3.setOnClickListener(view -> {
            // Perform the action when the next button is clicked
            // For example, you can fetch the input text or validate it here
            String userInput = inputEditText.getText().toString().trim();

            if (!userInput.isEmpty()) {
                // Proceed to the next step or save the text input
                // Example: move to the next screen or save data to Firebase
                uploadImageToStorage(null);
            }
        });
        ;

        Button nextButton3 = findViewById(R.id.nextButton3);
        nextButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImageToStorage(null);
                Intent intent = new Intent(getApplicationContext(),looking_message.class);
                startActivity(intent);
            }
        });

    }
    // Firebase Storage에 이미지 업로드 및 데이터 만들기
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
        String description = "lookingquest" +timeStamp+"_"+user.getUid();
        activity.put("activityDescription","lookingquest"); //waterquest, removequest, smellquest
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
                                                Intent intent = new Intent(getApplicationContext(), looking_message.class);
                                                startActivity(intent);
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
