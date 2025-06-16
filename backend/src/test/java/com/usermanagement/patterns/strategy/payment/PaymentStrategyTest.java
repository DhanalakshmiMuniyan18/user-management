package com.usermanagement.patterns.strategy.payment;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PaymentStrategyTest {
    @Test
    void testCreditCardPayment() {
        PaymentContext context = new PaymentContext();
        context.setPaymentStrategy(new CreditCardPayment());
        assertTrue(context.processPayment(100.0));
    }

    @Test
    void testPayPalPayment() {
        PaymentContext context = new PaymentContext();
        context.setPaymentStrategy(new PayPalPayment());
        assertTrue(context.processPayment(200.0));
    }

    @Test
    void testBankTransferPayment() {
        PaymentContext context = new PaymentContext();
        context.setPaymentStrategy(new BankTransferPayment());
        assertTrue(context.processPayment(300.0));
    }

    @Test
    void testNoStrategySet() {
        PaymentContext context = new PaymentContext();
        Exception exception = assertThrows(IllegalStateException.class, () -> context.processPayment(50.0));
        assertEquals("Payment strategy not set", exception.getMessage());
    }
}

