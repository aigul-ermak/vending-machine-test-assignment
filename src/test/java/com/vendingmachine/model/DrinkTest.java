package com.vendingmachine.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DrinkTest {

    @Test
    @DisplayName("Should create drink with default threshold")
    void testDrinkCreationDefaultThreshold() {
        Drink drink = new Drink("D1", "Cola", 3, 330);

        assertThat(drink.getCode()).isEqualTo("D1");
        assertThat(drink.getDescription()).isEqualTo("Cola");
        assertThat(drink.getPrice()).isEqualTo(3);
        assertThat(drink.getVolumeMl()).isEqualTo(330);
        assertThat(drink.getMinVolumeThreshold()).isEqualTo(50);
    }

    @Test
    @DisplayName("Should create drink with custom threshold")
    void testDrinkCreationCustomThreshold() {
        Drink drink = new Drink("D1", "Water", 2, 500, 100);

        assertThat(drink.getVolumeMl()).isEqualTo(500);
        assertThat(drink.getMinVolumeThreshold()).isEqualTo(100);
    }

    @Test
    @DisplayName("Should identify drinks above threshold")
    void testIsAboveThreshold() {
        Drink drink = new Drink("D1", "Cola", 3, 330);

        assertThat(drink.isAboveThreshold()).isTrue();
    }

    @Test
    @DisplayName("Should identify drinks below threshold")
    void testIsBelowThreshold() {
        Drink drink = new Drink("D1", "Almost Empty", 2, 45); // Below 50ml

        assertThat(drink.isAboveThreshold()).isFalse();
    }

    @Test
    @DisplayName("Should identify drinks exactly at threshold")
    void testIsAtThreshold() {
        Drink drink = new Drink("D1", "Minimal", 2, 50); // Exactly 50ml

        assertThat(drink.isAboveThreshold()).isTrue(); // >= threshold
    }

    @Test
    @DisplayName("Should allow dispensing drinks above threshold with stock")
    void testCanDispenseAboveThreshold() {
        Drink drink = new Drink("D1", "Cola", 3, 330);
        drink.setStock(5);

        assertThat(drink.canDispense()).isTrue();
    }

    @Test
    @DisplayName("Should not allow dispensing drinks below threshold")
    void testCannotDispenseBelowThreshold() {
        Drink drink = new Drink("D1", "Low Volume", 2, 40);
        drink.setStock(5);

        assertThat(drink.canDispense()).isFalse();
    }

    @Test
    @DisplayName("Should not allow dispensing out of stock drinks")
    void testCannotDispenseOutOfStock() {
        Drink drink = new Drink("D1", "Cola", 3, 330);
        drink.setStock(0);

        assertThat(drink.canDispense()).isFalse();
    }

    @Test
    @DisplayName("Should format item details for normal drinks")
    void testGetItemDetailsNormal() {
        Drink drink = new Drink("D1", "Cola", 3, 330);

        String details = drink.getItemDetails();

        assertThat(details).isEqualTo("Cola - 330ml");
    }

    @Test
    @DisplayName("Should format item details for low volume drinks")
    void testGetItemDetailsLowVolume() {
        Drink drink = new Drink("D1", "Almost Empty", 2, 45);

        String details = drink.getItemDetails();

        assertThat(details).contains("45ml");
        assertThat(details).contains("BELOW MINIMUM VOLUME");
    }

    @Test
    @DisplayName("Should work with custom threshold correctly")
    void testCustomThresholdBehavior() {

        Drink drink1 = new Drink("D1", "Water", 2, 150, 100);
        Drink drink2 = new Drink("D2", "Juice", 2, 80, 100);

        drink1.setStock(3);
        drink2.setStock(3);

        assertThat(drink1.isAboveThreshold()).isTrue();
        assertThat(drink1.canDispense()).isTrue();

        assertThat(drink2.isAboveThreshold()).isFalse();
        assertThat(drink2.canDispense()).isFalse();
    }

    @Test
    @DisplayName("Should inherit Item properties correctly")
    void testInheritedProperties() {
        Drink drink = new Drink("D1", "Cola", 3, 330);
        drink.setStock(10);

        assertThat(drink.isInStock()).isTrue();
        drink.decrementStock();
        assertThat(drink.getStock()).isEqualTo(9);

        String str = drink.toString();
        assertThat(str).contains("D1");
        assertThat(str).contains("Cola");
        assertThat(str).contains("$3");
        assertThat(str).contains("Stock: 9");
    }

    @Test
    @DisplayName("Should handle edge case volumes")
    void testEdgeCaseVolumes() {
        Drink zeroVolume = new Drink("D1", "Empty", 1, 0);
        Drink largeVolume = new Drink("D2", "Big Bottle", 5, 2000);

        zeroVolume.setStock(1);
        largeVolume.setStock(1);

        assertThat(zeroVolume.isAboveThreshold()).isFalse();
        assertThat(zeroVolume.canDispense()).isFalse();

        assertThat(largeVolume.isAboveThreshold()).isTrue();
        assertThat(largeVolume.canDispense()).isTrue();
    }

    @Test
    @DisplayName("Should handle drinks exactly at threshold volume")
    void testDrinkExactlyAtThreshold() {
        Drink exactlyAtDefaultThreshold = new Drink("D1", "Exactly 50ml", 2, 50);
        exactlyAtDefaultThreshold.setStock(3);

        assertThat(exactlyAtDefaultThreshold.getMinVolumeThreshold()).isEqualTo(50);
        assertThat(exactlyAtDefaultThreshold.getVolumeMl()).isEqualTo(50);
        assertThat(exactlyAtDefaultThreshold.isAboveThreshold()).isTrue();
        assertThat(exactlyAtDefaultThreshold.canDispense()).isTrue();
        assertThat(exactlyAtDefaultThreshold.getItemDetails()).isEqualTo("Exactly 50ml - 50ml");

        Drink exactlyAtCustomThreshold = new Drink("D2", "Exactly 100ml", 3, 100, 100);
        exactlyAtCustomThreshold.setStock(3);

        assertThat(exactlyAtCustomThreshold.getMinVolumeThreshold()).isEqualTo(100);
        assertThat(exactlyAtCustomThreshold.getVolumeMl()).isEqualTo(100);
        assertThat(exactlyAtCustomThreshold.isAboveThreshold()).isTrue();
        assertThat(exactlyAtCustomThreshold.canDispense()).isTrue();
        assertThat(exactlyAtCustomThreshold.getItemDetails()).isEqualTo("Exactly 100ml - 100ml");
    }

    @Test
    @DisplayName("Should handle drinks one unit below threshold")
    void testDrinkOneBelowThreshold() {
        Drink oneBelowDefault = new Drink("D1", "49ml Drink", 2, 49);
        oneBelowDefault.setStock(3);

        assertThat(oneBelowDefault.isAboveThreshold()).isFalse();
        assertThat(oneBelowDefault.canDispense()).isFalse();
        assertThat(oneBelowDefault.getItemDetails()).contains("BELOW MINIMUM VOLUME");

        Drink oneBelowCustom = new Drink("D2", "99ml Drink", 3, 99, 100);
        oneBelowCustom.setStock(3);

        assertThat(oneBelowCustom.isAboveThreshold()).isFalse();
        assertThat(oneBelowCustom.canDispense()).isFalse();
        assertThat(oneBelowCustom.getItemDetails()).contains("BELOW MINIMUM VOLUME");
    }
}