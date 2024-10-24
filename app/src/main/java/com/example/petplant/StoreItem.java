package com.example.petplant;

import java.io.Serializable;

public class StoreItem implements Serializable {
    private String name;
    private int imageResource;
    private int price;
    private boolean isPurchased; // 구매 여부 추가

    public StoreItem(String name, int imageResource, int price) {
        this.name = name;
        this.imageResource = imageResource;
        this.price = price;
        this.isPurchased = false; // 기본값은 미구매 상태
    }

    public String getName() {
        return name;
    }

    public int getImageResource() {
        return imageResource;
    }

    public int getPrice() {
        return price;
    }

    public boolean isPurchased() {
        return isPurchased;
    }

    public void setPurchased(boolean purchased) {
        isPurchased = purchased;
    }
}
