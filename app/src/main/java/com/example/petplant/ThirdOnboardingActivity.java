package com.example.petplant;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class ThirdOnboardingActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;

    private FirebaseStorage storage;
    private StorageReference storageRef;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;

    private ImageView profileImage;
    private EditText nameInput;
    private Button editButton, start_home;
    private ImageButton back;
    private Uri imageUri;
    private String profileImageUrl;  // URL을 저장할 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_onboarding);

        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.button_sound);

        // Firebase 초기화
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        String plantName = getIntent().getStringExtra("name");

        // View 요소 초기화
        profileImage = findViewById(R.id.profile_image);
        nameInput = findViewById(R.id.name_input);
        editButton = findViewById(R.id.edit_button);
        start_home = findViewById(R.id.start_home);

        // '편집' 버튼 클릭 리스너 (갤러리 열기)
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                openGallery();
            }
        });

        // '시작하기' 버튼 클릭 리스너 (Firestore와 Storage에 데이터 저장)
        start_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
                saveProfileData(plantName);
            }
        });

        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), SecondOnboardingActivity.class)));
    }

    // Firestore에 사용자 정보와 이미지 저장
    private void saveProfileData(String plantName) {
        DocumentReference sfRef = db.collection("users").document(user.getUid());

        // Firestore에 이름, 식물 이름, 이메일 업데이트
        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(sfRef);

                // 사용자 이메일 가져오기
                String userEmail = user.getEmail();

                // Firestore에 데이터 업데이트
                transaction.update(sfRef, "name", nameInput.getText().toString());
                transaction.update(sfRef, "plantName", plantName != null ? plantName : "");  // null 방지
                transaction.update(sfRef, "email", userEmail);  // 이메일 추가

                String path = snapshot.getString("profileImageUrl");
                if (path == null || path.isEmpty()) {
                    path = "Images/profileImageUrl" + user.getUid();
                    transaction.update(sfRef, "profileImageUrl", path);
                }
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("ThirdOnboardingActivity", "Firestore 업데이트 성공");
                // Firestore 업데이트가 성공하면, 이미지를 Firebase Storage에 업로드
                uploadImageToStorage();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("ThirdOnboardingActivity", "Firestore 업데이트 실패", e);
            }
        });
    }

    // Firebase Storage에 이미지 업로드
    private void uploadImageToStorage() {
        if (profileImage.getDrawable() != null) {
            // 이미지가 설정되어 있으면 업로드
            profileImage.setDrawingCacheEnabled(true);
            profileImage.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) profileImage.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            // Firestore에 저장된 userImageUrl 경로로 이미지 업로드
            StorageReference imageRef = storageRef.child("Images/profileImageUrl" + user.getUid());
            UploadTask uploadTask = imageRef.putBytes(data);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // 업로드 실패 처리
                    Log.e("ThirdOnboardingActivity", "이미지 업로드 실패", exception);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // 업로드 성공하면 이미지 URL을 가져와 Firestore에 저장
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUri) {
                            profileImageUrl = downloadUri.toString();
                            Log.d("ThirdOnboardingActivity", "Image URL: " + profileImageUrl);  // 로그 추가

                            // Firestore에 이미지 다운로드 URL 업데이트
                            db.collection("users").document(user.getUid())
                                    .update("profileImageUrl", profileImageUrl)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("ThirdOnboardingActivity", "Firestore에 이미지 URL 저장 성공");
                                        startProfileActivity();  // profile 액티비티로 이동
                                    });
                        }
                    });
                }
            });
        } else {
            // 이미지가 없을 경우 바로 프로필 화면으로 이동
            startProfileActivity();
        }
    }

    // 갤러리 열기
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                profileImage.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void startProfileActivity() {
        Intent intent = new Intent(ThirdOnboardingActivity.this, profile_loading.class);
        intent.putExtra("profileImageUri", profileImageUrl);  // 프로필 이미지 URI 전달
        intent.putExtra("name", nameInput.getText().toString());
        intent.putExtra("plantName", getIntent().getStringExtra("plantName"));
        startActivity(intent);
        overridePendingTransition(0, 0);

        Log.d("ThirdOnboardingActivity", "HomeMainActivity로 이동");
    }
}
