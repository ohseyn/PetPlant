package com.example.petplant;

import com.google.firebase.Timestamp;

import java.util.List;

//currentTab 이랑 Strikers 수정.
public class UserActivity {
    private String documentId;
    private String userName;
    private String activityDescription;
    private Timestamp timestamp;
    private String imageUrI;    // 필드명을 Firestore에 맞춰서 수정
    private String textActivity;
    private String userId;
    private String plantName;
    private String currentTab;
    private int likes;
    private List<Sticker> stickers; // 추가된 스티커 목록 필드

    // 빈 생성자
    public UserActivity() {}
    public UserActivity(String documentId, String userName, String activityDescription, Timestamp timestamp, String imageUrI, String textActivity, String userId, String plantName, int likes, List<Sticker> stickers, String currentTab) {
        this.documentId = documentId;
        this.userName = userName;
        this.activityDescription = activityDescription;
        this.timestamp = timestamp;
        this.imageUrI = imageUrI;  // 필드명 수정
        this.textActivity = textActivity;
        this.userId = userId;
        this.plantName = plantName;
        this.likes = likes;
        this.stickers = stickers; // 스티커 초기화
        this.currentTab = currentTab;
    }

// Getter 메서드들
public String getDocumentId() {
    return documentId;
}

public String getUserName() {
    return userName;
}

public List<Sticker> getStickers() {
    return stickers; // 스티커 반환
}

public String getActivityDescription() {
    return activityDescription;
}

public Timestamp getTimestamp() {
    return timestamp;
}

public String getImageUrI() {
    return imageUrI;  // 필드명 수정
}

public String getTextActivity() {
    return textActivity;
}

public String getUserId() {
    return userId;
}

public String getPlantName() {
    return plantName;
}

public int getLikes() {
    return likes;
}

public String getCurrentTab(){return currentTab;}
// Setter 메서드들
public void setDocumentId(String documentId) {
    this.documentId = documentId;
}

public void setCurrentTab(String currentTab){this.currentTab = currentTab;}
public void setUserName(String userName) {
    this.userName = userName;
}

public void setActivityDescription(String activityDescription) {
    this.activityDescription = activityDescription;
}

public void setTimestamp(Timestamp timestamp) {
    this.timestamp = timestamp;
}

public void setImageUrI(String imageUrI) {
    this.imageUrI = imageUrI;  // 필드명 수정
}

public void setTextActivity(String textActivity) {
    this.textActivity = textActivity;
}

public void setUserId(String userId) {
    this.userId = userId;
}

public void setPlantName(String plantName) {
    this.plantName = plantName;
}

public void setLikes(int likes) {
    this.likes = likes;
}

public void setStickers(List<Sticker> stickers) {
    this.stickers = stickers; // 스티커 목록 설정
}
}