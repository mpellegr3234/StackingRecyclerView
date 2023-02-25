package com.example.mpellegrino.stackinglist;

public class CartItem {
    private String mName;
    private String mPrice;

    public CartItem(String name, String price) {
        this.mName = name;
        this.mPrice = price;
    }

    public String getName() {
        return mName;
    }

    public String getPrice() {
        return mPrice;
    }
}
