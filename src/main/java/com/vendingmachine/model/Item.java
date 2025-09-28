package com.vendingmachine.model;

public abstract class Item {
    private String code;
    private String description;
    private int price;
    private int stock;

    public Item(String code, String description, int price) {
        this.code = code;
        this.description = description;
        this.price = price;
        this.stock = 0;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void decrementStock() {
        if (stock > 0) {
            stock--;
        }
    }

    public boolean isInStock() {
        return stock > 0;
    }

    public abstract boolean canDispense();

    public abstract String getItemDetails();

    @Override
    public String toString() {
        return String.format("%s - %s: $%d (Stock: %d)",
                code,
                description,
                price,
                stock);
    }
}