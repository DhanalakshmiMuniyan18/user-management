/**
 * Context for executing payment strategies.
 * @author Saravanamuthukumar S
 */
package com.usermanagement.payment.context;

import com.usermanagement.dto.PaymentRequest;
import com.usermanagement.dto.PaymentResult;
import com.usermanagement.payment.factory.PaymentStrategyFactory;
import com.usermanagement.payment.strategy.PaymentStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentContext {
    private final PaymentStrategyFactory factory;

    @Autowired
    public PaymentContext(PaymentStrategyFactory factory) {
        this.factory = factory;
    }

    /**
     * Executes the payment using the appropriate strategy.
     * @param request the payment request
     * @return the payment result
     */
    public PaymentResult execute(PaymentRequest request) {
        PaymentStrategy strategy = factory.getStrategy(request.getPaymentMethod());
        if (!strategy.validate(request)) {
            return PaymentResult.builder()
                .orderId(request.getOrderId())
                .amount(request.getAmount().doubleValue())
                .status("REJECTED")
                .message("Validation failed")
                .build();
        }
        return strategy.processPayment(request);
    }
} 