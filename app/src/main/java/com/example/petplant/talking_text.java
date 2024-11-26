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

public class talking_text extends AppCompatActivity {
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private EditText inputEditText;
    private TextView charCountTextView;
    private TextView plantName;
    private Button nextButton3;
    private static final int MAX_CHAR_COUNT = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talking_text);

        // Firebase 초기화
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

        // View 초기화
        plantName = findViewById(R.id.plantName);
        inputEditText = findViewById(R.id.inputEditText);
        charCountTextView = findViewById(R.id.charCountTextView);
        nextButton3 = findViewById(R.id.nextButton3);

        // 초기 글자 수 설정
        charCountTextView.setText("0/" + MAX_CHAR_COUNT);

        // TextWatcher 추가
        inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                charCountTextView.setText(s.length() + "/" + MAX_CHAR_COUNT);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Firebase에서 plantName 가져오기
        if (user != null) {
            getProfileImageFromFirebase();
        } else {
            Log.e("talking_text", "User not authenticated.");
        }

        // nextButton 클릭 리스너 설정
        nextButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userInput = inputEditText.getText().toString().trim();
                if (!userInput.isEmpty()) {
                    uploadImageToStorage(null);
                    Intent intent = new Intent(getApplicationContext(), talking_message.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            }
        });
    }

    private void getProfileImageFromFirebase() {
        db.collection("users").document(auth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String userPlantName = documentSnapshot.getString("plantName");
                            if (plantName != null) {
                                plantName.setText(userPlantName != null ? userPlantName : "식물 이름 없음");
                            } else {
                                Log.e("talking_text", "plantName TextView is null");
                            }
                        } else {
                            Log.d("talking_text", "No such document in Firestore");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("talking_text", "Error fetching plant name from Firebase", e);
                    }
                });
    }

    private void uploadImageToStorage(Uri imageUri) {
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        db = FirebaseFirestore.getInstance();

        Log.d("user", user.getUid());
        String textActivity = inputEditText.getText().toString();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        Intent thisIntent = getIntent();
        Map<String, Object> activity = new HashMap<>();
        String description = "talkingquest" + timeStamp + "_" + user.getUid();
        activity.put("activityDescription", "talkingquest");
        activity.put("imageUrI", "");
        activity.put("plantName", thisIntent.getStringExtra("plantName"));
        activity.put("textActivity", textActivity);
        activity.put("timestamp", Timestamp.now());
        activity.put("userId", user.getUid());

        db.collection("activities").document(description)
                .set(activity)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        db.collection("activities").document(description)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                Intent intent = new Intent(getApplicationContext(), talking_message.class);
                                                startActivity(intent);
                                                overridePendingTransition(0, 0);
                                            } else {
                                                Log.w(TAG, "No such document");
                                            }
                                        } else {
                                            Log.w(TAG, "Error getting document", task.getException());
                                        }
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }
}
