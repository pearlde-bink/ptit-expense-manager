package com.example.expensemanager.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Budget implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("userId")
    private int userId;

    @SerializedName("amount")
    private double amount;

    @SerializedName("month")
    private int month;

    @SerializedName("year")
    private int year;

    public Budget(int id, int userId,  double amount, int month, int year) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.month = month;
        this.year = year;
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

    public double getAmount() {
        return amount;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }
}