package com.uhreckysw.balancer.backend.db;

import java.util.ArrayList;

public class Receipt {
    public static class Item {
        public String name;
        public float price;
        public int quantity;

        public Item setName(String name) {
            this.name = name;
            return this;
        }

        public Item setPrice(Float price) {
            this.price = price;
            return this;
        }

        public Item setQuantity(int quantity) {
            this.quantity = quantity;
            return this;
        }
    }

    public String id;
    public String merchant;
    public ArrayList<Item> items;

    public Receipt setId(String id) {
        this.id = id;
        return this;
    }

    public Receipt setMechant(String merchant) {
        this.merchant = merchant;
        return this;
    }

    public Receipt setItems(ArrayList<Item> items) {
        this.items = items;
        return this;
    }
}
