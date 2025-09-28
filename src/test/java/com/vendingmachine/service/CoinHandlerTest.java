package com.vendingmachine.service;

import com.vendingmachine.model.PaymentResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CoinHandlerTest {

    private CoinHandler coinHandler;

    @BeforeEach
    void setUp() {
        coinHandler = new CoinHandler();
    }

    @Test
    @DisplayName("Should accept valid coins")
    void testValidateValidCoins() {
        List<Integer> validCoins = Arrays.asList(1, 2, 5, 10);

        boolean result = coinHandler.validateCoins(validCoins);

        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 5, 10})
    @DisplayName("Should accept each valid coin denomination")
    void testEachValidCoin(int coin) {
        List<Integer> coins = Arrays.asList(coin);

        boolean result = coinHandler.validateCoins(coins);

        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 4, 6, 7, 8, 9, 25, 50, 100})
    @DisplayName("Should reject invalid coin denominations")
    void testInvalidCoins(int invalidCoin) {
        List<Integer> coins = Arrays.asList(invalidCoin);

        boolean result = coinHandler.validateCoins(coins);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should reject null coin list")
    void testValidateNullCoins() {
        boolean result = coinHandler.validateCoins(null);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should reject empty coin list")
    void testValidateEmptyCoins() {
        List<Integer> emptyList = new ArrayList<>();

        boolean result = coinHandler.validateCoins(emptyList);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should calculate total correctly")
    void testCalculateTotal() {
        List<Integer> coins = Arrays.asList(1, 2, 5, 10, 2);

        int total = coinHandler.calculateTotal(coins);

        assertThat(total).isEqualTo(20);
    }

    @Test
    @DisplayName("Should handle null coins in calculation")
    void testCalculateTotalWithNull() {
        int total = coinHandler.calculateTotal(null);

        assertThat(total).isEqualTo(0);
    }

    @Test
    @DisplayName("Should calculate change with optimal coins")
    void testCalculateChangeOptimal() {

        List<Integer> change = coinHandler.calculateChange(18);

        assertThat(change).containsExactly(10, 5, 2, 1);
    }

    @Test
    @DisplayName("Should calculate change using largest coins first")
    void testCalculateChangeLargestFirst() {

        List<Integer> change = coinHandler.calculateChange(23);

        assertThat(change).containsExactly(10, 10, 2, 1);
    }

    @Test
    @DisplayName("Should return empty list for zero change")
    void testCalculateZeroChange() {
        List<Integer> change = coinHandler.calculateChange(0);

        assertThat(change).isEmpty();
    }

    @Test
    @DisplayName("Should return empty list for negative change")
    void testCalculateNegativeChange() {
        List<Integer> change = coinHandler.calculateChange(-5);

        assertThat(change).isEmpty();
    }

    @Test
    @DisplayName("Should process successful payment with exact amount")
    void testProcessPaymentExact() {
        List<Integer> coins = Arrays.asList(2, 1);
        int price = 3;

        PaymentResult result = coinHandler.processPayment(coins, price);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getTotalPaid()).isEqualTo(3);
        assertThat(result.getChange()).isEmpty();
    }

    @Test
    @DisplayName("Should process successful payment with change")
    void testProcessPaymentWithChange() {
        List<Integer> coins = Arrays.asList(10);
        int price = 3;

        PaymentResult result = coinHandler.processPayment(coins, price);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getTotalPaid()).isEqualTo(10);
        assertThat(result.getChange()).containsExactly(5, 2);
    }

    @Test
    @DisplayName("Should fail payment with insufficient funds")
    void testProcessPaymentInsufficientFunds() {
        List<Integer> coins = Arrays.asList(1, 1);
        int price = 5;

        PaymentResult result = coinHandler.processPayment(coins, price);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).contains("Insufficient funds");
        assertThat(result.getTotalPaid()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should fail payment with invalid coins")
    void testProcessPaymentInvalidCoins() {
        List<Integer> coins = Arrays.asList(1, 3, 5);
        int price = 5;

        PaymentResult result = coinHandler.processPayment(coins, price);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).contains("Invalid coins");
    }

    @Test
    @DisplayName("Should format accepted coins correctly")
    void testFormatAcceptedCoins() {
        String formatted = coinHandler.formatAcceptedCoins();

        assertThat(formatted).isEqualTo("Accepted coins: $1, $2, $5, $10");
    }

    @Test
    @DisplayName("Should format coin list with counts")
    void testFormatCoins() {
        List<Integer> coins = Arrays.asList(5, 2, 5, 1, 2, 2);

        String formatted = coinHandler.formatCoins(coins);


        assertThat(formatted).contains("2 × $5");
        assertThat(formatted).contains("3 × $2");
        assertThat(formatted).contains("$1");
    }

    @Test
    @DisplayName("Should format single coins without count")
    void testFormatSingleCoins() {
        List<Integer> coins = Arrays.asList(10, 5, 2, 1);

        String formatted = coinHandler.formatCoins(coins);

        assertThat(formatted).isEqualTo("$10, $5, $2, $1");
    }

    @Test
    @DisplayName("Should format empty coin list as None")
    void testFormatEmptyCoins() {
        List<Integer> emptyList = new ArrayList<>();

        String formatted = coinHandler.formatCoins(emptyList);

        assertThat(formatted).isEqualTo("None");
    }

    @Test
    @DisplayName("Should get accepted coins list")
    void testGetAcceptedCoins() {
        List<Integer> acceptedCoins = coinHandler.getAcceptedCoins();

        assertThat(acceptedCoins)
                .hasSize(4)
                .containsExactly(1, 2, 5, 10);
    }

    @Test
    @DisplayName("Should handle maximum coin value calculations")
    void testMaximumCoinValueCalculations() {

        List<Integer> manyLargeCoins = new ArrayList<>();

        int maxSafeCoins = Integer.MAX_VALUE / 10 - 1000;
        for (int i = 0; i < Math.min(maxSafeCoins, 100000); i++) {
            manyLargeCoins.add(10);
        }

        int total = coinHandler.calculateTotal(manyLargeCoins);
        assertThat(total).isEqualTo(manyLargeCoins.size() * 10);
        assertThat(total).isPositive();
    }

    @Test
    @DisplayName("Should handle maximum change calculation")
    void testMaximumChangeCalculation() {
        int largeChangeAmount = 100000;

        List<Integer> change = coinHandler.calculateChange(largeChangeAmount);

        int calculatedTotal = change.stream().mapToInt(Integer::intValue).sum();
        assertThat(calculatedTotal).isEqualTo(largeChangeAmount);

        long tenDollarCoins = change.stream().filter(coin -> coin == 10).count();
        assertThat(tenDollarCoins).isEqualTo(10000);
    }

    @Test
    @DisplayName("Should handle edge case coin combinations")
    void testEdgeCaseCoinCombinations() {
        List<Integer> edgeCaseCoins = Arrays.asList(
                Integer.MAX_VALUE - 10, 10
        );

        boolean isValid = coinHandler.validateCoins(edgeCaseCoins);
        assertThat(isValid).isFalse();

        List<Integer> maxValidCoins = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            maxValidCoins.add(10); // $10,000 total
        }

        boolean validLargeCollection = coinHandler.validateCoins(maxValidCoins);
        assertThat(validLargeCollection).isTrue();

        int totalLarge = coinHandler.calculateTotal(maxValidCoins);
        assertThat(totalLarge).isEqualTo(10000);
    }

    @Test
    @DisplayName("Should handle null and edge cases in coin lists")
    void testNullAndEdgeCaseCoinLists() {
        List<Integer> coinsWithNull = Arrays.asList(1, null, 5, 2);

        int total = coinHandler.calculateTotal(coinsWithNull);
        assertThat(total).isEqualTo(8);

        boolean valid = coinHandler.validateCoins(coinsWithNull);
        assertThat(valid).isFalse();

        List<Integer> emptyCoins = new ArrayList<>();
        assertThat(coinHandler.calculateTotal(emptyCoins)).isEqualTo(0);
        assertThat(coinHandler.validateCoins(emptyCoins)).isFalse();
        assertThat(coinHandler.calculateChange(0)).isEmpty();
    }
}