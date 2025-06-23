package com.usermanagement.observer.impl;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Saravanamuthukumar S
 */
public class OrderEventTest {

    @Test
    public void testOrderEventConstructor() {
        OrderEvent event = new OrderEvent("12345", "67890", 100.0, "ORDER_PLACED", "Order placed successfully");

        assertEquals("12345", event.getOrderId());
        assertEquals("67890", event.getUserId());
        assertEquals(100.0, event.getAmount());
        assertEquals("ORDER_PLACED", event.getEventType());
        assertEquals("Order placed successfully", event.getMessage());
    }

    @Test
    public void testOrderEventEquality() {
        OrderEvent event1 = new OrderEvent("12345", "67890", 100.0, "ORDER_PLACED", "Order placed successfully");
        OrderEvent event2 = new OrderEvent("12345", "67890", 100.0, "ORDER_PLACED", "Order placed successfully");
        OrderEvent event3 = new OrderEvent("54321", "67890", 100.0, "ORDER_PLACED", "Order placed successfully");

        assertEquals(event1, event1);
        assertNotEquals(event1, event2); // Events are unique even with same data
        assertNotEquals(event1, event3);
        assertNotEquals(event1, null);
        assertNotEquals(event1, new Object());
    }

    @Test
    public void testOrderEventHashCode() {
        OrderEvent event1 = new OrderEvent("12345", "67890", 100.0, "ORDER_PLACED", "Order placed successfully");
        OrderEvent event2 = new OrderEvent("12345", "67890", 100.0, "ORDER_PLACED", "Order placed successfully");

        assertNotEquals(event1.hashCode(), event2.hashCode()); // Events are unique even with same data
    }

    @Test
    public void testToString() {
        OrderEvent event = new OrderEvent("12345", "67890", 100.0, "ORDER_PLACED", "Order placed successfully");
        String toString = event.toString();

        assertTrue(toString.contains("orderId='12345'"));
        assertTrue(toString.contains("userId='67890'"));
        assertTrue(toString.contains("amount=100.0"));
        assertTrue(toString.contains("eventType='ORDER_PLACED'"));
        assertTrue(toString.contains("message='Order placed successfully'"));
    }
} 