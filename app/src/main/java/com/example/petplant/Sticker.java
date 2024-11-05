package com.example.petplant;
public class Sticker {
    private int drawableResourceId;
    private String stickerUserId;
    private String kind;
    // 기본 생성자 (Firebase Firestore에서 객체로 변환할 때 필요)
    public Sticker() {}

    public Sticker(int drawableResourceId,String kind,String userId) {
        this.drawableResourceId = drawableResourceId;
        this.kind = kind;
        this.stickerUserId = userId;
    }

    public int getDrawableResourceId() {
        return drawableResourceId;
    }

    public String getKind() {
        return kind;
    }

    public String getStickerUserId() {
        return  stickerUserId;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public void setStickerUserId(String userId) {
       this.stickerUserId = userId;
    }


    public void setDrawableResourceId(int drawableResourceId) {
        this.drawableResourceId = drawableResourceId;
    }
}
