package com.vendingmachine.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Snack extends Item {
    private LocalDate manufactureDate;
    private int shelfLifeDays;

    public Snack(String code, String description, int price, LocalDate manufactureDate, int shelfLifeDays) {
        super(code, description, price);
        this.manufactureDate = manufactureDate;
        this.shelfLifeDays = shelfLifeDays;
    }

    public LocalDate getManufactureDate() {
        return manufactureDate;
    }

    public int getShelfLifeDays() {
        return shelfLifeDays;
    }

    public boolean isExpired() {
        LocalDate expiryDate = manufactureDate.plusDays(shelfLifeDays);
        return LocalDate.now().isAfter(expiryDate);
    }

    public int getDaysUntilExpiry() {
        LocalDate expiryDate = manufactureDate.plusDays(shelfLifeDays);
        long days = ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
        return (int) days;
    }

    @Override
    public boolean canDispense() {
        return isInStock() && !isExpired();
    }

    @Override
    public String getItemDetails() {
        if (isExpired()) {
            return String.format("%s (EXPIRED)", getDescription());
        } else {
            int daysLeft = getDaysUntilExpiry();
            if (daysLeft <= 3) {
                return String.format("%s (Expires in %d day%s)",
                    getDescription(),
                    daysLeft,
                    daysLeft == 1 ? "" : "s");
            } else {
                return getDescription();
            }
        }
    }
}