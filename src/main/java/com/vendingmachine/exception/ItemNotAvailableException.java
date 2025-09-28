package com.vendingmachine.exception;

import com.vendingmachine.model.Item;

public class ItemNotAvailableException extends VendingMachineException {
    private final Item item;
    private final String reason;

    public ItemNotAvailableException(Item item, String reason) {
        super(String.format("%s is not available: %s", item.getDescription(), reason));
        this.item = item;
        this.reason = reason;
    }

    public static ItemNotAvailableException outOfStock(Item item) {
        return new ItemNotAvailableException(item, "out of stock");
    }

    public static ItemNotAvailableException expired(Item item) {
        return new ItemNotAvailableException(item, "product has expired");
    }

    public static ItemNotAvailableException lowVolume(Item item, int currentVolume) {
        String reason = String.format("volume too low (%dml)", currentVolume);
        return new ItemNotAvailableException(item, reason);
    }

    public Item getItem() {
        return item;
    }

    public String getReason() {
        return reason;
    }
}