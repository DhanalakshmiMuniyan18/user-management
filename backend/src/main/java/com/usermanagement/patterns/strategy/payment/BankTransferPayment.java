package com.usermanagement.patterns.strategy.payment;

public class BankTransferPayment implements PaymentStrategy {
    @Override
    public boolean pay(double amount) {
        // Simulate bank transfer payment logic
        System.out.println("Paid " + amount + " using Bank Transfer.");
        return true;
    }
}

