package com.uhreckysw.balancer.backend.db;

public class Limit {
    public int id;
    public String name;
    public float value;
    public float spent;
    public int days;
    public String category;

    public Limit setId(int id) {
        this.id = id;
        return this;
    }

    public Limit setName(String name) {
        this.name = name;
        return this;
    }

    public Limit setValue(float value) {
        this.value = value;
        return this;
    }

    public Limit setSpent(float spent) {
        this.spent = spent;
        return this;
    }

    public Limit setDays(int days) {
        this.days = days;
        return this;
    }

    public Limit setCategory(String category) {
        this.category = category;
        return this;
    }

}
