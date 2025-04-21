package com.example.expensemanager.model;

import java.io.Serializable;
import java.util.Date;

public class Entry implements Serializable {
    private Date date;
    private String title;
    private double amount;
    private String category;
    private double vatPercentage;
    private String paymentMethod;
    private boolean isExpense;

    public Entry(Date date, String title, double amount, String category, double vatPercentage, String paymentMethod, boolean isExpense) {
        this.date = date;
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.vatPercentage = vatPercentage;
        this.paymentMethod = paymentMethod;
        this.isExpense = isExpense;
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

    public double getVatPercentage() {
        return vatPercentage;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public boolean isExpense() {
        return isExpense;
    }
}