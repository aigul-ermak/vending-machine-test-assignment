package com.vendingmachine.service;

import com.vendingmachine.exception.VendingMachineException;
import com.vendingmachine.model.DispenseResult;
import com.vendingmachine.model.Drink;
import com.vendingmachine.model.Snack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VendingMachineTest {

    private VendingMachine vendingMachine;

    @BeforeEach
    void setUp() {
        vendingMachine = new VendingMachine();
    }

    @Test
    @DisplayName("Should enforce maximum snack limit of 3")
    void testMaxSnackLimit() throws VendingMachineException {

        for (int i = 1; i <= 3; i++) {
            Snack snack = new Snack("S" + i, "Snack " + i, 2, LocalDate.now(), 30);
            vendingMachine.addItem(snack);
        }

        assertThat(vendingMachine.getSnackCount()).isEqualTo(3);

        Snack extraSnack = new Snack("S4", "Extra Snack", 2, LocalDate.now(), 30);
        assertThatThrownBy(() -> vendingMachine.addItem(extraSnack))
                .isInstanceOf(VendingMachineException.class)
                .hasMessageContaining("Cannot add more than 3 different snacks");
    }

    @Test
    @DisplayName("Should enforce maximum drink limit of 3")
    void testMaxDrinkLimit() throws VendingMachineException {

        for (int i = 1; i <= 3; i++) {
            Drink drink = new Drink("D" + i, "Drink " + i, 2, 330);
            vendingMachine.addItem(drink);
        }

        assertThat(vendingMachine.getDrinkCount()).isEqualTo(3);

        Drink extraDrink = new Drink("D4", "Extra Drink", 2, 330);
        assertThatThrownBy(() -> vendingMachine.addItem(extraDrink))
                .isInstanceOf(VendingMachineException.class)
                .hasMessageContaining("Cannot add more than 3 different drinks");
    }

    @Test
    @DisplayName("Should successfully dispense item with exact payment")
    void testSuccessfulDispenseExactPayment() throws VendingMachineException {

        Snack snack = new Snack("S1", "Chips", 3, LocalDate.now(), 30);
        snack.setStock(5);
        vendingMachine.addItem(snack);

        List<Integer> coins = Arrays.asList(1, 2);

        DispenseResult result = vendingMachine.dispenseItem("S1", coins);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getItem()).isNotNull();
        assertThat(result.getChange()).isEmpty();
        assertThat(snack.getStock()).isEqualTo(4);
    }

    @Test
    @DisplayName("Should successfully dispense item with overpayment and return change")
    void testSuccessfulDispenseWithChange() throws VendingMachineException {

        Drink drink = new Drink("D1", "Cola", 3, 330);
        drink.setStock(10);
        vendingMachine.addItem(drink);

        List<Integer> coins = Arrays.asList(5, 2);

        DispenseResult result = vendingMachine.dispenseItem("D1", coins);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getChange()).containsExactly(2, 2);
        assertThat(drink.getStock()).isEqualTo(9);
    }

    @Test
    @DisplayName("Should fail to dispense when item not found")
    void testDispenseItemNotFound() {
        List<Integer> coins = Arrays.asList(5);

        DispenseResult result = vendingMachine.dispenseItem("INVALID", coins);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).contains("Item not found");
    }

    @Test
    @DisplayName("Should fail to dispense with insufficient funds")
    void testDispenseInsufficientFunds() throws VendingMachineException {

        Snack snack = new Snack("S1", "Candy", 5, LocalDate.now(), 30);
        snack.setStock(3);
        vendingMachine.addItem(snack);

        List<Integer> coins = Arrays.asList(1, 2);

        DispenseResult result = vendingMachine.dispenseItem("S1", coins);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).contains("Insufficient funds");
        assertThat(snack.getStock()).isEqualTo(3);
    }

    @Test
    @DisplayName("Should not dispense expired snacks")
    void testCannotDispenseExpiredSnack() throws VendingMachineException {

        Snack expiredSnack = new Snack("S1", "Old Chips", 2,
                LocalDate.now().minusDays(40), 30); // Expired 10 days ago
        expiredSnack.setStock(5);
        vendingMachine.addItem(expiredSnack);

        List<Integer> coins = Arrays.asList(2);

        DispenseResult result = vendingMachine.dispenseItem("S1", coins);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).contains("not available");
        assertThat(expiredSnack.getStock()).isEqualTo(5);
    }

    @Test
    @DisplayName("Should not dispense drinks below volume threshold")
    void testCannotDispenseLowVolumeDrink() throws VendingMachineException {

        Drink lowVolumeDrink = new Drink("D1", "Almost Empty", 2, 40); // Below 50ml threshold
        lowVolumeDrink.setStock(3);
        vendingMachine.addItem(lowVolumeDrink);

        List<Integer> coins = Arrays.asList(2);

        DispenseResult result = vendingMachine.dispenseItem("D1", coins);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).contains("not available");
        assertThat(lowVolumeDrink.getStock()).isEqualTo(3);
    }

    @Test
    @DisplayName("Should not dispense out of stock items")
    void testCannotDispenseOutOfStock() throws VendingMachineException {

        Snack snack = new Snack("S1", "Chips", 2, LocalDate.now(), 30);
        snack.setStock(0);
        vendingMachine.addItem(snack);

        List<Integer> coins = Arrays.asList(2);

        DispenseResult result = vendingMachine.dispenseItem("S1", coins);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).contains("out of stock");
    }

    @Test
    @DisplayName("Should handle invalid coins properly")
    void testInvalidCoins() throws VendingMachineException {

        Snack snack = new Snack("S1", "Chips", 2, LocalDate.now(), 30);
        snack.setStock(5);
        vendingMachine.addItem(snack);

        List<Integer> coins = Arrays.asList(3, 7);

        DispenseResult result = vendingMachine.dispenseItem("S1", coins);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).contains("Invalid coins");
    }
}