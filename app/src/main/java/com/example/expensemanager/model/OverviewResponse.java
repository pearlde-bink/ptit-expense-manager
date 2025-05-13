package com.example.expensemanager.model;

import java.util.List;

public class OverviewResponse {
    private double budgetedAmount;
    private double totalIncome;
    private double totalExpense;
    private double currentAmount;
    private List<Entry> entries;

    // Getters và Setters

    public double getBudgetedAmount() {
        return budgetedAmount;
    }

    public double getTotalIncome() {
        return totalIncome;
    }

    public double getTotalExpense() {
        return totalExpense;
    }

    public double getCurrentAmount() {
        return currentAmount;
    }

    public List<Entry> getEntries() {
        return entries;
    }
}