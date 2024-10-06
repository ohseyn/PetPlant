package com.example.petplant;

import com.google.firebase.Timestamp;

public class UserActivity {
    private String documentId;
    private String userName;
    private String activityDescription;
    private Timestamp timestamp;
    private String imageUrI;    // 필드명을 Firestore에 맞춰서 수정
    private String textActivity;
    private String userId;
    private String plantName;
    private int likes;

    // 빈 생성자
    public UserActivity() {}

    // 생성자
    public UserActivity(String documentId, String userName, String activityDescription, Timestamp timestamp, String imageUrI, String textActivity, String userId, String plantName, int likes) {
        this.documentId = documentId;
        this.userName = userName;
        this.activityDescription = activityDescription;
        this.timestamp = timestamp;
        this.imageUrI = imageUrI;  // 필드명 수정
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
}
