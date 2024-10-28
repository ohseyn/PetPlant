package com.example.petplant;

public class PageItem {
    private String category;
    private String title;
    private String point;
    private String buttonText;
    private int imageResource;    // 배경 이미지 리소스
    private int buttonColorResId; // 버튼 색상 리소스
    private int textColorResId;

    public PageItem(String category, String title, String point, String buttonText,
                    int imageResource, int buttonColorResId, int textColorResId) {
        this.category = category;
        this.title = title;
        this.point = point;
        this.buttonText = buttonText;
        this.imageResource = imageResource;
        this.buttonColorResId = buttonColorResId;
        this.textColorResId = textColorResId;
    }

    public String getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public String getPoint() {
        return point;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public int getImageResource() {
        return imageResource;
    }

    public int getButtonColorResId() {
        return buttonColorResId;
    }

    public int getTextColorResId() {
        return textColorResId;
    }
}
