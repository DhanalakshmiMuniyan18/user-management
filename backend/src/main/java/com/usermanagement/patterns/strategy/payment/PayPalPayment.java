package com.usermanagement.patterns.strategy.payment;

public class PayPalPayment implements PaymentStrategy {
    @Override
    public boolean pay(double amount) {
        // Simulate PayPal payment logic
        System.out.println("Paid " + amount + " using PayPal.");
        return true;
    }
}

