package com.vendingmachine;

import com.vendingmachine.model.Drink;
import com.vendingmachine.model.Snack;
import com.vendingmachine.service.VendingMachine;
import com.vendingmachine.exception.VendingMachineException;
import com.vendingmachine.ui.ConsoleUI;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        System.out.println("Vending Machine System");
        System.out.println("======================");
        System.out.println("Initializing system...\n");

        VendingMachine vendingMachine = initializeVendingMachine();

        if (vendingMachine == null) {
            System.err.println("Failed to initialize vending machine. Exiting.");
            return;
        }

        ConsoleUI consoleUI = new ConsoleUI(vendingMachine);
        consoleUI.start();
    }

    private static VendingMachine initializeVendingMachine() {
        VendingMachine machine = new VendingMachine();

        System.out.println("Loading inventory...\n");

        try {
            Snack chips = new Snack("S1", "Lays Classic Chips", 2,
                    LocalDate.now().minusDays(5), 30);
            chips.setStock(5);
            machine.addItem(chips);

            Snack cookies = new Snack("S2", "Chocolate Cookies", 3,
                    LocalDate.now().minusDays(2), 14);
            cookies.setStock(3);
            machine.addItem(cookies);

            Snack candy = new Snack("S3", "Snickers Bar", 2,
                    LocalDate.now().minusDays(50), 60);
            candy.setStock(4);
            machine.addItem(candy);

            Drink coke = new Drink("D1", "Coca-Cola", 2, 330);
            coke.setStock(6);
            machine.addItem(coke);

            Drink water = new Drink("D2", "Spring Water", 1, 500);
            water.setStock(8);
            machine.addItem(water);

            Drink juice = new Drink("D3", "Orange Juice", 2, 45);
            juice.setStock(2);
            machine.addItem(juice);

            System.out.println("Inventory loaded successfully!");
            System.out.println("   - " + machine.getSnackCount() + " snack types");
            System.out.println("   - " + machine.getDrinkCount() + " drink types");

        } catch (VendingMachineException e) {
            System.err.println("Error loading inventory: " + e.getMessage());
            e.printStackTrace();
            return null;
        }

        return machine;
    }
}