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
        System.out.println("\n Welcome to the Vending Machine!");

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
       
        List<Integer> totalCoins = new ArrayList<>();
        boolean paymentComplete = false;

        while (!paymentComplete) {
            List<Integer> coins = getCoinsFromUser(item.getPrice(), calculateTotal(totalCoins));
            if (coins == null) {
                System.out.println("Transaction cancelled.");
                if (!totalCoins.isEmpty()) {
                    System.out.println("Returning coins: " + formatCoinList(totalCoins));
                }
                pauseForUser();
                return;
            }

            totalCoins.addAll(coins);
            int totalPaid = calculateTotal(totalCoins);

            if (totalPaid >= item.getPrice()) {
                paymentComplete = true;
                System.out.println("\n Processing transaction...");
                DispenseResult result = vendingMachine.dispenseItem(itemCode, totalCoins);
                displayResult(result);
                pauseForUser();
            } else {
                int deficit = item.getPrice() - totalPaid;
                System.out.printf("\nInsufficient funds. You paid: $%d, Required: $%d, Deficit: $%d\n",
                    totalPaid, item.getPrice(), deficit);
                System.out.print("Would you like to add more coins? (yes/no): ");
                String response = scanner.nextLine().trim().toLowerCase();

                if (!response.equals("yes") && !response.equals("y")) {
                    System.out.println("Transaction cancelled.");
                    System.out.println("Returning coins: " + formatCoinList(totalCoins));
                    pauseForUser();
                    return;
                }
            }
        }
    }

    private List<Integer> getCoinsFromUser() {
        return getCoinsFromUser(0, 0);
    }

    private List<Integer> getCoinsFromUser(int targetPrice, int alreadyPaid) {
        System.out.println("\nEnter coins one by one (valid: 1, 2, 5, 10)");
        System.out.println("Type 'done' when finished, or 'cancel' to abort:");
        if (targetPrice > 0 && alreadyPaid > 0) {
            System.out.printf("Amount paid so far: $%d, Amount needed: $%d\n", alreadyPaid, targetPrice - alreadyPaid);
        } else if (targetPrice > 0) {
            System.out.printf("Amount needed: $%d\n", targetPrice);
        }

        List<Integer> coins = new ArrayList<>();
        int sessionTotal = 0;

        while (true) {
            System.out.printf("Current session total: $%d - Enter coin: ", sessionTotal);

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
                    sessionTotal += coinValue;
                    System.out.printf("Added %s\n", formatCoin(coinValue));
                    if (targetPrice > 0) {
                        int totalSoFar = alreadyPaid + sessionTotal;
                        if (totalSoFar >= targetPrice) {
                            System.out.printf("Sufficient funds reached! Total: $%d\n", totalSoFar);
                        }
                    }
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

    private int calculateTotal(List<Integer> coins) {
        return coins.stream().mapToInt(Integer::intValue).sum();
    }

    private String formatCoinList(List<Integer> coins) {
        if (coins == null || coins.isEmpty()) {
            return "None";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < coins.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(formatCoin(coins.get(i)));
        }
        return sb.toString();
    }

    private void displayResult(DispenseResult result) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("TRANSACTION RESULT");
        System.out.println("=".repeat(50));

        if (result.isSuccess()) {
            System.out.println(result.getMessage());

            if (!result.getChange().isEmpty()) {
                System.out.println("\nChange returned:");
                for (Integer coin : result.getChange()) {
                    System.out.println("  - " + formatCoin(coin));
                }
            } else {
                System.out.println("\nNo change required - exact payment!");
            }
        } else {
            System.out.println(" " + result.getMessage());
        }

        System.out.println("=".repeat(50));
    }

    private void viewInventory() {
        System.out.println("\nCurrent Inventory:");
        vendingMachine.displayInventory();
        pauseForUser();
    }

    private void exitSystem() {
        System.out.println("\nGoodbye! Have a great day!");
        running = false;
    }

    private void pauseForUser() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }
}