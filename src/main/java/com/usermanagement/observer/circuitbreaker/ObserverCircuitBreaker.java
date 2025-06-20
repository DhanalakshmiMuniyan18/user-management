/**
 * Circuit breaker utility for observer fault tolerance using Resilience4j.
 * @author Saravanamuthukumar S
 */
package com.usermanagement.observer.circuitbreaker;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.time.Duration;
import java.util.concurrent.Callable;

public class ObserverCircuitBreaker {
    private static final CircuitBreakerRegistry registry = CircuitBreakerRegistry.ofDefaults();

    public static <T> T runWithBreaker(String observerName, Callable<T> callable) throws Exception {
        CircuitBreaker circuitBreaker = registry.circuitBreaker(observerName, CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(10))
                .permittedNumberOfCallsInHalfOpenState(2)
                .slidingWindowSize(10)
                .build());
        try {
            return CircuitBreaker.decorateCallable(circuitBreaker, callable).call();
        } catch (Exception ex) {
            throw ex;
        }
    }
} 