package com.vendingmachine.model;

import java.util.List;
import java.util.ArrayList;

public class PaymentResult {
    private final boolean success;
    private final int totalPaid;
    private final List<Integer> change;
    private final String message;

    public PaymentResult(boolean success, int totalPaid, List<Integer> change, String message) {
        this.success = success;
        this.totalPaid = totalPaid;
        this.change = change != null ? new ArrayList<>(change) : new ArrayList<>();
        this.message = message;
    }

    public static PaymentResult success(int totalPaid, List<Integer> change) {
        return new PaymentResult(true, totalPaid, change, "Payment successful");
    }

    public static PaymentResult insufficientFunds(int totalPaid, int required) {
        String msg = String.format("Insufficient funds. Paid: $%d.%02d, Required: $%d.%02d",
                totalPaid / 100, totalPaid % 100,
                required / 100, required % 100);
        return new PaymentResult(false, totalPaid, null, msg);
    }

    public static PaymentResult invalidCoins(List<Integer> invalidCoins) {
        String msg = "Invalid coins detected: " + invalidCoins;
        return new PaymentResult(false, 0, null, msg);
    }

    public boolean isSuccess() {
        return success;
    }

    public int getTotalPaid() {
        return totalPaid;
    }

    public List<Integer> getChange() {
        return new ArrayList<>(change);
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        if (success) {
            return String.format("Payment successful. Total: $%d.%02d, Change: %s",
                    totalPaid / 100, totalPaid % 100, formatChange());
        } else {
            return "Payment failed: " + message;
        }
    }

    private String formatChange() {
        if (change.isEmpty()) {
            return "None";
        }
        StringBuilder sb = new StringBuilder();
        for (Integer coin : change) {
            if (sb.length() > 0) sb.append(", ");
            if (coin >= 100) {
                sb.append("$").append(coin / 100);
            } else {
                sb.append(coin).append("¢");
            }
        }
        return sb.toString();
    }
}