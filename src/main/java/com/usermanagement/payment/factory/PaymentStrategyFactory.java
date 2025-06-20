/**
 * Factory for selecting the appropriate PaymentStrategy implementation.
 * @author Saravanamuthukumar S
 */
package com.usermanagement.payment.factory;

import com.usermanagement.payment.strategy.PaymentStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PaymentStrategyFactory {
    private final Map<String, PaymentStrategy> strategyMap;

    @Autowired
    public PaymentStrategyFactory(List<PaymentStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(PaymentStrategy::getStrategyName, s -> s));
    }

    /**
     * Returns the PaymentStrategy for the given payment type.
     * @param type the payment type (e.g., CREDIT_CARD, PAYPAL)
     * @return the PaymentStrategy
     */
    public PaymentStrategy getStrategy(String type) {
        PaymentStrategy strategy = strategyMap.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported payment type: " + type);
        }
        return strategy;
    }
} 