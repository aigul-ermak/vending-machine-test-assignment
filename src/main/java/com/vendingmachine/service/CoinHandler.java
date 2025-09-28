package com.vendingmachine.service;

import com.vendingmachine.model.PaymentResult;
import java.util.*;

public class CoinHandler {
    private static final List<Integer> ACCEPTED_COINS = Arrays.asList(
        1,   // 1 dollar coin
        2,   // 2 dollar coin
        5,   // 5 dollar coin
        10   // 10 dollar coin
    );

    private final Set<Integer> acceptedCoinsSet;

    public CoinHandler() {
        this.acceptedCoinsSet = new HashSet<>(ACCEPTED_COINS);
    }

    public PaymentResult processPayment(List<Integer> coins, int price) {
        List<Integer> invalidCoins = findInvalidCoins(coins);
        if (!invalidCoins.isEmpty()) {
            return PaymentResult.invalidCoins(invalidCoins);
        }

        int totalPaid = calculateTotal(coins);

        if (totalPaid < price) {
            return PaymentResult.insufficientFunds(totalPaid, price);
        }

        int changeAmount = totalPaid - price;
        List<Integer> change = calculateChange(changeAmount);

        return PaymentResult.success(totalPaid, change);
    }

    public boolean validateCoins(List<Integer> coins) {
        if (coins == null || coins.isEmpty()) {
            return false;
        }
        return coins.stream().allMatch(acceptedCoinsSet::contains);
    }

    private List<Integer> findInvalidCoins(List<Integer> coins) {
        List<Integer> invalid = new ArrayList<>();
        if (coins == null) {
            return invalid;
        }
        for (Integer coin : coins) {
            if (coin == null || !acceptedCoinsSet.contains(coin)) {
                invalid.add(coin);
            }
        }
        return invalid;
    }

    public int calculateTotal(List<Integer> coins) {
        if (coins == null || coins.isEmpty()) {
            return 0;
        }
        return coins.stream()
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();
    }

    public List<Integer> calculateChange(int amount) {
        List<Integer> change = new ArrayList<>();
        if (amount <= 0) {
            return change;
        }

        List<Integer> sortedCoins = new ArrayList<>(ACCEPTED_COINS);
        Collections.sort(sortedCoins, Collections.reverseOrder());

        int remaining = amount;
        for (Integer coin : sortedCoins) {
            while (remaining >= coin) {
                change.add(coin);
                remaining -= coin;
            }
        }

        return change;
    }

    public List<Integer> getAcceptedCoins() {
        return new ArrayList<>(ACCEPTED_COINS);
    }

    public String formatAcceptedCoins() {
        StringBuilder sb = new StringBuilder("Accepted coins: ");
        for (int i = 0; i < ACCEPTED_COINS.size(); i++) {
            if (i > 0) sb.append(", ");
            int coin = ACCEPTED_COINS.get(i);
            sb.append("$").append(coin);
        }
        return sb.toString();
    }

    public String formatCoins(List<Integer> coins) {
        if (coins == null || coins.isEmpty()) {
            return "None";
        }

        Map<Integer, Integer> coinCount = new TreeMap<>(Collections.reverseOrder());
        for (Integer coin : coins) {
            coinCount.put(coin, coinCount.getOrDefault(coin, 0) + 1);
        }

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, Integer> entry : coinCount.entrySet()) {
            if (sb.length() > 0) sb.append(", ");
            int coin = entry.getKey();
            int count = entry.getValue();

            String coinStr = "$" + coin;
            if (count > 1) {
                sb.append(count).append(" × ").append(coinStr);
            } else {
                sb.append(coinStr);
            }
        }
        return sb.toString();
    }
}