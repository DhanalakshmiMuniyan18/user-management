package com.usermanagement.observer.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author Saravanamuthukumar S
 */
public class OrderEventSubjectTest {

    private OrderEventSubject subject;

    @Mock
    private EmailNotificationObserver emailObserver;

    @Mock
    private SmsNotificationObserver smsObserver;

    @Mock
    private AnalyticsObserver analyticsObserver;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        subject = new OrderEventSubject();
    }

    @Test
    public void testRegisterObserver() {
        subject.registerObserver(emailObserver);
        assertTrue(subject.getObservers().contains(emailObserver));
    }

    @Test
    public void testRegisterNullObserver() {
        subject.registerObserver(null);
        assertTrue(subject.getObservers().isEmpty());
    }

    @Test
    public void testRemoveObserver() {
        subject.registerObserver(emailObserver);
        subject.removeObserver(emailObserver);
        assertFalse(subject.getObservers().contains(emailObserver));
    }

    @Test
    public void testRemoveNullObserver() {
        subject.registerObserver(emailObserver);
        subject.removeObserver(null);
        assertTrue(subject.getObservers().contains(emailObserver));
    }

    @Test
    public void testNotifyObservers() throws Exception {
        OrderEvent event = new OrderEvent("12345", "67890", 100.0, "ORDER_PLACED", "Order placed successfully");

        subject.registerObserver(emailObserver);
        subject.registerObserver(smsObserver);
        subject.registerObserver(analyticsObserver);

        // Ensure supports(event) returns true for all observers
        when(emailObserver.supports(event)).thenReturn(true);
        when(smsObserver.supports(event)).thenReturn(true);
        when(analyticsObserver.supports(event)).thenReturn(true);

        subject.notifyObservers(event);

        verify(emailObserver, times(1)).onEvent(event);
        verify(smsObserver, times(1)).onEvent(event);
        verify(analyticsObserver, times(1)).onEvent(event);
    }

    @Test
    public void testNotifyObserversWithNullEvent() throws Exception {
        subject.registerObserver(emailObserver);
        subject.registerObserver(smsObserver);
        subject.registerObserver(analyticsObserver);

        subject.notifyObservers(null);

        verify(emailObserver, never()).onEvent(any());
        verify(smsObserver, never()).onEvent(any());
        verify(analyticsObserver, never()).onEvent(any());
    }

    @Test
    public void testClearObservers() {
        subject.registerObserver(emailObserver);
        subject.registerObserver(smsObserver);
        subject.registerObserver(analyticsObserver);

        subject.clearObservers();
        assertTrue(subject.getObservers().isEmpty());
    }

    @Test
    public void testObserverOrdering() throws Exception {
        subject.registerObserver(emailObserver);
        subject.registerObserver(smsObserver);
        subject.registerObserver(analyticsObserver);

        OrderEvent event = new OrderEvent("12345", "67890", 100.0, "ORDER_PLACED", "Order placed successfully");
        // Ensure supports(event) returns true for all observers
        when(emailObserver.supports(event)).thenReturn(true);
        when(smsObserver.supports(event)).thenReturn(true);
        when(analyticsObserver.supports(event)).thenReturn(true);

        subject.notifyObservers(event);

        // Verify order based on class name
        inOrder(analyticsObserver, emailObserver, smsObserver).verify(analyticsObserver).onEvent(event);
        inOrder(analyticsObserver, emailObserver, smsObserver).verify(emailObserver).onEvent(event);
        inOrder(analyticsObserver, emailObserver, smsObserver).verify(smsObserver).onEvent(event);
    }
} 