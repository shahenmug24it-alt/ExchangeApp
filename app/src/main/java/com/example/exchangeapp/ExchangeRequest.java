package com.example.exchangeapp;

public class ExchangeRequest {
    private String requestId;
    private String requesterId;
    private String ownerId;
    private String productId;
    private String productName;
    private String status;

    public ExchangeRequest() {
        // Default constructor required for Firebase Realtime Database
    }

    public ExchangeRequest(String requestId, String requesterId, String ownerId, String productId, String productName, String status) {
        this.requestId = requestId;
        this.requesterId = requesterId;
        this.ownerId = ownerId;
        this.productId = productId;
        this.productName = productName;
        this.status = status;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
