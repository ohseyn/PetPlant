package com.example.petplant;

public class PageItem {
    private String title;
    private String description;
    private String reward;
    private String buttonText;
    private int imageResource;

    public PageItem(String title, String description, String reward, String buttonText, int imageResource) {
        this.title = title;
        this.description = description;
        this.reward = reward;
        this.buttonText = buttonText;
        this.imageResource = imageResource;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getReward() {
        return reward;
    }

    public String getButtonText() {
        return buttonText;
    }

    public int getImageResource() {
        return imageResource;
    }
}