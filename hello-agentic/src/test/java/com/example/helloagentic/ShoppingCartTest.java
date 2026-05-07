package com.example.helloagentic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ShoppingCartTest {

    private ShoppingCart cart;

    @BeforeEach
    void setUp() {
        cart = new ShoppingCart();
    }

    // --- Fix #1: Discount math divides by 100 ---

    @Test
    void calculateTotalAppliesSave10DiscountCorrectly() {
        cart.addItem("Widget", 100.0, 1);
        double total = cart.calculateTotal("SAVE10");
        assertEquals(90.0, total, 0.001);
    }

    @Test
    void calculateTotalAppliesSave20DiscountCorrectly() {
        cart.addItem("Widget", 50.0, 2);
        double total = cart.calculateTotal("SAVE20");
        assertEquals(80.0, total, 0.001);
    }

    @Test
    void calculateTotalAppliesHalfDiscountCorrectly() {
        cart.addItem("Widget", 200.0, 1);
        double total = cart.calculateTotal("HALF");
        assertEquals(100.0, total, 0.001);
    }

    @Test
    void calculateTotalWithNoDiscount() {
        cart.addItem("Widget", 25.0, 4);
        double total = cart.calculateTotal(null);
        assertEquals(100.0, total, 0.001);
    }

    // --- Fix #2: Invalid discount code throws meaningful exception ---

    @Test
    void calculateTotalThrowsOnInvalidDiscountCode() {
        cart.addItem("Widget", 10.0, 1);
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> cart.calculateTotal("BOGUS"));
        assertEquals("Invalid discount code: BOGUS", ex.getMessage());
    }

    // --- Fix #3: Input validation on addItem ---

    @Test
    void addItemThrowsOnNullName() {
        assertThrows(IllegalArgumentException.class,
                () -> cart.addItem(null, 10.0, 1));
    }

    @Test
    void addItemThrowsOnBlankName() {
        assertThrows(IllegalArgumentException.class,
                () -> cart.addItem("  ", 10.0, 1));
    }

    @Test
    void addItemThrowsOnZeroPrice() {
        assertThrows(IllegalArgumentException.class,
                () -> cart.addItem("Widget", 0, 1));
    }

    @Test
    void addItemThrowsOnNegativePrice() {
        assertThrows(IllegalArgumentException.class,
                () -> cart.addItem("Widget", -5.0, 1));
    }

    @Test
    void addItemThrowsOnZeroQuantity() {
        assertThrows(IllegalArgumentException.class,
                () -> cart.addItem("Widget", 10.0, 0));
    }

    @Test
    void addItemThrowsOnNegativeQuantity() {
        assertThrows(IllegalArgumentException.class,
                () -> cart.addItem("Widget", 10.0, -1));
    }

    // --- Fix #6: Type-safe CartItem record (no more ClassCastException) ---

    @Test
    void addAndCalculateMultipleItems() {
        cart.addItem("A", 10.0, 2);
        cart.addItem("B", 5.50, 3);
        double total = cart.calculateTotal(null);
        assertEquals(36.5, total, 0.001);
    }

    @Test
    void removeItemDecrementsCount() {
        cart.addItem("A", 10.0, 1);
        cart.addItem("B", 20.0, 1);
        assertEquals(2, cart.getItemCount());
        cart.removeItem("A");
        assertEquals(1, cart.getItemCount());
    }
}
