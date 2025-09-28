package com.vendingmachine.exception;

public class InsufficientFundsException extends VendingMachineException {
    private final int totalPaid;
    private final int requiredAmount;

    public InsufficientFundsException(int totalPaid, int requiredAmount) {
        super(String.format("Insufficient funds. Paid: $%d, Required: $%d",
                totalPaid, requiredAmount));
        this.totalPaid = totalPaid;
        this.requiredAmount = requiredAmount;
    }

    public int getTotalPaid() {
        return totalPaid;
    }

    public int getRequiredAmount() {
        return requiredAmount;
    }

    public int getShortfall() {
        return requiredAmount - totalPaid;
    }
}