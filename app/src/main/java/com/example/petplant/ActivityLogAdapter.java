package com.example.petplant;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
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

        // Timestamp를 적절한 형식의 문자열로 변환
        if (activity.getTimestamp() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault());
            String formattedDate = sdf.format(activity.getTimestamp().toDate());  // Timestamp를 Date로 변환 후 포맷팅
            holder.textTime.setText(formattedDate);
        } else {
            holder.textTime.setText("Unknown time");  // Timestamp가 null일 경우 처리
        }

        // Plant Name을 표시
        if (activity.getPlantName() != null) {
            holder.textUserName.setText(activity.getPlantName());  // plantName을 userName 대신 표시
        }

        // 이미지 로드
        if (activity.getImageUri() != null && !activity.getImageUri().isEmpty()) {
            holder.imageActivity.setVisibility(View.VISIBLE);  // 이미지가 있으면 보여줌
            Glide.with(holder.itemView.getContext())
                    .load(activity.getImageUri())
                    .into(holder.imageActivity);
        } else {
            holder.imageActivity.setVisibility(View.GONE);  // 이미지가 없으면 숨김
        }
    }

    @Override
    public int getItemCount() {
        return activitiesList == null ? 0 : activitiesList.size();  // 리스트가 null일 경우 0 리턴
    }

    public static class ActivityViewHolder extends RecyclerView.ViewHolder {
        TextView textUserName, textActivity, textTime;
        ImageView imageActivity;
        ImageButton buttonLike;

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            textUserName = itemView.findViewById(R.id.textUserName);
            textActivity = itemView.findViewById(R.id.textDescription);
            textTime = itemView.findViewById(R.id.textTime);
            imageActivity = itemView.findViewById(R.id.imageActivity);
            buttonLike = itemView.findViewById(R.id.buttonLike);
        }
    }
}
