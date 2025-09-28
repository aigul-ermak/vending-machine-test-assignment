package com.vendingmachine.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class SnackTest {

    @Test
    @DisplayName("Should create snack with correct properties")
    void testSnackCreation() {
        LocalDate manufactureDate = LocalDate.now();
        Snack snack = new Snack("S1", "Chips", 3, manufactureDate, 30);

        assertThat(snack.getCode()).isEqualTo("S1");
        assertThat(snack.getDescription()).isEqualTo("Chips");
        assertThat(snack.getPrice()).isEqualTo(3);
        assertThat(snack.getManufactureDate()).isEqualTo(manufactureDate);
        assertThat(snack.getShelfLifeDays()).isEqualTo(30);
    }

    @Test
    @DisplayName("Should correctly identify expired snacks")
    void testIsExpired() {

        LocalDate oldDate = LocalDate.now().minusDays(40);
        Snack expiredSnack = new Snack("S1", "Old Chips", 2, oldDate, 30);

        assertThat(expiredSnack.isExpired()).isTrue();
    }

    @Test
    @DisplayName("Should correctly identify fresh snacks")
    void testIsNotExpired() {

        LocalDate recentDate = LocalDate.now().minusDays(5);
        Snack freshSnack = new Snack("S1", "Fresh Chips", 2, recentDate, 30);

        assertThat(freshSnack.isExpired()).isFalse();
    }

    @Test
    @DisplayName("Should calculate days until expiry correctly")
    void testGetDaysUntilExpiry() {

        LocalDate manufactureDate = LocalDate.now().minusDays(20);
        Snack snack = new Snack("S1", "Chips", 2, manufactureDate, 30);

        int daysLeft = snack.getDaysUntilExpiry();

        assertThat(daysLeft).isEqualTo(10);
    }

    @Test
    @DisplayName("Should return negative days for expired snacks")
    void testGetDaysUntilExpiryForExpired() {

        LocalDate oldDate = LocalDate.now().minusDays(35);
        Snack expiredSnack = new Snack("S1", "Old Chips", 2, oldDate, 30);

        int daysLeft = expiredSnack.getDaysUntilExpiry();

        assertThat(daysLeft).isEqualTo(-5);
    }

    @Test
    @DisplayName("Should allow dispensing fresh snacks in stock")
    void testCanDispenseFreshInStock() {
        Snack snack = new Snack("S1", "Chips", 2, LocalDate.now(), 30);
        snack.setStock(5);

        assertThat(snack.canDispense()).isTrue();
    }

    @Test
    @DisplayName("Should not allow dispensing expired snacks")
    void testCannotDispenseExpired() {
        LocalDate oldDate = LocalDate.now().minusDays(40);
        Snack expiredSnack = new Snack("S1", "Old Chips", 2, oldDate, 30);
        expiredSnack.setStock(5); // Has stock but expired

        assertThat(expiredSnack.canDispense()).isFalse();
    }

    @Test
    @DisplayName("Should not allow dispensing out of stock snacks")
    void testCannotDispenseOutOfStock() {
        Snack snack = new Snack("S1", "Chips", 2, LocalDate.now(), 30);
        snack.setStock(0); // Out of stock

        assertThat(snack.canDispense()).isFalse();
    }

    @Test
    @DisplayName("Should format item details for expired snacks")
    void testGetItemDetailsExpired() {
        LocalDate oldDate = LocalDate.now().minusDays(40);
        Snack expiredSnack = new Snack("S1", "Old Chips", 2, oldDate, 30);

        String details = expiredSnack.getItemDetails();

        assertThat(details).contains("EXPIRED");
    }

    @Test
    @DisplayName("Should show warning for snacks expiring soon")
    void testGetItemDetailsExpiringSoon() {

        LocalDate manufactureDate = LocalDate.now().minusDays(28);
        Snack snack = new Snack("S1", "Chips", 2, manufactureDate, 30);

        String details = snack.getItemDetails();

        assertThat(details).contains("Expires in 2 days");
    }

    @Test
    @DisplayName("Should show warning for snacks expiring in 1 day")
    void testGetItemDetailsExpiringTomorrow() {

        LocalDate manufactureDate = LocalDate.now().minusDays(29);
        Snack snack = new Snack("S1", "Chips", 2, manufactureDate, 30);

        String details = snack.getItemDetails();

        assertThat(details).contains("Expires in 1 day");
        assertThat(details).doesNotContain("days"); // Singular
    }

    @Test
    @DisplayName("Should not show warning for fresh snacks")
    void testGetItemDetailsFresh() {

        LocalDate manufactureDate = LocalDate.now().minusDays(5);
        Snack snack = new Snack("S1", "Chips", 2, manufactureDate, 30);

        String details = snack.getItemDetails();

        assertThat(details).isEqualTo("Chips");
        assertThat(details).doesNotContain("Expires");
    }

    @Test
    @DisplayName("Should inherit Item properties correctly")
    void testInheritedProperties() {
        Snack snack = new Snack("S1", "Chips", 3, LocalDate.now(), 30);
        snack.setStock(10);


        assertThat(snack.isInStock()).isTrue();
        snack.decrementStock();
        assertThat(snack.getStock()).isEqualTo(9);


        String str = snack.toString();
        assertThat(str).contains("S1");
        assertThat(str).contains("Chips");
        assertThat(str).contains("$3");
        assertThat(str).contains("Stock: 9");
    }

    @Test
    @DisplayName("Should handle snacks exactly at expiry date (day 0)")
    void testSnackExactlyAtExpiryDate() {
        LocalDate manufactureDate = LocalDate.now().minusDays(30);
        Snack snackExpiringToday = new Snack("S1", "Expiring Today", 2, manufactureDate, 30);
        snackExpiringToday.setStock(5);

        assertThat(snackExpiringToday.isExpired()).isFalse();
        assertThat(snackExpiringToday.getDaysUntilExpiry()).isEqualTo(0);
        assertThat(snackExpiringToday.canDispense()).isTrue();
        assertThat(snackExpiringToday.getItemDetails()).contains("Expires in 0 days");
    }

    @Test
    @DisplayName("Should handle snacks one day before expiry")
    void testSnackOneDayBeforeExpiry() {
        LocalDate manufactureDate = LocalDate.now().minusDays(29);
        Snack snackExpiringTomorrow = new Snack("S1", "Expiring Tomorrow", 2, manufactureDate, 30);
        snackExpiringTomorrow.setStock(5);

        assertThat(snackExpiringTomorrow.isExpired()).isFalse();
        assertThat(snackExpiringTomorrow.getDaysUntilExpiry()).isEqualTo(1);
        assertThat(snackExpiringTomorrow.canDispense()).isTrue();
        assertThat(snackExpiringTomorrow.getItemDetails()).contains("Expires in 1 day");
    }

    @Test
    @DisplayName("Should handle snacks one day after expiry date")
    void testSnackOneDayAfterExpiry() {
        LocalDate manufactureDate = LocalDate.now().minusDays(31);
        Snack expiredSnack = new Snack("S1", "Expired Yesterday", 2, manufactureDate, 30);
        expiredSnack.setStock(5);

        assertThat(expiredSnack.isExpired()).isTrue();
        assertThat(expiredSnack.getDaysUntilExpiry()).isEqualTo(-1);
        assertThat(expiredSnack.canDispense()).isFalse();
        assertThat(expiredSnack.getItemDetails()).contains("EXPIRED");
    }
}