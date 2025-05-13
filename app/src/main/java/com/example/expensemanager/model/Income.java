package com.example.expensemanager.model;

import java.io.Serializable;

public class Income implements Serializable {
    private String date; // Đổi thành String để gửi chuỗi ISO 8601
    private String title;
    private double amount;
    private long categoryId; // Đổi thành long để gửi số

    public Income(String date, String title, double amount, long categoryId) {
        this.date = date;
        this.title = title;
        this.amount = amount;
        this.categoryId = categoryId;
    }

    // Getters và setters
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public long getCategoryId() { return categoryId; }
    public void setCategoryId(long categoryId) { this.categoryId = categoryId; }
}