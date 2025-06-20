/**
 * Immutable event representing an order placement.
 * @author Saravanamuthukumar S
 */
package com.usermanagement.observer.impl;

import com.usermanagement.observer.core.Event;

public class OrderEvent extends Event {
    private final String orderId;
    private final String userId;
    private final double amount;

    public OrderEvent(String orderId, String userId, double amount) {
        super();
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
    }
    public String getOrderId() { return orderId; }
    public String getUserId() { return userId; }
    public double getAmount() { return amount; }
} 