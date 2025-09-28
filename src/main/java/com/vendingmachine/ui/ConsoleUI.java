package com.vendingmachine.ui;

import com.vendingmachine.model.DispenseResult;
import com.vendingmachine.service.VendingMachine;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private final VendingMachine vendingMachine;
    private final Scanner scanner;
    private boolean running;

    public ConsoleUI(VendingMachine vendingMachine) {
        this.vendingMachine = vendingMachine;
        this.scanner = new Scanner(System.in);
        this.running = false;
    }

    public void start() {
        running = true;
        System.out.println("\n🎰 Welcome to the Vending Machine! 🎰");

        while (running) {
            displayMenu();
            handleUserInput();
        }

        scanner.close();
        System.out.println("Thank you for using the Vending Machine!");
    }

    private void displayMenu() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("VENDING MACHINE MENU");
        System.out.println("=".repeat(60));

        vendingMachine.displayInventory();

        System.out.println("\n" + vendingMachine.getAcceptedCoinsInfo());
        System.out.println("\nOptions:");
        System.out.println("1. Purchase item");
        System.out.println("2. View inventory");
        System.out.println("3. Exit");
        System.out.print("\nEnter your choice (1-3): ");
    }

    private void handleUserInput() {
        try {
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    purchaseItem();
                    break;
                case "2":
                    viewInventory();
                    break;
                case "3":
                    exitSystem();
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1, 2, or 3.");
                    pauseForUser();
                    break;
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            pauseForUser();
        }
    }

    private void purchaseItem() {
        System.out.print("\nEnter item code (e.g., S1, D1): ");
        String itemCode = scanner.nextLine().trim().toUpperCase();

        if (itemCode.isEmpty()) {
            System.out.println("Please enter a valid item code.");
            pauseForUser();
            return;
        }

        if (vendingMachine.getItem(itemCode) == null) {
            System.out.println("Item not found: " + itemCode);
            pauseForUser();
            return;
        }

        var item = vendingMachine.getItem(itemCode);
        System.out.printf("\nSelected: %s - $%d\n",
                item.getDescription(),
                item.getPrice());

        List<Integer> coins = getCoinsFromUser();
        if (coins == null) {
            System.out.println("Transaction cancelled.");
            pauseForUser();
            return;
        }

        System.out.println("\n⏳ Processing transaction...");
        DispenseResult result = vendingMachine.dispenseItem(itemCode, coins);
        displayResult(result);
        pauseForUser();
    }

    private List<Integer> getCoinsFromUser() {
        System.out.println("\nEnter coins one by one (valid: 1, 2, 5, 10)");
        System.out.println("Type 'done' when finished, or 'cancel' to abort:");
        System.out.println("Example: Enter '5', then Enter, then type 'done' and Enter");

        List<Integer> coins = new ArrayList<>();
        int totalValue = 0;

        while (true) {
            System.out.printf("Current total: $%d - Enter coin: ", totalValue);

            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("done")) {
                if (coins.isEmpty()) {
                    System.out.println("No coins entered.");
                    continue;
                }
                break;
            }

            if (input.equals("cancel")) {
                return null;
            }

            try {
                int coinValue = Integer.parseInt(input);
                if (isValidCoin(coinValue)) {
                    coins.add(coinValue);
                    totalValue += coinValue;
                    System.out.printf("✓ Added %s\n", formatCoin(coinValue));
                } else {
                    System.out.println("Invalid coin. Valid coins: 1, 2, 5, 10");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number, 'done', or 'cancel'.");
            }
        }

        return coins;
    }

    private boolean isValidCoin(int coin) {
        return coin == 1 || coin == 2 || coin == 5 || coin == 10;
    }

    private String formatCoin(int coin) {
        return "$" + coin;
    }

    private void displayResult(DispenseResult result) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("TRANSACTION RESULT");
        System.out.println("=".repeat(50));

        if (result.isSuccess()) {
            System.out.println("✅ " + result.getMessage());

            if (!result.getChange().isEmpty()) {
                System.out.println("\n💰 Change returned:");
                for (Integer coin : result.getChange()) {
                    System.out.println("  - " + formatCoin(coin));
                }
            } else {
                System.out.println("\n💰 No change required - exact payment!");
            }
        } else {
            System.out.println(" " + result.getMessage());
        }

        System.out.println("=".repeat(50));
    }

    private void viewInventory() {
        System.out.println("\n Current Inventory:");
        vendingMachine.displayInventory();
        pauseForUser();
    }

    private void exitSystem() {
        System.out.println("\n Goodbye! Have a great day!");
        running = false;
    }

    private void pauseForUser() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }
}