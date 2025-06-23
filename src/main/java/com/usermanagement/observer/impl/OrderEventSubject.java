/**
 * Subject for order events that notifies observers.
 * @author Saravanamuthukumar S
 */
package com.usermanagement.observer.impl;

import com.usermanagement.observer.core.Observer;
import com.usermanagement.observer.core.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

@Component
public class OrderEventSubject implements Subject<OrderEvent> {
    private static final Logger logger = LoggerFactory.getLogger(OrderEventSubject.class);
    private final Set<Observer<OrderEvent>> observers = new ConcurrentSkipListSet<>(
        Comparator.comparing(o -> o.getClass().getName())
    );

    @Override
    public void registerObserver(Observer<OrderEvent> observer) {
        if (observer != null) {
            observers.add(observer);
            logger.debug("Observer registered: {}", observer.getClass().getSimpleName());
        }
    }

    @Override
    public void removeObserver(Observer<OrderEvent> observer) {
        if (observer != null) {
            observers.remove(observer);
            logger.debug("Observer removed: {}", observer.getClass().getSimpleName());
        }
    }

    @Override
    public void notifyObservers(OrderEvent event) {
        if (event != null) {
            observers.forEach(observer -> {
                try {
                    if (observer.supports(event)) {
                        observer.onEvent(event);
                        logger.debug("Observer {} processed event {}", observer.getClass().getSimpleName(), event.getEventId());
                    }
                } catch (Exception e) {
                    logger.error("Error in observer {}: {}", observer.getClass().getSimpleName(), e.getMessage(), e);
                }
            });
        }
    }

    public void clearObservers() {
        observers.clear();
        logger.debug("All observers cleared");
    }

    @Override
    public Set<Observer<OrderEvent>> getObservers() {
        return Collections.unmodifiableSet(observers);
    }
} 