package com.example.expensemanager.model;
import java.io.Serializable;
import java.util.Date;

public class Income implements Serializable {
    private Date date;
    private String title;
    private double amount;
    private String category;

    public Income(Date date, String title, double amount, String category) {
        this.date = date;
        this.title = title;
        this.amount = amount;
        this.category = category;
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
