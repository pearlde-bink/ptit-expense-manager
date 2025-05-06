// BudgetRequest.java
package com.example.expensemanager.model;

public class BudgetRequest {
    private int userId;
    private int categoryId;
    private double amount;
    private int month;
    private int year;

    public BudgetRequest(int userId, int categoryId, double amount, int month, int year) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.month = month;
        this.year = year;
    }

}