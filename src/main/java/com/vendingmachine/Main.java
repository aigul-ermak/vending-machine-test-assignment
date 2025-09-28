package com.vendingmachine;

public class Main {
    public static void main(String[] args) {
        System.out.println("Vending Machine System");
        System.out.println("======================");
        System.out.println("Starting up...");
        System.out.println();
        System.out.println("Press Enter to exit...");
        try {
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Goodbye!");
    }
}