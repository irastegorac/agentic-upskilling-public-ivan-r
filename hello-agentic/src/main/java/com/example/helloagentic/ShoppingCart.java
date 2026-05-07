package com.example.helloagentic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoppingCart {

    public record CartItem(String name, double price, int quantity) {}

    private final List<CartItem> items = new ArrayList<>();
    private final Map<String, Integer> discountCodes = new HashMap<>();

    public ShoppingCart() {
        discountCodes.put("SAVE10", 10);
        discountCodes.put("SAVE20", 20);
        discountCodes.put("HALF", 50);
    }

    public void addItem(String name, double price, int quantity) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Item name must not be null or blank");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        items.add(new CartItem(name, price, quantity));
    }

    public void removeItem(String name) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).name().equals(name)) {
                items.remove(i);
                return;
            }
        }
    }

    public double calculateTotal(String discountCode) {
        double total = 0;
        for (CartItem item : items) {
            total += item.price() * item.quantity();
        }

        if (discountCode != null) {
            Integer discount = discountCodes.get(discountCode);
            if (discount == null) {
                throw new IllegalArgumentException("Invalid discount code: " + discountCode);
            }
            total = total - (total * discount / 100.0);
        }

        return total;
    }

    public int getItemCount() {
        return items.size();
    }
}
