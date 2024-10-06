package com.example.petplant;

import com.google.firebase.Timestamp;

public class UserActivity {
    private String documentId;  // 문서 ID
    private String userName;    // 사용자 이름
    private String activityDescription; // 활동 설명
    private Timestamp timestamp;   // 활동 시간 (Firestore의 Timestamp 타입)
    private String imageUri;    // 이미지 URI (대소문자 확인)
    private String textActivity; // 활동 텍스트 설명
    private String userId;      // 사용자 ID
    private String plantName;   // 식물 이름
    private int likes;          // 좋아요 수

    // 빈 생성자 (Firebase에서 객체로 매핑할 때 필요)
    public UserActivity() {}

    // 생성자
    public UserActivity(String documentId, String userName, String activityDescription, Timestamp timestamp, String imageUri, String textActivity, String userId, String plantName, int likes) {
        this.documentId = documentId;
        this.userName = userName;
        this.activityDescription = activityDescription;
        this.timestamp = timestamp;
        this.imageUri = imageUri;
        this.textActivity = textActivity;
        this.userId = userId;
        this.plantName = plantName;
        this.likes = likes;
    }

    // Getter 메서드들
    public String getDocumentId() {
        return documentId;
    }

    public String getUserName() {
        return userName;
    }

    public String getActivityDescription() {
        return activityDescription;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getImageUri() {
        return imageUri;
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

    // Setter 메서드들
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setActivityDescription(String activityDescription) {
        this.activityDescription = activityDescription;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
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
}
