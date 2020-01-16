package com.uhreckysw.balancer.backend.db;

import java.util.Date;

public class Filter {
    public Date dateMin;
    public Date dateMax;
    public float priceMin;
    public float priceMax;
    public String category = "";
    public boolean isDefault = false;


    public Filter setDateMin(Date dateMin) {
        this.dateMin = dateMin;
        return this;
    }

    public Filter setDateMax(Date dateMax) {
        this.dateMax = dateMax;
        return this;
    }

    public Filter setPriceMin(float priceMin) {
        this.priceMin = priceMin;
        return this;
    }

    public Filter setPriceMax(float priceMax) {
        this.priceMax = priceMax;
        return this;
    }

    public Filter setCategory(String category) {
        this.category = category;
        return this;
    }

    public Filter makeDefault() {
        this.isDefault = true;
        return this;
    }
}
