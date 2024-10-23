package com.example.petplant;

import java.io.Serializable;

public class StoreItem implements Serializable {
    private String name;
    private int imageResource;
    private int price;

    public StoreItem(String name, int imageResource, int price) {
        this.name = name;
        this.imageResource = imageResource;
        this.price = price;
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
}
