package com.example.petplant;

public class Friend {
    private String userId;
    private String userName;

    public Friend() {
        // Firestore에 매핑할 기본 생성자
    }

    public Friend(String userId) {
        this.userId = userId;
        // 추가적으로 userName을 받아올 수 있다면 설정
        this.userName = "";  // Firestore에서 추가 정보를 가져와 설정 가능
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
