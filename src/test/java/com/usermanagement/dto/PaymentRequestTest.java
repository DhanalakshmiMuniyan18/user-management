package com.usermanagement.dto;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Saravanamuthukumar S
 */
public class PaymentRequestTest {

    @Test
    public void testBuilder() {
        Map<String, Object> details = new HashMap<>();
        details.put("cardNumber", "4111111111111111");

        PaymentRequest request = PaymentRequest.builder()
            .orderId("12345")
            .customerId("67890")
            .amount(new BigDecimal("100.00"))
            .currency("USD")
            .paymentMethod("CREDIT_CARD")
            .region("US")
            .merchantId("MERCH123")
            .paymentDetails(details)
            .build();

        assertEquals("12345", request.getOrderId());
        assertEquals("67890", request.getCustomerId());
        assertEquals(new BigDecimal("100.00"), request.getAmount());
        assertEquals("USD", request.getCurrency());
        assertEquals("CREDIT_CARD", request.getPaymentMethod());
        assertEquals("US", request.getRegion());
        assertEquals("MERCH123", request.getMerchantId());
        assertEquals(details, request.getPaymentDetails());
    }

    @Test
    public void testNoArgsConstructor() {
        PaymentRequest request = new PaymentRequest();
        assertNotNull(request);
    }

    @Test
    public void testAllArgsConstructor() {
        Map<String, Object> details = new HashMap<>();
        PaymentRequest request = new PaymentRequest(
            "12345",
            "67890",
            new BigDecimal("100.00"),
            "USD",
            "CREDIT_CARD",
            "US",
            "MERCH123",
            details
        );

        assertEquals("12345", request.getOrderId());
        assertEquals("67890", request.getCustomerId());
        assertEquals(new BigDecimal("100.00"), request.getAmount());
        assertEquals("USD", request.getCurrency());
        assertEquals("CREDIT_CARD", request.getPaymentMethod());
        assertEquals("US", request.getRegion());
        assertEquals("MERCH123", request.getMerchantId());
        assertEquals(details, request.getPaymentDetails());
    }

    @Test
    public void testSettersAndGetters() {
        PaymentRequest request = new PaymentRequest();
        Map<String, Object> details = new HashMap<>();

        request.setOrderId("12345");
        request.setCustomerId("67890");
        request.setAmount(new BigDecimal("100.00"));
        request.setCurrency("USD");
        request.setPaymentMethod("CREDIT_CARD");
        request.setRegion("US");
        request.setMerchantId("MERCH123");
        request.setPaymentDetails(details);

        assertEquals("12345", request.getOrderId());
        assertEquals("67890", request.getCustomerId());
        assertEquals(new BigDecimal("100.00"), request.getAmount());
        assertEquals("USD", request.getCurrency());
        assertEquals("CREDIT_CARD", request.getPaymentMethod());
        assertEquals("US", request.getRegion());
        assertEquals("MERCH123", request.getMerchantId());
        assertEquals(details, request.getPaymentDetails());
    }

    @Test
    void testPaymentRequestEquality() {
        Map<String, Object> details1 = new HashMap<>();
        details1.put("cardNumber", "4111111111111111");

        PaymentRequest request1 = PaymentRequest.builder()
                .orderId("1")
                .amount(new BigDecimal("100.00"))
                .currency("USD")
                .paymentMethod("CREDIT_CARD")
                .paymentDetails(details1)
                .build();

        Map<String, Object> details2 = new HashMap<>();
        details2.put("cardNumber", "4111111111111111");

        PaymentRequest request2 = PaymentRequest.builder()
                .orderId("1")
                .amount(new BigDecimal("100.00"))
                .currency("USD")
                .paymentMethod("CREDIT_CARD")
                .paymentDetails(details2)
                .build();

        Map<String, Object> details3 = new HashMap<>();
        details3.put("email", "test@example.com");

        PaymentRequest request3 = PaymentRequest.builder()
                .orderId("2")
                .amount(new BigDecimal("200.00"))
                .currency("EUR")
                .paymentMethod("PAYPAL")
                .paymentDetails(details3)
                .build();

        assertEquals(request1, request1);
        assertEquals(request1, request2);
        assertNotEquals(request1, request3);
        assertNotEquals(request1, null);
        assertNotEquals(request1, new Object());
    }

    @Test
    void testPaymentRequestHashCode() {
        Map<String, Object> details = new HashMap<>();
        details.put("cardNumber", "4111111111111111");

        PaymentRequest request1 = PaymentRequest.builder()
                .orderId("1")
                .amount(new BigDecimal("100.00"))
                .currency("USD")
                .paymentDetails(details)
                .build();

        PaymentRequest request2 = PaymentRequest.builder()
                .orderId("1")
                .amount(new BigDecimal("100.00"))
                .currency("USD")
                .paymentDetails(details)
                .build();

        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    public void testToString() {
        PaymentRequest request = PaymentRequest.builder()
            .orderId("12345")
            .customerId("67890")
            .amount(new BigDecimal("100.00"))
            .currency("USD")
            .paymentMethod("CREDIT_CARD")
            .region("US")
            .merchantId("MERCH123")
            .build();

        String toString = request.toString();
        assertTrue(toString.contains("orderId=12345"));
        assertTrue(toString.contains("customerId=67890"));
        assertTrue(toString.contains("amount=100.00"));
        assertTrue(toString.contains("currency=USD"));
        assertTrue(toString.contains("paymentMethod=CREDIT_CARD"));
        assertTrue(toString.contains("region=US"));
        assertTrue(toString.contains("merchantId=MERCH123"));
    }
} 