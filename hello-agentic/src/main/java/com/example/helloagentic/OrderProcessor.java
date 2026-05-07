package com.example.helloagentic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderProcessor {

    private static final double EXPRESS_MULTIPLIER = 2.5;
    private static final double DIGITAL_BULK_THRESHOLD = 100.0;
    private static final double DIGITAL_BULK_DISCOUNT = 0.05;
    private static final int YEARLY_MONTHS = 12;
    private static final double YEARLY_DISCOUNT = 0.85;

    public record OrderItem(String type, double price, int quantity, double weight, String billing) {

        @SuppressWarnings("unchecked")
        static OrderItem fromMap(Map<String, Object> map) {
            String type = (String) map.get("type");
            double price = map.containsKey("price") ? ((Number) map.get("price")).doubleValue() : 0;
            int quantity = map.containsKey("quantity") ? ((Number) map.get("quantity")).intValue() : 0;
            double weight = map.containsKey("weight") ? ((Number) map.get("weight")).doubleValue() : 0;
            String billing = (String) map.get("billing");
            return new OrderItem(type, price, quantity, weight, billing);
        }

        double baseTotal() {
            return price * quantity;
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> processOrder(Map<String, Object> orderData) {
        List<Map<String, Object>> rawItems = orderData != null
                ? (List<Map<String, Object>>) orderData.get("items")
                : null;

        if (rawItems == null || rawItems.isEmpty()) {
            return errorResult("No items in order");
        }

        List<OrderItem> items = rawItems.stream().map(OrderItem::fromMap).toList();
        boolean express = Boolean.TRUE.equals(orderData.get("express"));
        Double discountPercent = orderData.containsKey("discountPercent")
                ? ((Number) orderData.get("discountPercent")).doubleValue()
                : null;
        Double taxRate = orderData.containsKey("taxRate")
                ? ((Number) orderData.get("taxRate")).doubleValue()
                : null;

        double subtotal = 0;
        for (OrderItem item : items) {
            double itemTotal = calculateItemTotal(item, express);
            itemTotal = applyOrderDiscount(itemTotal, discountPercent);
            subtotal += itemTotal;
        }

        return buildResult(subtotal, taxRate, items.size());
    }

    static double calculateItemTotal(OrderItem item, boolean express) {
        return switch (item.type()) {
            case "physical" -> calculatePhysicalItem(item, express);
            case "digital" -> calculateDigitalItem(item);
            case "subscription" -> calculateSubscriptionItem(item);
            default -> 0;
        };
    }

    private static double calculatePhysicalItem(OrderItem item, boolean express) {
        double shipping = calculateShipping(item.weight());
        if (express) {
            shipping *= EXPRESS_MULTIPLIER;
        }
        return item.baseTotal() + shipping;
    }

    static double calculateShipping(double weight) {
        if (weight > 50) return 25.00;
        if (weight > 20) return 15.00;
        if (weight > 5) return 8.00;
        return 5.00;
    }

    private static double calculateDigitalItem(OrderItem item) {
        double total = item.baseTotal();
        if (total > DIGITAL_BULK_THRESHOLD) {
            total *= (1 - DIGITAL_BULK_DISCOUNT);
        }
        return total;
    }

    private static double calculateSubscriptionItem(OrderItem item) {
        double total = item.baseTotal();
        if ("yearly".equals(item.billing())) {
            total *= YEARLY_MONTHS * YEARLY_DISCOUNT;
        }
        return total;
    }

    private static double applyOrderDiscount(double amount, Double discountPercent) {
        if (discountPercent == null) {
            return amount;
        }
        return amount - (amount * discountPercent / 100);
    }

    private static Map<String, Object> buildResult(double subtotal, Double taxRate, int itemCount) {
        Map<String, Object> result = new HashMap<>();
        result.put("subtotal", subtotal);
        result.put("itemCount", itemCount);
        result.put("status", "processed");

        if (taxRate != null && taxRate != 0) {
            double tax = subtotal * taxRate / 100;
            result.put("tax", tax);
            result.put("total", subtotal + tax);
        } else {
            result.put("tax", 0.0);
            result.put("total", subtotal);
        }

        return result;
    }

    private static Map<String, Object> errorResult(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "error");
        result.put("message", message);
        result.put("total", 0.0);
        return result;
    }
}
