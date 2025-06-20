/**
 * PaymentStrategy defines the contract for all payment methods.
 * @author Saravanamuthukumar S
 */
package com.usermanagement.payment.strategy;

import com.usermanagement.dto.PaymentRequest;
import com.usermanagement.dto.PaymentResult;

public interface PaymentStrategy {
    /**
     * Validates the payment request according to the payment method's rules.
     * @param request the payment request
     * @return true if valid, false otherwise
     */
    boolean validate(PaymentRequest request);

    /**
     * Processes the payment asynchronously.
     * @param request the payment request
     * @return the payment result
     */
    PaymentResult processPayment(PaymentRequest request);

    /**
     * Handles webhook or callback payloads from payment providers.
     * @param payload the callback payload
     */
    void handleCallback(String payload);

    /**
     * Returns the strategy name (e.g., CREDIT_CARD, PAYPAL).
     * @return the strategy name
     */
    String getStrategyName();
} 