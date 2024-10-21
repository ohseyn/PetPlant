package com.example.petplant;

public class StoreItem {

    private String name;
    private int imageResource;

    public StoreItem(String name, int imageResource) {
        this.name = name;
        this.imageResource = imageResource;
    }

    public String getName() {
        return name;
    }

    public int getImageResource() {
        return imageResource;
    }
}
