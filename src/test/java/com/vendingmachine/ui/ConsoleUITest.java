package com.vendingmachine.ui;

import com.vendingmachine.model.DispenseResult;
import com.vendingmachine.model.Snack;
import com.vendingmachine.model.Item;
import com.vendingmachine.service.VendingMachine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ConsoleUITest {

    @Mock
    private VendingMachine mockVendingMachine;

    private ConsoleUI consoleUI;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    @DisplayName("Should handle insufficient funds with retry - user adds more coins")
    void testInsufficientFundsWithRetry() {

        Snack testItem = new Snack("S1", "Test Snack", 5, LocalDate.now(), 30);
        testItem.setStock(5);
        when(mockVendingMachine.getItem("S1")).thenReturn(testItem);


        DispenseResult successResult = DispenseResult.success(
            testItem, Arrays.asList()); // No change for exact $5


        when(mockVendingMachine.dispenseItem(eq("S1"), argThat(coins ->
            coins != null &&
            coins.stream().mapToInt(Integer::intValue).sum() >= 5
        ))).thenReturn(successResult);

        // Simulate user input:
        // 1 -> Purchase item
        // S1 -> Select item
        // 2 -> Insert $2
        // 1 -> Insert $1
        // done -> Finish first payment (insufficient)
        // yes -> Add more coins
        // 2 -> Insert $2
        // done -> Finish second payment
        // [Enter] -> Continue after success
        // 3 -> Exit
        String input = "1\nS1\n2\n1\ndone\nyes\n2\ndone\n\n3\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        consoleUI = new ConsoleUI(mockVendingMachine);
        consoleUI.start();

        String output = outputStream.toString();


        assertThat(output).contains("Selected: Test Snack - $5");
        assertThat(output).contains("Insufficient funds");
        assertThat(output).contains("Would you like to add more coins?");
        assertThat(output).contains("Amount paid so far: $3, Amount needed: $2");
        assertThat(output).contains("Successfully dispensed");
    }

    @Test
    @DisplayName("Should handle insufficient funds with cancel")
    void testInsufficientFundsWithCancel() {

        Snack testItem = new Snack("S1", "Test Snack", 5, LocalDate.now(), 30);
        testItem.setStock(5);
        when(mockVendingMachine.getItem("S1")).thenReturn(testItem);

        // Simulate user input:
        // 1 -> Purchase item
        // S1 -> Select item
        // 2 -> Insert $2
        // done -> Finish payment (insufficient)
        // no -> Don't add more coins
        // [Enter] -> Continue
        // 3 -> Exit
        String input = "1\nS1\n2\ndone\nno\n\n3\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        consoleUI = new ConsoleUI(mockVendingMachine);
        consoleUI.start();

        String output = outputStream.toString();


        assertThat(output).contains("Selected: Test Snack - $5");
        assertThat(output).contains("Insufficient funds");
        assertThat(output).contains("Would you like to add more coins?");
        assertThat(output).contains("Transaction cancelled");
        assertThat(output).contains("Returning coins: $2");


        verify(mockVendingMachine, never()).dispenseItem(anyString(), anyList());
    }

    @Test
    @DisplayName("Should accumulate coins across multiple attempts")
    void testCoinAccumulation() {

        Snack testItem = new Snack("S1", "Test Snack", 10, LocalDate.now(), 30);
        testItem.setStock(5);
        when(mockVendingMachine.getItem("S1")).thenReturn(testItem);

        // Simulate user input:
        // 1 -> Purchase item
        // S1 -> Select item ($10 required)
        // 2 -> Insert $2
        // done -> First attempt ($2 total - insufficient)
        // yes -> Add more coins
        // 5 -> Insert $5
        // done -> Second attempt ($7 total - insufficient)
        // yes -> Add more coins
        // 5 -> Insert $5
        // done -> Third attempt ($12 total - sufficient)
        // [Enter] -> Continue
        // 3 -> Exit
        String input = "1\nS1\n2\ndone\nyes\n5\ndone\nyes\n5\ndone\n\n3\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        DispenseResult successResult = DispenseResult.success(
            testItem, Arrays.asList(2)); // $2 change
        when(mockVendingMachine.dispenseItem(eq("S1"), anyList()))
            .thenReturn(successResult);

        consoleUI = new ConsoleUI(mockVendingMachine);
        consoleUI.start();

        String output = outputStream.toString();


        assertThat(output).contains("Amount needed: $10");
        assertThat(output).contains("Amount paid so far: $2, Amount needed: $8");
        assertThat(output).contains("Amount paid so far: $7, Amount needed: $3");
        assertThat(output).contains("Sufficient funds reached! Total: $12");
    }

    @Test
    @DisplayName("Should handle cancel during coin entry")
    void testCancelDuringCoinEntry() {

        Snack testItem = new Snack("S1", "Test Snack", 3, LocalDate.now(), 30);
        testItem.setStock(5);
        when(mockVendingMachine.getItem("S1")).thenReturn(testItem);

        // Simulate user input:
        // 1 -> Purchase item
        // S1 -> Select item
        // 1 -> Insert $1
        // cancel -> Cancel transaction
        // [Enter] -> Continue
        // 3 -> Exit
        String input = "1\nS1\n1\ncancel\n\n3\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        consoleUI = new ConsoleUI(mockVendingMachine);
        consoleUI.start();

        String output = outputStream.toString();


        assertThat(output).contains("Transaction cancelled");


        verify(mockVendingMachine, never()).dispenseItem(anyString(), anyList());
    }

    @Test
    @DisplayName("Should show helpful messages during payment")
    void testPaymentGuidanceMessages() {
        // Setup
        Snack testItem = new Snack("S1", "Test Snack", 7, LocalDate.now(), 30);
        testItem.setStock(5);
        when(mockVendingMachine.getItem("S1")).thenReturn(testItem);

        // Simulate user input:
        // 1 -> Purchase item
        // S1 -> Select item
        // 5 -> Insert $5
        // 2 -> Insert $2
        // done -> Complete payment
        // [Enter] -> Continue
        // 3 -> Exit
        String input = "1\nS1\n5\n2\ndone\n\n3\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        DispenseResult successResult = DispenseResult.success(testItem, Arrays.asList());
        when(mockVendingMachine.dispenseItem(eq("S1"), anyList()))
            .thenReturn(successResult);

        consoleUI = new ConsoleUI(mockVendingMachine);
        consoleUI.start();

        String output = outputStream.toString();


        assertThat(output).contains("Amount needed: $7");
        assertThat(output).contains("Current session total: $");
        assertThat(output).contains("Added $5");
        assertThat(output).contains("Sufficient funds reached! Total: $7");
    }

    @Test
    @DisplayName("Should handle exact payment without change")
    void testExactPayment() {

        Snack testItem = new Snack("S1", "Test Snack", 3, LocalDate.now(), 30);
        testItem.setStock(5);
        when(mockVendingMachine.getItem("S1")).thenReturn(testItem);


        String input = "1\nS1\n2\n1\ndone\n\n3\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        DispenseResult successResult = DispenseResult.success(testItem, Arrays.asList());
        when(mockVendingMachine.dispenseItem(eq("S1"), anyList()))
            .thenReturn(successResult);

        consoleUI = new ConsoleUI(mockVendingMachine);
        consoleUI.start();

        String output = outputStream.toString();

        assertThat(output).contains("No change required - exact payment!");
    }

    @Test
    @DisplayName("Should return all coins when cancelling after insufficient payment")
    void testReturnCoinsOnCancel() {

        Snack testItem = new Snack("S1", "Test Snack", 10, LocalDate.now(), 30);
        testItem.setStock(5);
        when(mockVendingMachine.getItem("S1")).thenReturn(testItem);

        // Simulate user input:
        // 1 -> Purchase
        // S1 -> Select expensive item
        // 5 -> Insert $5
        // 2 -> Insert $2
        // done -> Insufficient
        // no -> Don't add more
        // [Enter] -> Continue
        // 3 -> Exit
        String input = "1\nS1\n5\n2\ndone\nno\n\n3\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        consoleUI = new ConsoleUI(mockVendingMachine);
        consoleUI.start();

        String output = outputStream.toString();


        assertThat(output).contains("Transaction cancelled");
        assertThat(output).contains("Returning coins: $5, $2");
    }


    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setIn(System.in);
    }
}