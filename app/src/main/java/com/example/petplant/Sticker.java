package com.example.petplant;

public class Sticker {
    private int drawableResourceId; // 스티커 리소스 ID

    // 생성자 및 Getter/Setter
    public Sticker() {}

    public Sticker(int drawableResourceId) {
        this.drawableResourceId = drawableResourceId;
    }

    public int getDrawableResourceId() {
        return drawableResourceId;
    }

    public void setDrawableResourceId(int drawableResourceId) {
        this.drawableResourceId = drawableResourceId;
    }
}

