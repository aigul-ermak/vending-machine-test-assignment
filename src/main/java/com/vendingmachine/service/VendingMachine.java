package com.vendingmachine.service;

import com.vendingmachine.model.Item;
import com.vendingmachine.model.Snack;
import com.vendingmachine.model.Drink;
import com.vendingmachine.model.DispenseResult;
import com.vendingmachine.model.PaymentResult;
import com.vendingmachine.exception.VendingMachineException;
import com.vendingmachine.exception.ItemNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class VendingMachine {
    private Map<String, Item> inventory;
    private CoinHandler coinHandler;
    private static final int MAX_SNACKS = 3;
    private static final int MAX_DRINKS = 3;
    private int snackCount = 0;
    private int drinkCount = 0;

    public VendingMachine() {
        this.inventory = new HashMap<>();
        this.coinHandler = new CoinHandler();
    }

    public void addItem(Item item) throws VendingMachineException {
        validateInventoryLimit(item);
        inventory.put(item.getCode(), item);
        System.out.println("Added: " + item.toString());
    }

    private void validateInventoryLimit(Item item) throws VendingMachineException {
        if (item instanceof Snack) {
            if (snackCount >= MAX_SNACKS) {
                throw new VendingMachineException(
                    "Cannot add more than " + MAX_SNACKS + " different snacks");
            }
            snackCount++;
        } else if (item instanceof Drink) {
            if (drinkCount >= MAX_DRINKS) {
                throw new VendingMachineException(
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
            System.out.printf("%s %s | %s | $%d | %d | %s\n",
                status,
                item.getCode(),
                item.getDescription(),
                item.getPrice(),
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

    public DispenseResult dispenseItem(String code, List<Integer> coins) {
        try {
            Item item = findItem(code);

            if (!item.canDispense()) {
                return DispenseResult.cannotDispense(item);
            }


            if (!item.isInStock()) {
                return DispenseResult.outOfStock(item);
            }


            PaymentResult paymentResult = coinHandler.processPayment(coins, item.getPrice());
            if (!paymentResult.isSuccess()) {
                return DispenseResult.paymentFailed(paymentResult.getMessage());
            }

            item.decrementStock();

            return DispenseResult.success(item, paymentResult.getChange());

        } catch (ItemNotFoundException e) {
            return DispenseResult.itemNotFound(code);
        }
    }

    private Item findItem(String code) throws ItemNotFoundException {
        Item item = inventory.get(code);
        if (item == null) {
            throw new ItemNotFoundException(code);
        }
        return item;
    }

    public String getAcceptedCoinsInfo() {
        return coinHandler.formatAcceptedCoins();
    }
}