package com.example.expensemanager.model;

public class Budget {
    private int id;
    private int userId;
    private int categoryId;
    private double amount;
    private int month; // 1-12
    private int year;

    public Budget(int id, int userId, int categoryId, double amount, int month, int year) {
        this.id = id;
        this.userId = userId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.month = month;
        this.year = year;
    }

    public Budget(int userId, int categoryId, double amount, int month, int year) {
        this.id = -1; // -1 indicates a new budget to be inserted
        this.userId = userId;
        this.categoryId = categoryId;
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

    public int getCategoryId() {
        return categoryId;
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