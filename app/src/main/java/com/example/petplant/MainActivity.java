package com.example.petplant;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //private FirebaseAnalytics mFirebaseAnalytics;
    FirebaseFirestore db;
    FirebaseAuth auth;
    Button button_logout;
    TextView textView;
    FirebaseUser user;
    EditText friendEmailEditText;
    Button sendRequestButton;
    RecyclerView friendRequestsRecyclerView;
    FriendRequestAdapter adapter;
    List<FriendRequest> friendRequestList = new ArrayList<>();

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_main);

        //mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
//        auth = FirebaseAuth.getInstance();
        //button_logout = findViewById(R.id.logout);
        //textView = findViewById(R.id.user_details);
//        user = auth.getCurrentUser();
//        db = FirebaseFirestore.getInstance();
//        friendEmailEditText = findViewById(R.id.friend_email);
//        sendRequestButton = findViewById(R.id.btn_send_request);
//        friendRequestsRecyclerView = findViewById(R.id.friend_requests_list);

//        friendRequestsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        adapter = new FriendRequestAdapter(friendRequestList, db, user.getUid());
//        friendRequestsRecyclerView.setAdapter(adapter);
//
//        if(user == null){
//            Intent intent = new Intent(getApplicationContext(), Login.class);
//            startActivity(intent);
//            finish();
//        }
//        else {
//            textView.setText(user.getEmail());
//            loadFriendRequests();
//            saveFCMToken();
//        }
//
//        button_logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FirebaseAuth.getInstance().signOut();
//                Intent intent = new Intent(getApplicationContext(), Login.class);
//                startActivity(intent);
//                finish();
//            }
//        });
//
//        sendRequestButton.setOnClickListener(v -> {
//            String friendEmail = friendEmailEditText.getText().toString();
//
//            if (TextUtils.isEmpty(friendEmail)) {
//                Toast.makeText(MainActivity.this, "Enter friend's email", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            // 친구의 UID 가져오기 (이메일로 사용자 찾기)
//            db.collection("users").whereEqualTo("email", friendEmail)
//                    .get()
//                    .addOnSuccessListener(queryDocumentSnapshots -> {
//                        if (!queryDocumentSnapshots.isEmpty()) {
//                            DocumentSnapshot friendDoc = queryDocumentSnapshots.getDocuments().get(0);
//                            String friendUid = friendDoc.getId();
//                            String myEmail = friendDoc.getString("email");
//
//                            // 내 UID 가져오기
//                            String myUid = user.getUid();
//
//                            // 친구 요청 데이터 Firestore에 저장
//                            Map<String, Object> request = new HashMap<>();
//                            request.put("from", myUid);
//                            request.put("to", friendUid);
//                            request.put("fromEmail", user.getEmail());
//                            request.put("status", "pending");
//
//                            db.collection("friend_requests").add(request)
//                                    .addOnSuccessListener(aVoid -> {
//                                        Toast.makeText(MainActivity.this, "Friend request sent", Toast.LENGTH_SHORT).show();
//                                    })
//                                    .addOnFailureListener(e -> {
//                                        Toast.makeText(MainActivity.this, "Failed to send request", Toast.LENGTH_SHORT).show();
//                                    });
//                        } else {
//                            Toast.makeText(MainActivity.this, "User not found", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//        });
//    }
//
//    private void saveFCMToken() {
//        FirebaseMessaging.getInstance().getToken()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        String token = task.getResult();
//
//                        // Firestore에 토큰 저장
//                        db.collection("users").document(user.getUid())
//                                .update("fcmToken", token)
//                                .addOnSuccessListener(aVoid -> Log.d("FCM", "Token saved successfully"))
//                                .addOnFailureListener(e -> Log.e("FCM", "Error saving token", e));
//                    } else {
//                        Log.w("FCM", "Fetching FCM registration token failed", task.getException());
//                    }
//                });
//    }
//
//        private void loadFriendRequests() {
//            db.collection("friend_requests")
//                    .whereEqualTo("to", user.getUid())  // 자신에게 온 요청만
//                    .get()
//                    .addOnSuccessListener(queryDocumentSnapshots -> {
//                        friendRequestList.clear();
//                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
//                            FriendRequest friendRequest = doc.toObject(FriendRequest.class);
//                            friendRequest.setId(doc.getId());
//                            friendRequest.setFromEmail(doc.getString("fromEmail"));
//                            friendRequestList.add(friendRequest);
//                        }
//                        adapter.notifyDataSetChanged();
//                    });
//        }
}