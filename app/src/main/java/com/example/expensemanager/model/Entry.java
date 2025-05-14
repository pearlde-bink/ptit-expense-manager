package com.example.expensemanager.model;

import java.io.Serializable;
import java.util.Date;

public class Entry implements Serializable {
    private int id;
    private Date date;
    private String title;
    private double amount;
    private String entry_type; // expense hoặc income

    private Category category;

    public Entry() {}

    public Entry(int id, Date date, String title, double amount, String entry_type) {
        this.id = id;
        this.date = date;
        this.title = title;
        this.amount = amount;
        this.entry_type = entry_type;
    }

    public int getId() {
        return id;
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

    public String getEntryType() {
        return entry_type;
    }

    public boolean isExpense() {
        return "expense".equalsIgnoreCase(entry_type);
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setEntryType(String entry_type) {
        this.entry_type = entry_type;
    }

    public String getCategory() {
        if(category == null) return "DEFAULT";
        return category.getTitle();
    }
}