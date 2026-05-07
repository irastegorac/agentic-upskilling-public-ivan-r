package com.example.helloagentic;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class LRUCache {

    private final int capacity;
    private final Map<String, Node> items;
    private final LinkedList<Node> order;

    private static class Node {
        String key;
        Object value;

        Node(String key, Object value) {
            this.key = key;
            this.value = value;
        }
    }

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.items = new HashMap<>();
        this.order = new LinkedList<>();
    }

    public Object get(String key) {
        Node node = items.get(key);
        if (node != null) {
            order.remove(node);
            order.addFirst(node);
            return node.value;
        }
        return null;
    }

    public void put(String key, Object value) {
        Node existing = items.get(key);
        if (existing != null) {
            order.remove(existing);
            existing.value = value;
            order.addFirst(existing);
            return;
        }
        if (order.size() >= capacity) {
            Node oldest = order.removeLast();
            items.remove(oldest.key);
        }
        Node node = new Node(key, value);
        order.addFirst(node);
        items.put(key, node);
    }
}
