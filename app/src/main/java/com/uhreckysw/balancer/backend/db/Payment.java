package com.uhreckysw.balancer.backend.db;

import java.util.Date;

public class Payment {
    public int id;
    public String item;
    public float price;
    public Date date_of_buy;
    public String category;
    public String description;
    public Receipt receipt;

    public Payment setId(int id) {
        this.id = id;
        return this;
    }

    public Payment setItem(String item) {
        this.item = item;
        return this;
    }

    public Payment setPrice(float price) {
        this.price = price;
        return this;
    }

    public Payment setDate_of_buy(Date date_of_buy) {
        this.date_of_buy = date_of_buy;
        return this;
    }

    public Payment setCategory(String category) {
        this.category = category;
        return this;
    }

    public Payment setDescription(String description) {
        this.description = description;
        return this;
    }

    public Payment setReceipt(Receipt receipt) {
        this.receipt = receipt;
        return this;
    }
}
