package com.example.petplant;

public class PageItem {
    private String category;
    private String title;
    private String point;
    private String buttonText;
    private int imageResource;    // 배경 이미지 리소스
    private int buttonColorResId; // 버튼 색상 리소스
    private int textColorResId;
    private boolean isButtonEnabled; // 버튼 활성화 여부

    // 생성자
    public PageItem(String category, String title, String point, String buttonText,
                    int imageResource, int buttonColorResId, int textColorResId) {
        this.category = category;
        this.title = title;
        this.point = point;
        this.buttonText = buttonText;
        this.imageResource = imageResource;
        this.buttonColorResId = buttonColorResId;
        this.textColorResId = textColorResId;
        this.isButtonEnabled = !buttonText.equals("완료"); // 초기 상태 설정
    }

    // Getter와 Setter 메서드
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
        this.isButtonEnabled = !buttonText.equals("완료"); // 텍스트 변경 시 활성화 상태 업데이트
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

    public boolean isButtonEnabled() {
        return isButtonEnabled;
    }

    public void setButtonEnabled(boolean isEnabled) {
        this.isButtonEnabled = isEnabled;
    }
}
