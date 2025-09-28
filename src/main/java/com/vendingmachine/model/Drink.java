package com.vendingmachine.model;

public class Drink extends Item {
    private int volumeMl;
    private int minVolumeThreshold;

    public Drink(String code, String description, int price, int volumeMl) {
        super(code, description, price);
        this.volumeMl = volumeMl;
        this.minVolumeThreshold = 50;
    }

    public Drink(String code, String description, int price, int volumeMl, int minVolumeThreshold) {
        super(code, description, price);
        this.volumeMl = volumeMl;
        this.minVolumeThreshold = minVolumeThreshold;
    }

    public int getVolumeMl() {
        return volumeMl;
    }

    public int getMinVolumeThreshold() {
        return minVolumeThreshold;
    }

    public boolean isAboveThreshold() {
        return volumeMl >= minVolumeThreshold;
    }

    @Override
    public boolean canDispense() {
        return isInStock() && isAboveThreshold();
    }

    @Override
    public String getItemDetails() {
        if (!isAboveThreshold()) {
            return String.format("%s - %dml (BELOW MINIMUM VOLUME)", getDescription(), volumeMl);
        } else {
            return String.format("%s - %dml", getDescription(), volumeMl);
        }
    }
}