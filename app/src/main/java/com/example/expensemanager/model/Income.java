package com.example.expensemanager.model;

import com.google.gson.annotations.SerializedName;


import java.io.Serializable;
import java.util.Date;

public class Income implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("date")
//    private Date date;
    private String date;

    @SerializedName("title")
    private String title;

    @SerializedName("amount")
    private double amount;

    @SerializedName("category")
    private String category;

    @SerializedName("userId")
    private int userId;

    private long categoryId; // Đổi thành long để gửi số

    public Income(int id, String date, String title, double amount, String category, int userId, long categoryId) {
        this.id = id;
        this.date = date;
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.userId = userId;
        this.categoryId = categoryId;
    }

    public Income(Date date, String title, double amount, String category) {}


    public Income(String date, String title, double amount, long categoryId) {
        this.date = date;
        this.title = title;
        this.amount = amount;
        this.categoryId = categoryId;
    }

    public String getCategory() {
        return category;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

