package com.usermanagement.patterns.strategy.payment;

public class CreditCardPayment implements PaymentStrategy {
    @Override
    public boolean pay(double amount) {
        // Simulate credit card payment logic
        System.out.println("Paid " + amount + " using Credit Card.");
        return true;
    }
}

