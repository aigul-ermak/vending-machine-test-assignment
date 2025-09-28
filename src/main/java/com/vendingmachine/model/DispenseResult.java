package com.vendingmachine.model;

import java.util.List;
import java.util.ArrayList;

public class DispenseResult {
    private final boolean success;
    private final Item item;
    private final List<Integer> change;
    private final String message;

    public DispenseResult(boolean success, Item item, List<Integer> change, String message) {
        this.success = success;
        this.item = item;
        this.change = change != null ? new ArrayList<>(change) : new ArrayList<>();
        this.message = message;
    }

    public static DispenseResult success(Item item, List<Integer> change) {
        String msg = String.format("Successfully dispensed: %s", item.getDescription());
        return new DispenseResult(true, item, change, msg);
    }

    public static DispenseResult itemNotFound(String code) {
        String msg = String.format("Item not found: %s", code);
        return new DispenseResult(false, null, null, msg);
    }

    public static DispenseResult itemNotAvailable(Item item, String reason) {
        String msg = String.format("%s is not available: %s", item.getDescription(), reason);
        return new DispenseResult(false, item, null, msg);
    }

    public static DispenseResult outOfStock(Item item) {
        String msg = String.format("%s is out of stock", item.getDescription());
        return new DispenseResult(false, item, null, msg);
    }

    public static DispenseResult paymentFailed(String reason) {
        return new DispenseResult(false, null, null, reason);
    }

    public static DispenseResult cannotDispense(Item item) {
        String reason = "";
        if (item instanceof Snack) {
            Snack snack = (Snack) item;
            if (snack.isExpired()) {
                reason = "Product has expired";
            }
        } else if (item instanceof Drink) {
            Drink drink = (Drink) item;
            if (!drink.isAboveThreshold()) {
                reason = String.format("Volume too low (%dml)", drink.getVolumeMl());
            }
        }
        return itemNotAvailable(item, reason);
    }

    public boolean isSuccess() {
        return success;
    }

    public Item getItem() {
        return item;
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
            StringBuilder sb = new StringBuilder();
            sb.append("SUCCESS: ").append(message);
            if (!change.isEmpty()) {
                sb.append("\nChange returned: ");
                for (int i = 0; i < change.size(); i++) {
                    if (i > 0) sb.append(", ");
                    int coin = change.get(i);
                    if (coin >= 100) {
                        sb.append("$").append(coin / 100);
                    } else {
                        sb.append(coin).append("¢");
                    }
                }
            }
            return sb.toString();
        } else {
            return "FAILED: " + message;
        }
    }
}