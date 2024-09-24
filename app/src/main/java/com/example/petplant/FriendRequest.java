package com.example.petplant;

public class FriendRequest {
    private String from;
    private String status;
    private String requestId;  // Firestore의 문서 ID를 저장할 필드

    // 기본 생성자 (Firestore에서 객체로 매핑할 때 필요)
    public FriendRequest() {}

    // Getter and Setter for 'from'
    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    // Getter and Setter for 'status'
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Getter and Setter for 'requestId'
    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
