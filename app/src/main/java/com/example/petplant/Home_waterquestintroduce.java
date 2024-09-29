package com.example.petplant;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import static com.google.firebase.auth.AuthKt.auth;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Home_waterquestintroduce extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS_CODE = 100;
    private Uri photoURI;
    private String currentPhotoPath;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;


    private String ImageUrl;  // URL을 저장할 변수
    Intent intent = getIntent();
    String timeStamp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waterquestintroduce);
        // Firebase 초기화
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();


        Button button = findViewById(R.id.do_quest);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissions();
            }
        });
    }

    // 런타임 권한 요청 코드
    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSIONS_CODE);
        } else {
            dispatchTakePictureIntent();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            Log.d(TAG, "Camera app found, preparing to launch");

            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                Log.e(TAG, "Error occurred while creating the File", ex);
                return;
            }
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this, "com.example.mureok.fileprovider", photoFile);

                // 로그로 URI 확인
                Log.d(TAG, "Photo URI: " + photoURI.toString());

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                takePictureLauncher.launch(takePictureIntent);
            } else {
                Log.e(TAG, "Failed to create image file.");
            }
        } else {
            Log.e(TAG, "No camera app found to handle the intent.");
            Toast.makeText(this, "카메라 앱이 없습니다. 다른 앱을 사용하거나 카메라 앱을 설치해주세요.", Toast.LENGTH_LONG).show();

            // 이미지 선택을 위한 대체 코드
            Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickPhotoLauncher.launch(pickPhotoIntent);
        }
    }

    private File createImageFile() throws IOException {
        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (storageDir != null && !storageDir.exists()) {
            boolean isCreated = storageDir.mkdirs();
            if (!isCreated) {
                Log.e(TAG, "Failed to create directory for image file: " + storageDir.getAbsolutePath());
            }
        }
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        currentPhotoPath = image.getAbsolutePath();
        Log.d(TAG, "Photo file created at path: " + currentPhotoPath);

        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 부여되었을 때 사진 촬영 인텐트 실행
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "카메라와 저장소 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private final ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // 사진이 정상적으로 저장되었습니다.
                    Log.d(TAG, "Photo saved to: " + currentPhotoPath);
                    // 사진이 성공적으로 촬영되었을 때 다음 화면으로 이동
                    Intent intent = new Intent(this, waterquest_message.class);
                    intent.putExtra("photoPath", currentPhotoPath);
                    uploadImageToStorage(null);
                    startActivity(intent);
                } else {
                    Log.e(TAG, "Failed to take picture");
                }
            }
    );

    private final ActivityResultLauncher<Intent> pickPhotoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Uri selectedImageUri = result.getData().getData();
                    currentPhotoPath = selectedImageUri.toString();
                    if (selectedImageUri != null) {
                        Log.d(TAG, "Image selected: " + selectedImageUri.toString());

                        uploadImageToStorage(selectedImageUri);
//                        Intent intent;
//                        intent = new Intent(this, waterquest_message.class);
//                        intent.putExtra("photoPath", selectedImageUri.toString());
//                        startActivity(intent);

                    } else {
                        Log.e(TAG, "No image selected");
                    }
                } else {
                    Log.e(TAG, "Failed to select image");
                }
            }
    );

    // Firebase Storage에 이미지 업로드 및 데이터 만들기
    private void uploadImageToStorage(Uri imageUri) {
        // Firebase 초기화
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();


        Log.d("user",user.getUid());

        Bitmap bitmap = null;
        Log.d("sss",imageUri.toString());
        // URI가 null이 아닐 경우, URI에서 Bitmap을 생성
        if (imageUri != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            } catch (IOException e) {
                Log.e("UploadImage", "Error getting Bitmap from URI", e);
            }
        }
        // URI가 null일 경우, currentPhotoPath를 사용하여 Bitmap 생성
        else if (currentPhotoPath != null) {
            bitmap = BitmapFactory.decodeFile(currentPhotoPath);
        }

        // Bitmap이 null인지 확인 후 처리
        if (bitmap != null) {
            Log.d("Bitmap", bitmap.toString());
            // 여기에 Firebase Storage에 이미지를 업로드하는 로직 추가
        } else {
            Log.e("UploadImage", "Bitmap is null");
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        // Firestore에 저장된 userImageUrl 경로로 이미지 업로드
        StorageReference imageRef = storageRef.child("Images/waterquest" + user.getUid()+ timeStamp);
        UploadTask uploadTask = imageRef.putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // 업로드 실패 처리
                Log.e("Home_removequest_introduce", "이미지 업로드 실패", exception);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // 업로드 성공하면 이미지 URL 및 퀘스트 정보 Firestore에 저장
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadUri) {
                        ImageUrl = downloadUri.toString();
                        Log.d("ThirdOnboardingActivity", "Image URL: " + ImageUrl);  // 로그 추가
                        Intent thisIntent = getIntent();
                        Map<String, Object> activity = new HashMap<>();
                        String description = "waterquest" +timeStamp+"_"+user.getUid();
                        activity.put("activityDescription","waterquest"); //waterquest, removequest, smellquest
                        activity.put("imageUrI", ImageUrl);
                        activity.put("plantName", thisIntent.getStringExtra("plantName")); // Reference to the plant document
                        activity.put("textActivity", "");
                        activity.put("timestamp", timeStamp);
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
                                                                Intent intent = new Intent(Home_waterquestintroduce.this, waterquest_message.class);
                                                                intent.putExtra("photoPath", imageUri.toString());
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
                });
            }
        });

    }
}
