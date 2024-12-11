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

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ActivityLogAdapter extends RecyclerView.Adapter<ActivityLogAdapter.ActivityViewHolder> {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    private List<UserActivity> activitiesList;
    private OnActivityActionListener listener;

    private static final int TYPE_GREEN = 0;
    private static final int TYPE_BLUE = 1;
    private static final int TYPE_PINK = 2;
    private static final int TYPE_DEFAULT = 3;

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
        View view;
        if (viewType == TYPE_GREEN) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_logitem_green, parent, false);
        } else if (viewType == TYPE_BLUE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_logitem_blue, parent, false);
        } else if (viewType == TYPE_PINK) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_logitem_pink, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_logitem_default, parent, false);
        }
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        UserActivity activity = activitiesList.get(position);

        // 사용자 이름 표시
        String userName = activity.getPlantName() != null ? activity.getPlantName() : activity.getUserName();
        holder.textUserName.setText(userName);

        // 활동 설명 및 제목 설정
//        String description = activity.getActivityDescription();
//        switch (description) {
//            case "waterquest":
//                holder.textActivity.setText("가꾸기 활동");
//                holder.textActivityTitle.setText("물 주기 활동 완료");
//                break;
//
//            case "removequest":
//                holder.textActivity.setText("가꾸기 활동");
//                holder.textActivityTitle.setText("곁순 제거해주기 활동 완료");
//                break;
//
//            case "artificialquest":
//                holder.textActivity.setText("더 보살피기 활동");
//                holder.textActivityTitle.setText("인공수정 해주기 활동 완료");
//                break;
//
//            case "sandquest":
//                holder.textActivity.setText("더 보살피기 활동");
//                holder.textActivityTitle.setText("비료 주기 활동 완료");
//                break;
//
//            case "smellquest":
//                holder.textActivity.setText("친해지기 활동");
//                holder.textActivityTitle.setText("향 맡아보기 활동 완료");
//                break;
//
//            case "lookingquest":
//                holder.textActivity.setText("친해지기 활동");
//                holder.textActivityTitle.setText("바라보기 활동 완료");
//                break;
//
//            case "touchingquest":
//                holder.textActivity.setText("친해지기 활동");
//                holder.textActivityTitle.setText("쓰다듬고 만지기 활동 완료");
//                break;
//
//            case "talkingquest":
//                holder.textActivity.setText("친해지기 활동");
//                holder.textActivityTitle.setText("말 걸기 활동 완료");
//                break;
//
//            default:
//                holder.textActivity.setText("기타 활동");
//                holder.textActivityTitle.setText("기타 활동 완료");
//                break;
//        }

//        // 글 미리보기 추가 (친해지기 활동만)
//        if ("smellquest".equals(description) || "lookingquest".equals(description) ||
//                "touchingquest".equals(description) || "talkingquest".equals(description)) {
//            if (activity.getTextActivity() != null && !activity.getTextActivity().isEmpty()) {
//                holder.textPreview.setVisibility(View.VISIBLE);
//                holder.textPreview.setText(activity.getTextActivity());
//            } else {
//                holder.textPreview.setVisibility(View.GONE);
//            }
//        } else {
//            holder.textPreview.setVisibility(View.GONE); // 기타 활동은 미리보기 숨김
//        }

        // 활동 설명 설정
        holder.textActivity.setText(activity.getActivityDescription());

        String description = activity.getActivityDescription();
        if ("sandquest".equals(description) || "artificialquest".equals(description)) {
            holder.textActivity.setText("더 보살피기 활동");
        } else if ("waterquest".equals(description) || "removequest".equals(description)) {
            holder.textActivity.setText("가꾸기 활동");
        } else if ("smellquest".equals(description) || "lookingquest".equals(description) ||
                "touchingquest".equals(description) || "talkingquest".equals(description)) {
            holder.textActivity.setText("친해지기 활동");

            // 글 미리보기 추가
            if (activity.getTextActivity() != null && !activity.getTextActivity().isEmpty()) {
                //holder.textPreview.setVisibility(View.VISIBLE);
                //holder.textPreview.setText(activity.getTextActivity());
            } else {
                //holder.textPreview.setVisibility(View.GONE);
            }
        } else {
            holder.textActivity.setText("기타 활동");
            //holder.textActivityTitle.setText("기타 활동 완료");
            //holder.textPreview.setVisibility(View.GONE); // 기타 활동에는 글 미리보기 없음
        }

        // 좋아요 버튼 클릭 시 BottomSheetDialog 표시
        holder.buttonLike.setOnClickListener(v -> showBottomSheetDialog(v.getContext(), holder, activity));

        // Timestamp를 문자열로 변환
        if (activity.getTimestamp() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("a hh:mm", Locale.getDefault());
            DateFormatSymbols symbols = DateFormatSymbols.getInstance(Locale.getDefault());
            symbols.setAmPmStrings(new String[]{"오전", "오후"});
            sdf.setDateFormatSymbols(symbols);

            // 형식화된 시간 설정
            String formattedTime = sdf.format(activity.getTimestamp().toDate());
            holder.textTime.setText(formattedTime);  // 오전/오후 hh:mm 형식으로 표시
        } else {
            holder.textTime.setText("Unknown time");
        }

        // 이미지 로드
        if (activity.getImageUrI() != null && !activity.getImageUrI().isEmpty()) {
            holder.imageActivity.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(activity.getImageUrI())
                    .into(holder.imageActivity);
        } else {
            holder.imageActivity.setVisibility(View.GONE);
        }

        // 항목 클릭 시 다이얼로그 호출
        holder.itemView.setOnClickListener(v -> {
            if ("sandquest".equals(description) || "artificialquest".equals(description)) {
                showPictureDialog(holder.itemView.getContext(), activity);
            } else if ("waterquest".equals(description) || "removequest".equals(description)) {
                showPictureDialog2(holder.itemView.getContext(), activity);
            } else if ("smellquest".equals(description) || "lookingquest".equals(description) ||
                    "touchingquest".equals(description) || "talkingquest".equals(description)) {
                showTextDialog(holder.itemView.getContext(), activity);
            }
        });

        // 스티커 표시
        if (activity.getStickers() != null) {
            List<Sticker> stickers = activity.getStickers();
            holder.viewSticker1.setVisibility(View.GONE);
            holder.viewSticker2.setVisibility(View.GONE);
            holder.frameSticker.setVisibility(View.GONE);

            for (Sticker sticker : stickers) {
                Log.d("test", sticker.getStickerUserId());
                if (activity.getCurrentTab().equals("friends") && !sticker.getStickerUserId().equals(user.getUid())) {
                    return;
                }
                int drawableResId = getDrawableResourceId(sticker.getKind());
                holder.updateStickerView(drawableResId);
            }
        }
    }


    @Override
    public int getItemViewType(int position) {
        String description = activitiesList.get(position).getActivityDescription();
        if ("sandquest".equals(description) || "artificialquest".equals(description)) {
            return TYPE_BLUE;
        } else if ("waterquest".equals(description) || "removequest".equals(description)) {
            return TYPE_GREEN;
        } else if ("smellquest".equals(description) || "lookingquest".equals(description) ||
                "touchingquest".equals(description) || "talkingquest".equals(description)) {
            return TYPE_PINK;
        } else {
            return TYPE_DEFAULT;
        }
    }

    private void showPictureDialog(Context context, UserActivity activity) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.activity_detail_picture_blue);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView title = dialog.findViewById(R.id.textActivityTitle);
        TextView plantName = dialog.findViewById(R.id.plantName);
        TextView time = dialog.findViewById(R.id.textActivityTime);
        ImageView imageActivity = dialog.findViewById(R.id.imageActivity);

        //title.setText(activity.getActivityDescription() + " 활동 완료");

        String description = activity.getActivityDescription();
        switch (description) {
            case "waterquest":
                title.setText("물 주기 활동 완료!");
                break;
            case "removequest":
                title.setText("곁순 제거해주기 활동 완료!");
                break;
            case "artificialquest":
                title.setText("인공수정 해주기 활동 완료!");
                break;
            case "sandquest":
                title.setText("비료 주기 활동 완료!");
                break;
            default:
                title.setText("활동 완료!");
                break;
        }

        plantName.setText(activity.getPlantName());
        SimpleDateFormat sdf = new SimpleDateFormat("a hh:mm", Locale.getDefault());
        time.setText(sdf.format(activity.getTimestamp().toDate()));

        if (activity.getImageUrI() != null && !activity.getImageUrI().isEmpty()) {
            Glide.with(context).load(activity.getImageUrI()).into(imageActivity);
        } else {
            imageActivity.setImageResource(R.drawable.guideimage1);
        }

        dialog.show();
    }

    private void showPictureDialog2(Context context, UserActivity activity) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.activity_detail_picture_green);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView title = dialog.findViewById(R.id.textActivityTitle);
        TextView plantName = dialog.findViewById(R.id.plantName);
        TextView time = dialog.findViewById(R.id.textActivityTime);
        ImageView imageActivity = dialog.findViewById(R.id.imageActivity);

        //title.setText(activity.getActivityDescription() + " 활동 완료");

        String description = activity.getActivityDescription();
        switch (description) {
            case "waterquest":
                title.setText("물 주기 활동 완료!");
                break;
            case "removequest":
                title.setText("곁순 제거해주기 활동 완료!");
                break;
            case "artificialquest":
                title.setText("인공수정 해주기 활동 완료!");
                break;
            case "sandquest":
                title.setText("비료 주기 활동 완료!");
                break;
            default:
                title.setText("활동 완료!");
                break;
        }

        plantName.setText(activity.getPlantName());
        SimpleDateFormat sdf = new SimpleDateFormat("a hh:mm", Locale.getDefault());
        time.setText(sdf.format(activity.getTimestamp().toDate()));

        if (activity.getImageUrI() != null && !activity.getImageUrI().isEmpty()) {
            Glide.with(context).load(activity.getImageUrI()).into(imageActivity);
        } else {
            imageActivity.setImageResource(R.drawable.guideimage1);
        }

        dialog.show();
    }

    private void showTextDialog(Context context, UserActivity activity) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.activity_detail_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView title = dialog.findViewById(R.id.textActivityTitle);
        TextView plantName = dialog.findViewById(R.id.plantName);
        TextView time = dialog.findViewById(R.id.textActivityTime);
        TextView description = dialog.findViewById(R.id.textActivityDescription);

        //title.setText(activity.getActivityDescription() + " 활동 완료");

        String activityType = activity.getActivityDescription();
        switch (activityType) {
            case "smellquest":
                title.setText("향 맡아보기 활동 완료!");
                break;
            case "lookingquest":
                title.setText("바라보기 활동 완료!");
                break;
            case "touchingquest":
                title.setText("쓰다듬고 만지기 활동 완료!");
                break;
            case "talkingquest":
                title.setText("말 걸기 활동 완료!");
                break;
            default:
                title.setText("활동 완료!");
                break;
        }

        plantName.setText(activity.getPlantName());
        SimpleDateFormat sdf = new SimpleDateFormat("a hh:mm", Locale.getDefault());
        time.setText(sdf.format(activity.getTimestamp().toDate()));
        description.setText(activity.getTextActivity());

        dialog.show();
    }

    private void addStickerFirebase(String sticker, UserActivity activity) {
        DocumentReference docRef = db.collection("activities").document(activity.getDocumentId());
        Map<String, Object> newSticker = new HashMap<>();
        newSticker.put("kind", sticker);
        assert user != null;
        newSticker.put("stickerUserId", user.getUid());

        docRef.update("stickers", FieldValue.arrayUnion(newSticker))
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirestoreExample", "스티커가 추가되었습니다.");
                    DocumentReference userDocRef = db.collection("users").document(user.getUid());
                    userDocRef.get().addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String userNames = documentSnapshot.getString("name");
                            if (userNames != null) {
                                requestActivity(userNames, sticker, activity.getUserId());
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
                    Log.e("FirestoreExample", "스티커 추가 실패: " + e.getMessage());
                });
    }

    private void requestActivity(String userName, String kind, String userUid) {
        if (user == null) {
            Log.e("requestActivity", "User is not authenticated.");
            return;
        }

        DocumentReference userDocRef = db.collection("users").document(userUid);
        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<Map<String, Object>> activityRequest = (List<Map<String, Object>>) documentSnapshot.get("activityRequest");

                if (activityRequest == null) {
                    activityRequest = new ArrayList<>();
                }

                Map<String, Object> activityData = new HashMap<>();
                activityData.put("userName", userName);
                activityData.put("kind", kind);

                activityRequest.add(activityData);

                userDocRef.update("activityRequest", activityRequest)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("FirestoreExample", "Activity successfully added.");
                        })
                        .addOnFailureListener(e -> {
                            Log.e("FirestoreExample", "Error adding activity: " + e.getMessage());
                        });

            } else {
                Log.d("FirestoreExample", "Document not found, creating new document.");

                List<Map<String, Object>> activityRequest = new ArrayList<>();
                Map<String, Object> activityData = new HashMap<>();
                activityData.put("userName", userName);
                activityData.put("kind", kind);

                activityRequest.add(activityData);
                Map<String, Object> newUserData = new HashMap<>();
                newUserData.put("activityRequest", activityRequest);

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
            default:
                return R.drawable.sticker1;
        }
    }

    private void showBottomSheetDialog(Context context, ActivityViewHolder holder, UserActivity activity) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_layout, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        bottomSheetView.findViewById(R.id.sticker1).setOnClickListener(v -> {
            holder.updateStickerView(R.drawable.sticker1);
            addStickerFirebase("sticker1", activity);
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.sticker2).setOnClickListener(v -> {
            holder.updateStickerView(R.drawable.sticker2);
            addStickerFirebase("sticker2", activity);
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.sticker3).setOnClickListener(v -> {
            holder.updateStickerView(R.drawable.sticker3);
            addStickerFirebase("sticker3", activity);
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.sticker4).setOnClickListener(v -> {
            holder.updateStickerView(R.drawable.sticker4);
            addStickerFirebase("sticker4", activity);
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.sticker5).setOnClickListener(v -> {
            holder.updateStickerView(R.drawable.sticker5);
            addStickerFirebase("sticker5", activity);
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.sticker6).setOnClickListener(v -> {
            holder.updateStickerView(R.drawable.sticker6);
            addStickerFirebase("sticker6", activity);
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.sticker7).setOnClickListener(v -> {
            holder.updateStickerView(R.drawable.sticker7);
            addStickerFirebase("sticker7", activity);
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.sticker8).setOnClickListener(v -> {
            holder.updateStickerView(R.drawable.sticker8);
            addStickerFirebase("sticker8", activity);
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

        View viewSticker1, viewSticker2;
        FrameLayout frameSticker;
        ImageView imageSticker;
        TextView textSticker;
        private int stickerCount = 0;

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            textUserName = itemView.findViewById(R.id.textUserName);
            textActivity = itemView.findViewById(R.id.textActivity);
            textTime = itemView.findViewById(R.id.textTime);
            imageActivity = itemView.findViewById(R.id.imageActivity);
            buttonLike = itemView.findViewById(R.id.buttonLike);
            viewSticker1 = itemView.findViewById(R.id.viewSticker1);
            viewSticker2 = itemView.findViewById(R.id.viewSticker2);
            frameSticker = itemView.findViewById(R.id.frameSticker);
            imageSticker = itemView.findViewById(R.id.imageSticker);
            textSticker = itemView.findViewById(R.id.textSticker);
        }

        public void updateStickerView(int stickerResId) {
            stickerCount++;

            if (stickerCount == 1) {
                viewSticker1.setVisibility(View.VISIBLE);
                viewSticker1.setBackgroundResource(stickerResId);
            } else if (stickerCount == 2) {
                viewSticker2.setVisibility(View.VISIBLE);
                viewSticker2.setBackgroundResource(stickerResId);
            } else if (stickerCount == 3) {
                frameSticker.setVisibility(View.VISIBLE);
                frameSticker.setBackgroundColor(0x00000000);
                imageSticker.setVisibility(View.VISIBLE);
                imageSticker.setImageResource(stickerResId);
                textSticker.setVisibility(View.GONE);
            } else {
                frameSticker.setVisibility(View.VISIBLE);
                imageSticker.setVisibility(View.VISIBLE);
                imageSticker.setImageResource(stickerResId);
                textSticker.setBackgroundColor(0x88A9A9A9);
                textSticker.setText("+" + (stickerCount - 3));
                textSticker.setVisibility(View.VISIBLE);
            }
        }
    }
}
