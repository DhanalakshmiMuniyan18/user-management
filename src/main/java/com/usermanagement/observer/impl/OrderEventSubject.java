/**
 * Thread-safe, async, error-isolating subject for OrderEvent notifications.
 * @author Saravanamuthukumar S
 */
package com.usermanagement.observer.impl;

import com.usermanagement.observer.core.*;
import com.usermanagement.observer.metrics.ObserverMetrics;
import com.usermanagement.observer.circuitbreaker.ObserverCircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class OrderEventSubject implements Subject<OrderEvent> {
    private static final Logger logger = LoggerFactory.getLogger(OrderEventSubject.class);
    private final Set<Observer<OrderEvent>> observers = new ConcurrentSkipListSet<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();

    @Override
    public void registerObserver(Observer<OrderEvent> observer) {
        observers.add(observer);
        logger.info("Observer registered: {}", observer.getClass().getSimpleName());
    }

    @Override
    public void removeObserver(Observer<OrderEvent> observer) {
        observers.remove(observer);
        logger.info("Observer removed: {}", observer.getClass().getSimpleName());
    }

    @Override
    public void notifyObservers(OrderEvent event) {
        for (Observer<OrderEvent> observer : observers) {
            if (observer.supports(event)) {
                executor.submit(() -> {
                    try {
                        ObserverCircuitBreaker.runWithBreaker(
                            observer.getClass().getSimpleName(),
                            () -> {
                                observer.onEvent(event);
                                ObserverMetrics.recordSuccess(observer, event);
                                return null;
                            }
                        );
                        logger.info("Observer {} handled event {}", observer.getClass().getSimpleName(), event.getEventId());
                    } catch (Exception ex) {
                        ObserverMetrics.recordFailure(observer, event, ex);
                        logger.error("Error in observer {}: {}", observer.getClass().getSimpleName(), ex.getMessage(), ex);
                    }
                });
            }
        }
    }

    @Override
    public Set<Observer<OrderEvent>> getObservers() {
        return observers;
    }
} 