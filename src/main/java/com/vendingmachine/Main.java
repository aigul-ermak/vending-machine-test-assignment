package com.vendingmachine;

import com.vendingmachine.model.Drink;
import com.vendingmachine.model.Snack;
import com.vendingmachine.service.VendingMachine;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        System.out.println("Vending Machine System");
        System.out.println("======================");
        System.out.println("Starting up...\n");

        VendingMachine vendingMachine = initializeVendingMachine();

        vendingMachine.displayInventory();

        System.out.println("\nPress Enter to exit...");
        try {
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Goodbye!");
    }

    private static VendingMachine initializeVendingMachine() {
        VendingMachine machine = new VendingMachine();

        System.out.println("Loading inventory...\n");


        Snack chips = new Snack("S1", "Lays Classic Chips", 150,
                LocalDate.now().minusDays(5), 30);
        chips.setStock(5);
        machine.addItem(chips);

        Snack cookies = new Snack("S2", "Chocolate Cookies", 200,
                LocalDate.now().minusDays(2), 14);
        cookies.setStock(3);
        machine.addItem(cookies);

        Snack candy = new Snack("S3", "Snickers Bar", 175,
                LocalDate.now().minusDays(50), 60);
        candy.setStock(4);
        machine.addItem(candy);


        Drink coke = new Drink("D1", "Coca-Cola", 125, 330);
        coke.setStock(6);
        machine.addItem(coke);

        Drink water = new Drink("D2", "Spring Water", 100, 500);
        water.setStock(8);
        machine.addItem(water);

        Drink juice = new Drink("D3", "Orange Juice", 150, 45);
        juice.setStock(2);
        machine.addItem(juice);

        return machine;
    }
}