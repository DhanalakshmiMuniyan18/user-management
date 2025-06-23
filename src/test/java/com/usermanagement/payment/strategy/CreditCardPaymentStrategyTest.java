package com.usermanagement.payment.strategy;

import com.usermanagement.dto.PaymentRequest;
import com.usermanagement.dto.PaymentResult;
import com.usermanagement.dto.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author Saravanamuthukumar S
 */
public class CreditCardPaymentStrategyTest {

    @Mock
    private Logger logger;

    private CreditCardPaymentStrategy strategy;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        strategy = new CreditCardPaymentStrategy();
    }

    @Test
    public void testValidate() {
        PaymentRequest request = createValidRequest();
        assertTrue(strategy.validate(request));
    }

    @Test
    public void testValidateInvalidRequest() {
        PaymentRequest request = createInvalidRequest();
        assertTrue(strategy.validate(request));
    }

    @Test
    public void testProcessPayment() {
        PaymentRequest request = createValidRequest();
        PaymentResult result = strategy.processPayment(request);

        assertNotNull(result);
        assertEquals(request.getOrderId(), result.getOrderId());
        assertEquals(request.getAmount().doubleValue(), result.getAmount());
        assertNotNull(result.getTransactionId());
        assertEquals(PaymentStatus.PENDING.name(), result.getStatus());
        assertNotNull(result.getMessage());
    }

    @Test
    public void testProcessPaymentWithInvalidRequest() {
        PaymentRequest request = createInvalidRequest();
        PaymentResult result = strategy.processPayment(request);

        assertNotNull(result);
        assertEquals(request.getOrderId(), result.getOrderId());
        assertEquals(request.getAmount().doubleValue(), result.getAmount());
        assertNotNull(result.getTransactionId());
        assertEquals(PaymentStatus.PENDING.name(), result.getStatus());
        assertNotNull(result.getMessage());
    }

    @Test
    public void testHandleCallback() {
        String payload = "{\"orderId\":\"12345\",\"status\":\"SUCCESS\"}";
        strategy.handleCallback(payload);
    }

    @Test
    public void testGetStrategyName() {
        assertEquals("CREDIT_CARD", strategy.getStrategyName());
    }

    private PaymentRequest createValidRequest() {
        Map<String, Object> details = new HashMap<>();
        details.put("cardNumber", "4111111111111111");
        details.put("expiryMonth", "12");
        details.put("expiryYear", "2025");
        details.put("cvv", "123");

        return PaymentRequest.builder()
            .orderId("12345")
            .customerId("67890")
            .amount(new BigDecimal("100.00"))
            .currency("USD")
            .paymentMethod("CREDIT_CARD")
            .region("US")
            .merchantId("MERCH123")
            .paymentDetails(details)
            .build();
    }

    private PaymentRequest createInvalidRequest() {
        Map<String, Object> details = new HashMap<>();
        details.put("cardNumber", "invalid");
        details.put("expiryMonth", "13"); // Invalid month
        details.put("expiryYear", "2020"); // Expired year
        details.put("cvv", "12"); // Invalid CVV

        return PaymentRequest.builder()
            .orderId("12345")
            .customerId("67890")
            .amount(new BigDecimal("100.00"))
            .currency("USD")
            .paymentMethod("CREDIT_CARD")
            .region("US")
            .merchantId("MERCH123")
            .paymentDetails(details)
            .build();
    }
} 