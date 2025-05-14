package com.example.expensemanager.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class Expense implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("date")
    private Date date;

    @SerializedName("title")
    private String title;

    @SerializedName("amount")
    private double amount;

    @SerializedName("category")
    private String category;

    @SerializedName("userId")
    private int userId;

    public Expense(Date date, String title, double amount, String category) {

    private long categoryId; // Đổi thành long để gửi số

    public Expense(String date, String title, double amount, long categoryId) {
        this.date = date;
        this.title = title;
        this.amount = amount;
        this.categoryId = categoryId;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }
}