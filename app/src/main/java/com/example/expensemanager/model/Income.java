package com.example.expensemanager.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Date;

public class Income implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("date")
    private Date date;

    @SerializedName("title")
    private String title;

    @SerializedName("amount")
    private double amount;

    @SerializedName("category")
    private String category;  // Đổi từ Category object thành String

    // Constructor for creating a new income
    public Income(Date date, String title, double amount, String category) {
        this.date = date;
        this.title = title;
        this.amount = amount;
        this.category = category;
    }

    // Default constructor for Gson
    public Income() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}