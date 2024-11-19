package com.example.petplant;

import java.io.Serializable;

public class StoreItem implements Serializable {
    private String name;
    private int iconImageResource; // 아이콘 이미지 리소스 ID
    private int imageResource; // 실제 적용될 이미지 리소스 ID
    private int price;
    private boolean isPurchased; // 구매 여부 추가
    private String type;
    private boolean isCurrentlyApplied;

    public StoreItem(String name, int iconImageResource, int imageResource, int price, String type) {
        this.name = name;
        this.iconImageResource = iconImageResource;
        this.imageResource = imageResource;
        this.price = price;
        this.isPurchased = false; // 기본값은 미구매 상태
        this.type = type;
    }

    public boolean isBackground() {
        return "background".equals(type);
    }

    public boolean isItem() {
        return "item".equals(type);
    }

    public String getName() {
        return name;
    }

    public int getIconImageResource() { return iconImageResource; }

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

    public boolean isCurrentlyApplied() { return isCurrentlyApplied; }

    public void setCurrentlyApplied(boolean currentlyApplied) { isCurrentlyApplied = currentlyApplied; }
}
