package com.example.petplant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petplant.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder>{

    private ArrayList<Map<String,String>> localDataSet;

    // 뷰홀더 클래스
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView plantTextView;
        private CircleImageView profileImageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textName);
            plantTextView = itemView.findViewById(R.id.textPlantName);
            profileImageView = itemView.findViewById(R.id.friendProfileImageView);
        }
        public List<View> getViews() { // 메서드 이름 변경
            return Arrays.asList(nameTextView, plantTextView, profileImageView);
        }
    }

    // 생성자를 통해서 데이터를 전달받도록 함
    public FriendListAdapter (ArrayList<Map<String,String>> dataSet) {
        localDataSet = dataSet;
    }

    @NonNull
    @Override   // ViewHolder 객체를 생성하여 리턴한다.
    public FriendListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_friend_item, parent, false);
        FriendListAdapter.ViewHolder viewHolder = new FriendListAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override   // ViewHolder안의 내용을 position에 해당되는 데이터로 교체한다.
    public void onBindViewHolder(@NonNull FriendListAdapter.ViewHolder holder, int position) {
        Map<String, String> friendData = localDataSet.get(position); // Map 형태의 데이터 가져오기
        String name = friendData.get("name"); // 이름 가져오기
        String plantName = friendData.get("plantName"); // 식물 이름 가져오기
        String profileImageUri = friendData.get("profileImageUri"); // 프로필 이미지 URI 가져오기

        // Glide를 사용하여 이미지 로드
        Glide.with(holder.itemView.getContext())
                .load(profileImageUri)
                .into(holder.profileImageView); // CircleImageView에 이미지 설정

        // TextView에 데이터 바인딩
        holder.nameTextView.setText(name); // 이름 설정
        holder.plantTextView.setText(plantName); // 식물 이름 설정
    }

    @Override   // 전체 데이터의 갯수를 리턴한다.
    public int getItemCount() {
        return localDataSet.size();
    }
}