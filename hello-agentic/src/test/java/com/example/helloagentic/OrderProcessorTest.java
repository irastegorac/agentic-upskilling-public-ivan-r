package com.example.helloagentic;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Characterization tests capturing the existing behavior of OrderProcessor.processOrder
 * before refactoring. Every test must pass identically after the refactor.
 */
class OrderProcessorTest {

    // --- Error / empty cases ---

    @Test
    void nullOrderReturnsError() {
        Map<String, Object> result = OrderProcessor.processOrder(null);
        assertEquals("error", result.get("status"));
        assertEquals("No items in order", result.get("message"));
        assertEquals(0.0, result.get("total"));
    }

    @Test
    void emptyItemsListReturnsError() {
        Map<String, Object> result = OrderProcessor.processOrder(Map.of("items", List.of()));
        assertEquals("error", result.get("status"));
        assertEquals("No items in order", result.get("message"));
        assertEquals(0.0, result.get("total"));
    }

    // --- Physical items ---

    @Test
    void physicalItemLightweight() {
        // weight <= 5 → shipping $5
        var order = Map.<String, Object>of("items", List.of(
                Map.<String, Object>of("type", "physical", "price", 10.0, "quantity", 2, "weight", 3.0)));
        Map<String, Object> result = OrderProcessor.processOrder(order);
        assertEquals("processed", result.get("status"));
        // 10*2 + 5 shipping = 25
        assertEquals(25.0, (double) result.get("subtotal"), 0.001);
        assertEquals(25.0, (double) result.get("total"), 0.001);
    }

    @Test
    void physicalItemMediumWeight() {
        // weight > 5, <= 20 → shipping $8
        var order = Map.<String, Object>of("items", List.of(
                Map.<String, Object>of("type", "physical", "price", 20.0, "quantity", 1, "weight", 10.0)));
        Map<String, Object> result = OrderProcessor.processOrder(order);
        assertEquals(28.0, (double) result.get("subtotal"), 0.001);
    }

    @Test
    void physicalItemHeavy() {
        // weight > 20, <= 50 → shipping $15
        var order = Map.<String, Object>of("items", List.of(
                Map.<String, Object>of("type", "physical", "price", 50.0, "quantity", 1, "weight", 30.0)));
        Map<String, Object> result = OrderProcessor.processOrder(order);
        assertEquals(65.0, (double) result.get("subtotal"), 0.001);
    }

    @Test
    void physicalItemVeryHeavy() {
        // weight > 50 → shipping $25
        var order = Map.<String, Object>of("items", List.of(
                Map.<String, Object>of("type", "physical", "price", 100.0, "quantity", 1, "weight", 60.0)));
        Map<String, Object> result = OrderProcessor.processOrder(order);
        assertEquals(125.0, (double) result.get("subtotal"), 0.001);
    }

    @Test
    void physicalItemExpressShipping() {
        // weight <= 5 → $5 shipping × 2.5 express = $12.50
        var order = Map.of(
                "items", List.<Map<String, Object>>of(
                        Map.of("type", "physical", "price", 10.0, "quantity", 1, "weight", 2.0)),
                "express", Boolean.TRUE);
        Map<String, Object> result = OrderProcessor.processOrder(order);
        assertEquals(22.5, (double) result.get("subtotal"), 0.001);
    }

    // --- Digital items ---

    @Test
    void digitalItemNoDiscount() {
        // total <= 100, no bulk discount
        var order = Map.<String, Object>of("items", List.of(
                Map.<String, Object>of("type", "digital", "price", 25.0, "quantity", 2)));
        Map<String, Object> result = OrderProcessor.processOrder(order);
        assertEquals(50.0, (double) result.get("subtotal"), 0.001);
    }

    @Test
    void digitalItemBulkDiscount() {
        // total > 100 → 5% discount: 50*3=150 → 150*0.95=142.50
        var order = Map.<String, Object>of("items", List.of(
                Map.<String, Object>of("type", "digital", "price", 50.0, "quantity", 3)));
        Map<String, Object> result = OrderProcessor.processOrder(order);
        assertEquals(142.5, (double) result.get("subtotal"), 0.001);
    }

    // --- Subscription items ---

    @Test
    void subscriptionMonthly() {
        var order = Map.<String, Object>of("items", List.of(
                Map.<String, Object>of("type", "subscription", "price", 10.0, "quantity", 1, "billing", "monthly")));
        Map<String, Object> result = OrderProcessor.processOrder(order);
        assertEquals(10.0, (double) result.get("subtotal"), 0.001);
    }

    @Test
    void subscriptionYearly() {
        // price * quantity * 12 * 0.85
        var order = Map.<String, Object>of("items", List.of(
                Map.<String, Object>of("type", "subscription", "price", 10.0, "quantity", 1, "billing", "yearly")));
        Map<String, Object> result = OrderProcessor.processOrder(order);
        assertEquals(102.0, (double) result.get("subtotal"), 0.001);
    }

    @Test
    void subscriptionUnknownBillingDefaultsToMonthly() {
        var order = Map.<String, Object>of("items", List.of(
                Map.<String, Object>of("type", "subscription", "price", 10.0, "quantity", 1, "billing", "weekly")));
        Map<String, Object> result = OrderProcessor.processOrder(order);
        assertEquals(10.0, (double) result.get("subtotal"), 0.001);
    }

    // --- Unknown type ---

    @Test
    void unknownTypeContributesZero() {
        var order = Map.<String, Object>of("items", List.of(
                Map.<String, Object>of("type", "gift_card", "price", 50.0, "quantity", 1)));
        Map<String, Object> result = OrderProcessor.processOrder(order);
        assertEquals(0.0, (double) result.get("subtotal"), 0.001);
    }

    // --- Order-level discount ---

    @Test
    void orderLevelDiscountApplied() {
        // 10% discount on digital item: 25*2=50 → 50 - 5 = 45
        var order = Map.of(
                "items", List.<Map<String, Object>>of(
                        Map.of("type", "digital", "price", 25.0, "quantity", 2)),
                "discountPercent", 10.0);
        Map<String, Object> result = OrderProcessor.processOrder(order);
        assertEquals(45.0, (double) result.get("subtotal"), 0.001);
    }

    // --- Tax ---

    @Test
    void taxApplied() {
        var order = Map.of(
                "items", List.<Map<String, Object>>of(
                        Map.of("type", "digital", "price", 100.0, "quantity", 1)),
                "taxRate", 8.0);
        Map<String, Object> result = OrderProcessor.processOrder(order);
        assertEquals(100.0, (double) result.get("subtotal"), 0.001);
        assertEquals(8.0, (double) result.get("tax"), 0.001);
        assertEquals(108.0, (double) result.get("total"), 0.001);
    }

    @Test
    void zeroTaxRate() {
        var order = Map.of(
                "items", List.<Map<String, Object>>of(
                        Map.of("type", "digital", "price", 50.0, "quantity", 1)),
                "taxRate", 0.0);
        Map<String, Object> result = OrderProcessor.processOrder(order);
        assertEquals(0.0, (double) result.get("tax"), 0.001);
        assertEquals(50.0, (double) result.get("total"), 0.001);
    }

    // --- Item count ---

    @Test
    void itemCountReflectsNumberOfLineItems() {
        var order = Map.<String, Object>of("items", List.of(
                Map.<String, Object>of("type", "digital", "price", 10.0, "quantity", 1),
                Map.<String, Object>of("type", "digital", "price", 20.0, "quantity", 2)));
        Map<String, Object> result = OrderProcessor.processOrder(order);
        assertEquals(2, result.get("itemCount"));
    }

    // --- Combined scenario ---

    @Test
    void mixedOrderWithDiscountAndTax() {
        // physical: 10*1 + $5 shipping = 15, after 10% discount = 13.50
        // digital:  50*1 = 50, no bulk discount, after 10% discount = 45
        // subtotal = 58.50, tax 10% = 5.85, total = 64.35
        var order = Map.of(
                "items", List.<Map<String, Object>>of(
                        Map.of("type", "physical", "price", 10.0, "quantity", 1, "weight", 2.0),
                        Map.of("type", "digital", "price", 50.0, "quantity", 1)),
                "discountPercent", 10.0,
                "taxRate", 10.0);
        Map<String, Object> result = OrderProcessor.processOrder(order);
        assertEquals(58.5, (double) result.get("subtotal"), 0.001);
        assertEquals(5.85, (double) result.get("tax"), 0.001);
        assertEquals(64.35, (double) result.get("total"), 0.001);
    }
}
