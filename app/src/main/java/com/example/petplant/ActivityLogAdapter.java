package com.example.petplant;

import android.app.Dialog;
import android.content.Context;
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

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ActivityLogAdapter extends RecyclerView.Adapter<ActivityLogAdapter.ActivityViewHolder> {
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
        holder.buttonLike.setOnClickListener(v -> showBottomSheetDialog(v.getContext(), holder));

        // Timestamp를 적절한 형식의 문자열로 변환
        if (activity.getTimestamp() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault());
            String formattedDate = sdf.format(activity.getTimestamp().toDate());
            holder.textTime.setText(formattedDate);
        } else {
            holder.textTime.setText("Unknown time");
        }

        // Plant Name을 표시
        if (activity.getPlantName() != null) {
            holder.textUserName.setText(activity.getPlantName());
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

        // 활동 클릭 이벤트 설정
        holder.itemView.setOnClickListener(v -> {
            if (activity.getActivityDescription().equals("waterquest") ||
                    activity.getActivityDescription().equals("artificialquest") ||
                    activity.getActivityDescription().equals("sandquest") ||
                    activity.getActivityDescription().equals("removequest")) {
                showPictureDialog(holder.itemView.getContext(), activity);
            } else if (activity.getActivityDescription().equals("smellquest") ||
                    activity.getActivityDescription().equals("lookingquest") ||
                    activity.getActivityDescription().equals("touchingquest") ||
                    activity.getActivityDescription().equals("talkingquest")) {
                showTextDialog(holder.itemView.getContext(), activity);
            }
        });
    }

    private void showPictureDialog(Context context, UserActivity activity) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.activity_detail_picture);  // 사진이 있는 레이아웃
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView title = dialog.findViewById(R.id.textActivityTitle);
        TextView plantName = dialog.findViewById(R.id.plantName);
        TextView time = dialog.findViewById(R.id.textActivityTime);
        ImageView imageActivity = dialog.findViewById(R.id.imageActivity);
        LinearLayout stickerLayout = dialog.findViewById(R.id.stickerLayout);

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
            stickerLayout.removeAllViews(); // 이전 뷰 제거

            for (Sticker sticker : activity.getStickers()) {
                // 새로운 뷰를 생성하여 스티커 추가
                ImageView stickerView = new ImageView(context);
                stickerView.setLayoutParams(new LinearLayout.LayoutParams(40, 40)); // 크기 설정
                stickerView.setBackgroundResource(sticker.getDrawableResourceId()); // 스티커 리소스 설정
                stickerLayout.addView(stickerView); // 스티커를 레이아웃에 추가
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
        LinearLayout stickerLayout = dialog.findViewById(R.id.stickerLayout);

        title.setText(activity.getActivityDescription());
        plantName.setText(activity.getPlantName());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault());
        time.setText(sdf.format(activity.getTimestamp().toDate()));
        description.setText(activity.getTextActivity());

        // 스티커 추가
        if (activity.getStickers() != null && !activity.getStickers().isEmpty()) {
            stickerLayout.removeAllViews(); // 이전 뷰 제거

            for (Sticker sticker : activity.getStickers()) {
                // 새로운 뷰를 생성하여 스티커 추가
                ImageView stickerView = new ImageView(context);
                stickerView.setLayoutParams(new LinearLayout.LayoutParams(40, 40)); // 크기 설정
                stickerView.setBackgroundResource(sticker.getDrawableResourceId()); // 스티커 리소스 설정
                stickerLayout.addView(stickerView); // 스티커를 레이아웃에 추가
            }
        }

        dialog.show();
    }


    private void addStickersToDialog(Dialog dialog, UserActivity activity) {
        LinearLayout stickerContainer = dialog.findViewById(R.id.stickerLayout);
        stickerContainer.removeAllViews(); // 기존 스티커 제거

        List<Sticker> stickers = activity.getStickers();
        if (stickers != null) {
            for (Sticker sticker : stickers) {
                ImageView stickerImageView = new ImageView(dialog.getContext());
                stickerImageView.setImageResource(sticker.getDrawableResourceId()); // 스티커 이미지 설정
                stickerImageView.setLayoutParams(new ViewGroup.LayoutParams(80, 80)); // 스티커 크기 설정
                stickerContainer.addView(stickerImageView);
            }
        }
    }

    private void showBottomSheetDialog(Context context, ActivityViewHolder holder) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_layout, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        bottomSheetView.findViewById(R.id.sticker1).setOnClickListener(v -> {
            holder.updateStickerView(R.drawable.sticker1);
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.sticker2).setOnClickListener(v -> {
            holder.updateStickerView(R.drawable.sticker2);
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.sticker3).setOnClickListener(v -> {
            holder.updateStickerView(R.drawable.sticker3);
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.sticker4).setOnClickListener(v -> {
            holder.updateStickerView(R.drawable.sticker4);
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.sticker5).setOnClickListener(v -> {
            holder.updateStickerView(R.drawable.sticker5);
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.sticker6).setOnClickListener(v -> {
            holder.updateStickerView(R.drawable.sticker6);
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.sticker7).setOnClickListener(v -> {
            holder.updateStickerView(R.drawable.sticker7);
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.sticker8).setOnClickListener(v -> {
            holder.updateStickerView(R.drawable.sticker8);
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
