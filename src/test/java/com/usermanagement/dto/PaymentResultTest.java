package com.usermanagement.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Saravanamuthukumar S
 */
public class PaymentResultTest {

    @Test
    public void testBuilder() {
        PaymentResult result = PaymentResult.builder()
            .orderId("12345")
            .amount(100.0)
            .transactionId("txn-123")
            .status(PaymentStatus.SUCCESS.name())
            .message("Payment successful")
            .build();

        assertEquals("12345", result.getOrderId());
        assertEquals(100.0, result.getAmount());
        assertEquals("txn-123", result.getTransactionId());
        assertEquals(PaymentStatus.SUCCESS.name(), result.getStatus());
        assertEquals("Payment successful", result.getMessage());
    }

    @Test
    public void testNoArgsConstructor() {
        PaymentResult result = new PaymentResult();
        assertNotNull(result);
    }

    @Test
    public void testAllArgsConstructor() {
        PaymentResult result = new PaymentResult(
            "12345",
            100.0,
            "txn-123",
            PaymentStatus.SUCCESS.name(),
            "Payment successful"
        );

        assertEquals("12345", result.getOrderId());
        assertEquals(100.0, result.getAmount());
        assertEquals("txn-123", result.getTransactionId());
        assertEquals(PaymentStatus.SUCCESS.name(), result.getStatus());
        assertEquals("Payment successful", result.getMessage());
    }

    @Test
    public void testSettersAndGetters() {
        PaymentResult result = new PaymentResult();

        result.setOrderId("12345");
        result.setAmount(100.0);
        result.setTransactionId("txn-123");
        result.setStatus(PaymentStatus.SUCCESS.name());
        result.setMessage("Payment successful");

        assertEquals("12345", result.getOrderId());
        assertEquals(100.0, result.getAmount());
        assertEquals("txn-123", result.getTransactionId());
        assertEquals(PaymentStatus.SUCCESS.name(), result.getStatus());
        assertEquals("Payment successful", result.getMessage());
    }

    @Test
    public void testToString() {
        PaymentResult result = PaymentResult.builder()
            .orderId("12345")
            .amount(100.0)
            .transactionId("txn-123")
            .status(PaymentStatus.SUCCESS.name())
            .message("Payment successful")
            .build();

        String toString = result.toString();
        assertTrue(toString.contains("orderId=12345"));
        assertTrue(toString.contains("amount=100.0"));
        assertTrue(toString.contains("transactionId=txn-123"));
        assertTrue(toString.contains("status=SUCCESS"));
        assertTrue(toString.contains("message=Payment successful"));
    }

    @Test
    void testPaymentResultEquality() {
        PaymentResult result1 = PaymentResult.builder()
                .orderId("1")
                .transactionId("TXN123")
                .status(PaymentStatus.SUCCESS.name())
                .message("Success")
                .amount(100.0)
                .build();

        PaymentResult result2 = PaymentResult.builder()
                .orderId("1")
                .transactionId("TXN123")
                .status(PaymentStatus.SUCCESS.name())
                .message("Success")
                .amount(100.0)
                .build();

        PaymentResult result3 = PaymentResult.builder()
                .orderId("2")
                .transactionId("TXN456")
                .status(PaymentStatus.FAILED.name())
                .message("Failed")
                .amount(200.0)
                .build();

        assertEquals(result1, result1);
        assertEquals(result1, result2);
        assertNotEquals(result1, result3);
        assertNotEquals(result1, null);
        assertNotEquals(result1, new Object());
    }

    @Test
    void testPaymentResultHashCode() {
        PaymentResult result1 = PaymentResult.builder()
                .orderId("1")
                .transactionId("TXN123")
                .status(PaymentStatus.SUCCESS.name())
                .build();

        PaymentResult result2 = PaymentResult.builder()
                .orderId("1")
                .transactionId("TXN123")
                .status(PaymentStatus.SUCCESS.name())
                .build();

        assertEquals(result1.hashCode(), result2.hashCode());
    }

    @Test
    void testAllPaymentStatuses() {
        // Test all possible payment statuses
        assertEquals("PENDING", PaymentStatus.PENDING.toString());
        assertEquals("SUCCESS", PaymentStatus.SUCCESS.toString());
        assertEquals("FAILED", PaymentStatus.FAILED.toString());
        assertEquals("CANCELLED", PaymentStatus.CANCELLED.toString());
        assertEquals("REFUNDED", PaymentStatus.REFUNDED.toString());
    }
} 