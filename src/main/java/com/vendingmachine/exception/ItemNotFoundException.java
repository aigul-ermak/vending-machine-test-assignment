package com.vendingmachine.exception;

public class ItemNotFoundException extends VendingMachineException {
    private final String itemCode;

    public ItemNotFoundException(String itemCode) {
        super(String.format("Item not found: %s", itemCode));
        this.itemCode = itemCode;
    }

    public String getItemCode() {
        return itemCode;
    }
}