/**
 * Immutable event representing an order placement.
 * @author Saravanamuthukumar S
 */
package com.usermanagement.observer.impl;

import com.usermanagement.observer.core.Event;
import lombok.Getter;

@Getter
public class OrderEvent extends Event {
    private final String orderId;
    private final String userId;
    private final double amount;
    private final String eventType;
    private final String message;

    public OrderEvent(String orderId, String userId, double amount, String eventType, String message) {
        super();
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.eventType = eventType;
        this.message = message;
    }

    @Override
    public String toString() {
        return String.format("OrderEvent{orderId='%s', userId='%s', amount=%.2f, eventType='%s', message='%s'}",
            orderId, userId, amount, eventType, message);
    }
} 