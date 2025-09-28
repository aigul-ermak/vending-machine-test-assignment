package com.vendingmachine.service;

import com.vendingmachine.model.Item;
import com.vendingmachine.model.Snack;
import com.vendingmachine.model.Drink;
import java.util.HashMap;
import java.util.Map;

public class VendingMachine {
    private Map<String, Item> inventory;
    private static final int MAX_SNACKS = 3;
    private static final int MAX_DRINKS = 3;
    private int snackCount = 0;
    private int drinkCount = 0;

    public VendingMachine() {
        this.inventory = new HashMap<>();
    }

    public void addItem(Item item) {
        validateInventoryLimit(item);
        inventory.put(item.getCode(), item);
        System.out.println("Added: " + item.toString());
    }

    private void validateInventoryLimit(Item item) {
        if (item instanceof Snack) {
            if (snackCount >= MAX_SNACKS) {
                throw new IllegalStateException(
                    "Cannot add more than " + MAX_SNACKS + " different snacks");
            }
            snackCount++;
        } else if (item instanceof Drink) {
            if (drinkCount >= MAX_DRINKS) {
                throw new IllegalStateException(
                    "Cannot add more than " + MAX_DRINKS + " different drinks");
            }
            drinkCount++;
        }
    }

    public void displayInventory() {
        System.out.println("\n=== VENDING MACHINE INVENTORY ===");
        System.out.println("Code | Item | Price | Stock | Details");
        System.out.println("-".repeat(50));

        for (Item item : inventory.values()) {
            String status = item.canDispense() ? "✓" : "✗";
            System.out.printf("%s %s | %s | $%d.%02d | %d | %s\n",
                status,
                item.getCode(),
                item.getDescription(),
                item.getPrice() / 100,
                item.getPrice() % 100,
                item.getStock(),
                item.getItemDetails()
            );
        }
        System.out.println("-".repeat(50));
    }

    public Item getItem(String code) {
        return inventory.get(code);
    }

    public int getSnackCount() {
        return snackCount;
    }

    public int getDrinkCount() {
        return drinkCount;
    }
}