package com.example.petplant;

public class StickerRequest {
    private String userName;
    private String stickerType;  // 스티커 종류

    // Constructor
    public StickerRequest(String userName, String stickerType) {
        this.userName = userName;
        this.stickerType = stickerType;
    }

    // Getter methods
    public String getUserName() {
        return userName;
    }

    public String getStickerType() {
        return stickerType;
    }
}


