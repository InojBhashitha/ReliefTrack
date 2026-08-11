package com.relieftrack.model;

import java.time.LocalDate;

import com.relieftrack.enums.Category;

public class ReliefItem {

    private int itemId;
    private String name;
    private Category category;
    private LocalDate expiryDate;

    public ReliefItem() {
    }

    public ReliefItem(int itemId, String name, Category category, LocalDate expiryDate) {
        this.itemId = itemId;
        this.name = name;
        this.category = category;
        this.expiryDate = expiryDate;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    @Override
    public String toString() {
        return name + " (" + category + ") | Exp: " + expiryDate;
    }
}