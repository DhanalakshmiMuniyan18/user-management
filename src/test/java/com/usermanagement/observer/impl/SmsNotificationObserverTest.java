package com.usermanagement.observer.impl;

import com.usermanagement.observer.core.ObserverPriority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author Saravanamuthukumar S
 */
public class SmsNotificationObserverTest {

    @Mock
    private Logger logger;

    private SmsNotificationObserver observer;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        observer = new SmsNotificationObserver();
    }

    @Test
    public void testOnEvent() throws Exception {
        OrderEvent event = new OrderEvent("12345", "67890", 100.0, "ORDER_PLACED", "Order placed successfully");
        observer.onEvent(event);
    }

    @Test
    public void testOnEventWithNullEvent() {
        assertThrows(IllegalArgumentException.class, () -> observer.onEvent(null));
    }

    @Test
    public void testSupports() {
        OrderEvent event = new OrderEvent("12345", "67890", 150.0, "ORDER_PLACED", "Order placed successfully");
        assertTrue(observer.supports(event));

        event = new OrderEvent("12345", "67890", 50.0, "ORDER_PLACED", "Order placed successfully");
        assertFalse(observer.supports(event));
        assertFalse(observer.supports(null));
    }

    @Test
    public void testGetPriority() {
        assertEquals(ObserverPriority.MEDIUM, observer.getPriority());
    }
} 