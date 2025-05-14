package com.example.expensemanager.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Category implements Serializable {
    @SerializedName("id")
    private int id;

    private int userId;

    @SerializedName("title")
    private String title;

    @SerializedName("type")
    private String type; // "INCOME" hoặc "EXPENSE"

    @SerializedName("icon")
    private String icon;

    @SerializedName("created_at")
    private String createdAt; // hoặc Date nếu muốn parse ra Date

    public Category(int id, int userId, String title, String type, String icon) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.type = type;
        this.icon = icon;
    }

    public Category(Integer id) {
        this.id = id;
    }

    // Getters & Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
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

    @Override
    public String toString() {
        return title;
    }
}