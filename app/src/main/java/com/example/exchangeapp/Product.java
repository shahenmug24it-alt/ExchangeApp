package com.example.exchangeapp;

public class Product {
    private String productId;
    private String name;
    private String description;
    private String price;
    private String userId;

    public Product() {
        // Default constructor required for Firebase Realtime Database calls
    }

    public Product(String productId, String name, String description, String price, String userId) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.userId = userId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
