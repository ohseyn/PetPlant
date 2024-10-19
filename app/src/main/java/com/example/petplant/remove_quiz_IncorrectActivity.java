package com.example.petplant;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class remove_quiz_IncorrectActivity extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseUser user;
    Long coin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rmeove_quiz_incorrect);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();


        Button check_guide = findViewById(R.id.check_guide);
        check_guide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 코인 업데이트
                updateCoinInFirestore();
            }
        });
    }

    private void updateCoinInFirestore() {
        if (user != null) {
            DocumentReference docRef = db.collection("users").document(user.getUid());

            // Firestore에서 코인 값 가져오기
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        // 현재 코인 값 가져오기
                        Long currentCoin = documentSnapshot.getLong("coin");
                        if (currentCoin == null) {
                            currentCoin = documentSnapshot.getLong("coin");  // 코인 값이 없으면 0으로 설정
                        }

                        // 15코인 추가
                        Long updatedCoin = currentCoin + 5;

                        // Firestore에 업데이트
                        docRef.update("coin", updatedCoin).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // 완료 버튼을 누르면 HomeMainActivity로 돌아가면서 완료 상태를 전달
                                    Intent intent = new Intent(getApplicationContext(), HomeMainActivity.class);
                                    intent.putExtra("coin", coin);
                                    intent.putExtra("completed", true); // 완료 상태 전달
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                } else {
                                    Log.e("Firestore", "Error updating coin", task.getException());
                                }
                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("Firestore", "Error fetching coin data", e);
                }
            });
        }
    }
}
