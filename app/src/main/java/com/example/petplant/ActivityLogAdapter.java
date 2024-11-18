package com.example.petplant;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ActivityLogAdapter extends RecyclerView.Adapter<ActivityLogAdapter.ActivityViewHolder> {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    // FirebaseAuth 인스턴스 가져오기
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    // 현재 로그인한 사용자 가져오기
    FirebaseUser user = mAuth.getCurrentUser();
    private List<UserActivity> activitiesList;
    private OnActivityActionListener listener;

    public interface OnActivityActionListener {
        void onReactionClick(UserActivity activity);  // 반응 클릭 처리
    }

    public ActivityLogAdapter(List<UserActivity> activitiesList, OnActivityActionListener listener) {
        this.activitiesList = activitiesList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_log_item, parent, false);
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        UserActivity activity = activitiesList.get(position);

        // 사용자 이름과 활동 설명을 표시
        holder.textUserName.setText(activity.getUserName());
        holder.textActivity.setText(activity.getActivityDescription());

        // 좋아요 버튼 클릭 시 BottomSheetDialog 표시
        holder.buttonLike.setOnClickListener(v -> showBottomSheetDialog(v.getContext(), holder, activity));

        // Timestamp를 적절한 형식의 문자열로 변환
        if (activity.getTimestamp() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault());
            String formattedDate = sdf.format(activity.getTimestamp().toDate());
            holder.textTime.setText(formattedDate);
        } else {
            holder.textTime.setText("Unknown time");
        }

        // 식물 이름 표시 (activity.getPlantName()이 존재할 경우)
        if (activity.getPlantName() != null) {
            holder.textUserName.setText(activity.getPlantName());
        }

        // 이미지가 있을 경우 Glide로 로드
        if (activity.getImageUrI() != null && !activity.getImageUrI().isEmpty()) {
            holder.imageActivity.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(activity.getImageUrI())
                    .into(holder.imageActivity);
        } else {
            holder.imageActivity.setVisibility(View.GONE);
        }

        // 활동 항목 클릭 시 해당 다이얼로그 호출
        holder.itemView.setOnClickListener(v -> {
            String description = activity.getActivityDescription();
            if ("sandquest".equals(description) || "artificialquest".equals(description)) {
                showPictureDialog(holder.itemView.getContext(), activity);
            } else if("waterquest".equals(description) || "removequest".equals(description)) {
                showPictureDialog2(holder.itemView.getContext(), activity);
            } else if ("smellquest".equals(description) || "lookingquest".equals(description) ||
                    "touchingquest".equals(description) || "talkingquest".equals(description)) {
                showTextDialog(holder.itemView.getContext(), activity);
            }
        });

        // 나의 활동을 보느냐 친구의 활동을 보느냐에 대한 분기점.
        if (activity.getStickers() != null) {  // 스티커가 존재하는지 확인

            List<Sticker> stickers = activity.getStickers();
            // 기존에 추가된 스티커를 모두 지우고 새로 추가하기
            // 기존 스티커 초기화
            holder.viewSticker1.setVisibility(View.GONE);
            holder.viewSticker2.setVisibility(View.GONE);
            holder.frameSticker.setVisibility(View.GONE);
            // 리스트의 모든 Sticker 객체에 대해 반복
            for (Sticker sticker : stickers) {
                Log.d("test",sticker.getStickerUserId());
                // currentTab이 "friends"일 때만 userId가 일치하는 스티커만 처리
                if (activity.getCurrentTab().equals("friends") && !sticker.getStickerUserId().equals(user.getUid())) {
                    return;  // friends 탭에서, 현재 사용자와 일치하지 않으면 아무것도 하지 않음
                }
                // 스티커의 kind 값을 사용하여 drawable 리소스를 업데이트
                String kind = sticker.getKind();  // 예: "sticker1", "sticker2" 등
                int drawableResId = getDrawableResourceId(kind);  // kind에 맞는 리소스를 찾아 반환
                holder.updateStickerView(drawableResId);  // 해당 리소스를 뷰에 업데이트
            }
        }
    }
    // 사진이 있는 다이얼로그를 띄우는 메서드
    private void showPictureDialog(Context context, UserActivity activity) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.activity_detail_picture_blue);  // 사진이 있는 레이아웃

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView title = dialog.findViewById(R.id.textActivityTitle);
        TextView plantName = dialog.findViewById(R.id.plantName);
        TextView time = dialog.findViewById(R.id.textActivityTime);
        ImageView imageActivity = dialog.findViewById(R.id.imageActivity);

        title.setText(activity.getActivityDescription());
        plantName.setText(activity.getPlantName());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault());
        time.setText(sdf.format(activity.getTimestamp().toDate()));

        if (activity.getImageUrI() != null && !activity.getImageUrI().isEmpty()) {
            Glide.with(context).load(activity.getImageUrI()).into(imageActivity);
        } else {
            imageActivity.setImageResource(R.drawable.guideimage1);  // 기본 이미지 설정
        }

        if (activity.getStickers() != null && !activity.getStickers().isEmpty()) {
             // 이전 뷰 제거

            for (Sticker sticker : activity.getStickers()) {
                // 새로운 뷰를 생성하여 스티커 추가
                ImageView stickerView = new ImageView(context);
                stickerView.setLayoutParams(new LinearLayout.LayoutParams(40, 40)); // 크기 설정
                stickerView.setBackgroundResource(sticker.getDrawableResourceId()); // 스티커 리소스 설정
            }
        }

        dialog.show();
    }

    private void showPictureDialog2(Context context, UserActivity activity) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.activity_detail_picture_green);  // 사진이 있는 레이아웃

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView title = dialog.findViewById(R.id.textActivityTitle);
        TextView plantName = dialog.findViewById(R.id.plantName);
        TextView time = dialog.findViewById(R.id.textActivityTime);
        ImageView imageActivity = dialog.findViewById(R.id.imageActivity);

        title.setText(activity.getActivityDescription());
        plantName.setText(activity.getPlantName());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault());
        time.setText(sdf.format(activity.getTimestamp().toDate()));

        if (activity.getImageUrI() != null && !activity.getImageUrI().isEmpty()) {
            Glide.with(context).load(activity.getImageUrI()).into(imageActivity);
        } else {
            imageActivity.setImageResource(R.drawable.guideimage1);  // 기본 이미지 설정
        }

        if (activity.getStickers() != null && !activity.getStickers().isEmpty()) {
            // 이전 뷰 제거

            for (Sticker sticker : activity.getStickers()) {
                // 새로운 뷰를 생성하여 스티커 추가
                ImageView stickerView = new ImageView(context);
                stickerView.setLayoutParams(new LinearLayout.LayoutParams(40, 40)); // 크기 설정
                stickerView.setBackgroundResource(sticker.getDrawableResourceId()); // 스티커 리소스 설정
            }
        }

        dialog.show();
    }
    private void showTextDialog(Context context, UserActivity activity) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.activity_detail_dialog); // 텍스트만 있는 레이아웃
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView title = dialog.findViewById(R.id.textActivityTitle);
        TextView plantName = dialog.findViewById(R.id.plantName);
        TextView time = dialog.findViewById(R.id.textActivityTime);
        TextView description = dialog.findViewById(R.id.textActivityDescription);


        title.setText(activity.getActivityDescription());
        plantName.setText(activity.getPlantName());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault());
        time.setText(sdf.format(activity.getTimestamp().toDate()));
        description.setText(activity.getTextActivity());

        // 스티커 추가
        if (activity.getStickers() != null && !activity.getStickers().isEmpty()) {
             // 이전 뷰 제거

            for (Sticker sticker : activity.getStickers()) {
                // 새로운 뷰를 생성하여 스티커 추가
                ImageView stickerView = new ImageView(context);
                stickerView.setLayoutParams(new LinearLayout.LayoutParams(40, 40)); // 크기 설정
                stickerView.setBackgroundResource(sticker.getDrawableResourceId()); // 스티커 리소스 설정

            }
        }

        dialog.show();
    }


    private void addStickersToDialog(Dialog dialog, UserActivity activity) {
        List<Sticker> stickers = activity.getStickers();
        if (stickers != null) {
            for (Sticker sticker : stickers) {
                ImageView stickerImageView = new ImageView(dialog.getContext());
                stickerImageView.setImageResource(sticker.getDrawableResourceId()); // 스티커 이미지 설정
                stickerImageView.setLayoutParams(new ViewGroup.LayoutParams(80, 80)); // 스티커 크기 설정
            }
        }
    }
    private void addStickerFirebase(String sticker,UserActivity activity)
    {
        // 문서 참조 (문서 ID와 컬렉션 이름을 지정)
        DocumentReference docRef = db.collection("activities")
                .document(activity.getDocumentId());

        // 추가할 새로운 스티커 객체 (Map 사용)
        Map<String, Object> newSticker = new HashMap<>();
        newSticker.put("kind", sticker);
        assert user != null;
        newSticker.put("stickerUserId", user.getUid());

        // Firestore 배열에 객체를 추가
        docRef.update("stickers", FieldValue.arrayUnion(newSticker))
                .addOnSuccessListener(aVoid -> {
                    // 성공적으로 업데이트된 경우
                    Log.d("FirestoreExample", "스티커가 추가되었습니다.");
                    DocumentReference userDocRef = db.collection("users").document(user.getUid());
                    // 사용자 이름을 Firestore에서 가져오기
                    userDocRef.get().addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // 사용자 이름을 가져옴
                            String userNames = documentSnapshot.getString("name");
                            if (userNames != null) {
                                // 이름이 있을 경우, activity 데이터를 추가
                                requestActivity(userNames,sticker,activity.getUserId());
                            } else {
                                Log.e("requestActivity", "User name is not found.");
                            }
                        } else {
                            Log.e("requestActivity", "User document does not exist.");
                        }
                    }).addOnFailureListener(e -> {
                        Log.e("requestActivity", "Error getting user document: " + e.getMessage());
                    });
                })
                .addOnFailureListener(e -> {
                    // 오류가 발생한 경우
                    Log.e("FirestoreExample", "스티커 추가 실패: " + e.getMessage());
                });

    }
    private void requestActivity(String userName, String kind, String userUid) {

        if (user == null) {
            Log.e("requestActivity", "User is not authenticated.");
            return;
        }

        // 사용자 ID로 문서 참조 생성 (users 컬렉션에 추가)
        DocumentReference userDocRef = db.collection("users").document(userUid);

        // Firestore에서 기존 데이터를 확인
        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // 기존 문서가 있을 경우
                // 기존 activityRequest 배열을 가져옴
                List<Map<String, Object>> activityRequest = (List<Map<String, Object>>) documentSnapshot.get("activityRequest");

                if (activityRequest == null) {
                    // activityRequest 배열이 없으면 새로 생성
                    activityRequest = new ArrayList<>();
                }

                // 추가할 데이터 준비
                Map<String, Object> activityData = new HashMap<>();
                activityData.put("userName", userName);  // 사용자 이름
                activityData.put("kind", kind);  // 스티커 종류

                // 기존 배열에 새 데이터 추가
                activityRequest.add(activityData);

                // Firestore에 새로운 배열로 업데이트
                userDocRef.update("activityRequest", activityRequest)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("FirestoreExample", "Activity successfully added.");
                        })
                        .addOnFailureListener(e -> {
                            Log.e("FirestoreExample", "Error adding activity: " + e.getMessage());
                        });

            } else {
                // 문서가 없을 경우, 새로 생성
                Log.d("FirestoreExample", "Document not found, creating new document.");

                // 새로운 activityRequest 배열을 준비
                List<Map<String, Object>> activityRequest = new ArrayList<>();

                // 새로 추가할 데이터 준비
                Map<String, Object> activityData = new HashMap<>();
                activityData.put("userName", userName);  // 사용자 이름
                activityData.put("kind", kind);  // 스티커 종류

                // 배열에 데이터 추가
                activityRequest.add(activityData);

                // 새로운 문서 생성 (문서가 없으면 새로 생성)
                Map<String, Object> newUserData = new HashMap<>();
                newUserData.put("activityRequest", activityRequest);

                // Firestore에 데이터 추가
                userDocRef.set(newUserData)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("FirestoreExample", "Activity successfully added.");
                        })
                        .addOnFailureListener(e -> {
                            Log.e("FirestoreExample", "Error adding activity: " + e.getMessage());
                        });
            }
        }).addOnFailureListener(e -> {
            Log.e("FirestoreExample", "Error getting document: " + e.getMessage());
        });
    }


    private int getDrawableResourceId(String kind) {
        switch (kind) {
            case "sticker1":
                return R.drawable.sticker1;
            case "sticker2":
                return R.drawable.sticker2;
            case "sticker3":
                return R.drawable.sticker3;
            case "sticker4":
                return R.drawable.sticker4;
            case "sticker5":
                return R.drawable.sticker5;
            case "sticker6":
                return R.drawable.sticker6;
            case "sticker7":
                return R.drawable.sticker7;
            case "sticker8":
                return R.drawable.sticker8;
            // 필요시 기본 스티커를 추가
            default:
                return R.drawable.sticker1;  // 기본 스티커 리소스
        }
    }

    private void showBottomSheetDialog(Context context, ActivityViewHolder holder, UserActivity activity) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_layout, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        //여기서 setOnClickListener 마다 데이터 전달
        //데이터를 받아올 때는 updateStickerView 를 사용해서 사용.
        bottomSheetView.findViewById(R.id.sticker1).setOnClickListener(v -> {
            holder.updateStickerView(R.drawable.sticker1);
            addStickerFirebase("sticker1",activity);
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.sticker2).setOnClickListener(v -> {
            holder.updateStickerView(R.drawable.sticker2);
            addStickerFirebase("sticker2",activity);
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.sticker3).setOnClickListener(v -> {
            holder.updateStickerView(R.drawable.sticker3);
            addStickerFirebase("sticker3",activity);
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.sticker4).setOnClickListener(v -> {
            holder.updateStickerView(R.drawable.sticker4);
            addStickerFirebase("sticker4",activity);
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.sticker5).setOnClickListener(v -> {
            holder.updateStickerView(R.drawable.sticker5);
            addStickerFirebase("sticker5",activity);
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.sticker6).setOnClickListener(v -> {
            holder.updateStickerView(R.drawable.sticker6);
            addStickerFirebase("sticker6",activity);
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.sticker7).setOnClickListener(v -> {
            holder.updateStickerView(R.drawable.sticker7);
            addStickerFirebase("sticker7",activity);
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.sticker8).setOnClickListener(v -> {
            holder.updateStickerView(R.drawable.sticker8);
            addStickerFirebase("sticker8",activity);
            bottomSheetDialog.dismiss();
        });
        bottomSheetDialog.show();
    }
    @Override
    public int getItemCount() {
        return activitiesList == null ? 0 : activitiesList.size();
    }

    public static class ActivityViewHolder extends RecyclerView.ViewHolder {
        TextView textUserName, textActivity, textTime;
        ImageView imageActivity;
        ImageButton buttonLike;

        // 스티커가 표시될 View들
        View viewSticker1, viewSticker2;
        FrameLayout frameSticker;
        ImageView imageSticker;
        TextView textSticker;
        private int stickerCount = 0; // ViewHolder별 스티커 선택 수 추적

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            textUserName = itemView.findViewById(R.id.textUserName);
            textActivity = itemView.findViewById(R.id.textActivity);
            textTime = itemView.findViewById(R.id.textTime);
            imageActivity = itemView.findViewById(R.id.imageActivity);
            buttonLike = itemView.findViewById(R.id.buttonLike);

            // 스티커가 표시될 View 초기화
            viewSticker1 = itemView.findViewById(R.id.viewSticker1);
            viewSticker2 = itemView.findViewById(R.id.viewSticker2);
            frameSticker = itemView.findViewById(R.id.frameSticker);
            imageSticker = itemView.findViewById(R.id.imageSticker);
            textSticker = itemView.findViewById(R.id.textSticker);
        }

        // 스티커를 업데이트하는 메서드
        public void updateStickerView(int stickerResId) {
            stickerCount++; // 스티커 선택 횟수 증가

            if (stickerCount == 1) {
                viewSticker1.setVisibility(View.VISIBLE);
                viewSticker1.setBackgroundResource(stickerResId);
            } else if (stickerCount == 2) {
                viewSticker2.setVisibility(View.VISIBLE);
                viewSticker2.setBackgroundResource(stickerResId);
            } else if (stickerCount == 3) {
                // 세 번째 스티커에는 스티커 이미지만 표시
                frameSticker.setVisibility(View.VISIBLE);
                frameSticker.setBackgroundColor(0x00000000); // 투명 배경
                imageSticker.setVisibility(View.VISIBLE);
                imageSticker.setImageResource(stickerResId);
                textSticker.setVisibility(View.GONE); // 숫자 숨김
            } else {
                // 네 번째 스티커부터는 반투명 회색 배경과 숫자 표시
                frameSticker.setVisibility(View.VISIBLE);

                // 반투명 회색 배경을 별도의 오버레이 뷰로 추가하여 이미지와 숫자가 모두 보이도록
                imageSticker.setVisibility(View.VISIBLE);
                imageSticker.setImageResource(stickerResId);

                // textSticker의 배경을 반투명 회색으로 설정하고 겹쳐 표시
                textSticker.setBackgroundColor(0x88A9A9A9); // 반투명 회색 배경
                textSticker.setText("+" + (stickerCount - 3));
                textSticker.setVisibility(View.VISIBLE);
            }
        }
    }
}
